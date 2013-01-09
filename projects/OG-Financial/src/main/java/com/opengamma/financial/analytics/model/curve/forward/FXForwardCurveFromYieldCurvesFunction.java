/**
 * Copyright (C) 2012 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.financial.analytics.model.curve.forward;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Iterables;
import com.opengamma.OpenGammaRuntimeException;
import com.opengamma.analytics.financial.model.interestrate.curve.ForwardCurve;
import com.opengamma.analytics.financial.model.interestrate.curve.ForwardCurveYieldImplied;
import com.opengamma.analytics.financial.model.interestrate.curve.YieldCurve;
import com.opengamma.core.config.ConfigSource;
import com.opengamma.engine.ComputationTarget;
import com.opengamma.engine.ComputationTargetType;
import com.opengamma.engine.function.AbstractFunction;
import com.opengamma.engine.function.FunctionCompilationContext;
import com.opengamma.engine.function.FunctionExecutionContext;
import com.opengamma.engine.function.FunctionInputs;
import com.opengamma.engine.value.ComputedValue;
import com.opengamma.engine.value.ValueProperties;
import com.opengamma.engine.value.ValuePropertyNames;
import com.opengamma.engine.value.ValueRequirement;
import com.opengamma.engine.value.ValueRequirementNames;
import com.opengamma.engine.value.ValueSpecification;
import com.opengamma.financial.OpenGammaCompilationContext;
import com.opengamma.financial.OpenGammaExecutionContext;
import com.opengamma.financial.analytics.model.forex.forward.FXForwardFunction;
import com.opengamma.financial.currency.ConfigDBCurrencyPairsSource;
import com.opengamma.financial.currency.CurrencyPair;
import com.opengamma.financial.currency.CurrencyPairs;
import com.opengamma.util.money.Currency;
import com.opengamma.util.money.UnorderedCurrencyPair;

/**
 *
 */
public class FXForwardCurveFromYieldCurvesFunction extends AbstractFunction.NonCompiledInvoker {
  private static final Logger s_logger = LoggerFactory.getLogger(FXForwardCurveFromYieldCurvesFunction.class);

  @Override
  public ComputationTargetType getTargetType() {
    return ComputationTargetType.PRIMITIVE;
  }

  @Override
  public Set<ValueSpecification> getResults(final FunctionCompilationContext context, final ComputationTarget target) {
    final ValueProperties properties = createValueProperties()
        .withAny(ValuePropertyNames.CURVE)
        .with(ForwardCurveValuePropertyNames.PROPERTY_FORWARD_CURVE_CALCULATION_METHOD, ForwardCurveValuePropertyNames.PROPERTY_YIELD_CURVE_IMPLIED_METHOD)
        .withAny(ValuePropertyNames.PAY_CURVE)
        .withAny(FXForwardFunction.PAY_CURVE_CALC_CONFIG)
        .withAny(ValuePropertyNames.RECEIVE_CURVE)
        .withAny(FXForwardFunction.RECEIVE_CURVE_CALC_CONFIG)
        .get();
    final ValueSpecification spec = new ValueSpecification(ValueRequirementNames.FORWARD_CURVE, target.toSpecification(), properties);
    return Collections.singleton(spec);
  }

  @Override
  public Set<ValueRequirement> getRequirements(final FunctionCompilationContext context, final ComputationTarget target, final ValueRequirement desiredValue) {
    final ValueProperties constraints = desiredValue.getConstraints();
    final Set<String> fxForwardCurveNames = constraints.getValues(ValuePropertyNames.CURVE);
    if (fxForwardCurveNames == null || fxForwardCurveNames.size() != 1) {
      return null;
    }
    final Set<String> payCurveNames = constraints.getValues(ValuePropertyNames.PAY_CURVE);
    if (payCurveNames == null || payCurveNames.size() != 1) {
      return null;
    }
    final Set<String> payCurveCalculationConfigs = constraints.getValues(FXForwardFunction.PAY_CURVE_CALC_CONFIG);
    if (payCurveCalculationConfigs == null || payCurveCalculationConfigs.size() != 1) {
      return null;
    }
    final Set<String> receiveCurveNames = constraints.getValues(ValuePropertyNames.RECEIVE_CURVE);
    if (receiveCurveNames == null || receiveCurveNames.size() != 1) {
      return null;
    }
    final Set<String> receiveCurveCalculationConfigs = constraints.getValues(FXForwardFunction.RECEIVE_CURVE_CALC_CONFIG);
    if (receiveCurveCalculationConfigs == null || receiveCurveCalculationConfigs.size() != 1) {
      return null;
    }
    final Set<ValueRequirement> result = new HashSet<ValueRequirement>();
    final ValueProperties payCurveProperties = ValueProperties.builder()
        .with(ValuePropertyNames.CURVE, Iterables.getOnlyElement(payCurveNames))
        .with(ValuePropertyNames.CURVE_CALCULATION_CONFIG, Iterables.getOnlyElement(payCurveCalculationConfigs))
        .withOptional(ValuePropertyNames.PAY_CURVE)
        .get();
    final ValueProperties receiveCurveProperties = ValueProperties.builder()
        .with(ValuePropertyNames.CURVE, Iterables.getOnlyElement(receiveCurveNames))
        .with(ValuePropertyNames.CURVE_CALCULATION_CONFIG, Iterables.getOnlyElement(receiveCurveCalculationConfigs))
        .withOptional(ValuePropertyNames.RECEIVE_CURVE)
        .get();
    final UnorderedCurrencyPair ccyPair = UnorderedCurrencyPair.of(target.getUniqueId());
    Currency payCurrency;
    Currency receiveCurrency;
    final ConfigSource configSource = OpenGammaCompilationContext.getConfigSource(context);
    final ConfigDBCurrencyPairsSource currencyPairsSource = new ConfigDBCurrencyPairsSource(configSource);
    final CurrencyPairs baseQuotePairs = currencyPairsSource.getCurrencyPairs(CurrencyPairs.DEFAULT_CURRENCY_PAIRS);
    final CurrencyPair baseQuotePair = baseQuotePairs.getCurrencyPair(ccyPair.getFirstCurrency(), ccyPair.getSecondCurrency());
    if (baseQuotePair == null) {
      throw new OpenGammaRuntimeException("Could not get base/quote pair for currency pair " + ccyPair);
    }
    if (baseQuotePair.getBase().equals(ccyPair.getFirstCurrency())) {
      payCurrency = baseQuotePair.getBase();
      receiveCurrency = baseQuotePair.getCounter();
    } else {
      payCurrency = baseQuotePair.getCounter();
      receiveCurrency = baseQuotePair.getBase();
    }
    result.add(new ValueRequirement(ValueRequirementNames.SPOT_RATE, ccyPair));
    result.add(new ValueRequirement(ValueRequirementNames.YIELD_CURVE, payCurrency.getUniqueId(), payCurveProperties));
    result.add(new ValueRequirement(ValueRequirementNames.YIELD_CURVE, receiveCurrency.getUniqueId(), receiveCurveProperties));
    return result;
  }

  @Override
  public boolean canApplyTo(final FunctionCompilationContext context, final ComputationTarget target) {
    if (target.getType() != ComputationTargetType.PRIMITIVE) {
      return false;
    }
    if (target.getUniqueId() == null) {
      s_logger.error("Target unique id was null, {}", target);
      return false;
    }
    return UnorderedCurrencyPair.OBJECT_SCHEME.equals(target.getUniqueId().getScheme());
  }

  @Override
  public Set<ComputedValue> execute(final FunctionExecutionContext executionContext, final FunctionInputs inputs, final ComputationTarget target, final Set<ValueRequirement> desiredValues) {
    Object payCurveObject = null;
    Object receiveCurveObject = null;
    Set<String> payCurveNames = null;
    Set<String> payCurveCalculationConfigs = null;
    Set<String> receiveCurveNames = null;
    Set<String> receiveCurveCalculationConfigs = null;
    final UnorderedCurrencyPair ccyPair = UnorderedCurrencyPair.of(target.getUniqueId());
    Currency payCurrency;
    Currency receiveCurrency;
    final ConfigSource configSource = OpenGammaExecutionContext.getConfigSource(executionContext);
    final ConfigDBCurrencyPairsSource currencyPairsSource = new ConfigDBCurrencyPairsSource(configSource);
    final CurrencyPairs baseQuotePairs = currencyPairsSource.getCurrencyPairs(CurrencyPairs.DEFAULT_CURRENCY_PAIRS);
    final CurrencyPair baseQuotePair = baseQuotePairs.getCurrencyPair(ccyPair.getFirstCurrency(), ccyPair.getSecondCurrency());
    if (baseQuotePair == null) {
      throw new OpenGammaRuntimeException("Could not get base/quote pair for currency pair " + ccyPair);
    }
    if (baseQuotePair.getBase().equals(ccyPair.getFirstCurrency())) {
      payCurrency = baseQuotePair.getBase();
      receiveCurrency = baseQuotePair.getCounter();
    } else {
      payCurrency = baseQuotePair.getCounter();
      receiveCurrency = baseQuotePair.getBase();
    }
    final Double spot = (Double) inputs.getValue(ValueRequirementNames.SPOT_RATE);
    for (final ComputedValue input : inputs.getAllValues()) {
      final ValueSpecification spec = input.getSpecification();
      final ValueProperties properties = spec.getProperties();
      if (spec.getTargetSpecification().getUniqueId().equals(payCurrency.getUniqueId())) {
        payCurveObject = input.getValue();
        payCurveNames = properties.getValues(ValuePropertyNames.CURVE);
        payCurveCalculationConfigs = properties.getValues(ValuePropertyNames.CURVE_CALCULATION_CONFIG);
      } else if (spec.getTargetSpecification().getUniqueId().equals(receiveCurrency.getUniqueId())) {
        receiveCurveObject = input.getValue();
        receiveCurveNames = properties.getValues(ValuePropertyNames.CURVE);
        receiveCurveCalculationConfigs = properties.getValues(ValuePropertyNames.CURVE_CALCULATION_CONFIG);
      }
    }
    if (payCurveObject == null) {
      throw new OpenGammaRuntimeException("Could not get pay curve");
    }
    if (receiveCurveObject == null) {
      throw new OpenGammaRuntimeException("Could not get receive curve");
    }
    if (payCurveNames == null || payCurveNames.size() != 1) {
      throw new OpenGammaRuntimeException("Null or non-unique curve name: " + payCurveNames);
    }
    if (receiveCurveNames == null || receiveCurveNames.size() != 1) {
      throw new OpenGammaRuntimeException("Null or non-unique curve name: " + receiveCurveNames);
    }
    final ValueRequirement desiredValue = desiredValues.iterator().next();
    final Set<String> fxForwardCurveNames = desiredValue.getConstraints().getValues(ValuePropertyNames.CURVE);
    if (fxForwardCurveNames == null || fxForwardCurveNames.size() != 1) {
      throw new OpenGammaRuntimeException("Null or non-unique FX forward curve names: " + fxForwardCurveNames);
    }
    final YieldCurve payCurve = (YieldCurve) payCurveObject;
    final YieldCurve receiveCurve = (YieldCurve) receiveCurveObject;
    final String fxForwardCurveName = fxForwardCurveNames.iterator().next();
    final ForwardCurve fxForwardCurve = new ForwardCurveYieldImplied(spot, payCurve, receiveCurve);
    final ValueProperties properties = createValueProperties()
        .with(ValuePropertyNames.CURVE, fxForwardCurveName)
        .with(ForwardCurveValuePropertyNames.PROPERTY_FORWARD_CURVE_CALCULATION_METHOD, ForwardCurveValuePropertyNames.PROPERTY_YIELD_CURVE_IMPLIED_METHOD)
        .with(ValuePropertyNames.PAY_CURVE, Iterables.getOnlyElement(payCurveNames))
        .with(FXForwardFunction.PAY_CURVE_CALC_CONFIG, Iterables.getOnlyElement(payCurveCalculationConfigs))
        .with(ValuePropertyNames.RECEIVE_CURVE, Iterables.getOnlyElement(receiveCurveNames))
        .with(FXForwardFunction.RECEIVE_CURVE_CALC_CONFIG, Iterables.getOnlyElement(receiveCurveCalculationConfigs))
        .get();
    final ValueSpecification resultSpec = new ValueSpecification(ValueRequirementNames.FORWARD_CURVE, target.toSpecification(), properties);
    return Collections.singleton(new ComputedValue(resultSpec, fxForwardCurve));
  }

}
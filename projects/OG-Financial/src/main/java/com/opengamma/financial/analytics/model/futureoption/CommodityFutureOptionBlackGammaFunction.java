/**
 * Copyright (C) 2012 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.financial.analytics.model.futureoption;

import java.util.Collections;
import java.util.Set;

import com.google.common.collect.Iterables;
import com.opengamma.analytics.financial.commodity.calculator.CommodityFutureOptionBlackGammaCalculator;
import com.opengamma.analytics.financial.equity.StaticReplicationDataBundle;
import com.opengamma.analytics.financial.interestrate.InstrumentDerivative;
import com.opengamma.engine.ComputationTarget;
import com.opengamma.engine.value.ComputedValue;
import com.opengamma.engine.value.ValueRequirement;
import com.opengamma.engine.value.ValueRequirementNames;
import com.opengamma.engine.value.ValueSpecification;

/**
 *
 */
public class CommodityFutureOptionBlackGammaFunction extends CommodityFutureOptionBlackFunction {

  /**
   * Default constructor
   */
  public CommodityFutureOptionBlackGammaFunction() {
    super(ValueRequirementNames.VALUE_GAMMA);
  }

  @Override
  protected Set<ComputedValue> computeValues(final InstrumentDerivative derivative, final StaticReplicationDataBundle market, final Set<ValueRequirement> desiredValues,
      final ComputationTarget target) {
    final ValueRequirement desiredValue = Iterables.getOnlyElement(desiredValues);
    final double gamma = derivative.accept(CommodityFutureOptionBlackGammaCalculator.getInstance(), market);
    final ValueSpecification spec = new ValueSpecification(getValueRequirementNames()[0], target.toSpecification(), createResultProperties(desiredValue.getConstraints()));
    return Collections.singleton(new ComputedValue(spec, gamma));
  }

}
/**
 * Copyright (C) 2012 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.financial.analytics.model.horizon;

import java.util.Collections;
import java.util.Set;

import com.opengamma.engine.ComputationTarget;
import com.opengamma.engine.ComputationTargetType;
import com.opengamma.engine.function.FunctionCompilationContext;
import com.opengamma.engine.value.ValueRequirement;
import com.opengamma.engine.value.ValueRequirementNames;
import com.opengamma.financial.analytics.OpenGammaFunctionExclusions;
import com.opengamma.financial.property.DefaultPropertyFunction;
import com.opengamma.financial.security.FinancialSecurity;
import com.opengamma.financial.security.option.FXBarrierOptionSecurity;
import com.opengamma.financial.security.option.FXDigitalOptionSecurity;
import com.opengamma.financial.security.option.FXOptionSecurity;
import com.opengamma.financial.security.option.NonDeliverableFXDigitalOptionSecurity;
import com.opengamma.financial.security.option.NonDeliverableFXOptionSecurity;
import com.opengamma.util.ArgumentChecker;

/**
 *
 */
public class FXOptionBlackThetaDefaults extends DefaultPropertyFunction {
  private final PriorityClass _priority;
  private final String _defaultNumberOfDays;

  public FXOptionBlackThetaDefaults(final String priority, final String defaultNumberOfDays) {
    super(ComputationTargetType.SECURITY, true);
    ArgumentChecker.notNull(priority, "priority");
    ArgumentChecker.notNull(defaultNumberOfDays, "default number of days");
    _priority = PriorityClass.valueOf(priority);
    _defaultNumberOfDays = defaultNumberOfDays;
  }

  @Override
  public boolean canApplyTo(final FunctionCompilationContext context, final ComputationTarget target) {
    if (target.getType() != ComputationTargetType.SECURITY) {
      return false;
    }
    if (!(target.getSecurity() instanceof FinancialSecurity)) {
      return false;
    }
    final FinancialSecurity security = (FinancialSecurity) target.getSecurity();
    final boolean isFXOption = (security instanceof FXOptionSecurity
        || target.getSecurity() instanceof FXBarrierOptionSecurity
        || target.getSecurity() instanceof FXDigitalOptionSecurity
        || target.getSecurity() instanceof NonDeliverableFXOptionSecurity
        || target.getSecurity() instanceof NonDeliverableFXDigitalOptionSecurity);
    return isFXOption;
  }

  @Override
  protected void getDefaults(final PropertyDefaults defaults) {
    defaults.addValuePropertyName(ValueRequirementNames.VALUE_THETA, ThetaPropertyNamesAndValues.PROPERTY_DAYS_TO_MOVE_FORWARD);
  }

  @Override
  protected Set<String> getDefaultValue(final FunctionCompilationContext context, final ComputationTarget target, final ValueRequirement desiredValue, final String propertyName) {
    if (ThetaPropertyNamesAndValues.PROPERTY_DAYS_TO_MOVE_FORWARD.equals(propertyName)) {
      return Collections.singleton(_defaultNumberOfDays);
    }
    return null;
  }

  @Override
  public PriorityClass getPriority() {
    return _priority;
  }

  @Override
  public String getMutualExclusionGroup() {
    return OpenGammaFunctionExclusions.FX_OPTION_THETA_DEFAULTS;
  }
}

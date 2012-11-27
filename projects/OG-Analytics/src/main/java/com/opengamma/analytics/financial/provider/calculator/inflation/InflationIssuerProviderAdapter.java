/**
 * Copyright (C) 2012 - present by OpenGamma Inc. and the OpenGamma group of companies
 * 
 * Please see distribution for license.
 */
package com.opengamma.analytics.financial.provider.calculator.inflation;

import com.opengamma.analytics.financial.interestrate.InstrumentDerivative;
import com.opengamma.analytics.financial.interestrate.InstrumentDerivativeVisitor;
import com.opengamma.analytics.financial.interestrate.InstrumentDerivativeVisitorSameMethodAdapter;
import com.opengamma.analytics.financial.provider.description.InflationIssuerProviderInterface;
import com.opengamma.analytics.financial.provider.description.InflationProviderInterface;

/**
 * 
 */
public class InflationIssuerProviderAdapter<RESULT_TYPE> extends InstrumentDerivativeVisitorSameMethodAdapter<InflationIssuerProviderInterface, RESULT_TYPE> {
  private final InstrumentDerivativeVisitor<InflationProviderInterface, RESULT_TYPE> _visitor;

  public InflationIssuerProviderAdapter(final InstrumentDerivativeVisitor<InflationProviderInterface, RESULT_TYPE> visitor) {
    _visitor = visitor;
  }

  @Override
  public RESULT_TYPE visit(final InstrumentDerivative derivative) {
    return derivative.accept(_visitor);
  }

  @Override
  public RESULT_TYPE visit(final InstrumentDerivative derivative, final InflationIssuerProviderInterface data) {
    return derivative.accept(_visitor, data.getInflationProvider());
  }
}
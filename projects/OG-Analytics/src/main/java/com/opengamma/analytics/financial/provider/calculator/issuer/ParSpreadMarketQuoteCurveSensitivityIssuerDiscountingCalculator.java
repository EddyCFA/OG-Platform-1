/**
 * Copyright (C) 2011 - present by OpenGamma Inc. and the OpenGamma group of companies
 * 
 * Please see distribution for license.
 */
package com.opengamma.analytics.financial.provider.calculator.issuer;

import com.opengamma.analytics.financial.interestrate.AbstractInstrumentDerivativeVisitor;
import com.opengamma.analytics.financial.interestrate.InstrumentDerivative;
import com.opengamma.analytics.financial.interestrate.bond.definition.BillTransaction;
import com.opengamma.analytics.financial.interestrate.bond.provider.BillTransactionDiscountingMethod;
import com.opengamma.analytics.financial.interestrate.cash.derivative.DepositCounterpart;
import com.opengamma.analytics.financial.interestrate.cash.provider.DepositCounterpartDiscountingMethod;
import com.opengamma.analytics.financial.provider.calculator.discounting.ParSpreadMarketQuoteCurveSensitivityDiscountingCalculator;
import com.opengamma.analytics.financial.provider.description.IssuerProviderInterface;
import com.opengamma.analytics.financial.provider.sensitivity.multicurve.MulticurveSensitivity;

/**
 * Calculates the present value of an ...
 */
public final class ParSpreadMarketQuoteCurveSensitivityIssuerDiscountingCalculator extends AbstractInstrumentDerivativeVisitor<IssuerProviderInterface, MulticurveSensitivity> {

  /**
   * The unique instance of the calculator.
   */
  private static final ParSpreadMarketQuoteCurveSensitivityIssuerDiscountingCalculator INSTANCE = new ParSpreadMarketQuoteCurveSensitivityIssuerDiscountingCalculator();

  /**
   * Gets the calculator instance.
   * @return The calculator.
   */
  public static ParSpreadMarketQuoteCurveSensitivityIssuerDiscountingCalculator getInstance() {
    return INSTANCE;
  }

  /**
   * Constructor.
   */
  private ParSpreadMarketQuoteCurveSensitivityIssuerDiscountingCalculator() {
  }

  /**
   * Pricing methods.
   */
  private static final DepositCounterpartDiscountingMethod METHOD_DEPO_CTPY = DepositCounterpartDiscountingMethod.getInstance();
  private static final BillTransactionDiscountingMethod METHOD_BILL_TR = BillTransactionDiscountingMethod.getInstance();
  /**
   * Composite calculator.
   */
  private static final ParSpreadMarketQuoteCurveSensitivityDiscountingCalculator PVDC = ParSpreadMarketQuoteCurveSensitivityDiscountingCalculator.getInstance();

  @Override
  public MulticurveSensitivity visit(final InstrumentDerivative derivative, final IssuerProviderInterface issuercurves) {
    try {
      return derivative.accept(this, issuercurves);
    } catch (Exception e) {
      return derivative.accept(PVDC, issuercurves.getMulticurveProvider());
    }
  }

  //     -----     Deposit     -----

  @Override
  public MulticurveSensitivity visitDepositCounterpart(final DepositCounterpart deposit, final IssuerProviderInterface issuercurves) {
    return METHOD_DEPO_CTPY.parSpreadCurveSensitivity(deposit, issuercurves);
  }

  //     -----     Bond/Bill     -----

  @Override
  public MulticurveSensitivity visitBillTransaction(final BillTransaction bill, final IssuerProviderInterface issuercurves) {
    return METHOD_BILL_TR.parSpreadCurveSensitivity(bill, issuercurves);
  }

}

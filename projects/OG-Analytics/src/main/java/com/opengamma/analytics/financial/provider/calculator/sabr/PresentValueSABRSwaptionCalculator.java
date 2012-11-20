/**
 * Copyright (C) 2011 - present by OpenGamma Inc. and the OpenGamma group of companies
 * 
 * Please see distribution for license.
 */
package com.opengamma.analytics.financial.provider.calculator.sabr;

import com.opengamma.analytics.financial.interestrate.AbstractInstrumentDerivativeVisitor;
import com.opengamma.analytics.financial.interestrate.InstrumentDerivative;
import com.opengamma.analytics.financial.interestrate.annuity.derivative.Annuity;
import com.opengamma.analytics.financial.interestrate.payments.derivative.CapFloorCMS;
import com.opengamma.analytics.financial.interestrate.payments.derivative.CouponCMS;
import com.opengamma.analytics.financial.interestrate.payments.derivative.Payment;
import com.opengamma.analytics.financial.interestrate.payments.provider.CapFloorCMSSABRReplicationMethod;
import com.opengamma.analytics.financial.interestrate.payments.provider.CouponCMSSABRReplicationMethod;
import com.opengamma.analytics.financial.interestrate.swaption.derivative.SwaptionPhysicalFixedIbor;
import com.opengamma.analytics.financial.interestrate.swaption.provider.SwaptionPhysicalFixedIborSABRMethod;
import com.opengamma.analytics.financial.provider.description.SABRSwaptionProviderInterface;
import com.opengamma.util.ArgumentChecker;
import com.opengamma.util.money.MultipleCurrencyAmount;

/**
 * Calculates the present value of an inflation instruments by discounting for a given MarketBundle
 */
public final class PresentValueSABRSwaptionCalculator extends AbstractInstrumentDerivativeVisitor<SABRSwaptionProviderInterface, MultipleCurrencyAmount> {

  /**
   * The unique instance of the calculator.
   */
  private static final PresentValueSABRSwaptionCalculator INSTANCE = new PresentValueSABRSwaptionCalculator();

  /**
   * Gets the calculator instance.
   * @return The calculator.
   */
  public static PresentValueSABRSwaptionCalculator getInstance() {
    return INSTANCE;
  }

  /**
   * Constructor.
   */
  private PresentValueSABRSwaptionCalculator() {
  }

  /**
   * Pricing methods.
   */
  private static final SwaptionPhysicalFixedIborSABRMethod METHOD_SWT_PHYS = SwaptionPhysicalFixedIborSABRMethod.getInstance();
  private static final CapFloorCMSSABRReplicationMethod METHOD_CMS_CAP = CapFloorCMSSABRReplicationMethod.getDefaultInstance();
  private static final CouponCMSSABRReplicationMethod METHOD_CMS_CPN = CouponCMSSABRReplicationMethod.getInstance();

  @Override
  public MultipleCurrencyAmount visit(final InstrumentDerivative derivative, final SABRSwaptionProviderInterface sabr) {
    return derivative.accept(this, sabr);
  }

  // -----     Payment/Coupon     ------

  @Override
  public MultipleCurrencyAmount visitSwaptionPhysicalFixedIbor(final SwaptionPhysicalFixedIbor swaption, final SABRSwaptionProviderInterface sabr) {
    return METHOD_SWT_PHYS.presentValue(swaption, sabr);
  }

  @Override
  public MultipleCurrencyAmount visitCouponCMS(final CouponCMS payment, final SABRSwaptionProviderInterface sabr) {
    return METHOD_CMS_CPN.presentValue(payment, sabr);
  }

  @Override
  public MultipleCurrencyAmount visitCapFloorCMS(final CapFloorCMS payment, final SABRSwaptionProviderInterface sabr) {
    return METHOD_CMS_CAP.presentValue(payment, sabr);
  }

  // -----     Annuity     ------

  @Override
  public MultipleCurrencyAmount visitGenericAnnuity(final Annuity<? extends Payment> annuity, final SABRSwaptionProviderInterface sabr) {
    ArgumentChecker.notNull(annuity, "Annuity");
    MultipleCurrencyAmount pv = visit(annuity.getNthPayment(0), sabr);
    for (int loopp = 1; loopp < annuity.getNumberOfPayments(); loopp++) {
      pv = pv.plus(visit(annuity.getNthPayment(loopp), sabr));
    }
    return pv;
  }

}

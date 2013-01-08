/**
 * Copyright (C) 2012 - present by OpenGamma Inc. and the OpenGamma group of companies
 * 
 * Please see distribution for license.
 */
package com.opengamma.analytics.financial.provider.calculator.discounting;

import com.opengamma.analytics.financial.interestrate.InstrumentDerivativeVisitorAdapter;
import com.opengamma.analytics.financial.interestrate.annuity.derivative.Annuity;
import com.opengamma.analytics.financial.interestrate.annuity.derivative.AnnuityCouponFixed;
import com.opengamma.analytics.financial.interestrate.payments.derivative.Coupon;
import com.opengamma.analytics.financial.interestrate.payments.derivative.CouponFixed;
import com.opengamma.analytics.financial.interestrate.payments.derivative.CouponIbor;
import com.opengamma.analytics.financial.interestrate.payments.derivative.CouponIborCompounded;
import com.opengamma.analytics.financial.interestrate.payments.derivative.CouponIborSpread;
import com.opengamma.analytics.financial.interestrate.payments.derivative.Payment;
import com.opengamma.analytics.financial.interestrate.payments.derivative.PaymentFixed;
import com.opengamma.analytics.financial.interestrate.swap.derivative.Swap;
import com.opengamma.analytics.financial.interestrate.swap.derivative.SwapFixedCoupon;
import com.opengamma.analytics.financial.provider.description.interestrate.MulticurveProviderInterface;
import com.opengamma.util.ArgumentChecker;

/**
 * Computes the present value change when the market quote changes by 1 (it is not rescaled to 1 basis point).
 * The meaning of "market quote" will change for each instrument.
 * For PaymentFixed, it is 0 (there is no rate).
 * For coupons, it is the discounted notional times the accrual factor.
 * For annuities, it is the sum of sensitivities of all payments.
 * For swaps it is the pvbp of the first leg.
 * @author marc
 */
public final class PresentValueMarketQuoteSensitivityDiscountingCalculator extends InstrumentDerivativeVisitorAdapter<MulticurveProviderInterface, Double> {

  /**
   * The unique instance of the calculator.
   */
  private static final PresentValueMarketQuoteSensitivityDiscountingCalculator INSTANCE = new PresentValueMarketQuoteSensitivityDiscountingCalculator();

  /**
   * Gets the calculator instance.
   * @return The calculator.
   */
  public static PresentValueMarketQuoteSensitivityDiscountingCalculator getInstance() {
    return INSTANCE;
  }

  /**
   * Constructor.
   */
  private PresentValueMarketQuoteSensitivityDiscountingCalculator() {
  }

  // -----     Payment/Coupon     ------

  @Override
  public Double visitFixedPayment(final PaymentFixed payment, final MulticurveProviderInterface multicurve) {
    return 0.0;
  }

  public Double visitCoupon(final Coupon coupon, final MulticurveProviderInterface multicurve) {
    ArgumentChecker.notNull(multicurve, "multicurve");
    ArgumentChecker.notNull(coupon, "Coupon");
    return multicurve.getDiscountFactor(coupon.getCurrency(), coupon.getPaymentTime()) * coupon.getPaymentYearFraction() * coupon.getNotional();
  }

  @Override
  public Double visitCouponFixed(final CouponFixed coupon, final MulticurveProviderInterface multicurve) {
    return visitCoupon(coupon, multicurve);
  }

  @Override
  public Double visitCouponIbor(final CouponIbor coupon, final MulticurveProviderInterface multicurve) {
    return visitCoupon(coupon, multicurve);
  }

  @Override
  public Double visitCouponIborSpread(final CouponIborSpread coupon, final MulticurveProviderInterface multicurve) {
    return visitCoupon(coupon, multicurve);
  }

  @Override
  public Double visitCouponIborCompounded(final CouponIborCompounded coupon, final MulticurveProviderInterface multicurve) {
    return visitCoupon(coupon, multicurve);
  }

  // -----     Annuity     ------

  @Override
  public Double visitGenericAnnuity(final Annuity<? extends Payment> annuity, final MulticurveProviderInterface multicurve) {
    ArgumentChecker.notNull(multicurve, "Market");
    ArgumentChecker.notNull(annuity, "Annuity");
    double pvbp = 0;
    for (final Payment p : annuity.getPayments()) {
      pvbp += p.accept(this, multicurve);
    }
    return pvbp;
  }

  @Override
  public Double visitFixedCouponAnnuity(final AnnuityCouponFixed annuity, final MulticurveProviderInterface multicurve) {
    return visitGenericAnnuity(annuity, multicurve);
  }

  // -----     Swap     ------

  @Override
  public Double visitSwap(final Swap<?, ?> swap, final MulticurveProviderInterface multicurve) {
    ArgumentChecker.notNull(multicurve, "Market");
    ArgumentChecker.notNull(swap, "Swap");
    return swap.getFirstLeg().accept(this, multicurve);
  }

  @Override
  public Double visitFixedCouponSwap(final SwapFixedCoupon<?> swap, final MulticurveProviderInterface multicurve) {
    return visitSwap(swap, multicurve);
  }

}
/**
 * Copyright (C) 2012 - present by OpenGamma Inc. and the OpenGamma group of companies
 * 
 * Please see distribution for license.
 */
package com.opengamma.analytics.financial.instrument.future;

import javax.time.calendar.Period;
import javax.time.calendar.ZonedDateTime;

import org.apache.commons.lang.ObjectUtils;

import com.opengamma.analytics.financial.instrument.InstrumentDefinition;
import com.opengamma.analytics.financial.instrument.InstrumentDefinitionVisitor;
import com.opengamma.analytics.financial.instrument.index.GeneratorSwapFixedIbor;
import com.opengamma.analytics.financial.instrument.swap.SwapFixedIborDefinition;
import com.opengamma.analytics.financial.interestrate.future.derivative.DeliverableSwapFuturesSecurity;
import com.opengamma.analytics.financial.interestrate.payments.derivative.Coupon;
import com.opengamma.analytics.financial.interestrate.swap.derivative.SwapFixedCoupon;
import com.opengamma.analytics.financial.schedule.ScheduleCalculator;
import com.opengamma.analytics.util.time.TimeCalculator;
import com.opengamma.util.ArgumentChecker;

/**
 * Description of Deliverable Interest Rate Swap Futures as traded on CME.
 */
public class DeliverableSwapFuturesSecurityDefinition implements InstrumentDefinition<DeliverableSwapFuturesSecurity> {

  /**
   * The futures last trading date. The date for which the delivery date is the spot date.
   */
  private final ZonedDateTime _lastTradingDate;
  /**
   * The delivery date. Usually the third Wednesday of the month is the spot date.
   */
  private final ZonedDateTime _deliveryDate;
  /**
   * The futures underlying swap. The delivery date should be the first accrual date of the underlying swap. The swap should be a receiver swap of notional 1.
   */
  private final SwapFixedIborDefinition _underlyingSwap;
  /**
   * The notional of the future (also called face value or contract value).
   */
  private final double _notional;

  /**
   * Constructor. The delivery date is the accrual start date of the first coupon of the swap fixed leg.
   * @param lastTradingDate The futures last trading date.
   * @param underlyingSwap The futures underlying swap.
   * @param notional The notional of the future.
   */
  public DeliverableSwapFuturesSecurityDefinition(final ZonedDateTime lastTradingDate, final SwapFixedIborDefinition underlyingSwap, final double notional) {
    ArgumentChecker.notNull(lastTradingDate, "Last trading date");
    ArgumentChecker.notNull(underlyingSwap, "Swap");
    ArgumentChecker.isTrue(Math.abs(underlyingSwap.getFixedLeg().getNthPayment(0).getNotional() - 1.0) < 1.0E-10, "Swap should be receiver of notional 1");
    _lastTradingDate = lastTradingDate;
    _deliveryDate = underlyingSwap.getFixedLeg().getNthPayment(0).getAccrualStartDate();
    _underlyingSwap = underlyingSwap;
    _notional = notional;
  }

  /**
   * Builder from the financial details.
   * @param effectiveDate The underlying swap effective date (delivery date).
   * @param generator The swap generator.
   * @param tenor The underlying swap tenor.
   * @param notional The futures notional.
   * @param rate The underlying swap rate.
   * @return The futures.
   */
  public static DeliverableSwapFuturesSecurityDefinition from(final ZonedDateTime effectiveDate, final GeneratorSwapFixedIbor generator, final Period tenor, final double notional, final double rate) {
    ArgumentChecker.notNull(effectiveDate, "Effective date");
    ArgumentChecker.notNull(generator, "Generator");
    final ZonedDateTime lastTradingDate = ScheduleCalculator.getAdjustedDate(effectiveDate, -generator.getSpotLag(), generator.getCalendar());
    final SwapFixedIborDefinition swap = SwapFixedIborDefinition.from(effectiveDate, tenor, generator, 1.0, rate, false);
    return new DeliverableSwapFuturesSecurityDefinition(lastTradingDate, swap, notional);
  }

  /**
   * Returns the futures last trading date.
   * @return The date.
   */
  public ZonedDateTime getLastTradingDate() {
    return _lastTradingDate;
  }

  /**
   * Returns the delivery date.
   * @return The date.
   */
  public ZonedDateTime getDeliveryDate() {
    return _deliveryDate;
  }

  /**
   * Returns the futures underlying swap.
   * @return The swap.
   */
  public SwapFixedIborDefinition getUnderlyingSwap() {
    return _underlyingSwap;
  }

  /**
   * Returns the notional of the future
   * @return The notional.
   */
  public double getNotional() {
    return _notional;
  }

  @Override
  public DeliverableSwapFuturesSecurity toDerivative(final ZonedDateTime date, final String... yieldCurveNames) {
    final double lastTradingTime = TimeCalculator.getTimeBetween(date, _lastTradingDate);
    final double deliveryTime = TimeCalculator.getTimeBetween(date, _deliveryDate);
    final SwapFixedCoupon<? extends Coupon> underlyingSwap = _underlyingSwap.toDerivative(date, yieldCurveNames);
    return new DeliverableSwapFuturesSecurity(lastTradingTime, deliveryTime, underlyingSwap, _notional);
  }

  @Override
  public <U, V> V accept(final InstrumentDefinitionVisitor<U, V> visitor, final U data) {
    return visitor.visitDeliverableSwapFuturesSecurityDefinition(this, data);
  }

  @Override
  public <V> V accept(final InstrumentDefinitionVisitor<?, V> visitor) {
    return visitor.visitDeliverableSwapFuturesSecurityDefinition(this);
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + _deliveryDate.hashCode();
    result = prime * result + _lastTradingDate.hashCode();
    long temp;
    temp = Double.doubleToLongBits(_notional);
    result = prime * result + (int) (temp ^ (temp >>> 32));
    result = prime * result + _underlyingSwap.hashCode();
    return result;
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final DeliverableSwapFuturesSecurityDefinition other = (DeliverableSwapFuturesSecurityDefinition) obj;
    if (!ObjectUtils.equals(_deliveryDate, other._deliveryDate)) {
      return false;
    }
    if (!ObjectUtils.equals(_lastTradingDate, other._lastTradingDate)) {
      return false;
    }
    if (Double.doubleToLongBits(_notional) != Double.doubleToLongBits(other._notional)) {
      return false;
    }
    if (!ObjectUtils.equals(_underlyingSwap, other._underlyingSwap)) {
      return false;
    }
    return true;
  }

}

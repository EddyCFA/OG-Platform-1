/**
 * Copyright (C) 2012 - present by OpenGamma Inc. and the OpenGamma group of companies
 * 
 * Please see distribution for license.
 */
package com.opengamma.analytics.financial.credit.creditdefaultswap;

import javax.time.calendar.ZonedDateTime;

import org.testng.annotations.Test;

import com.opengamma.analytics.financial.credit.BuySellProtection;
import com.opengamma.analytics.financial.credit.DebtSeniority;
import com.opengamma.analytics.financial.credit.PriceType;
import com.opengamma.analytics.financial.credit.RestructuringClause;
import com.opengamma.analytics.financial.credit.StubType;
import com.opengamma.analytics.financial.credit.creditdefaultswap.definition.StandardCreditDefaultSwapDefinition;
import com.opengamma.analytics.financial.credit.obligormodel.CreditRating;
import com.opengamma.analytics.financial.credit.obligormodel.CreditRatingFitch;
import com.opengamma.analytics.financial.credit.obligormodel.CreditRatingMoodys;
import com.opengamma.analytics.financial.credit.obligormodel.CreditRatingStandardAndPoors;
import com.opengamma.analytics.financial.credit.obligormodel.Region;
import com.opengamma.analytics.financial.credit.obligormodel.Sector;
import com.opengamma.analytics.financial.credit.obligormodel.definition.Obligor;
import com.opengamma.financial.convention.businessday.BusinessDayConvention;
import com.opengamma.financial.convention.businessday.BusinessDayConventionFactory;
import com.opengamma.financial.convention.calendar.Calendar;
import com.opengamma.financial.convention.calendar.MondayToFridayCalendar;
import com.opengamma.financial.convention.daycount.DayCount;
import com.opengamma.financial.convention.daycount.DayCountFactory;
import com.opengamma.financial.convention.frequency.PeriodFrequency;
import com.opengamma.util.money.Currency;
import com.opengamma.util.time.DateUtils;

/**
 * Tests to verify the correct construction of a standard CDS contract
 */
public class StandardCreditDefaultSwapDefinitionTest {

  // --------------------------------------------------------------------------------------------------------------------------------------------------

  // TODO : Make sure that all the input arguments have been error checked 

  // --------------------------------------------------------------------------------------------------------------------------------------------------

  // CDS contract parameters (don't need the curves in this case as we are just testing the validity of the input parameters)

  private static final BuySellProtection buySellProtection = BuySellProtection.BUY;

  private static final String protectionBuyerTicker = "MSFT";
  private static final String protectionBuyerShortName = "Microsoft";
  private static final String protectionBuyerREDCode = "ABC123";

  private static final String protectionSellerTicker = "IBM";
  private static final String protectionSellerShortName = "International Business Machines";
  private static final String protectionSellerREDCode = "XYZ321";

  private static final String referenceEntityTicker = "BT";
  private static final String referenceEntityShortName = "British telecom";
  private static final String referenceEntityREDCode = "123ABC";

  private static final CreditRating protectionBuyerCompositeRating = CreditRating.AA;
  private static final CreditRating protectionBuyerImpliedRating = CreditRating.A;

  private static final CreditRating protectionSellerCompositeRating = CreditRating.AA;
  private static final CreditRating protectionSellerImpliedRating = CreditRating.A;

  private static final CreditRating referenceEntityCompositeRating = CreditRating.AA;
  private static final CreditRating referenceEntityImpliedRating = CreditRating.A;

  private static final CreditRatingMoodys protectionBuyerCreditRatingMoodys = CreditRatingMoodys.AA;
  private static final CreditRatingStandardAndPoors protectionBuyerCreditRatingStandardAndPoors = CreditRatingStandardAndPoors.A;
  private static final CreditRatingFitch protectionBuyerCreditRatingFitch = CreditRatingFitch.AA;

  private static final boolean protectionBuyerHasDefaulted = false;

  private static final CreditRatingMoodys protectionSellerCreditRatingMoodys = CreditRatingMoodys.AA;
  private static final CreditRatingStandardAndPoors protectionSellerCreditRatingStandardAndPoors = CreditRatingStandardAndPoors.A;
  private static final CreditRatingFitch protectionSellerCreditRatingFitch = CreditRatingFitch.AA;

  private static final boolean protectionSellerHasDefaulted = false;

  private static final CreditRatingMoodys referenceEntityCreditRatingMoodys = CreditRatingMoodys.AA;
  private static final CreditRatingStandardAndPoors referenceEntityCreditRatingStandardAndPoors = CreditRatingStandardAndPoors.A;
  private static final CreditRatingFitch referenceEntityCreditRatingFitch = CreditRatingFitch.AA;

  private static final boolean referenceEntityHasDefaulted = false;

  private static final Sector protectionBuyerSector = Sector.INDUSTRIALS;
  private static final Region protectionBuyerRegion = Region.NORTHAMERICA;
  private static final String protectionBuyerCountry = "United States";

  private static final Sector protectionSellerSector = Sector.INDUSTRIALS;
  private static final Region protectionSellerRegion = Region.NORTHAMERICA;
  private static final String protectionSellerCountry = "United States";

  private static final Sector referenceEntitySector = Sector.INDUSTRIALS;
  private static final Region referenceEntityRegion = Region.EUROPE;
  private static final String referenceEntityCountry = "United Kingdom";

  private static final Currency currency = Currency.USD;

  private static final DebtSeniority debtSeniority = DebtSeniority.SENIOR;
  private static final RestructuringClause restructuringClause = RestructuringClause.NORE;

  private static final Calendar calendar = new MondayToFridayCalendar("TestCalendar");

  private static final ZonedDateTime startDate = DateUtils.getUTCDate(2007, 10, 22);
  private static final ZonedDateTime effectiveDate = DateUtils.getUTCDate(2007, 10, 23);
  private static final ZonedDateTime maturityDate = DateUtils.getUTCDate(2012, 12, 20);
  private static final ZonedDateTime valuationDate = DateUtils.getUTCDate(2007, 10, 23);

  private static final StubType stubType = StubType.FRONTSHORT;
  private static final PeriodFrequency couponFrequency = PeriodFrequency.QUARTERLY;
  private static final DayCount daycountFractionConvention = DayCountFactory.INSTANCE.getDayCount("ACT/360");
  private static final BusinessDayConvention businessdayAdjustmentConvention = BusinessDayConventionFactory.INSTANCE.getBusinessDayConvention("Following");

  private static final boolean immAdjustMaturityDate = true;
  private static final boolean adjustEffectiveDate = true;
  private static final boolean adjustMaturityDate = true;

  private static final double notional = 10000000.0;
  private static final double recoveryRate = 0.40;
  private static final boolean includeAccruedPremium = false;
  private static final PriceType priceType = PriceType.DIRTY;
  private static final boolean protectionStart = true;

  private static final double quotedSpread = 100.0;
  private static final StandardCDSCoupon premiumLegCoupon = StandardCDSCoupon._500bps;
  private static final double upfrontAmount = 0.5;
  private static final int cashSettlementDate = 3;
  // --------------------------------------------------------------------------------------------------------------------------------------------------

  // Construct the obligors in the contract

  private static final Obligor protectionBuyer = new Obligor(
      protectionBuyerTicker,
      protectionBuyerShortName,
      protectionBuyerREDCode,
      protectionBuyerCompositeRating,
      protectionBuyerImpliedRating,
      protectionBuyerCreditRatingMoodys,
      protectionBuyerCreditRatingStandardAndPoors,
      protectionBuyerCreditRatingFitch,
      protectionBuyerHasDefaulted,
      protectionBuyerSector,
      protectionBuyerRegion,
      protectionBuyerCountry);

  private static final Obligor protectionSeller = new Obligor(
      protectionSellerTicker,
      protectionSellerShortName,
      protectionSellerREDCode,
      protectionSellerCompositeRating,
      protectionSellerImpliedRating,
      protectionSellerCreditRatingMoodys,
      protectionSellerCreditRatingStandardAndPoors,
      protectionSellerCreditRatingFitch,
      protectionSellerHasDefaulted,
      protectionSellerSector,
      protectionSellerRegion,
      protectionSellerCountry);

  private static final Obligor referenceEntity = new Obligor(
      referenceEntityTicker,
      referenceEntityShortName,
      referenceEntityREDCode,
      referenceEntityCompositeRating,
      referenceEntityImpliedRating,
      referenceEntityCreditRatingMoodys,
      referenceEntityCreditRatingStandardAndPoors,
      referenceEntityCreditRatingFitch,
      referenceEntityHasDefaulted,
      referenceEntitySector,
      referenceEntityRegion,
      referenceEntityCountry);

  // --------------------------------------------------------------------------------------------------------------------------------------------------

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void testNullBuySellProtectionField() {

    new StandardCreditDefaultSwapDefinition(null, protectionBuyer, protectionSeller, referenceEntity, currency, debtSeniority, restructuringClause, calendar, startDate, effectiveDate,
        maturityDate, valuationDate, stubType, couponFrequency, daycountFractionConvention, businessdayAdjustmentConvention, immAdjustMaturityDate, adjustEffectiveDate,
        adjustMaturityDate, notional, recoveryRate, includeAccruedPremium, priceType, protectionStart, quotedSpread, premiumLegCoupon, upfrontAmount, cashSettlementDate);

  }

  // --------------------------------------------------------------------------------------------------------------------------------------------------

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void testNullProtectionBuyerField() {

    new StandardCreditDefaultSwapDefinition(buySellProtection, null, protectionSeller, referenceEntity, currency, debtSeniority, restructuringClause, calendar, startDate, effectiveDate,
        maturityDate, valuationDate, stubType, couponFrequency, daycountFractionConvention, businessdayAdjustmentConvention, immAdjustMaturityDate, adjustEffectiveDate,
        adjustMaturityDate, notional, recoveryRate, includeAccruedPremium, priceType, protectionStart, quotedSpread, premiumLegCoupon, upfrontAmount, cashSettlementDate);
  }

  // --------------------------------------------------------------------------------------------------------------------------------------------------

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void testNullProtectionSellerField() {

    new StandardCreditDefaultSwapDefinition(buySellProtection, protectionBuyer, null, referenceEntity, currency, debtSeniority, restructuringClause, calendar, startDate, effectiveDate,
        maturityDate, valuationDate, stubType, couponFrequency, daycountFractionConvention, businessdayAdjustmentConvention, immAdjustMaturityDate, adjustEffectiveDate,
        adjustMaturityDate, notional, recoveryRate, includeAccruedPremium, priceType, protectionStart, quotedSpread, premiumLegCoupon, upfrontAmount, cashSettlementDate);
  }

  // --------------------------------------------------------------------------------------------------------------------------------------------------

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void testNullReferenceEntityField() {

    new StandardCreditDefaultSwapDefinition(buySellProtection, protectionBuyer, protectionSeller, null, currency, debtSeniority, restructuringClause, calendar, startDate, effectiveDate,
        maturityDate, valuationDate, stubType, couponFrequency, daycountFractionConvention, businessdayAdjustmentConvention, immAdjustMaturityDate, adjustEffectiveDate,
        adjustMaturityDate, notional, recoveryRate, includeAccruedPremium, priceType, protectionStart, quotedSpread, premiumLegCoupon, upfrontAmount, cashSettlementDate);
  }

  // --------------------------------------------------------------------------------------------------------------------------------------------------

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void testNullCurrencyField() {

    new StandardCreditDefaultSwapDefinition(buySellProtection, protectionBuyer, protectionSeller, referenceEntity, null, debtSeniority, restructuringClause, calendar, startDate, effectiveDate,
        maturityDate, valuationDate, stubType, couponFrequency, daycountFractionConvention, businessdayAdjustmentConvention, immAdjustMaturityDate, adjustEffectiveDate,
        adjustMaturityDate, notional, recoveryRate, includeAccruedPremium, priceType, protectionStart, quotedSpread, premiumLegCoupon, upfrontAmount, cashSettlementDate);
  }

  // --------------------------------------------------------------------------------------------------------------------------------------------------

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void testNullDebtSeniorityField() {

    new StandardCreditDefaultSwapDefinition(buySellProtection, protectionBuyer, protectionSeller, referenceEntity, currency, null, restructuringClause, calendar, startDate, effectiveDate,
        maturityDate, valuationDate, stubType, couponFrequency, daycountFractionConvention, businessdayAdjustmentConvention, immAdjustMaturityDate, adjustEffectiveDate,
        adjustMaturityDate, notional, recoveryRate, includeAccruedPremium, priceType, protectionStart, quotedSpread, premiumLegCoupon, upfrontAmount, cashSettlementDate);
  }

  // --------------------------------------------------------------------------------------------------------------------------------------------------

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void testNullRestructuringClauseField() {

    new StandardCreditDefaultSwapDefinition(buySellProtection, protectionBuyer, protectionSeller, referenceEntity, currency, debtSeniority, null, calendar, startDate, effectiveDate,
        maturityDate, valuationDate, stubType, couponFrequency, daycountFractionConvention, businessdayAdjustmentConvention, immAdjustMaturityDate, adjustEffectiveDate,
        adjustMaturityDate, notional, recoveryRate, includeAccruedPremium, priceType, protectionStart, quotedSpread, premiumLegCoupon, upfrontAmount, cashSettlementDate);
  }

  // --------------------------------------------------------------------------------------------------------------------------------------------------

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void testNullCalendarField() {

    new StandardCreditDefaultSwapDefinition(buySellProtection, protectionBuyer, protectionSeller, referenceEntity, currency, debtSeniority, restructuringClause, null, startDate, effectiveDate,
        maturityDate, valuationDate, stubType, couponFrequency, daycountFractionConvention, businessdayAdjustmentConvention, immAdjustMaturityDate, adjustEffectiveDate,
        adjustMaturityDate, notional, recoveryRate, includeAccruedPremium, priceType, protectionStart, quotedSpread, premiumLegCoupon, upfrontAmount, cashSettlementDate);
  }

  // --------------------------------------------------------------------------------------------------------------------------------------------------

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void testNullStartDateField() {

    new StandardCreditDefaultSwapDefinition(buySellProtection, protectionBuyer, protectionSeller, referenceEntity, currency, debtSeniority, restructuringClause, calendar, null, effectiveDate,
        maturityDate, valuationDate, stubType, couponFrequency, daycountFractionConvention, businessdayAdjustmentConvention, immAdjustMaturityDate, adjustEffectiveDate,
        adjustMaturityDate, notional, recoveryRate, includeAccruedPremium, priceType, protectionStart, quotedSpread, premiumLegCoupon, upfrontAmount, cashSettlementDate);
  }

  // --------------------------------------------------------------------------------------------------------------------------------------------------

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void testNullEffectiveDateField() {

    new StandardCreditDefaultSwapDefinition(buySellProtection, protectionBuyer, protectionSeller, referenceEntity, currency, debtSeniority, restructuringClause, calendar, startDate, null,
        maturityDate, valuationDate, stubType, couponFrequency, daycountFractionConvention, businessdayAdjustmentConvention, immAdjustMaturityDate, adjustEffectiveDate,
        adjustMaturityDate, notional, recoveryRate, includeAccruedPremium, priceType, protectionStart, quotedSpread, premiumLegCoupon, upfrontAmount, cashSettlementDate);
  }

  // --------------------------------------------------------------------------------------------------------------------------------------------------

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void testNullMaturityDateField() {

    new StandardCreditDefaultSwapDefinition(buySellProtection, protectionBuyer, protectionSeller, referenceEntity, currency, debtSeniority, restructuringClause, calendar, startDate, effectiveDate,
        null, valuationDate, stubType, couponFrequency, daycountFractionConvention, businessdayAdjustmentConvention, immAdjustMaturityDate, adjustEffectiveDate,
        adjustMaturityDate, notional, recoveryRate, includeAccruedPremium, priceType, protectionStart, quotedSpread, premiumLegCoupon, upfrontAmount, cashSettlementDate);
  }

  // --------------------------------------------------------------------------------------------------------------------------------------------------

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void testNullValuationDateField() {

    new StandardCreditDefaultSwapDefinition(buySellProtection, protectionBuyer, protectionSeller, referenceEntity, currency, debtSeniority, restructuringClause, calendar, startDate, effectiveDate,
        maturityDate, null, stubType, couponFrequency, daycountFractionConvention, businessdayAdjustmentConvention, immAdjustMaturityDate, adjustEffectiveDate,
        adjustMaturityDate, notional, recoveryRate, includeAccruedPremium, priceType, protectionStart, quotedSpread, premiumLegCoupon, upfrontAmount, cashSettlementDate);
  }

  // --------------------------------------------------------------------------------------------------------------------------------------------------

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void testNullStubTypeField() {

    new StandardCreditDefaultSwapDefinition(buySellProtection, protectionBuyer, protectionSeller, referenceEntity, currency, debtSeniority, restructuringClause, calendar, startDate, effectiveDate,
        maturityDate, valuationDate, null, couponFrequency, daycountFractionConvention, businessdayAdjustmentConvention, immAdjustMaturityDate, adjustEffectiveDate,
        adjustMaturityDate, notional, recoveryRate, includeAccruedPremium, priceType, protectionStart, quotedSpread, premiumLegCoupon, upfrontAmount, cashSettlementDate);
  }

  // --------------------------------------------------------------------------------------------------------------------------------------------------

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void testNullCouponFrequencyField() {

    new StandardCreditDefaultSwapDefinition(buySellProtection, protectionBuyer, protectionSeller, referenceEntity, currency, debtSeniority, restructuringClause, calendar, startDate, effectiveDate,
        maturityDate, valuationDate, stubType, null, daycountFractionConvention, businessdayAdjustmentConvention, immAdjustMaturityDate, adjustEffectiveDate,
        adjustMaturityDate, notional, recoveryRate, includeAccruedPremium, priceType, protectionStart, quotedSpread, premiumLegCoupon, upfrontAmount, cashSettlementDate);
  }

  // --------------------------------------------------------------------------------------------------------------------------------------------------

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void testNullDayCountFractionConventionField() {

    new StandardCreditDefaultSwapDefinition(buySellProtection, protectionBuyer, protectionSeller, referenceEntity, currency, debtSeniority, restructuringClause, calendar, startDate, effectiveDate,
        maturityDate, valuationDate, stubType, couponFrequency, null, businessdayAdjustmentConvention, immAdjustMaturityDate, adjustEffectiveDate,
        adjustMaturityDate, notional, recoveryRate, includeAccruedPremium, priceType, protectionStart, quotedSpread, premiumLegCoupon, upfrontAmount, cashSettlementDate);
  }

  // --------------------------------------------------------------------------------------------------------------------------------------------------

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void testNullBusinessDayAdjustmentConventionField() {

    new StandardCreditDefaultSwapDefinition(buySellProtection, protectionBuyer, protectionSeller, referenceEntity, currency, debtSeniority, restructuringClause, calendar, startDate, effectiveDate,
        maturityDate, valuationDate, stubType, couponFrequency, daycountFractionConvention, null, immAdjustMaturityDate, adjustEffectiveDate,
        adjustMaturityDate, notional, recoveryRate, includeAccruedPremium, priceType, protectionStart, quotedSpread, premiumLegCoupon, upfrontAmount, cashSettlementDate);
  }

  // --------------------------------------------------------------------------------------------------------------------------------------------------

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void testNotionalIsPositive() {

    new StandardCreditDefaultSwapDefinition(buySellProtection, protectionBuyer, protectionSeller, referenceEntity, currency, debtSeniority, restructuringClause, calendar, startDate, effectiveDate,
        maturityDate, valuationDate, stubType, couponFrequency, daycountFractionConvention, businessdayAdjustmentConvention, immAdjustMaturityDate, adjustEffectiveDate,
        adjustMaturityDate, -notional, recoveryRate, includeAccruedPremium, priceType, protectionStart, quotedSpread, premiumLegCoupon, upfrontAmount, cashSettlementDate);
  }

  // --------------------------------------------------------------------------------------------------------------------------------------------------

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void testRecoveryRateIsPositive() {

    new StandardCreditDefaultSwapDefinition(buySellProtection, protectionBuyer, protectionSeller, referenceEntity, currency, debtSeniority, restructuringClause, calendar, startDate, effectiveDate,
        maturityDate, valuationDate, stubType, couponFrequency, daycountFractionConvention, businessdayAdjustmentConvention, immAdjustMaturityDate, adjustEffectiveDate,
        adjustMaturityDate, notional, -recoveryRate, includeAccruedPremium, priceType, protectionStart, quotedSpread, premiumLegCoupon, upfrontAmount, cashSettlementDate);
  }

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void testRecoveryRateIsLessThanUnity() {

    new StandardCreditDefaultSwapDefinition(buySellProtection, protectionBuyer, protectionSeller, referenceEntity, currency, debtSeniority, restructuringClause, calendar, startDate, effectiveDate,
        maturityDate, valuationDate, stubType, couponFrequency, daycountFractionConvention, businessdayAdjustmentConvention, immAdjustMaturityDate, adjustEffectiveDate,
        adjustMaturityDate, notional, 1.0 + recoveryRate, includeAccruedPremium, priceType, protectionStart, quotedSpread, premiumLegCoupon, upfrontAmount, cashSettlementDate);
  }

  // --------------------------------------------------------------------------------------------------------------------------------------------------

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void testStartDateIsBeforeValuationDate() {

    final ZonedDateTime testValuationDate = startDate.minusDays(1);

    new StandardCreditDefaultSwapDefinition(buySellProtection, protectionBuyer, protectionSeller, referenceEntity, currency, debtSeniority, restructuringClause, calendar, startDate, effectiveDate,
        maturityDate, testValuationDate, stubType, couponFrequency, daycountFractionConvention, businessdayAdjustmentConvention, immAdjustMaturityDate, adjustEffectiveDate,
        adjustMaturityDate, notional, recoveryRate, includeAccruedPremium, priceType, protectionStart, quotedSpread, premiumLegCoupon, upfrontAmount, cashSettlementDate);
  }

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void testStartDateIsBeforeEffectiveDate() {

    final ZonedDateTime testEffectiveDate = startDate.minusDays(1);

    new StandardCreditDefaultSwapDefinition(buySellProtection, protectionBuyer, protectionSeller, referenceEntity, currency, debtSeniority, restructuringClause, calendar, startDate,
        testEffectiveDate,
        maturityDate, valuationDate, stubType, couponFrequency, daycountFractionConvention, businessdayAdjustmentConvention, immAdjustMaturityDate, adjustEffectiveDate,
        adjustMaturityDate, notional, recoveryRate, includeAccruedPremium, priceType, protectionStart, quotedSpread, premiumLegCoupon, upfrontAmount, cashSettlementDate);
  }

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void testStartDateIsBeforeMaturityDate() {

    final ZonedDateTime testMaturityDate = startDate.minusDays(1);

    new StandardCreditDefaultSwapDefinition(buySellProtection, protectionBuyer, protectionSeller, referenceEntity, currency, debtSeniority, restructuringClause, calendar, startDate, effectiveDate,
        testMaturityDate, valuationDate, stubType, couponFrequency, daycountFractionConvention, businessdayAdjustmentConvention, immAdjustMaturityDate, adjustEffectiveDate,
        adjustMaturityDate, notional, recoveryRate, includeAccruedPremium, priceType, protectionStart, quotedSpread, premiumLegCoupon, upfrontAmount, cashSettlementDate);
  }

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void testValuationDateIsBeforeMaturityDate() {

    final ZonedDateTime testValuationDate = maturityDate.plusDays(1);

    new StandardCreditDefaultSwapDefinition(buySellProtection, protectionBuyer, protectionSeller, referenceEntity, currency, debtSeniority, restructuringClause, calendar, startDate, effectiveDate,
        maturityDate, testValuationDate, stubType, couponFrequency, daycountFractionConvention, businessdayAdjustmentConvention, immAdjustMaturityDate, adjustEffectiveDate,
        adjustMaturityDate, notional, recoveryRate, includeAccruedPremium, priceType, protectionStart, quotedSpread, premiumLegCoupon, upfrontAmount, cashSettlementDate);
  }

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void testValuationDateIsAfterEffectiveDate() {

    final ZonedDateTime testValuationDate = effectiveDate.minusDays(1);

    new StandardCreditDefaultSwapDefinition(buySellProtection, protectionBuyer, protectionSeller, referenceEntity, currency, debtSeniority, restructuringClause, calendar, startDate, effectiveDate,
        maturityDate, testValuationDate, stubType, couponFrequency, daycountFractionConvention, businessdayAdjustmentConvention, immAdjustMaturityDate, adjustEffectiveDate,
        adjustMaturityDate, notional, recoveryRate, includeAccruedPremium, priceType, protectionStart, quotedSpread, premiumLegCoupon, upfrontAmount, cashSettlementDate);
  }

  // --------------------------------------------------------------------------------------------------------------------------------------------------

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void testQuotedSpreadIsPositive() {

    new StandardCreditDefaultSwapDefinition(buySellProtection, protectionBuyer, protectionSeller, referenceEntity, currency, debtSeniority, restructuringClause, calendar, startDate, effectiveDate,
        maturityDate, valuationDate, stubType, couponFrequency, daycountFractionConvention, businessdayAdjustmentConvention, immAdjustMaturityDate, adjustEffectiveDate,
        adjustMaturityDate, notional, recoveryRate, includeAccruedPremium, priceType, protectionStart, -quotedSpread, premiumLegCoupon, upfrontAmount, cashSettlementDate);
  }

  // --------------------------------------------------------------------------------------------------------------------------------------------------

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void testNullStandardCDSCouponField() {

    new StandardCreditDefaultSwapDefinition(buySellProtection, protectionBuyer, protectionSeller, referenceEntity, currency, debtSeniority, restructuringClause, calendar, startDate, effectiveDate,
        maturityDate, valuationDate, stubType, couponFrequency, daycountFractionConvention, businessdayAdjustmentConvention, immAdjustMaturityDate, adjustEffectiveDate,
        adjustMaturityDate, notional, recoveryRate, includeAccruedPremium, priceType, protectionStart, quotedSpread, null, upfrontAmount, cashSettlementDate);
  }

  //--------------------------------------------------------------------------------------------------------------------------------------------------

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void testCashSettlementDateIsPositive() {

    new StandardCreditDefaultSwapDefinition(buySellProtection, protectionBuyer, protectionSeller, referenceEntity, currency, debtSeniority, restructuringClause, calendar, startDate, effectiveDate,
        maturityDate, valuationDate, stubType, couponFrequency, daycountFractionConvention, businessdayAdjustmentConvention, immAdjustMaturityDate, adjustEffectiveDate,
        adjustMaturityDate, notional, recoveryRate, includeAccruedPremium, priceType, protectionStart, quotedSpread, premiumLegCoupon, upfrontAmount, -cashSettlementDate);
  }

  // --------------------------------------------------------------------------------------------------------------------------------------------------
}

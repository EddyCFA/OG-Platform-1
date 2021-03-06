/**
 * Copyright (C) 2011 - present by OpenGamma Inc. and the OpenGamma group of companies
 * 
 * Please see distribution for license.
 */
package com.opengamma.financial.convention;

import static com.opengamma.core.id.ExternalSchemes.bloombergTickerSecurityId;
import static com.opengamma.financial.convention.InMemoryConventionBundleMaster.simpleNameSecurityId;

import javax.time.calendar.Period;

import org.apache.commons.lang.Validate;

import com.opengamma.core.id.ExternalSchemes;
import com.opengamma.financial.convention.businessday.BusinessDayConvention;
import com.opengamma.financial.convention.businessday.BusinessDayConventionFactory;
import com.opengamma.financial.convention.daycount.DayCount;
import com.opengamma.financial.convention.daycount.DayCountFactory;
import com.opengamma.financial.convention.frequency.Frequency;
import com.opengamma.financial.convention.frequency.SimpleFrequencyFactory;
import com.opengamma.id.ExternalId;
import com.opengamma.id.ExternalIdBundle;

/**
 * 
 */
public class NZConventions {

  public static synchronized void addFixedIncomeInstrumentConventions(final ConventionBundleMaster conventionMaster) {
    Validate.notNull(conventionMaster, "convention master");
    final BusinessDayConvention modified = BusinessDayConventionFactory.INSTANCE.getBusinessDayConvention("Modified Following");
    final BusinessDayConvention following = BusinessDayConventionFactory.INSTANCE.getBusinessDayConvention("Following");
    final DayCount act365 = DayCountFactory.INSTANCE.getDayCount("Actual/365");
    final Frequency quarterly = SimpleFrequencyFactory.INSTANCE.getFrequency(Frequency.QUARTERLY_NAME);
    final Frequency semiAnnual = SimpleFrequencyFactory.INSTANCE.getFrequency(Frequency.SEMI_ANNUAL_NAME);
    final Frequency annual = SimpleFrequencyFactory.INSTANCE.getFrequency(Frequency.ANNUAL_NAME);
    final ExternalId nz = ExternalSchemes.financialRegionId("NZ");

    final ConventionBundleMasterUtils utils = new ConventionBundleMasterUtils(conventionMaster);

    utils.addConventionBundle(ExternalIdBundle.of(bloombergTickerSecurityId("NZ00O/N Index"), simpleNameSecurityId("NZD LIBOR O/N")), "NZD LIBOR O/N", act365,
        following, Period.ofDays(1), 0, false, nz);
    utils.addConventionBundle(ExternalIdBundle.of(bloombergTickerSecurityId("NZ00S/N Index"), simpleNameSecurityId("NZD LIBOR S/N")), "NZD LIBOR S/N", act365,
        following, Period.ofDays(1), 0, false, nz);
    utils.addConventionBundle(ExternalIdBundle.of(bloombergTickerSecurityId("NZ00T/N Index"), simpleNameSecurityId("NZD LIBOR T/N")), "NZD LIBOR T/N", act365,
        following, Period.ofDays(1), 0, false, nz);
    utils.addConventionBundle(ExternalIdBundle.of(bloombergTickerSecurityId("NZ0001W Index"), simpleNameSecurityId("NZD LIBOR 1w")), "NZD LIBOR 1w", act365,
        following, Period.ofDays(1), 2, false, nz);
    utils.addConventionBundle(ExternalIdBundle.of(bloombergTickerSecurityId("NZ0002W Index"), simpleNameSecurityId("NZD LIBOR 2w")), "NZD LIBOR 2w", act365,
        following, Period.ofDays(1), 2, false, nz);
    utils.addConventionBundle(ExternalIdBundle.of(bloombergTickerSecurityId("NZ0001M Index"), simpleNameSecurityId("NZD LIBOR 1m")), "NZD LIBOR 1m", act365,
        following, Period.ofMonths(1), 2, false, nz);
    utils.addConventionBundle(ExternalIdBundle.of(bloombergTickerSecurityId("NZ0002M Index"), simpleNameSecurityId("NZD LIBOR 2m")), "NZD LIBOR 2m", act365,
        following, Period.ofMonths(2), 2, false, nz);
    utils.addConventionBundle(ExternalIdBundle.of(bloombergTickerSecurityId("NZ0003M Index"), simpleNameSecurityId("NZD LIBOR 3m")), "NZD LIBOR 3m", act365, 
        following, Period.ofMonths(3), 2, false, nz);
    utils.addConventionBundle(ExternalIdBundle.of(bloombergTickerSecurityId("NZ0004M Index"), simpleNameSecurityId("NZD LIBOR 4m")), "NZD LIBOR 4m", act365,
        following, Period.ofMonths(4), 2, false, nz);
    utils.addConventionBundle(ExternalIdBundle.of(bloombergTickerSecurityId("NZ0005M Index"), simpleNameSecurityId("NZD LIBOR 5m")), "NZD LIBOR 5m", act365,
        following, Period.ofMonths(5), 2, false, nz);
    utils.addConventionBundle(ExternalIdBundle.of(bloombergTickerSecurityId("NZ0006M Index"), simpleNameSecurityId("NZD LIBOR 6m")), "NZD LIBOR 6m", act365, 
        following, Period.ofMonths(6), 2, false, nz);
    utils.addConventionBundle(ExternalIdBundle.of(bloombergTickerSecurityId("NZ0007M Index"), simpleNameSecurityId("NZD LIBOR 7m")), "NZD LIBOR 7m", act365,
        following, Period.ofMonths(7), 2, false, nz);
    utils.addConventionBundle(ExternalIdBundle.of(bloombergTickerSecurityId("NZ0008M Index"), simpleNameSecurityId("NZD LIBOR 8m")), "NZD LIBOR 8m", act365,
        following, Period.ofMonths(8), 2, false, nz);
    utils.addConventionBundle(ExternalIdBundle.of(bloombergTickerSecurityId("NZ0009M Index"), simpleNameSecurityId("NZD LIBOR 9m")), "NZD LIBOR 9m", act365,
        following, Period.ofMonths(9), 2, false, nz);
    utils.addConventionBundle(ExternalIdBundle.of(bloombergTickerSecurityId("NZ0010M Index"), simpleNameSecurityId("NZD LIBOR 10m")), "NZD LIBOR 10m", act365,
        following, Period.ofMonths(10), 2, false, nz);
    utils.addConventionBundle(ExternalIdBundle.of(bloombergTickerSecurityId("NZ0011M Index"), simpleNameSecurityId("NZD LIBOR 11m")), "NZD LIBOR 11m", act365,
        following, Period.ofMonths(11), 2, false, nz);
    utils.addConventionBundle(ExternalIdBundle.of(bloombergTickerSecurityId("NZ0012M Index"), simpleNameSecurityId("NZD LIBOR 12m")), "NZD LIBOR 12m", act365, 
        following, Period.ofMonths(12), 2, false, nz);

    //TODO need to check that these are right for deposit rates
    utils.addConventionBundle(ExternalIdBundle.of(bloombergTickerSecurityId("NDDR1T Curncy"), simpleNameSecurityId("NZD DEPOSIT 1d")), "NZD DEPOSIT 1d", act365,
        following, Period.ofDays(1), 0, false, nz);
    utils.addConventionBundle(ExternalIdBundle.of(bloombergTickerSecurityId("NDDR2T Curncy"), simpleNameSecurityId("NZD DEPOSIT 2d")), "NZD DEPOSIT 2d", act365,
        following, Period.ofDays(1), 0, false, nz);
    utils.addConventionBundle(ExternalIdBundle.of(bloombergTickerSecurityId("NDDR3T Curncy"), simpleNameSecurityId("NZD DEPOSIT 3d")), "NZD DEPOSIT 3d", act365,
        following, Period.ofDays(1), 2, false, nz);
    utils.addConventionBundle(ExternalIdBundle.of(bloombergTickerSecurityId("NDDR1Z Curncy"), simpleNameSecurityId("NZD DEPOSIT 1w")), "NZD DEPOSIT 1w", act365,
        following, Period.ofDays(7), 2, false, nz);
    utils.addConventionBundle(ExternalIdBundle.of(bloombergTickerSecurityId("NDDR2Z Curncy"), simpleNameSecurityId("NZD DEPOSIT 2w")), "NZD DEPOSIT 2w", act365,
        following, Period.ofDays(14), 2, false, nz);
    utils.addConventionBundle(ExternalIdBundle.of(bloombergTickerSecurityId("NDDR3Z Curncy"), simpleNameSecurityId("NZD DEPOSIT 3w")), "NZD DEPOSIT 3w", act365,
        following, Period.ofDays(21), 2, false, nz);
    utils.addConventionBundle(ExternalIdBundle.of(bloombergTickerSecurityId("NDDRA Curncy"), simpleNameSecurityId("NZD DEPOSIT 1m")), "NZD DEPOSIT 1m", act365,
        following, Period.ofMonths(1), 2, false, nz);
    utils.addConventionBundle(ExternalIdBundle.of(bloombergTickerSecurityId("NDDRB Curncy"), simpleNameSecurityId("NZD DEPOSIT 2m")), "NZD DEPOSIT 2m", act365,
        following, Period.ofMonths(2), 2, false, nz);
    utils.addConventionBundle(ExternalIdBundle.of(bloombergTickerSecurityId("NDDRC Curncy"), simpleNameSecurityId("NZD DEPOSIT 3m")), "NZD DEPOSIT 3m", act365,
        following, Period.ofMonths(3), 2, false, nz);
    utils.addConventionBundle(ExternalIdBundle.of(bloombergTickerSecurityId("NDDRD Curncy"), simpleNameSecurityId("NZD DEPOSIT 4m")), "NZD DEPOSIT 4m", act365,
        following, Period.ofMonths(4), 2, false, nz);
    utils.addConventionBundle(ExternalIdBundle.of(bloombergTickerSecurityId("NDDRE Curncy"), simpleNameSecurityId("NZD DEPOSIT 5m")), "NZD DEPOSIT 5m", act365,
        following, Period.ofMonths(5), 2, false, nz);
    utils.addConventionBundle(ExternalIdBundle.of(bloombergTickerSecurityId("NDDRF Curncy"), simpleNameSecurityId("NZD DEPOSIT 6m")), "NZD DEPOSIT 6m", act365,
        following, Period.ofMonths(6), 2, false, nz);
    utils.addConventionBundle(ExternalIdBundle.of(bloombergTickerSecurityId("NDDRG Curncy"), simpleNameSecurityId("NZD DEPOSIT 7m")), "NZD DEPOSIT 7m", act365,
        following, Period.ofMonths(7), 2, false, nz);
    utils.addConventionBundle(ExternalIdBundle.of(bloombergTickerSecurityId("NDDRH Curncy"), simpleNameSecurityId("NZD DEPOSIT 8m")), "NZD DEPOSIT 8m", act365,
        following, Period.ofMonths(8), 2, false, nz);
    utils.addConventionBundle(ExternalIdBundle.of(bloombergTickerSecurityId("NDDRI Curncy"), simpleNameSecurityId("NZD DEPOSIT 9m")), "NZD DEPOSIT 9m", act365,
        following, Period.ofMonths(9), 2, false, nz);
    utils.addConventionBundle(ExternalIdBundle.of(bloombergTickerSecurityId("NDDRJ Curncy"), simpleNameSecurityId("NZD DEPOSIT 10m")), "NZD DEPOSIT 10m", act365,
        following, Period.ofMonths(10), 2, false, nz);
    utils.addConventionBundle(ExternalIdBundle.of(bloombergTickerSecurityId("NDDRK Curncy"), simpleNameSecurityId("NZD DEPOSIT 11m")), "NZD DEPOSIT 11m", act365,
        following, Period.ofMonths(11), 2, false, nz);
    utils.addConventionBundle(ExternalIdBundle.of(bloombergTickerSecurityId("NDDR1 Curncy"), simpleNameSecurityId("NZD DEPOSIT 1y")), "NZD DEPOSIT 1y", act365,
        following, Period.ofYears(1), 2, false, nz);
    utils.addConventionBundle(ExternalIdBundle.of(bloombergTickerSecurityId("NDDR2 Curncy"), simpleNameSecurityId("NZD DEPOSIT 2y")), "NZD DEPOSIT 2y", act365,
        following, Period.ofYears(2), 2, false, nz);
    utils.addConventionBundle(ExternalIdBundle.of(bloombergTickerSecurityId("NDDR3 Curncy"), simpleNameSecurityId("NZD DEPOSIT 3y")), "NZD DEPOSIT 3y", act365,
        following, Period.ofYears(3), 2, false, nz);
    utils.addConventionBundle(ExternalIdBundle.of(bloombergTickerSecurityId("NDDR4 Curncy"), simpleNameSecurityId("NZD DEPOSIT 4y")), "NZD DEPOSIT 4y", act365,
        following, Period.ofYears(4), 2, false, nz);
    utils.addConventionBundle(ExternalIdBundle.of(bloombergTickerSecurityId("NDDR5 Curncy"), simpleNameSecurityId("NZD DEPOSIT 5y")), "NZD DEPOSIT 5y", act365,
        following, Period.ofYears(5), 2, false, nz);
    utils.addConventionBundle(ExternalIdBundle.of(bloombergTickerSecurityId("NZOCRS Index"), simpleNameSecurityId("RBNZ CASH DAILY RATE")),
        "RBNZ CASH DAILY RATE", act365, following, Period.ofDays(1), 0, false, nz, 0); // review publication lag when doing OIS.

    utils.addConventionBundle(ExternalIdBundle.of(simpleNameSecurityId("NZD_SWAP")), "NZD_SWAP", act365, modified, semiAnnual, 2, nz, act365,
        modified, quarterly, 2, simpleNameSecurityId("NZD LIBOR 3m"), nz, true);
    utils.addConventionBundle(ExternalIdBundle.of(simpleNameSecurityId("NZD_3M_SWAP")), "NZD_3M_SWAP", act365, modified, semiAnnual, 2, nz,
        act365, modified, quarterly, 2, simpleNameSecurityId("NZD LIBOR 3m"), nz, true);
    // Overnight Index Swap Convention have additional flag, publicationLag
    final Integer publicationLag = 0;
    utils.addConventionBundle(ExternalIdBundle.of(simpleNameSecurityId("NZD_OIS_SWAP")), "NZD_OIS_SWAP", act365, modified, annual, 0, nz,
        act365, modified, annual, 0, simpleNameSecurityId("RBNZ CASH DAILY RATE"), nz, true, publicationLag);
  }
}

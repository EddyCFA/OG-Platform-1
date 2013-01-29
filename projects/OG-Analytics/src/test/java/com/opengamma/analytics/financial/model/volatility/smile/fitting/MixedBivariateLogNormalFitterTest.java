/**
 * Copyright (C) 2013 - present by OpenGamma Inc. and the OpenGamma group of companies
 * 
 * Please see distribution for license.
 */
package com.opengamma.analytics.financial.model.volatility.smile.fitting;

import static org.testng.AssertJUnit.assertEquals;

import java.util.Arrays;
import java.util.Random;

import org.testng.annotations.Test;

import com.opengamma.analytics.financial.model.option.definition.SmileDeltaParameters;
import com.opengamma.analytics.financial.model.option.pricing.analytic.formula.EuropeanVanillaOption;
import com.opengamma.analytics.financial.model.volatility.BlackFormulaRepository;
import com.opengamma.analytics.financial.model.volatility.smile.function.MixedBivariateLogNormalModelVolatility;
import com.opengamma.analytics.financial.model.volatility.smile.function.MixedLogNormalModelData;
import com.opengamma.analytics.financial.model.volatility.smile.function.MixedLogNormalVolatilityFunction;
import com.opengamma.analytics.math.random.NormalRandomNumberGenerator;
import com.opengamma.util.ArgumentChecker;

/**
 * 
 */
public class MixedBivariateLogNormalFitterTest {

  @Test
      (enabled = false)
      public void FittingTestManyDataPts() {

    final int nNorms = 2;
    final int nParams = 5 * nNorms - 3;
    final int nDataPts = 200;
    final int nDataPtsX = 100;
    final int nParamsX = 3 * nNorms - 2;

    final double fwdX = 1.1;
    final double fwdY = 0.9;
    final double time = 0.6;

    double[] xx = new double[nDataPts];
    double[] yy = new double[nDataPts];
    double[] aa = new double[nParams];
    double[] aaGuess1 = new double[nParams];
    double[] aaGuess2 = new double[nParams];
    double[] aaGuess3 = new double[nParams];
    double[] aaX = new double[nParamsX];
    double[] aaGuessX = new double[nParamsX];

    //  Random objRand = new Random();

    for (int i = 0; i < nParams; ++i) {
      aa[i] = 1. + 0.0125 * i;
    }

    double rateX = 1. / nDataPtsX;
    double rateY = 1. / (nDataPts - nDataPtsX);
    for (int i = 0; i < nDataPtsX; ++i) {
      xx[i] = fwdX * (0.5 + rateX * i);
    }
    for (int i = nDataPtsX; i < nDataPts; ++i) {
      xx[i] = fwdY * (0.5 + rateY * i);
    }

    yy = getFunctionValues(aa, xx, fwdX, fwdY, time, nNorms, nDataPts, nDataPtsX);

    for (int i = 0; i < nParams; ++i) {
      aaGuess1[i] = 0.9;
    }

    for (int i = 0; i < nParams; ++i) {
      aaGuess2[i] = 1.2;
      //      aaGuess2[i] = objRand.nextDouble();
      //      aaGuess2[i] = 0.5 + objRand.nextDouble();
    }

    /////Comparing true parameters with parameters returned by fitting/////

    System.out.println("true value: " + aa[0] + "\t" + aa[1]);
    System.out.println("\n");

    MixedBivariateLogNormalFitter fitter1 = new MixedBivariateLogNormalFitter(aaGuess1, xx, yy, time, fwdX, fwdY, nNorms, nDataPtsX, 1);
    System.out.println("guess: " + aaGuess1[0] + "\t" + aaGuess1[1]);
    fitter1.doFit();
    aaGuess1 = fitter1.getParams();
    System.out.println("inintial sq: " + fitter1.getInitialSq());
    System.out.println("answer: " + aaGuess1[0] + "\t" + aaGuess1[1]);
    System.out.println("sq: " + fitter1.getFinalSq());
    System.out.println("\n");

    MixedBivariateLogNormalFitter fitter2 = new MixedBivariateLogNormalFitter(aaGuess2, xx, yy, time, fwdX, fwdY, nNorms, nDataPtsX, 1);
    System.out.println("guess: " + aaGuess2[0] + "\t" + aaGuess2[1]);
    fitter2.doFit();
    System.out.println("inintial sq: " + fitter2.getInitialSq());
    aaGuess2 = fitter2.getParams();
    System.out.println("answer: " + aaGuess2[0] + "\t" + aaGuess2[1]);
    System.out.println("sq: " + fitter2.getFinalSq());
    System.out.println("\n");

    MixedBivariateLogNormalFitter fitter3 = new MixedBivariateLogNormalFitter(aa, xx, yy, time, fwdX, fwdY, nNorms, nDataPtsX, 1);
    System.out.println("guess: " + aa[0] + "\t" + aa[1]);
    fitter3.doFit();
    System.out.println("inintial sq: " + fitter3.getInitialSq());
    aaGuess3 = fitter3.getParams();
    System.out.println("answer: " + aaGuess3[0] + "\t" + aaGuess3[1]);
    System.out.println("sq: " + fitter3.getFinalSq());
    System.out.println("\n");

    /////Comparing true volatility smile with volatility smile derived with fitted parameters/////

    for (int i = 0; i < nNorms; ++i) {
      aaGuessX[i] = aaGuess1[i];
    }
    for (int i = 0; i < nNorms - 1; ++i) {
      aaGuessX[i + nNorms] = aaGuess1[i + 2 * nNorms];
      aaGuessX[i + 2 * nNorms - 1] = aaGuess1[i + 3 * nNorms - 1];
    }

    for (int i = 0; i < nNorms; ++i) {
      aaX[i] = aaGuess2[i];
    }
    for (int i = 0; i < nNorms - 1; ++i) {
      aaX[i + nNorms] = aaGuess2[i + 2 * nNorms];
      aaX[i + 2 * nNorms - 1] = aaGuess2[i + 3 * nNorms - 1];
    }

    final MixedLogNormalModelData objAns1 = new MixedLogNormalModelData(aaGuessX, true);
    final MixedLogNormalModelData objAns2 = new MixedLogNormalModelData(aaX, true);
    final MixedLogNormalVolatilityFunction volfunc = MixedLogNormalVolatilityFunction.getInstance();

    for (int i = 0; i < 100; i++) {
      double k = fwdX * (0.5 + 1. * i / 100.);
      final EuropeanVanillaOption option = new EuropeanVanillaOption(k, time, true);
      final double vol2 = volfunc.getVolatility(option, fwdX, objAns1);
      final double vol3 = volfunc.getVolatility(option, fwdX, objAns2);
      System.out.println(k + "\t" + vol2 + "\t" + vol3 + "\t" + yy[i]);
    }

  }

  @Test
      (enabled = false)
      public void TestDerivingZ1() {

    final int nNorms = 2;
    final int nParams = 5 * nNorms - 3;
    final int nDataPts = 14;
    final int nDataPtsX = 7;
    final int nParamsX = 3 * nNorms - 2;
    final int nParamsY = 3 * nNorms - 2;

    final double fwdX = 1.;
    final double fwdY = 1.;
    final double fwdZ = fwdX / fwdY;
    final double time = 1.0;

    double[] xx = new double[nDataPts];
    double[] yy = new double[nDataPts];
    double[] aaGuess1 = new double[nParams];
    double[] aaGuess1X = new double[nParamsX];
    double[] aaGuess1Y = new double[nParamsY];
    double[] rhos = new double[nNorms];

    Random obj = new Random();

    double[] inRelativePartialForwardsX = {1., 1. };
    double[] inRelativePartialForwardsY = {Math.exp(-0.2), (1. - Math.exp(-0.2) * 0.7) / 0.3 };

    double[] inSigmasX = {0.25, 0.7 };
    double[] inSigmasY = {0.3, 0.5 };

    double[] inWeights = {0.7, 0.3 };

    final MixedLogNormalModelData inObjX = new MixedLogNormalModelData(inWeights, inSigmasX, inRelativePartialForwardsX);
    final MixedLogNormalModelData inObjY = new MixedLogNormalModelData(inWeights, inSigmasY, inRelativePartialForwardsY);

    xx = new double[] {0.5, 0.7, 0.9, 1.0, 1.2, 1.5, 1.8, 0.5, 0.7, 0.9, 1.0, 1.2, 1.5, 1.8 };

    final MixedLogNormalVolatilityFunction volfunc = MixedLogNormalVolatilityFunction.getInstance();
    Arrays.fill(yy, 0.);

    for (int j = 0; j < nDataPtsX; ++j) {
      EuropeanVanillaOption option = new EuropeanVanillaOption(xx[j], time, true);
      yy[j] = volfunc.getVolatility(option, fwdX, inObjX);
    }

    for (int j = nDataPtsX; j < nDataPts; ++j) {
      EuropeanVanillaOption option = new EuropeanVanillaOption(xx[j], time, true);
      yy[j] = volfunc.getVolatility(option, fwdY, inObjY);
    }

    rhos[0] = 0.2;
    rhos[1] = 0.9;

    final MixedBivariateLogNormalModelVolatility objTrueZ = new MixedBivariateLogNormalModelVolatility(inWeights, inSigmasX,
        inSigmasY, inRelativePartialForwardsX, inRelativePartialForwardsY, rhos);

    final double[] inSigmasZ = objTrueZ.getSigmasZ();
    final double[] inRelativePartialForwardsZ = objTrueZ.getRelativeForwardsZ();

    System.out.println("true values: " + inSigmasX[0] + "\t" + inSigmasX[1]);
    System.out.println("\n");

    for (int i = 0; i < nParams; ++i) {
      aaGuess1[i] = 1e-2 + obj.nextDouble();
    }
    MixedBivariateLogNormalFitter fitter1 = new MixedBivariateLogNormalFitter(aaGuess1, xx, yy, time, fwdX, fwdY, nNorms, nDataPtsX, 1);

    boolean fitDone = false;

    while (fitDone == false) {

      for (int i = 0; i < nNorms; ++i) {
        aaGuess1X[i] = aaGuess1[i];
        aaGuess1Y[i] = aaGuess1[i + nNorms];
      }
      for (int i = 0; i < nNorms - 1; ++i) {
        aaGuess1X[i + nNorms] = aaGuess1[i + 2 * nNorms];
        aaGuess1X[i + 2 * nNorms - 1] = aaGuess1[i + 3 * nNorms - 1];
        aaGuess1Y[i + nNorms] = aaGuess1[i + 2 * nNorms];
        aaGuess1Y[i + 2 * nNorms - 1] = aaGuess1[i + 4 * nNorms - 2];
      }

      MixedLogNormalModelData tmpObj1X = new MixedLogNormalModelData(aaGuess1X, true);
      double[] tmpSigmasX = tmpObj1X.getVolatilities();

      System.out.println("guess: " + tmpSigmasX[0] + "\t" + tmpSigmasX[1]);
      fitter1.doFit();
      aaGuess1 = fitter1.getParams();
      System.out.println("inintial sq: " + fitter1.getInitialSq());

      for (int i = 0; i < nNorms; ++i) {
        aaGuess1X[i] = aaGuess1[i];
        aaGuess1Y[i] = aaGuess1[i + nNorms];
      }
      for (int i = 0; i < nNorms - 1; ++i) {
        aaGuess1X[i + nNorms] = aaGuess1[i + 2 * nNorms];
        aaGuess1X[i + 2 * nNorms - 1] = aaGuess1[i + 3 * nNorms - 1];
        aaGuess1Y[i + nNorms] = aaGuess1[i + 2 * nNorms];
        aaGuess1Y[i + 2 * nNorms - 1] = aaGuess1[i + 4 * nNorms - 2];
      }

      tmpObj1X = new MixedLogNormalModelData(aaGuess1X, true);
      tmpSigmasX = tmpObj1X.getVolatilities();

      System.out.println("answer: " + tmpSigmasX[0] + "\t" + tmpSigmasX[1]);
      System.out.println("sq: " + fitter1.getFinalSq());
      System.out.println("\n");

      if (fitter1.getFinalSq() <= fitter1.getInitialSq() * 1e-10) {
        fitDone = true;
      } else {
        for (int i = 0; i < nParams; ++i) {
          aaGuess1[i] = 1e-2 + obj.nextDouble();
        }
        fitter1 = new MixedBivariateLogNormalFitter(aaGuess1, xx, yy, time, fwdX, fwdY, nNorms, nDataPtsX, 1);
      }

    }

    for (int i = 0; i < nNorms; ++i) {
      aaGuess1X[i] = aaGuess1[i];
      aaGuess1Y[i] = aaGuess1[i + nNorms];
    }
    for (int i = 0; i < nNorms - 1; ++i) {
      aaGuess1X[i + nNorms] = aaGuess1[i + 2 * nNorms];
      aaGuess1X[i + 2 * nNorms - 1] = aaGuess1[i + 3 * nNorms - 1];
      aaGuess1Y[i + nNorms] = aaGuess1[i + 2 * nNorms];
      aaGuess1Y[i + 2 * nNorms - 1] = aaGuess1[i + 4 * nNorms - 2];
    }

    final MixedLogNormalModelData objAns1X = new MixedLogNormalModelData(aaGuess1X, true);
    final double[] weights = objAns1X.getWeights();
    final double[] sigmasX = objAns1X.getVolatilities();
    final double[] relativePartialForwardsX = objAns1X.getRelativeForwards();

    final MixedLogNormalModelData objAns1Y = new MixedLogNormalModelData(aaGuess1Y, true);
    final double[] sigmasY = objAns1Y.getVolatilities();
    final double[] relativePartialForwardsY = objAns1Y.getRelativeForwards();

    final MixedBivariateLogNormalModelVolatility objZ = new MixedBivariateLogNormalModelVolatility(weights, sigmasX,
        sigmasY, relativePartialForwardsX, relativePartialForwardsY, rhos);

    final double[] sigmasZ = objZ.getSigmasZ();
    final double[] relativePartialForwardsZ = objZ.getRelativeForwardsZ();

    System.out.println("Parameters XY");
    for (int i = 0; i < nNorms; ++i) {

      System.out.println(weights[i] + "\t" + inWeights[i]);
      assertEquals("weights ", weights[i], inWeights[i], Math.abs((inWeights[0] + inWeights[1]) / 2.) * 1e-6);
    }
    for (int i = 0; i < nNorms; ++i) {
      System.out.println(sigmasX[i] + "\t" + inSigmasX[i]);
      assertEquals("sigmasY ", sigmasX[i], inSigmasX[i], Math.abs((inSigmasX[0] + inSigmasX[1]) / 2.) * 1e-6);
    }
    for (int i = 0; i < nNorms; ++i) {
      System.out.println(sigmasY[i] + "\t" + inSigmasY[i]);
      assertEquals("sigmasY ", sigmasY[i], inSigmasY[i], Math.abs((inSigmasY[0] + inSigmasY[1]) / 2.) * 1e-6);
    }
    for (int i = 0; i < nNorms; ++i) {
      System.out.println(relativePartialForwardsX[i] + "\t" + inRelativePartialForwardsX[i]);
      assertEquals("relativePartialForwardsX ", relativePartialForwardsX[i], inRelativePartialForwardsX[i], Math.abs((inRelativePartialForwardsX[0] + inRelativePartialForwardsX[1]) / 2.) * 1e-6);
    }
    for (int i = 0; i < nNorms; ++i) {
      System.out.println(relativePartialForwardsY[i] + "\t" + inRelativePartialForwardsY[i]);
      assertEquals("relativePartialForwardsY ", relativePartialForwardsY[i], inRelativePartialForwardsY[i], Math.abs((inRelativePartialForwardsY[0] + inRelativePartialForwardsY[1]) / 2.) * 1e-6);
    }

    System.out.println("\n");
    System.out.println("Parameters Z");

    for (int i = 0; i < nNorms; ++i) {
      System.out.println(sigmasZ[i] + "\t" + inSigmasZ[i]);
      assertEquals("sigmasZ ", sigmasZ[i], inSigmasZ[i], Math.abs((inSigmasZ[0] + inSigmasZ[1]) / 2.) * 1e-6);
    }
    for (int i = 0; i < nNorms; ++i) {
      System.out.println(relativePartialForwardsZ[i] + "\t" + inRelativePartialForwardsZ[i]);
      assertEquals("relativePartialForwardsZ ", relativePartialForwardsZ[i], inRelativePartialForwardsZ[i], Math.abs((inRelativePartialForwardsZ[0] + inRelativePartialForwardsZ[1]) / 2.) * 1e-6);
    }

    System.out.println("\n");
    System.out.println("Imp Vols XYZ from fitting");

    double[] ansVolsX = new double[100];
    double[] ansVolsY = new double[100];
    double[] ansVolsZ = new double[100];
    for (int i = 0; i < 100; i++) {
      double k = fwdX * (0.1 + 2. * i / 100.);
      final EuropeanVanillaOption option = new EuropeanVanillaOption(k, time, true);
      ansVolsX[i] = volfunc.getVolatility(option, fwdX, objAns1X);
      ansVolsY[i] = volfunc.getVolatility(option, fwdY, objAns1Y);
      ansVolsZ[i] = objZ.getImpliedVolatilityZ(option, fwdZ);
      System.out.println(k + "\t" + ansVolsX[i] + "\t" + ansVolsY[i] + "\t" + ansVolsZ[i]);
    }

    System.out.println("\n");
    System.out.println("True Imp Vols XYZ");

    double[] trueVolsX = new double[100];
    double[] trueVolsY = new double[100];
    double[] trueVolsZ = new double[100];
    for (int i = 0; i < 100; i++) {
      double k = fwdY * (0.1 + 2. * i / 100.);
      final EuropeanVanillaOption option = new EuropeanVanillaOption(k, time, true);
      trueVolsX[i] = volfunc.getVolatility(option, fwdX, inObjX);
      trueVolsY[i] = volfunc.getVolatility(option, fwdY, inObjY);
      trueVolsZ[i] = objTrueZ.getImpliedVolatilityZ(option, fwdZ);
      System.out.println(k + "\t" + trueVolsX[i] + "\t" + trueVolsY[i] + "\t" + trueVolsZ[i]);

    }

    System.out.println("\n");
    final double[] kDataSet = {0.5, 0.7, 0.9, 1.0, 1.2, 1.5, 1.8 };
    for (double k : kDataSet) {
      final EuropeanVanillaOption option = new EuropeanVanillaOption(k, time, true);
      final double trueVol = objTrueZ.getImpliedVolatilityZ(option, fwdZ);
      System.out.println(k + "\t" + trueVol);
    }

    System.out.println("\n");
    System.out.println("Imp Vols XYZ (1e-6)");

    for (int i = 0; i < 100; i++) {
      double k = fwdX * (0.1 + 2. * i / 100.);
      assertEquals("Imp Vols of X " + k, ansVolsX[i], trueVolsX[i], Math.abs((inSigmasX[0] + inSigmasX[1]) / 2.) * 1e-6);
      assertEquals("Imp Vols of Y " + k, ansVolsY[i], trueVolsY[i], Math.abs((inSigmasY[0] + inSigmasY[1]) / 2.) * 1e-6);
      assertEquals("Imp Vols of Z " + k, ansVolsZ[i], trueVolsZ[i], Math.abs((inSigmasZ[0] + inSigmasZ[1]) / 2.) * 1e-6);
    }

    System.out.println("\n");
    System.out.println("Density X (1e-6)");

    double[] ansDensityX = new double[100];
    double[] ansDensityY = new double[100];
    double[] ansDensityZ = new double[100];
    double[] trueDensityX = new double[100];
    double[] trueDensityY = new double[100];
    double[] trueDensityZ = new double[100];
    for (int i = 0; i < 100; i++) {
      double k = fwdX * (0.01 + 2. * i / 100.);
      final EuropeanVanillaOption option = new EuropeanVanillaOption(k, time, true);
      ansDensityX[i] = getDualGamma(option, fwdX, objAns1X);
      trueDensityX[i] = getDualGamma(option, fwdX, inObjX);
      //  System.out.println(k + "\t" + ansDensityX[i] + "\t" + trueDensityX[i]);
      assertEquals("Density of X " + k, ansDensityX[i], trueDensityX[i], 1e-6);
    }

    System.out.println("\n");
    System.out.println("Density Y (1e-6)");

    for (int i = 0; i < 100; i++) {
      double k = fwdY * (0.01 + 2. * i / 100.);
      final EuropeanVanillaOption option = new EuropeanVanillaOption(k, time, true);
      ansDensityY[i] = getDualGamma(option, fwdY, objAns1Y);
      trueDensityY[i] = getDualGamma(option, fwdY, inObjY);
      //  System.out.println(k + "\t" + ansDensityY[i] + "\t" + trueDensityY[i]);
      assertEquals("Density of Y " + k, ansDensityY[i], trueDensityY[i], 1e-6);
    }

    System.out.println("\n");
    System.out.println("Density Z (1e-6)");

    for (int i = 0; i < 100; i++) {
      double k = fwdY * (0.01 + 2. * i / 100.);
      final EuropeanVanillaOption option = new EuropeanVanillaOption(k, time, true);
      ansDensityZ[i] = getDualGammaZ(option, fwdZ, objZ);
      trueDensityZ[i] = getDualGammaZ(option, fwdZ, objTrueZ);
      //  System.out.println(k + "\t" + ansDensityZ[i] + "\t" + trueDensityZ[i]);
      assertEquals("Density of Z " + k, ansDensityZ[i], trueDensityZ[i], 1e-6);
    }

    System.out.println("\n");
    System.out.println("Density(exp)");

    for (int i = 0; i < 100; i++) {
      double k = fwdX * (0.1 + 9. * i / 100.);
      final double denValueX = getDensity(weights, sigmasX, relativePartialForwardsX, k);
      final double denValueY = getDensity(weights, sigmasY, relativePartialForwardsY, k);
      final double denValueZ = getDensity(weights, sigmasZ, relativePartialForwardsZ, k);
      System.out.println(Math.log(k) + "\t" + denValueX + "\t" + denValueY + "\t" + denValueZ);
    }

    System.out.println("\n");
    System.out.println("Put-Call Parity X (1e-10)");

    for (int i = 0; i < 100; i++) {
      double k = fwdY * (0.01 + 2. * i / 100.);
      final EuropeanVanillaOption optionCall = new EuropeanVanillaOption(k, time, true);
      final EuropeanVanillaOption optionPut = new EuropeanVanillaOption(k, time, false);
      final double callPrice = getPrice(optionCall, fwdX, objAns1X);
      final double putPrice = getPrice(optionPut, fwdX, objAns1X);
      //     System.out.println(k + "\t" + (callPrice - putPrice) + "\t" + (fwdX - k));
      assertEquals("Put-Call Parity X " + k, (callPrice - putPrice), (fwdX - k), fwdX * 1e-10);
    }

    System.out.println("\n");
    System.out.println("Put-Call Parity Y (1e-10)");

    for (int i = 0; i < 100; i++) {
      double k = fwdY * (0.01 + 2. * i / 100.);
      final EuropeanVanillaOption optionCall = new EuropeanVanillaOption(k, time, true);
      final EuropeanVanillaOption optionPut = new EuropeanVanillaOption(k, time, false);
      final double callPrice = getPrice(optionCall, fwdY, objAns1Y);
      final double putPrice = getPrice(optionPut, fwdY, objAns1Y);
      //    System.out.println(k + "\t" + (callPrice - putPrice) + "\t" + (fwdY - k));
      assertEquals("Put-Call Parity Y " + k, (callPrice - putPrice), (fwdY - k), fwdY * 1e-10);
    }

    System.out.println("\n");
    System.out.println("Put-Call Parity Z (1e-8)");

    for (int i = 0; i < 100; i++) {
      double k = fwdZ * (0.01 + 2. * i / 100.);
      final EuropeanVanillaOption optionCall = new EuropeanVanillaOption(k, time, true);
      final EuropeanVanillaOption optionPut = new EuropeanVanillaOption(k, time, false);
      final double callPrice = objZ.getPriceZ(optionCall, fwdZ);
      final double putPrice = objZ.getPriceZ(optionPut, fwdZ);
      //     System.out.println(k + "\t" + (callPrice - putPrice) + "\t" + (fwdY - k));
      assertEquals("Put-Call Parity Z " + k, (callPrice - putPrice), (fwdY - k), fwdY * 1e-10);
    }

  }

  @Test
      (enabled = false)
      public void TestError() {

    final int nNorms = 2;
    final int nParams = 5 * nNorms - 3;
    final int nDataPts = 14;
    final int nDataPtsX = 7;
    final int nParamsX = 3 * nNorms - 2;
    final int nParamsY = 3 * nNorms - 2;

    final double fwdX = 1.;
    final double fwdY = 1.;
    final double time = 1.0;

    double[] xx = new double[nDataPts];
    double[] yy = new double[nDataPts];
    double[] aaGuess1 = new double[nParams];
    double[] aaGuess1X = new double[nParamsX];
    double[] aaGuess1Y = new double[nParamsY];
    double[] rhos = new double[nNorms];

    Random obj = new Random();

    double[] inRelativePartialForwardsX = {1., 1. };
    double[] inRelativePartialForwardsY = {Math.exp(-0.2), (1. - Math.exp(-0.2) * 0.7) / 0.3 };

    double[] inSigmasX = {0.25, 0.7 };
    double[] inSigmasY = {0.3, 0.5 };

    double[] inWeights = {0.7, 0.3 };

    final MixedLogNormalModelData inObjX = new MixedLogNormalModelData(inWeights, inSigmasX, inRelativePartialForwardsX);
    final MixedLogNormalModelData inObjY = new MixedLogNormalModelData(inWeights, inSigmasY, inRelativePartialForwardsY);

    xx = new double[] {0.5, 0.7, 0.9, 1.0, 1.2, 1.5, 1.8, 0.5, 0.7, 0.9, 1.0, 1.2, 1.5, 1.8 };

    final MixedLogNormalVolatilityFunction volfunc = MixedLogNormalVolatilityFunction.getInstance();
    Arrays.fill(yy, 0.);

    for (int j = 0; j < nDataPtsX; ++j) {
      EuropeanVanillaOption option = new EuropeanVanillaOption(xx[j], time, true);
      yy[j] = volfunc.getVolatility(option, fwdX, inObjX);
    }

    for (int j = nDataPtsX; j < nDataPts; ++j) {
      EuropeanVanillaOption option = new EuropeanVanillaOption(xx[j], time, true);
      yy[j] = volfunc.getVolatility(option, fwdY, inObjY);
    }

    for (int i = 0; i < nNorms; ++i) {
      rhos[i] = 0.3 * (i + 1.);
    }

    System.out.println("true values: " + inSigmasX[0] + "\t" + inSigmasX[1]);
    System.out.println("\n");

    //    for (int i = 0; i < nParams; ++i) {
    //      aaGuess1[i] = 1e-2 + obj.nextDouble(); //Too small guess values may cause instability of fitting
    //      System.out.println(aaGuess1[i]);
    //    }

    aaGuess1 = new double[] {0.7754891006466627, 0.43606844423507685, 0.012213666603921194, 0.29500288815152165, 0.5444481098115485, 0.5291315433000237, 0.2231641334515797 };

    MixedBivariateLogNormalFitter fitter1 = new MixedBivariateLogNormalFitter(aaGuess1, xx, yy, time, fwdX, fwdY, nNorms, nDataPtsX, 1);

    boolean fitDone = false;
    int counter = 0;

    while (fitDone == false && counter <= 100000) {
      counter += 1;

      for (int i = 0; i < nNorms; ++i) {
        aaGuess1X[i] = aaGuess1[i];
        aaGuess1Y[i] = aaGuess1[i + nNorms];
      }
      for (int i = 0; i < nNorms - 1; ++i) {
        aaGuess1X[i + nNorms] = aaGuess1[i + 2 * nNorms];
        aaGuess1X[i + 2 * nNorms - 1] = aaGuess1[i + 3 * nNorms - 1];
        aaGuess1Y[i + nNorms] = aaGuess1[i + 2 * nNorms];
        aaGuess1Y[i + 2 * nNorms - 1] = aaGuess1[i + 4 * nNorms - 2];
      }

      MixedLogNormalModelData tmpObj1X = new MixedLogNormalModelData(aaGuess1X, true);
      double[] tmpSigmasX = tmpObj1X.getVolatilities();

      System.out.println("guess: " + tmpSigmasX[0] + "\t" + tmpSigmasX[1]);

      fitter1.doFit();
      aaGuess1 = fitter1.getParams();
      System.out.println("inintial sq: " + fitter1.getInitialSq());
      for (int i = 0; i < nNorms; ++i) {
        aaGuess1X[i] = aaGuess1[i];
        aaGuess1Y[i] = aaGuess1[i + nNorms];
      }
      for (int i = 0; i < nNorms - 1; ++i) {
        aaGuess1X[i + nNorms] = aaGuess1[i + 2 * nNorms];
        aaGuess1X[i + 2 * nNorms - 1] = aaGuess1[i + 3 * nNorms - 1];
        aaGuess1Y[i + nNorms] = aaGuess1[i + 2 * nNorms];
        aaGuess1Y[i + 2 * nNorms - 1] = aaGuess1[i + 4 * nNorms - 2];
      }

      tmpObj1X = new MixedLogNormalModelData(aaGuess1X, true);
      tmpSigmasX = tmpObj1X.getVolatilities();

      System.out.println("answer: " + tmpSigmasX[0] + "\t" + tmpSigmasX[1]);
      System.out.println("sq: " + fitter1.getFinalSq());
      System.out.println("\n");

      if (fitter1.getFinalSq() < fitter1.getInitialSq() * 1e-10) {
        //   fitDone = true;
      } else {
        for (int i = 0; i < nParams; ++i) {
          aaGuess1[i] = 1e-2 + obj.nextDouble();
          System.out.println(aaGuess1[i]);
        }
        fitter1 = new MixedBivariateLogNormalFitter(aaGuess1, xx, yy, time, fwdX, fwdY, nNorms, nDataPtsX, 1);
      }

    }

    //    for (int i = 0; i < nNorms; ++i) {
    //      aaGuess1X[i] = aaGuess1[i];
    //      aaGuess1Y[i] = aaGuess1[i + nNorms];
    //    }
    //    for (int i = 0; i < nNorms - 1; ++i) {
    //      aaGuess1X[i + nNorms] = aaGuess1[i + 2 * nNorms];
    //      aaGuess1X[i + 2 * nNorms - 1] = aaGuess1[i + 3 * nNorms - 1];
    //      aaGuess1Y[i + nNorms] = aaGuess1[i + 2 * nNorms];
    //      aaGuess1Y[i + 2 * nNorms - 1] = aaGuess1[i + 4 * nNorms - 2];
    //    }
    //
    //    final MixedLogNormalModelData objAns1X = new MixedLogNormalModelData(aaGuess1X, true);
    //    final double[] weights = objAns1X.getWeights();
    //    final double[] sigmasX = objAns1X.getVolatilities();
    //    final double[] relativePartialForwardsX = objAns1X.getRelativeForwards();
    //
    //    final MixedLogNormalModelData objAns1Y = new MixedLogNormalModelData(aaGuess1Y, true);
    //    final double[] sigmasY = objAns1Y.getVolatilities();
    //    final double[] relativePartialForwardsY = objAns1Y.getRelativeForwards();
    //
    //    System.out.println("Imp Vols XYZ from fitting");
    //
    //    final MixedBivariateLogNormalModelVolatility objZ = new MixedBivariateLogNormalModelVolatility(weights, sigmasX,
    //        sigmasY, relativePartialForwardsX, relativePartialForwardsY, rhos);
    //
    //    double[] ansVolsX = new double[100];
    //    double[] ansVolsY = new double[100];
    //    double[] ansVolsZ = new double[100];
    //    for (int i = 0; i < 100; i++) {
    //      double k = fwdX * (0.1 + 2. * i / 100.);
    //      final EuropeanVanillaOption option = new EuropeanVanillaOption(k, time, true);
    //      ansVolsX[i] = volfunc.getVolatility(option, fwdX, objAns1X);
    //      ansVolsY[i] = volfunc.getVolatility(option, fwdY, objAns1Y);
    //      ansVolsZ[i] = objZ.getVolatilityZ(option, fwdZ);
    //      //     System.out.println(k + "\t" + ansVolsX[i] + "\t" + ansVolsY[i] + "\t" + ansVolsZ[i]);
    //    }
    //
    //    System.out.println("\n");
    //    System.out.println("True Imp Vols XYZ");
    //
    //    final MixedBivariateLogNormalModelVolatility objTrueZ = new MixedBivariateLogNormalModelVolatility(inWeights, inSigmasX,
    //        inSigmasY, inRelativePartialForwardsX, inRelativePartialForwardsY, rhos);
    //
    //    double[] trueVolsX = new double[100];
    //    double[] trueVolsY = new double[100];
    //    double[] trueVolsZ = new double[100];
    //    for (int i = 0; i < 100; i++) {
    //      double k = fwdY * (0.1 + 2. * i / 100.);
    //      final EuropeanVanillaOption option = new EuropeanVanillaOption(k, time, true);
    //      trueVolsX[i] = volfunc.getVolatility(option, fwdX, inObjX);
    //      trueVolsY[i] = volfunc.getVolatility(option, fwdY, inObjY);
    //      trueVolsZ[i] = objTrueZ.getVolatilityZ(option, fwdZ);
    //      //     System.out.println(k + "\t" + trueVolsX[i] + "\t" + trueVolsY[i] + "\t" + trueVolsZ[i]);
    //
    //    }
    //
    //    System.out.println("\n");
    //    System.out.println("Imp Vols XYZ (1e-5)");
    //
    //    final double[] inSigmasZ = objTrueZ.getVolatilitiesZ();
    //
    //    for (int i = 0; i < 100; i++) {
    //      double k = fwdX * (0.1 + 2. * i / 100.);
    //      assertEquals("Imp Vols of X " + k, ansVolsX[i], trueVolsX[i], Math.abs((inSigmasX[0] + inSigmasX[1]) / 2.) * 1e-5);
    //      assertEquals("Imp Vols of Y " + k, ansVolsY[i], trueVolsY[i], Math.abs((inSigmasY[0] + inSigmasY[1]) / 2.) * 1e-5);
    //      assertEquals("Imp Vols of Z " + k, ansVolsZ[i], trueVolsZ[i], Math.abs((inSigmasZ[0] + inSigmasZ[1]) / 2.) * 1e-5);
    //    }

  }

  @Test
      (enabled = false)
      public void TestAccuracy() {

    final int nNorms = 2;
    final int nParams = 5 * nNorms - 3;
    final int nDataPts = 14;
    final int nDataPtsX = 7;
    final int nParamsX = 3 * nNorms - 2;
    final int nParamsY = 3 * nNorms - 2;

    final double fwdX = 1.;
    final double fwdY = 1.;
    final double fwdZ = fwdX / fwdY;
    final double time = 1.0;

    double[] xx = new double[nDataPts];
    double[] yy = new double[nDataPts];
    double[] aaGuess1 = new double[nParams];
    double[] aaGuess1X = new double[nParamsX];
    double[] aaGuess1Y = new double[nParamsY];
    double[] rhos = new double[nNorms];

    Random obj = new Random();

    double[] inRelativePartialForwardsX = {1., 1. };
    double[] inRelativePartialForwardsY = {Math.exp(-0.2), (1. - Math.exp(-0.2) * 0.7) / 0.3 };

    double[] inSigmasX = {0.25, 0.7 };
    double[] inSigmasY = {0.3, 0.5 };

    double[] inWeights = {0.7, 0.3 };

    final MixedLogNormalModelData inObjX = new MixedLogNormalModelData(inWeights, inSigmasX, inRelativePartialForwardsX);
    final MixedLogNormalModelData inObjY = new MixedLogNormalModelData(inWeights, inSigmasY, inRelativePartialForwardsY);

    xx = new double[] {0.5, 0.7, 0.9, 1.0, 1.2, 1.5, 1.8, 0.5, 0.7, 0.9, 1.0, 1.2, 1.5, 1.8 };

    final MixedLogNormalVolatilityFunction volfunc = MixedLogNormalVolatilityFunction.getInstance();
    Arrays.fill(yy, 0.);

    for (int j = 0; j < nDataPtsX; ++j) {
      EuropeanVanillaOption option = new EuropeanVanillaOption(xx[j], time, true);
      yy[j] = volfunc.getVolatility(option, fwdX, inObjX);
    }

    for (int j = nDataPtsX; j < nDataPts; ++j) {
      EuropeanVanillaOption option = new EuropeanVanillaOption(xx[j], time, true);
      yy[j] = volfunc.getVolatility(option, fwdY, inObjY);
    }

    for (int i = 0; i < nNorms; ++i) {
      rhos[i] = 0.2 * (0.5 * i + 1.);
    }

    System.out.println("true values: " + inSigmasX[0] + "\t" + inSigmasX[1]);
    System.out.println("\n");

    int ctr = 0;
    while (ctr <= 1000) {
      ++ctr;

      for (int i = 0; i < nParams; ++i) {
        aaGuess1[i] = 1e-2 + obj.nextDouble();
      }
      MixedBivariateLogNormalFitter fitter1 = new MixedBivariateLogNormalFitter(aaGuess1, xx, yy, time, fwdX, fwdY, nNorms, nDataPtsX, 1);

      boolean fitDone = false;

      while (fitDone == false) {

        for (int i = 0; i < nNorms; ++i) {
          aaGuess1X[i] = aaGuess1[i];
          aaGuess1Y[i] = aaGuess1[i + nNorms];
        }
        for (int i = 0; i < nNorms - 1; ++i) {
          aaGuess1X[i + nNorms] = aaGuess1[i + 2 * nNorms];
          aaGuess1X[i + 2 * nNorms - 1] = aaGuess1[i + 3 * nNorms - 1];
          aaGuess1Y[i + nNorms] = aaGuess1[i + 2 * nNorms];
          aaGuess1Y[i + 2 * nNorms - 1] = aaGuess1[i + 4 * nNorms - 2];
        }

        MixedLogNormalModelData tmpObj1X = new MixedLogNormalModelData(aaGuess1X, true);
        double[] tmpSigmasX = tmpObj1X.getVolatilities();

        System.out.println("guess: " + tmpSigmasX[0] + "\t" + tmpSigmasX[1]);
        fitter1.doFit();
        aaGuess1 = fitter1.getParams();
        System.out.println("inintial sq: " + fitter1.getInitialSq());

        for (int i = 0; i < nNorms; ++i) {
          aaGuess1X[i] = aaGuess1[i];
          aaGuess1Y[i] = aaGuess1[i + nNorms];
        }
        for (int i = 0; i < nNorms - 1; ++i) {
          aaGuess1X[i + nNorms] = aaGuess1[i + 2 * nNorms];
          aaGuess1X[i + 2 * nNorms - 1] = aaGuess1[i + 3 * nNorms - 1];
          aaGuess1Y[i + nNorms] = aaGuess1[i + 2 * nNorms];
          aaGuess1Y[i + 2 * nNorms - 1] = aaGuess1[i + 4 * nNorms - 2];
        }

        tmpObj1X = new MixedLogNormalModelData(aaGuess1X, true);
        tmpSigmasX = tmpObj1X.getVolatilities();

        if (fitter1.getFinalSq() <= fitter1.getInitialSq() * 1e-10) {
          fitDone = true;
          System.out.println("\n");
          System.out.println("answer: " + tmpSigmasX[0] + "\t" + tmpSigmasX[1]);
          System.out.println("sq: " + fitter1.getFinalSq());
          System.out.println("\n");
          System.out.println("\n");
        } else {
          for (int i = 0; i < nParams; ++i) {
            aaGuess1[i] = 1e-2 + obj.nextDouble();
          }
          fitter1 = new MixedBivariateLogNormalFitter(aaGuess1, xx, yy, time, fwdX, fwdY, nNorms, nDataPtsX, 1);
        }

      }

      for (int i = 0; i < nNorms; ++i) {
        aaGuess1X[i] = aaGuess1[i];
        aaGuess1Y[i] = aaGuess1[i + nNorms];
      }
      for (int i = 0; i < nNorms - 1; ++i) {
        aaGuess1X[i + nNorms] = aaGuess1[i + 2 * nNorms];
        aaGuess1X[i + 2 * nNorms - 1] = aaGuess1[i + 3 * nNorms - 1];
        aaGuess1Y[i + nNorms] = aaGuess1[i + 2 * nNorms];
        aaGuess1Y[i + 2 * nNorms - 1] = aaGuess1[i + 4 * nNorms - 2];
      }

      final MixedLogNormalModelData objAns1X = new MixedLogNormalModelData(aaGuess1X, true);
      final double[] weights = objAns1X.getWeights();
      final double[] sigmasX = objAns1X.getVolatilities();
      final double[] relativePartialForwardsX = objAns1X.getRelativeForwards();

      final MixedLogNormalModelData objAns1Y = new MixedLogNormalModelData(aaGuess1Y, true);
      final double[] sigmasY = objAns1Y.getVolatilities();
      final double[] relativePartialForwardsY = objAns1Y.getRelativeForwards();

      final MixedBivariateLogNormalModelVolatility objZ = new MixedBivariateLogNormalModelVolatility(weights, sigmasX,
          sigmasY, relativePartialForwardsX, relativePartialForwardsY, rhos);

      double[] ansVolsX = new double[100];
      double[] ansVolsY = new double[100];
      double[] ansVolsZ = new double[100];
      for (int i = 0; i < 100; i++) {
        double k = fwdX * (0.1 + 2. * i / 100.);
        final EuropeanVanillaOption option = new EuropeanVanillaOption(k, time, true);
        ansVolsX[i] = volfunc.getVolatility(option, fwdX, objAns1X);
        ansVolsY[i] = volfunc.getVolatility(option, fwdY, objAns1Y);
        ansVolsZ[i] = objZ.getImpliedVolatilityZ(option, fwdZ);
        //   System.out.println(k + "\t" + ansVolsX[i] + "\t" + ansVolsY[i] + "\t" + ansVolsZ[i]);
      }

      final MixedBivariateLogNormalModelVolatility objTrueZ = new MixedBivariateLogNormalModelVolatility(inWeights, inSigmasX,
          inSigmasY, inRelativePartialForwardsX, inRelativePartialForwardsY, rhos);

      double[] trueVolsX = new double[100];
      double[] trueVolsY = new double[100];
      double[] trueVolsZ = new double[100];
      for (int i = 0; i < 100; i++) {
        double k = fwdY * (0.1 + 2. * i / 100.);
        final EuropeanVanillaOption option = new EuropeanVanillaOption(k, time, true);
        trueVolsX[i] = volfunc.getVolatility(option, fwdX, inObjX);
        trueVolsY[i] = volfunc.getVolatility(option, fwdY, inObjY);
        trueVolsZ[i] = objTrueZ.getImpliedVolatilityZ(option, fwdZ);
        //     System.out.println(k + "\t" + trueVolsX[i] + "\t" + trueVolsY[i] + "\t" + trueVolsZ[i]);

      }

      final double[] inSigmasZ = objTrueZ.getSigmasZ();

      for (int i = 0; i < 100; i++) {
        double k = fwdX * (0.1 + 2. * i / 100.);
        assertEquals("Imp Vols of X " + k, ansVolsX[i], trueVolsX[i], Math.abs((inSigmasX[0] + inSigmasX[1]) / 2.) * 1e-6);
        assertEquals("Imp Vols of Y " + k, ansVolsY[i], trueVolsY[i], Math.abs((inSigmasY[0] + inSigmasY[1]) / 2.) * 1e-6);
        assertEquals("Imp Vols of Z " + k, ansVolsZ[i], trueVolsZ[i], Math.abs((inSigmasZ[0] + inSigmasZ[1]) / 2.) * 1e-6);
      }

    }
  }

  @Test
      (enabled = false)
      public void TestNoise() {

    final int nNorms = 2;
    final int nParams = 5 * nNorms - 3;
    final int nDataPts = 14;
    final int nDataPtsX = 7;
    final int nParamsX = 3 * nNorms - 2;
    final int nParamsY = 3 * nNorms - 2;

    final double fwdX = 1.;
    final double fwdY = 1.;
    final double fwdZ = fwdX / fwdY;
    final double time = 1.0;

    double[] xx = new double[nDataPts];
    double[] yy = new double[nDataPts];
    double[] yyNoRand = new double[nDataPts];
    double[] aaGuess1 = new double[nParams];
    double[] aaGuess1X = new double[nParamsX];
    double[] aaGuess1Y = new double[nParamsY];
    double[] rhos = new double[nNorms];

    Random objRand = new Random();
    NormalRandomNumberGenerator objRandNorm = new NormalRandomNumberGenerator(0, 5e-3);

    double[] inRelativePartialForwardsX = {1., 1. };
    double[] inRelativePartialForwardsY = {Math.exp(-0.2), (1. - Math.exp(-0.2) * 0.7) / 0.3 };

    double[] inSigmasX = {0.25, 0.7 };
    double[] inSigmasY = {0.3, 0.5 };

    double[] inWeights = {0.7, 0.3 };

    final MixedLogNormalModelData inObjX = new MixedLogNormalModelData(inWeights, inSigmasX, inRelativePartialForwardsX);
    final MixedLogNormalModelData inObjY = new MixedLogNormalModelData(inWeights, inSigmasY, inRelativePartialForwardsY);

    xx = new double[] {0.5, 0.7, 0.9, 1.0, 1.2, 1.5, 1.8, 0.5, 0.7, 0.9, 1.0, 1.2, 1.5, 1.8 };

    final MixedLogNormalVolatilityFunction volfunc = MixedLogNormalVolatilityFunction.getInstance();
    Arrays.fill(yy, 0.);

    for (int j = 0; j < nDataPtsX; ++j) {
      EuropeanVanillaOption option = new EuropeanVanillaOption(xx[j], time, true);
      double[] tmpRandNorm = objRandNorm.getVector(1);
      yyNoRand[j] = volfunc.getVolatility(option, fwdX, inObjX);
      yy[j] = yyNoRand[j] * (1. + tmpRandNorm[0]);
    }

    for (int j = nDataPtsX; j < nDataPts; ++j) {
      EuropeanVanillaOption option = new EuropeanVanillaOption(xx[j], time, true);
      double[] tmpRandNorm = objRandNorm.getVector(1);
      yyNoRand[j] = volfunc.getVolatility(option, fwdY, inObjY);
      yy[j] = yyNoRand[j] * (1. + tmpRandNorm[0]);
    }

    for (int j = 0; j < nDataPts; ++j) {
      System.out.println(yy[j] + "\t" + yyNoRand[j]);
    }

    rhos = new double[] {0.4, 0.4 };

    final MixedBivariateLogNormalModelVolatility objTrueZ = new MixedBivariateLogNormalModelVolatility(inWeights, inSigmasX,
        inSigmasY, inRelativePartialForwardsX, inRelativePartialForwardsY, rhos);

    //    final double[] inSigmasZ = objTrueZ.getSigmasZ();
    //    final double[] inRelativePartialForwardsZ = objTrueZ.getRelativeForwardsZ();

    System.out.println("true values: " + inSigmasX[0] + "\t" + inSigmasX[1]);
    System.out.println("\n");

    for (int i = 0; i < nParams; ++i) {
      aaGuess1[i] = 1e-2 + objRand.nextDouble();
    }
    MixedBivariateLogNormalFitter fitter1 = new MixedBivariateLogNormalFitter(aaGuess1, xx, yy, time, fwdX, fwdY, nNorms, nDataPtsX, 1);

    boolean fitDone = false;

    while (fitDone == false) {

      for (int i = 0; i < nNorms; ++i) {
        aaGuess1X[i] = aaGuess1[i];
        aaGuess1Y[i] = aaGuess1[i + nNorms];
      }
      for (int i = 0; i < nNorms - 1; ++i) {
        aaGuess1X[i + nNorms] = aaGuess1[i + 2 * nNorms];
        aaGuess1X[i + 2 * nNorms - 1] = aaGuess1[i + 3 * nNorms - 1];
        aaGuess1Y[i + nNorms] = aaGuess1[i + 2 * nNorms];
        aaGuess1Y[i + 2 * nNorms - 1] = aaGuess1[i + 4 * nNorms - 2];
      }

      MixedLogNormalModelData tmpObj1X = new MixedLogNormalModelData(aaGuess1X, true);
      double[] tmpSigmasX = tmpObj1X.getVolatilities();

      System.out.println("guess: " + tmpSigmasX[0] + "\t" + tmpSigmasX[1]);
      fitter1.doFit();
      aaGuess1 = fitter1.getParams();
      System.out.println("inintial sq: " + fitter1.getInitialSq());

      for (int i = 0; i < nNorms; ++i) {
        aaGuess1X[i] = aaGuess1[i];
        aaGuess1Y[i] = aaGuess1[i + nNorms];
      }
      for (int i = 0; i < nNorms - 1; ++i) {
        aaGuess1X[i + nNorms] = aaGuess1[i + 2 * nNorms];
        aaGuess1X[i + 2 * nNorms - 1] = aaGuess1[i + 3 * nNorms - 1];
        aaGuess1Y[i + nNorms] = aaGuess1[i + 2 * nNorms];
        aaGuess1Y[i + 2 * nNorms - 1] = aaGuess1[i + 4 * nNorms - 2];
      }

      tmpObj1X = new MixedLogNormalModelData(aaGuess1X, true);
      tmpSigmasX = tmpObj1X.getVolatilities();

      System.out.println("answer: " + tmpSigmasX[0] + "\t" + tmpSigmasX[1]);
      System.out.println("sq: " + fitter1.getFinalSq());
      System.out.println("\n");

      if (fitter1.getFinalSq() <= fitter1.getInitialSq() * 1e-6) {
        fitDone = true;
      } else {
        for (int i = 0; i < nParams; ++i) {
          aaGuess1[i] = 1e-2 + objRand.nextDouble();
        }
        fitter1 = new MixedBivariateLogNormalFitter(aaGuess1, xx, yy, time, fwdX, fwdY, nNorms, nDataPtsX, 1);
      }

    }

    for (int i = 0; i < nNorms; ++i) {
      aaGuess1X[i] = aaGuess1[i];
      aaGuess1Y[i] = aaGuess1[i + nNorms];
    }
    for (int i = 0; i < nNorms - 1; ++i) {
      aaGuess1X[i + nNorms] = aaGuess1[i + 2 * nNorms];
      aaGuess1X[i + 2 * nNorms - 1] = aaGuess1[i + 3 * nNorms - 1];
      aaGuess1Y[i + nNorms] = aaGuess1[i + 2 * nNorms];
      aaGuess1Y[i + 2 * nNorms - 1] = aaGuess1[i + 4 * nNorms - 2];
    }

    final MixedLogNormalModelData objAns1X = new MixedLogNormalModelData(aaGuess1X, true);
    final double[] weights = objAns1X.getWeights();
    final double[] sigmasX = objAns1X.getVolatilities();
    final double[] relativePartialForwardsX = objAns1X.getRelativeForwards();

    final MixedLogNormalModelData objAns1Y = new MixedLogNormalModelData(aaGuess1Y, true);
    final double[] sigmasY = objAns1Y.getVolatilities();
    final double[] relativePartialForwardsY = objAns1Y.getRelativeForwards();

    final MixedBivariateLogNormalModelVolatility objZ = new MixedBivariateLogNormalModelVolatility(weights, sigmasX,
        sigmasY, relativePartialForwardsX, relativePartialForwardsY, rhos);

    //    final double[] sigmasZ = objZ.getSigmasZ();
    //    final double[] relativePartialForwardsZ = objZ.getRelativeForwardsZ();

    System.out.println("Parameters XY");
    for (int i = 0; i < nNorms; ++i) {
      System.out.println(weights[i] + "\t" + inWeights[i]);
      //   assertEquals("weights ", weights[i], inWeights[i], Math.abs((inWeights[0] + inWeights[1]) / 2.) * 1e-6);
    }
    for (int i = 0; i < nNorms; ++i) {
      System.out.println(sigmasX[i] + "\t" + inSigmasX[i]);
      //    assertEquals("sigmasY ", sigmasX[i], inSigmasX[i], Math.abs((inSigmasX[0] + inSigmasX[1]) / 2.) * 1e-6);
    }
    for (int i = 0; i < nNorms; ++i) {
      System.out.println(sigmasY[i] + "\t" + inSigmasY[i]);
      //    assertEquals("sigmasY ", sigmasY[i], inSigmasY[i], Math.abs((inSigmasY[0] + inSigmasY[1]) / 2.) * 1e-6);
    }
    for (int i = 0; i < nNorms; ++i) {
      System.out.println(relativePartialForwardsX[i] + "\t" + inRelativePartialForwardsX[i]);
      //     assertEquals("relativePartialForwardsX ", relativePartialForwardsX[i], inRelativePartialForwardsX[i], Math.abs((inRelativePartialForwardsX[0] + inRelativePartialForwardsX[1]) / 2.) * 1e-6);
    }
    for (int i = 0; i < nNorms; ++i) {
      System.out.println(relativePartialForwardsY[i] + "\t" + inRelativePartialForwardsY[i]);
      //    assertEquals("relativePartialForwardsY ", relativePartialForwardsY[i], inRelativePartialForwardsY[i], Math.abs((inRelativePartialForwardsY[0] + inRelativePartialForwardsY[1]) / 2.) * 1e-6);
    }
    //
    //    System.out.println("\n");
    //    System.out.println("Parameters Z");
    //
    //    for (int i = 0; i < nNorms; ++i) {
    //      System.out.println(sigmasZ[i] + "\t" + inSigmasZ[i]);
    //      assertEquals("sigmasZ ", sigmasZ[i], inSigmasZ[i], Math.abs((inSigmasZ[0] + inSigmasZ[1]) / 2.) * 1e-6);
    //    }
    //    for (int i = 0; i < nNorms; ++i) {
    //      System.out.println(relativePartialForwardsZ[i] + "\t" + inRelativePartialForwardsZ[i]);
    //      assertEquals("relativePartialForwardsZ ", relativePartialForwardsZ[i], inRelativePartialForwardsZ[i], Math.abs((inRelativePartialForwardsZ[0] + inRelativePartialForwardsZ[1]) / 2.) * 1e-6);
    //    }

    System.out.println("\n");
    System.out.println("Imp Vols XYZ from fitting");

    double[] ansVolsX = new double[100];
    double[] ansVolsY = new double[100];
    double[] ansVolsZ = new double[100];
    for (int i = 0; i < 100; i++) {
      double k = fwdX * (0.1 + 2. * i / 100.);
      final EuropeanVanillaOption option = new EuropeanVanillaOption(k, time, true);
      ansVolsX[i] = volfunc.getVolatility(option, fwdX, objAns1X);
      ansVolsY[i] = volfunc.getVolatility(option, fwdY, objAns1Y);
      ansVolsZ[i] = objZ.getImpliedVolatilityZ(option, fwdZ);
      System.out.println(k + "\t" + ansVolsX[i] + "\t" + ansVolsY[i] + "\t" + ansVolsZ[i]);
    }

    System.out.println("\n");
    System.out.println("True Imp Vols XYZ");

    double[] trueVolsX = new double[100];
    double[] trueVolsY = new double[100];
    double[] trueVolsZ = new double[100];
    for (int i = 0; i < 100; i++) {
      double k = fwdY * (0.1 + 2. * i / 100.);
      final EuropeanVanillaOption option = new EuropeanVanillaOption(k, time, true);
      trueVolsX[i] = volfunc.getVolatility(option, fwdX, inObjX);
      trueVolsY[i] = volfunc.getVolatility(option, fwdY, inObjY);
      trueVolsZ[i] = objTrueZ.getImpliedVolatilityZ(option, fwdZ);
      System.out.println(k + "\t" + trueVolsX[i] + "\t" + trueVolsY[i] + "\t" + trueVolsZ[i]);

    }

  }

  @Test
      (enabled = false)
      public void TestMarketData() {

    final double[] deltas = new double[] {0.15, 0.25 };
    final double[] expiries = new double[] {7. / 365, 14 / 365., 21 / 365., 1 / 12., 3 / 12., 0.5, 0.75, 1, 5 };

    final double spotGBPUSD = 1.5993;
    final double spotEURGBP = 0.83559;
    final double spotEURUSD = 1.3364;

    final double[] fwdRateGBPUSD = new double[] {-0.000070, -0.000122, -0.000182, -0.000259, -0.000659, -0.0012900, -0.0019010, -0.002664, -0.0043820 };
    final double[] fwdRateEURGBP = new double[] {0.000074, 0.000148, 0.000221, 0.000323, 0.00088250, 0.001677, 0.002481, 0.003268, 0.013689 };
    final double[] fwdRateEURUSD = new double[] {0.000062, 0.000132, 0.0002035, 0.00030000, 0.00086550, 0.001638, 0.002328, 0.00298, 0.018482 };

    final double[] atmGBPUSD = new double[] {0.060250000000000005, 0.0604, 0.0604, 0.0609, 0.0639, 0.069025, 0.07295, 0.0751, 0.09445 };
    final double[] atmEURGBP = new double[] {0.06465, 0.06255, 0.06215, 0.06215, 0.06269999999999999, 0.06565, 0.067625, 0.06925, 0.0885 };
    final double[] atmEURUSD = new double[] {0.0885, 0.0809, 0.08085, 0.080275, 0.080525, 0.082825, 0.08504999999999999, 0.086725, 0.09865 };

    final double[][] rrGBPUSD = new double[][] { {-0.001075, -0.0019, -0.002525, -0.0031750000000000003, -0.00865, -0.0139, -0.01565, -0.0169, -0.0191 },
        {-0.00045, -0.00115, -0.0016250000000000001, -0.002075, -0.005675, -0.00895, -0.01, -0.010900000000000002, -0.012549999999999999 } };
    final double[][] rrEURGBP = new double[][] { {0.0018, 0.001325, 0.0008749999999999999, 0.000525, -0.001325, -0.002925, -0.005275, -0.006125, -0.0113 },
        {0.0015, 0.001, 0.000675, 0.0004, -0.0009, -0.0017499999999999998, -0.00345, -0.0040999999999999995, -0.00825 } };
    final double[][] rrEURUSD = new double[][] { {0.000525, -0.00005, -0.0011, -0.0025, -0.00845, -0.013700000000000002, -0.016675, -0.01905, -0.02265 },
        {0.000675, 0.000125, -0.000625, -0.0017000000000000001, -0.005575, -0.00905, -0.0108, -0.012225, -0.014975 } };

    final double[][] buttGBPUSD = new double[][] {
        {0.00315, 0.0028000000000000004, 0.0028499999999999997, 0.0026000000000000003, 0.0040999999999999995, 0.005375, 0.0060750000000000005, 0.006725, 0.00735 },
        {0.0013750000000000001, 0.00125, 0.001275, 0.001225, 0.0020250000000000003, 0.002575, 0.002775, 0.003025, 0.003 } };
    final double[][] buttEURGBP = new double[][] { {0.00305, 0.0027250000000000004, 0.002925, 0.00305, 0.0037, 0.005124999999999999, 0.006375, 0.006875, 0.00865 },
        {0.0014000000000000002, 0.0008, 0.001175, 0.0014000000000000002, 0.0016500000000000002, 0.00215, 0.0027250000000000004, 0.0029, 0.0034000000000000002 } };
    final double[][] buttEURUSD = new double[][] { {0.0026000000000000003, 0.0022500000000000003, 0.002425, 0.0023, 0.004225, 0.005625, 0.00645, 0.0072, 0.006600000000000001 },
        {0.001225, 0.0011250000000000001, 0.001175, 0.001075, 0.0020250000000000003, 0.0026000000000000003, 0.0029, 0.0031750000000000003, 0.002925 } };

    final int nDeltas = deltas.length;
    final int nExpiries = expiries.length;

    final double[] fwdGBPUSD = new double[nExpiries];
    final double[] fwdEURGBP = new double[nExpiries];
    final double[] fwdEURUSD = new double[nExpiries];

    for (int i = 0; i < nExpiries; ++i) {
      fwdGBPUSD[i] = spotGBPUSD * Math.exp(fwdRateGBPUSD[i] * expiries[i]);
      fwdEURGBP[i] = spotEURGBP * Math.exp(fwdRateEURGBP[i] * expiries[i]);
      fwdEURUSD[i] = spotEURUSD * Math.exp(fwdRateEURUSD[i] * expiries[i]);
    }

    double[][] strikeGBPUSD = new double[nExpiries][];
    double[][] volGBPUSD = new double[nExpiries][];
    double[][] strikeEURGBP = new double[nExpiries][];
    double[][] volEURGBP = new double[nExpiries][];
    double[][] strikeEURUSD = new double[nExpiries][];
    double[][] volEURUSD = new double[nExpiries][];

    for (int i = 0; i < nExpiries; i++) {
      final double[] rr1 = new double[nDeltas];
      final double[] s1 = new double[nDeltas];
      final double[] rr2 = new double[nDeltas];
      final double[] s2 = new double[nDeltas];
      final double[] rr3 = new double[nDeltas];
      final double[] s3 = new double[nDeltas];
      for (int j = 0; j < nDeltas; j++) {
        rr1[j] = rrGBPUSD[j][i];
        s1[j] = buttGBPUSD[j][i];
        rr2[j] = rrEURGBP[j][i];
        s2[j] = buttEURGBP[j][i];
        rr3[j] = rrEURUSD[j][i];
        s3[j] = buttEURUSD[j][i];
      }
      final SmileDeltaParameters cal1 = new SmileDeltaParameters(expiries[i], atmGBPUSD[i], deltas, rr1, s1);
      final SmileDeltaParameters cal2 = new SmileDeltaParameters(expiries[i], atmEURGBP[i], deltas, rr2, s2);
      final SmileDeltaParameters cal3 = new SmileDeltaParameters(expiries[i], atmEURUSD[i], deltas, rr3, s3);
      strikeGBPUSD[i] = cal1.getStrike(fwdGBPUSD[i]);
      volGBPUSD[i] = cal1.getVolatility();
      strikeEURGBP[i] = cal2.getStrike(fwdEURGBP[i]);
      volEURGBP[i] = cal2.getVolatility();
      strikeEURUSD[i] = cal3.getStrike(fwdEURUSD[i]);
      volEURUSD[i] = cal3.getVolatility();
    }

    final int nNorms = 2;
    final int nParams = 5 * nNorms - 3;

    final int nDataPtsX = 2 * nDeltas + 1;
    final int nDataPtsY = 2 * nDeltas + 1;
    final int nDataPts = nDataPtsX + nDataPtsY;
    final int nParamsX = 3 * nNorms - 2;
    final int nParamsY = 3 * nNorms - 2;

    double[] xx = new double[nDataPts];
    double[] yy = new double[nDataPts];
    double[] aaGuess = new double[nParams];
    double[] aaGuessX = new double[nParamsX];
    double[] aaGuessY = new double[nParamsY];
    double[] rhos = new double[nNorms];

    Random objRand = new Random();

    final int choiceOfExpiry = 8;
    final double fwdX = fwdEURUSD[choiceOfExpiry];
    final double fwdY = fwdGBPUSD[choiceOfExpiry];
    final double fwdZ = fwdX / fwdY;
    final double time = expiries[choiceOfExpiry];

    for (int j = 0; j < nDataPtsX; ++j) {
      xx[j] = strikeEURUSD[choiceOfExpiry][j];
      xx[j + nDataPtsX] = strikeGBPUSD[choiceOfExpiry][j];
      yy[j] = volEURUSD[choiceOfExpiry][j];
      yy[j + nDataPtsX] = volGBPUSD[choiceOfExpiry][j];
    }

    //   final MixedLogNormalVolatilityFunction volfunc = MixedLogNormalVolatilityFunction.getInstance();

    rhos = new double[] {0.67, 0.69 };

    for (int i = 0; i < nParams; ++i) {
      aaGuess[i] = 1e-2 + objRand.nextDouble();
    }
    MixedBivariateLogNormalFitter fitter1 = new MixedBivariateLogNormalFitter(aaGuess, xx, yy, time, fwdX, fwdY, nNorms, nDataPtsX, 1);

    boolean fitDone = false;

    while (fitDone == false) {

      for (int i = 0; i < nNorms; ++i) {
        aaGuessX[i] = aaGuess[i];
        aaGuessY[i] = aaGuess[i + nNorms];
      }
      for (int i = 0; i < nNorms - 1; ++i) {
        aaGuessX[i + nNorms] = aaGuess[i + 2 * nNorms];
        aaGuessX[i + 2 * nNorms - 1] = aaGuess[i + 3 * nNorms - 1];
        aaGuessY[i + nNorms] = aaGuess[i + 2 * nNorms];
        aaGuessY[i + 2 * nNorms - 1] = aaGuess[i + 4 * nNorms - 2];
      }

      MixedLogNormalModelData tmpObj1X = new MixedLogNormalModelData(aaGuessX, true);
      double[] tmpSigmasX = tmpObj1X.getVolatilities();

      System.out.println("guess: " + tmpSigmasX[0] + "\t" + tmpSigmasX[1]);
      fitter1.doFit();
      aaGuess = fitter1.getParams();
      System.out.println("inintial sq: " + fitter1.getInitialSq());

      for (int i = 0; i < nNorms; ++i) {
        aaGuessX[i] = aaGuess[i];
        aaGuessY[i] = aaGuess[i + nNorms];
      }
      for (int i = 0; i < nNorms - 1; ++i) {
        aaGuessX[i + nNorms] = aaGuess[i + 2 * nNorms];
        aaGuessX[i + 2 * nNorms - 1] = aaGuess[i + 3 * nNorms - 1];
        aaGuessY[i + nNorms] = aaGuess[i + 2 * nNorms];
        aaGuessY[i + 2 * nNorms - 1] = aaGuess[i + 4 * nNorms - 2];
      }

      tmpObj1X = new MixedLogNormalModelData(aaGuessX, true);
      tmpSigmasX = tmpObj1X.getVolatilities();

      System.out.println("answer: " + tmpSigmasX[0] + "\t" + tmpSigmasX[1]);
      System.out.println("sq: " + fitter1.getFinalSq());
      System.out.println("\n");

      if (fitter1.getFinalSq() <= fitter1.getInitialSq() * 1e-10) {
        fitDone = true;
      } else {
        for (int i = 0; i < nParams; ++i) {
          aaGuess[i] = 1e-2 + objRand.nextDouble();
        }
        fitter1 = new MixedBivariateLogNormalFitter(aaGuess, xx, yy, time, fwdX, fwdY, nNorms, nDataPtsX, 1);
      }

    }

    for (int i = 0; i < nNorms; ++i) {
      aaGuessX[i] = aaGuess[i];
      aaGuessY[i] = aaGuess[i + nNorms];
    }
    for (int i = 0; i < nNorms - 1; ++i) {
      aaGuessX[i + nNorms] = aaGuess[i + 2 * nNorms];
      aaGuessX[i + 2 * nNorms - 1] = aaGuess[i + 3 * nNorms - 1];
      aaGuessY[i + nNorms] = aaGuess[i + 2 * nNorms];
      aaGuessY[i + 2 * nNorms - 1] = aaGuess[i + 4 * nNorms - 2];
    }

    final MixedLogNormalModelData objAns1X = new MixedLogNormalModelData(aaGuessX, true);
    final double[] weights = objAns1X.getWeights();
    final double[] sigmasX = objAns1X.getVolatilities();
    final double[] relativePartialForwardsX = objAns1X.getRelativeForwards();

    final MixedLogNormalModelData objAns1Y = new MixedLogNormalModelData(aaGuessY, true);
    final double[] sigmasY = objAns1Y.getVolatilities();
    final double[] relativePartialForwardsY = objAns1Y.getRelativeForwards();

    final MixedBivariateLogNormalModelVolatility objZ = new MixedBivariateLogNormalModelVolatility(weights, sigmasX,
        sigmasY, relativePartialForwardsX, relativePartialForwardsY, rhos);

    final double[] sigmasZ = objZ.getSigmasZ();
    final double[] relativePartialForwardsZ = objZ.getRelativeForwardsZ();

    System.out.println("Parameters XY(weights, sigmasX, sigmasY, relfwdX, relrwdY)");
    for (int i = 0; i < nNorms; ++i) {
      System.out.println(weights[i]);
    }
    for (int i = 0; i < nNorms; ++i) {
      System.out.println(sigmasX[i]);
    }
    for (int i = 0; i < nNorms; ++i) {
      System.out.println(sigmasY[i]);
    }
    for (int i = 0; i < nNorms; ++i) {
      System.out.println(relativePartialForwardsX[i]);
    }
    for (int i = 0; i < nNorms; ++i) {
      System.out.println(relativePartialForwardsY[i]);
    }

    System.out.println("\n");
    System.out.println("Parameters Z");

    for (int i = 0; i < nNorms; ++i) {
      System.out.println(sigmasZ[i]);
    }
    for (int i = 0; i < nNorms; ++i) {
      System.out.println(relativePartialForwardsZ[i]);
    }

    System.out.println("\n");
    System.out.println(fwdZ);

    System.out.println("\n");
    System.out.println("Imp Vols XYZ from fitting");

    //  double[] ansVolsX = new double[100];
    //  double[] ansVolsY = new double[100];
    double[] ansVolsZ = new double[100];
    for (int i = 0; i < 100; i++) {
      double k = fwdZ * (0.98 + .4 * i / 1000.);
      final EuropeanVanillaOption option = new EuropeanVanillaOption(k, time, true);
      //ansVolsX[i] = volfunc.getVolatility(option, fwdX, objAns1X);
      // ansVolsY[i] = volfunc.getVolatility(option, fwdY, objAns1Y);
      ansVolsZ[i] = objZ.getImpliedVolatilityZ(option, fwdZ);
      // System.out.println(k + "\t" + ansVolsX[i] + "\t" + ansVolsY[i]);
      System.out.println(k + "\t" + ansVolsZ[i]);
    }

    System.out.println("\n");
    System.out.println("EURUSD(X)");
    for (int i = 0; i < nDataPtsX; ++i) {
      System.out.println(strikeEURUSD[choiceOfExpiry][i] + "\t" + volEURUSD[choiceOfExpiry][i]);
    }
    System.out.println("\n");
    System.out.println("GBPUSD(Y)");
    for (int i = 0; i < nDataPtsX; ++i) {
      System.out.println(strikeGBPUSD[choiceOfExpiry][i] + "\t" + volGBPUSD[choiceOfExpiry][i]);
    }
    System.out.println("\n");
    System.out.println("EURGBP(Z)");
    for (int i = 0; i < nDataPtsX; ++i) {
      System.out.println(strikeEURGBP[choiceOfExpiry][i] + "\t" + volEURGBP[choiceOfExpiry][i]);
    }

    System.out.println("\n");
    System.out.println("fwdX: " + "\t" + fwdX);
    System.out.println("fwdY: " + "\t" + fwdY);
    System.out.println("time: " + "\t" + time);

  }

  @Test
      (enabled = false)
      public void TestXYfitterZfitter() {

    final double[] deltas = new double[] {0.15, 0.25 };
    final double[] expiries = new double[] {7. / 365, 14 / 365., 21 / 365., 1 / 12., 3 / 12., 0.5, 0.75, 1, 5 };

    final double spotGBPUSD = 1.5993;
    final double spotEURGBP = 0.83559;
    final double spotEURUSD = 1.3364;

    final double[] fwdRateGBPUSD = new double[] {-0.000070, -0.000122, -0.000182, -0.000259, -0.000659, -0.0012900, -0.0019010, -0.002664, -0.0043820 };
    final double[] fwdRateEURGBP = new double[] {0.000074, 0.000148, 0.000221, 0.000323, 0.00088250, 0.001677, 0.002481, 0.003268, 0.013689 };
    final double[] fwdRateEURUSD = new double[] {0.000062, 0.000132, 0.0002035, 0.00030000, 0.00086550, 0.001638, 0.002328, 0.00298, 0.018482 };

    final double[] atmGBPUSD = new double[] {0.060250000000000005, 0.0604, 0.0604, 0.0609, 0.0639, 0.069025, 0.07295, 0.0751, 0.09445 };
    final double[] atmEURGBP = new double[] {0.06465, 0.06255, 0.06215, 0.06215, 0.06269999999999999, 0.06565, 0.067625, 0.06925, 0.0885 };
    final double[] atmEURUSD = new double[] {0.0885, 0.0809, 0.08085, 0.080275, 0.080525, 0.082825, 0.08504999999999999, 0.086725, 0.09865 };

    final double[][] rrGBPUSD = new double[][] { {-0.001075, -0.0019, -0.002525, -0.0031750000000000003, -0.00865, -0.0139, -0.01565, -0.0169, -0.0191 },
        {-0.00045, -0.00115, -0.0016250000000000001, -0.002075, -0.005675, -0.00895, -0.01, -0.010900000000000002, -0.012549999999999999 } };
    final double[][] rrEURGBP = new double[][] { {0.0018, 0.001325, 0.0008749999999999999, 0.000525, -0.001325, -0.002925, -0.005275, -0.006125, -0.0113 },
        {0.0015, 0.001, 0.000675, 0.0004, -0.0009, -0.0017499999999999998, -0.00345, -0.0040999999999999995, -0.00825 } };
    final double[][] rrEURUSD = new double[][] { {0.000525, -0.00005, -0.0011, -0.0025, -0.00845, -0.013700000000000002, -0.016675, -0.01905, -0.02265 },
        {0.000675, 0.000125, -0.000625, -0.0017000000000000001, -0.005575, -0.00905, -0.0108, -0.012225, -0.014975 } };

    final double[][] buttGBPUSD = new double[][] {
        {0.00315, 0.0028000000000000004, 0.0028499999999999997, 0.0026000000000000003, 0.0040999999999999995, 0.005375, 0.0060750000000000005, 0.006725, 0.00735 },
        {0.0013750000000000001, 0.00125, 0.001275, 0.001225, 0.0020250000000000003, 0.002575, 0.002775, 0.003025, 0.003 } };
    final double[][] buttEURGBP = new double[][] { {0.00305, 0.0027250000000000004, 0.002925, 0.00305, 0.0037, 0.005124999999999999, 0.006375, 0.006875, 0.00865 },
        {0.0014000000000000002, 0.0008, 0.001175, 0.0014000000000000002, 0.0016500000000000002, 0.00215, 0.0027250000000000004, 0.0029, 0.0034000000000000002 } };
    final double[][] buttEURUSD = new double[][] { {0.0026000000000000003, 0.0022500000000000003, 0.002425, 0.0023, 0.004225, 0.005625, 0.00645, 0.0072, 0.006600000000000001 },
        {0.001225, 0.0011250000000000001, 0.001175, 0.001075, 0.0020250000000000003, 0.0026000000000000003, 0.0029, 0.0031750000000000003, 0.002925 } };

    final int nDeltas = deltas.length;
    final int nExpiries = expiries.length;

    final double[] fwdGBPUSD = new double[nExpiries];
    final double[] fwdEURGBP = new double[nExpiries];
    final double[] fwdEURUSD = new double[nExpiries];

    for (int i = 0; i < nExpiries; ++i) {
      fwdGBPUSD[i] = spotGBPUSD * Math.exp(fwdRateGBPUSD[i] * expiries[i]);
      fwdEURGBP[i] = spotEURGBP * Math.exp(fwdRateEURGBP[i] * expiries[i]);
      fwdEURUSD[i] = spotEURUSD * Math.exp(fwdRateEURUSD[i] * expiries[i]);
    }

    double[][] strikeGBPUSD = new double[nExpiries][];
    double[][] volGBPUSD = new double[nExpiries][];
    double[][] strikeEURGBP = new double[nExpiries][];
    double[][] volEURGBP = new double[nExpiries][];
    double[][] strikeEURUSD = new double[nExpiries][];
    double[][] volEURUSD = new double[nExpiries][];

    for (int i = 0; i < nExpiries; i++) {
      final double[] rr1 = new double[nDeltas];
      final double[] s1 = new double[nDeltas];
      final double[] rr2 = new double[nDeltas];
      final double[] s2 = new double[nDeltas];
      final double[] rr3 = new double[nDeltas];
      final double[] s3 = new double[nDeltas];
      for (int j = 0; j < nDeltas; j++) {
        rr1[j] = rrGBPUSD[j][i];
        s1[j] = buttGBPUSD[j][i];
        rr2[j] = rrEURGBP[j][i];
        s2[j] = buttEURGBP[j][i];
        rr3[j] = rrEURUSD[j][i];
        s3[j] = buttEURUSD[j][i];
      }
      final SmileDeltaParameters cal1 = new SmileDeltaParameters(expiries[i], atmGBPUSD[i], deltas, rr1, s1);
      final SmileDeltaParameters cal2 = new SmileDeltaParameters(expiries[i], atmEURGBP[i], deltas, rr2, s2);
      final SmileDeltaParameters cal3 = new SmileDeltaParameters(expiries[i], atmEURUSD[i], deltas, rr3, s3);
      strikeGBPUSD[i] = cal1.getStrike(fwdGBPUSD[i]);
      volGBPUSD[i] = cal1.getVolatility();
      strikeEURGBP[i] = cal2.getStrike(fwdEURGBP[i]);
      volEURGBP[i] = cal2.getVolatility();
      strikeEURUSD[i] = cal3.getStrike(fwdEURUSD[i]);
      volEURUSD[i] = cal3.getVolatility();
    }

    final int nNorms = 2;
    final int nParams = 5 * nNorms - 3;

    final int nDataPtsX = 2 * nDeltas + 1;
    final int nDataPtsY = 2 * nDeltas + 1;
    final int nDataPts = nDataPtsX + nDataPtsY;
    final int nParamsX = 3 * nNorms - 2;
    final int nParamsY = 3 * nNorms - 2;

    double[] xx = new double[nDataPts];
    double[] yy = new double[nDataPts];
    double[] aaGuess = new double[nParams];
    double[] aaGuessX = new double[nParamsX];
    double[] aaGuessY = new double[nParamsY];
    double[] rhosGuess = new double[nNorms];

    Random objRand = new Random();

    final int choiceOfExpiry = 3;
    final double fwdX = fwdEURUSD[choiceOfExpiry];
    final double fwdY = fwdGBPUSD[choiceOfExpiry];
    final double fwdZ = fwdX / fwdY;
    final double time = expiries[choiceOfExpiry];

    for (int j = 0; j < nDataPtsX; ++j) {
      xx[j] = strikeEURUSD[choiceOfExpiry][j];
      xx[j + nDataPtsX] = strikeGBPUSD[choiceOfExpiry][j];
      yy[j] = volEURUSD[choiceOfExpiry][j];
      yy[j + nDataPtsX] = volGBPUSD[choiceOfExpiry][j];
    }

    final MixedLogNormalVolatilityFunction volfunc = MixedLogNormalVolatilityFunction.getInstance();

    for (int i = 0; i < nParams; ++i) {
      aaGuess[i] = (choiceOfExpiry + 1.) * (1e-2 + objRand.nextDouble());
    }
    final double aaGuessFactor = choiceOfExpiry + 1.;
    MixedBivariateLogNormalFitter fitter1 = new MixedBivariateLogNormalFitter(aaGuess, xx, yy, time, fwdX, fwdY, nNorms, nDataPtsX, aaGuessFactor);

    final double prec = 1e-5; // Precision should be chosen depending on maturities

    boolean fitDone = false;
    int counter = 0;

    while (fitDone == false) {

      ++counter;

      for (int i = 0; i < nNorms; ++i) {
        aaGuessX[i] = aaGuess[i];
        aaGuessY[i] = aaGuess[i + nNorms];
      }
      for (int i = 0; i < nNorms - 1; ++i) {
        aaGuessX[i + nNorms] = aaGuess[i + 2 * nNorms];
        aaGuessX[i + 2 * nNorms - 1] = aaGuess[i + 3 * nNorms - 1];
        aaGuessY[i + nNorms] = aaGuess[i + 2 * nNorms];
        aaGuessY[i + 2 * nNorms - 1] = aaGuess[i + 4 * nNorms - 2];
      }

      fitter1.doFit();
      aaGuess = fitter1.getParams();
      System.out.println("XYinintial sq: " + fitter1.getInitialSq());

      for (int i = 0; i < nNorms; ++i) {
        aaGuessX[i] = aaGuess[i];
        aaGuessY[i] = aaGuess[i + nNorms];
      }
      for (int i = 0; i < nNorms - 1; ++i) {
        aaGuessX[i + nNorms] = aaGuess[i + 2 * nNorms];
        aaGuessX[i + 2 * nNorms - 1] = aaGuess[i + 3 * nNorms - 1];
        aaGuessY[i + nNorms] = aaGuess[i + 2 * nNorms];
        aaGuessY[i + 2 * nNorms - 1] = aaGuess[i + 4 * nNorms - 2];
      }

      System.out.println("XYsq: " + fitter1.getFinalSq());
      System.out.println("\n");

      if (fitter1.getFinalSq() <= prec) {
        fitDone = true;
        System.out.println("XY Fitting Done");
      } else {
        for (int i = 0; i < nParams; ++i) {
          aaGuess[i] = (choiceOfExpiry + 1.) * (1e-2 + objRand.nextDouble());
        }
        fitter1 = new MixedBivariateLogNormalFitter(aaGuess, xx, yy, time, fwdX, fwdY, nNorms, nDataPtsX, 1);
      }

      ArgumentChecker.isTrue(counter < 500, "Reduce the precision.");
    }

    for (int i = 0; i < nNorms; ++i) {
      aaGuessX[i] = aaGuess[i];
      aaGuessY[i] = aaGuess[i + nNorms];
    }
    for (int i = 0; i < nNorms - 1; ++i) {
      aaGuessX[i + nNorms] = aaGuess[i + 2 * nNorms];
      aaGuessX[i + 2 * nNorms - 1] = aaGuess[i + 3 * nNorms - 1];
      aaGuessY[i + nNorms] = aaGuess[i + 2 * nNorms];
      aaGuessY[i + 2 * nNorms - 1] = aaGuess[i + 4 * nNorms - 2];
    }

    final MixedLogNormalModelData objAns1X = new MixedLogNormalModelData(aaGuessX, true);
    final double[] weights = objAns1X.getWeights();
    final double[] sigmasX = objAns1X.getVolatilities();
    final double[] relativePartialForwardsX = objAns1X.getRelativeForwards();

    final MixedLogNormalModelData objAns1Y = new MixedLogNormalModelData(aaGuessY, true);
    final double[] sigmasY = objAns1Y.getVolatilities();
    final double[] relativePartialForwardsY = objAns1Y.getRelativeForwards();

    final Random randObj = new Random();

    for (int i = 0; i < nNorms; ++i) {
      rhosGuess[i] = 1. - randObj.nextDouble();
    }

    MixedBivariateLogNormalCorrelationFinder fitter = new MixedBivariateLogNormalCorrelationFinder(rhosGuess, strikeEURGBP[choiceOfExpiry], volEURGBP[choiceOfExpiry], time, weights, sigmasX, sigmasY,
        relativePartialForwardsX,
        relativePartialForwardsY, fwdX, fwdY);

    boolean fitRhoDone = false;
    int counterRho = 0;

    while (fitRhoDone == false) {
      ++counterRho;

      fitter.doFit();
      rhosGuess = fitter.getParams();
      System.out.println("\n");
      System.out.println("inintial sq: " + fitter.getInitialSq());

      System.out.println("final sq: " + fitter.getFinalSq());
      System.out.println("\n");

      if (fitter.getFinalSq() <= prec) {
        fitRhoDone = true;
        System.out.println("Rho Fitting Done");
      } else {
        for (int i = 0; i < nNorms; ++i) {
          rhosGuess[i] = 1. - randObj.nextDouble();
        }
        fitter = new MixedBivariateLogNormalCorrelationFinder(rhosGuess, strikeEURGBP[choiceOfExpiry], volEURGBP[choiceOfExpiry], time, weights, sigmasX, sigmasY, relativePartialForwardsX,
            relativePartialForwardsY, fwdX, fwdY);
      }

      ArgumentChecker.isTrue(counterRho < 500, "Too many inerations for rho. Start with new guess parameters.");
    }

    System.out.println("\n");

    rhosGuess = fitter.getParams();

    final MixedBivariateLogNormalModelVolatility objZ = new MixedBivariateLogNormalModelVolatility(weights, sigmasX,
        sigmasY, relativePartialForwardsX, relativePartialForwardsY, rhosGuess);

    final double[] sigmasZ = objZ.getSigmasZ();
    final double[] relativePartialForwardsZ = objZ.getRelativeForwardsZ();

    System.out.println("Parameters XY(weights, sigmasX, sigmasY, relfwdX, relrwdY)");
    for (int i = 0; i < nNorms; ++i) {
      System.out.println(weights[i]);
    }
    for (int i = 0; i < nNorms; ++i) {
      System.out.println(sigmasX[i]);
    }
    for (int i = 0; i < nNorms; ++i) {
      System.out.println(sigmasY[i]);
    }
    for (int i = 0; i < nNorms; ++i) {
      System.out.println(relativePartialForwardsX[i]);
    }
    for (int i = 0; i < nNorms; ++i) {
      System.out.println(relativePartialForwardsY[i]);
    }

    System.out.println("\n");
    System.out.println("fwdX: " + "\t" + fwdX);
    System.out.println("fwdY: " + "\t" + fwdY);

    System.out.println("\n");
    System.out.println("rhos");
    for (int i = 0; i < nNorms; ++i) {
      System.out.println(rhosGuess[i]);
    }

    System.out.println("\n");
    System.out.println("Parameters Z");

    for (int i = 0; i < nNorms; ++i) {
      System.out.println(sigmasZ[i]);
    }
    for (int i = 0; i < nNorms; ++i) {
      System.out.println(relativePartialForwardsZ[i]);
    }

    System.out.println("\n");
    System.out.println("fwdZ: " + "\t" + fwdZ);

    System.out.println("\n");
    System.out.println("Imp Vols Z from fitting");

    double[] ansVolsX = new double[100];
    double[] ansVolsY = new double[100];
    double[] ansVolsZ = new double[100];
    for (int i = 0; i < 100; i++) {
      double k = fwdZ * (0.9 + .2 * i / 100.); //The range should be appropriately chosen depending on expiry
      final EuropeanVanillaOption option = new EuropeanVanillaOption(k, time, true);
      ansVolsZ[i] = objZ.getImpliedVolatilityZ(option, fwdZ);
      System.out.println(k + "\t" + ansVolsZ[i]);
    }

    System.out.println("\n");
    System.out.println("Imp Vols X from fitting");
    for (int i = 0; i < 100; i++) {
      double k = fwdX * (0.9 + .2 * i / 100.); //The range should be appropriately chosen depending on expiry
      final EuropeanVanillaOption option = new EuropeanVanillaOption(k, time, true);
      ansVolsX[i] = volfunc.getVolatility(option, fwdX, objAns1X);
      System.out.println(k + "\t" + ansVolsX[i]);
    }

    System.out.println("\n");
    System.out.println("Imp Vols Y from fitting");
    for (int i = 0; i < 100; i++) {
      double k = fwdY * (0.9 + .2 * i / 100.); //The range should be appropriately chosen depending on expiry
      final EuropeanVanillaOption option = new EuropeanVanillaOption(k, time, true);
      ansVolsY[i] = volfunc.getVolatility(option, fwdY, objAns1Y);
      System.out.println(k + "\t" + ansVolsY[i]);
    }

    System.out.println("\n");
    System.out.println("EURUSD(X)");
    for (int i = 0; i < nDataPtsX; ++i) {
      System.out.println(strikeEURUSD[choiceOfExpiry][i] + "\t" + volEURUSD[choiceOfExpiry][i]);
    }
    System.out.println("\n");
    System.out.println("GBPUSD(Y)");
    for (int i = 0; i < nDataPtsX; ++i) {
      System.out.println(strikeGBPUSD[choiceOfExpiry][i] + "\t" + volGBPUSD[choiceOfExpiry][i]);
    }
    System.out.println("\n");
    System.out.println("EURGBP(Z)");
    for (int i = 0; i < nDataPtsX; ++i) {
      System.out.println(strikeEURGBP[choiceOfExpiry][i] + "\t" + volEURGBP[choiceOfExpiry][i]);
    }

    System.out.println("\n");

    System.out.println("time: " + "\t" + time);

  }

  @Test
      (enabled = false)
      public void CheckingModelParams() {

    final int nNorms = 2;
    final int nDataPts = 14;
    final int nParamsX = 3 * nNorms - 2;
    final int nParamsY = 3 * nNorms - 2;

    double[] yy = new double[nDataPts];
    double[] params = {0.03411550042789113, 0.0483524833777188, 0.2580039977762099, 0.4351726013225261, 0.8361207722192964, 0.7153215152466372, 0.7286439693179121 };
    double[] paramsX = new double[nParamsX];
    double[] paramsY = new double[nParamsY];

    double[] relativePartialForwardsX = new double[nNorms];
    double[] relativePartialForwardsY = new double[nNorms];

    double[] sigmasX = new double[nNorms];
    double[] sigmasY = new double[nNorms];

    double[] weights = new double[nNorms];

    for (int i = 0; i < nNorms; ++i) {
      paramsX[i] = params[i];
      paramsY[i] = params[i + nNorms];
    }
    for (int i = 0; i < nNorms - 1; ++i) {
      paramsX[i + nNorms] = params[i + 2 * nNorms];
      paramsX[i + 2 * nNorms - 1] = params[i + 3 * nNorms - 1];
      paramsY[i + nNorms] = params[i + 2 * nNorms];
      paramsY[i + 2 * nNorms - 1] = params[i + 4 * nNorms - 2];
    }

    final MixedLogNormalModelData inObjX = new MixedLogNormalModelData(paramsX, true);
    final MixedLogNormalModelData inObjY = new MixedLogNormalModelData(paramsY, true);

    relativePartialForwardsX = inObjX.getRelativeForwards();
    sigmasX = inObjX.getVolatilities();

    relativePartialForwardsY = inObjY.getRelativeForwards();
    sigmasY = inObjY.getVolatilities();

    weights = inObjX.getWeights();

    Arrays.fill(yy, 0.);

    System.out.println("sigmasX: " + "\t" + sigmasX[0] + "\t" + sigmasX[1]);
    System.out.println("sigmasY: " + "\t" + sigmasY[0] + "\t" + sigmasY[1]);
    System.out.println("weights: " + "\t" + weights[0] + "\t" + weights[1]);
    System.out.println("relativePartialForwardsX: " + "\t" + relativePartialForwardsX[0] + "\t" + relativePartialForwardsX[1]);
    System.out.println("relativePartialForwardsY: " + "\t" + relativePartialForwardsY[0] + "\t" + relativePartialForwardsY[1]);
    System.out.println("\n");

  }

  private double getDensity(final double[] ws, final double[] sigs, final double[] rpfs, final double iValue) {
    double res = 0.;
    final int nNorms = ws.length;

    for (int i = 0; i < nNorms; ++i) {
      res += ws[i] * Math.exp(-0.5 * (Math.log(iValue) - (Math.log(rpfs[i]) - 0.5 * sigs[i] * sigs[i])) * (Math.log(iValue) - (Math.log(rpfs[i]) - 0.5 * sigs[i] * sigs[i])) / sigs[i] / sigs[i]);
    }

    return res;
  }

  private double getPrice(final EuropeanVanillaOption option, final double forward, final MixedLogNormalModelData data) {
    final double[] w = data.getWeights();
    final double[] sigma = data.getVolatilities();
    final double[] rf = data.getRelativeForwards();
    final int n = w.length;
    final double t = option.getTimeToExpiry();
    final double k = option.getStrike();
    final double kStar = k / forward;
    final boolean isCall = option.isCall();

    double sum = 0;
    for (int i = 0; i < n; i++) {
      sum += w[i] * BlackFormulaRepository.price(rf[i], kStar, t, sigma[i], isCall);
    }
    return forward * sum;
  }

  private double getDualGamma(final EuropeanVanillaOption option, final double forward, final MixedLogNormalModelData data) {
    final double[] w = data.getWeights();
    final double[] sigma = data.getVolatilities();
    final double[] rf = data.getRelativeForwards();
    final int n = w.length;
    final double t = option.getTimeToExpiry();
    final double k = option.getStrike();
    final double kStar = k / forward;
    double sum = 0;
    for (int i = 0; i < n; i++) {
      sum += w[i] * BlackFormulaRepository.dualGamma(rf[i], kStar, t, sigma[i]);
    }
    return forward * sum;
  }

  private double getDualGammaZ(final EuropeanVanillaOption option, final double forward, final MixedBivariateLogNormalModelVolatility dataZ) {
    final double[] w = dataZ.getOrderedWeights();
    final double[] sigma = dataZ.getSigmasZ();
    final double[] rf = dataZ.getRelativeForwardsZ();
    final int n = w.length;
    final double t = option.getTimeToExpiry();
    final double k = option.getStrike();
    final double kStar = k / forward;
    double sum = 0;
    for (int i = 0; i < n; i++) {
      sum += w[i] * BlackFormulaRepository.dualGamma(rf[i], kStar, t, sigma[i]);
    }
    return forward * sum;
  }

  private double[] getFunctionValues(final double[] params, final double[] dataStrikes, final double forwardX, final double forwardY, final double timeToExpiry, final int nNormals, final int nData,
      final int nDataX) {

    final MixedLogNormalVolatilityFunction volfunc = MixedLogNormalVolatilityFunction.getInstance();

    final int dof = 3 * nNormals - 2;

    double[] paramsX = new double[dof];
    double[] paramsY = new double[dof];

    for (int i = 0; i < nNormals; ++i) {
      paramsX[i] = params[i];
    }
    for (int i = 0; i < nNormals - 1; ++i) {
      paramsX[i + nNormals] = params[i + 2 * nNormals];
      paramsX[i + 2 * nNormals - 1] = params[i + 3 * nNormals - 1];
    }

    for (int i = 0; i < nNormals; ++i) {
      paramsY[i] = params[i + nNormals];
    }
    for (int i = 0; i < nNormals - 1; ++i) {
      paramsY[i + nNormals] = params[i + 2 * nNormals];
      paramsY[i + 2 * nNormals - 1] = params[i + 4 * nNormals - 2];
    }

    MixedLogNormalModelData dataX = new MixedLogNormalModelData(paramsX, true);
    MixedLogNormalModelData dataY = new MixedLogNormalModelData(paramsY, true);

    double[] res = new double[nData];
    Arrays.fill(res, 0.);

    for (int j = 0; j < nDataX; ++j) {
      EuropeanVanillaOption option = new EuropeanVanillaOption(dataStrikes[j], timeToExpiry, true);
      res[j] = volfunc.getVolatility(option, forwardX, dataX);
    }

    for (int j = nDataX; j < nData; ++j) {
      EuropeanVanillaOption option = new EuropeanVanillaOption(dataStrikes[j], timeToExpiry, true);
      res[j] = volfunc.getVolatility(option, forwardY, dataY);
    }

    return res;
  }

}
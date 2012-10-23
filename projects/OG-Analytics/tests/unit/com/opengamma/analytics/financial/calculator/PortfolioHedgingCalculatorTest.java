/**
 * Copyright (C) 2012 - present by OpenGamma Inc. and the OpenGamma group of companies
 * 
 * Please see distribution for license.
 */
package com.opengamma.analytics.financial.calculator;

import static org.testng.AssertJUnit.assertTrue;
import static org.testng.internal.junit.ArrayAsserts.assertArrayEquals;

import org.testng.annotations.Test;

import com.opengamma.analytics.financial.curve.sensitivity.ParameterSensitivity;
import com.opengamma.analytics.financial.forex.method.FXMatrix;
import com.opengamma.analytics.math.matrix.CommonsMatrixAlgebra;
import com.opengamma.analytics.math.matrix.DoubleMatrix1D;
import com.opengamma.analytics.math.matrix.DoubleMatrix2D;
import com.opengamma.util.money.Currency;
import com.opengamma.util.tuple.ObjectsPair;
import com.opengamma.util.tuple.Pair;

/**
 * Tests the portfolio hedging calculator, with simplified examples and then with full scale data.
 */
public class PortfolioHedgingCalculatorTest {

  private static final Currency EUR = Currency.EUR;
  private static final Currency USD = Currency.USD;
  private static final String NAME_1 = "Dsc";
  private static final String NAME_2 = "Fwd";
  private static final Pair<String, Currency> NAME_1_EUR = new ObjectsPair<String, Currency>(NAME_1, EUR);
  private static final Pair<String, Currency> NAME_1_USD = new ObjectsPair<String, Currency>(NAME_1, USD);
  private static final Pair<String, Currency> NAME_2_EUR = new ObjectsPair<String, Currency>(NAME_2, EUR);
  private static final double EUR_USD = 1.25;
  private static final FXMatrix FX_MATRIX = new FXMatrix(EUR, USD, EUR_USD);

  private static final double[] SENSI_1 = {1.0, 2.0, 3.0, 0.4};
  private static final int NB_SENSI_1 = SENSI_1.length;
  private static final double[] SENSI_2 = {0.5, 1.0, 0.5, 1.0, 0.5};
  private static final int NB_SENSI_2 = SENSI_2.length;

  private static final CommonsMatrixAlgebra MATRIX = new CommonsMatrixAlgebra();

  private static final double TOLERANCE = 1.0E-8;

  @Test
  /**
   * Test the hedging portfolio with reference instruments equal to the curve construction instruments. 
   */
  public void exactSolution() {
    double[] sensiOpposite = new double[NB_SENSI_1];
    for (int loopnode = 0; loopnode < NB_SENSI_1; loopnode++) {
      sensiOpposite[loopnode] = -SENSI_1[loopnode];
    }
    ParameterSensitivity ps = new ParameterSensitivity();
    ps = ps.plus(NAME_1_EUR, new DoubleMatrix1D(SENSI_1));
    ParameterSensitivity[] rs = new ParameterSensitivity[NB_SENSI_1];
    for (int loopnode = 0; loopnode < NB_SENSI_1; loopnode++) {
      rs[loopnode] = new ParameterSensitivity();
      double[] r = new double[NB_SENSI_1];
      r[loopnode] = 1.0;
      rs[loopnode] = rs[loopnode].plus(NAME_1_EUR, new DoubleMatrix1D(r));
    }
    // Unit weights
    DoubleMatrix2D w1 = new DoubleMatrix2D(NB_SENSI_1, NB_SENSI_1);
    for (int loopnode = 0; loopnode < NB_SENSI_1; loopnode++) {
      w1.getData()[loopnode][loopnode] = 1.0;
    }
    double[] hedging1 = PortfolioHedgingCalculator.hedgeQuantity(ps, rs, w1, FX_MATRIX);
    assertArrayEquals("PortfolioHedgingCalculator: ", sensiOpposite, hedging1, TOLERANCE);
    // Non-uniform diagonal weights
    DoubleMatrix2D w2 = new DoubleMatrix2D(NB_SENSI_1, NB_SENSI_1);
    for (int loopnode = 0; loopnode < NB_SENSI_1; loopnode++) {
      w2.getData()[loopnode][loopnode] = loopnode + 1.0;
    }
    double[] hedging2 = PortfolioHedgingCalculator.hedgeQuantity(ps, rs, w2, FX_MATRIX);
    assertArrayEquals("PortfolioHedgingCalculator: ", sensiOpposite, hedging2, TOLERANCE);
    // Tri-diagonal weights
    double[][] w3Array = { {1.0, 0.5, 0, 0}, {0.5, 1.0, 0.5, 0}, {0, 0.5, 1.0, 0.5}, {0, 0, 0.5, 1.0}};
    DoubleMatrix2D w3 = new DoubleMatrix2D(w3Array);
    double[] hedging3 = PortfolioHedgingCalculator.hedgeQuantity(ps, rs, w3, FX_MATRIX);
    assertArrayEquals("PortfolioHedgingCalculator: ", sensiOpposite, hedging3, TOLERANCE);
  }

  @Test
  /**
   * Test the hedging portfolio. The answer is perturbed to check that it is at a minimum. 
   */
  public void checkMin() {
    int nbSensi = NB_SENSI_1 + NB_SENSI_2;
    ParameterSensitivity ps = new ParameterSensitivity();
    ps = ps.plus(NAME_1_EUR, new DoubleMatrix1D(SENSI_1));
    ps = ps.plus(NAME_1_USD, new DoubleMatrix1D(SENSI_1));
    ps = ps.plus(NAME_2_EUR, new DoubleMatrix1D(SENSI_2));
    int nbReference = 4;
    ParameterSensitivity[] rs = new ParameterSensitivity[nbReference];
    rs[0] = new ParameterSensitivity();
    rs[0] = rs[0].plus(NAME_1_EUR, new DoubleMatrix1D(new double[] {1.0, 0.0, 0.0, 0.0}));
    rs[0] = rs[0].plus(NAME_2_EUR, new DoubleMatrix1D(new double[] {1.0, 0.0, 0.0, 0.0, 0.0}));
    rs[1] = new ParameterSensitivity();
    rs[1] = rs[1].plus(NAME_1_EUR, new DoubleMatrix1D(new double[] {0.0, 0.5, 0.0, 0.0}));
    rs[1] = rs[1].plus(NAME_2_EUR, new DoubleMatrix1D(new double[] {0.0, 1.0, 0.0, 0.0, 0.0}));
    rs[2] = new ParameterSensitivity();
    rs[2] = rs[2].plus(NAME_1_EUR, new DoubleMatrix1D(new double[] {0.0, 0.0, 1.0, 2.0}));
    rs[2] = rs[2].plus(NAME_2_EUR, new DoubleMatrix1D(new double[] {0.0, 0.0, 0.0, 0.0, 0.0}));
    rs[3] = new ParameterSensitivity();
    rs[3] = rs[3].plus(NAME_1_EUR, new DoubleMatrix1D(new double[] {0.0, 0.0, 0.0, 0.0}));
    rs[3] = rs[3].plus(NAME_2_EUR, new DoubleMatrix1D(new double[] {0.0, 0.0, 1.0, 1.0, 1.0}));
    // Weights: tridiagonal + sum by curve
    DoubleMatrix2D w = new DoubleMatrix2D(nbSensi + 2, nbSensi);
    for (int loopnode = 0; loopnode < nbSensi; loopnode++) {
      w.getData()[loopnode][loopnode] = 1.0;
    }
    for (int loopnode = 0; loopnode < nbSensi - 1; loopnode++) {
      w.getData()[loopnode][loopnode + 1] = 0.5;
      w.getData()[loopnode + 1][loopnode] = 0.5;
    }
    w.getData()[NB_SENSI_1 + NB_SENSI_2] = new double[] {1.0, 1.0, 1.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0}; // Order?
    w.getData()[NB_SENSI_1 + NB_SENSI_2 + 1] = new double[] {0.0, 0.0, 0.0, 0.0, 1.0, 1.0, 1.0, 1.0, 1.0};
    DoubleMatrix2D wtW = (DoubleMatrix2D) MATRIX.multiply(MATRIX.getTranspose(w), w);
    // Hedging
    double[] hedging = PortfolioHedgingCalculator.hedgeQuantity(ps, rs, w, FX_MATRIX);
    ParameterSensitivity psMin = new ParameterSensitivity();
    psMin = psMin.plus(ps);
    for (int loopref = 0; loopref < nbReference; loopref++) { // To created the hedge portfolio
      psMin = psMin.plus(rs[loopref].multiplyBy(hedging[loopref]));
    }
    DoubleMatrix1D psMinMatrix = psMin.convert(FX_MATRIX, EUR).toMatrix();
    DoubleMatrix2D psMinMatrixT = new DoubleMatrix2D(new double[][] {psMinMatrix.getData()});
    double penalty = ((DoubleMatrix2D) MATRIX.multiply(psMinMatrixT, MATRIX.multiply(wtW, psMinMatrix))).getEntry(0, 0);

    double shift = 0.01;
    double[] penaltyPlus = new double[nbReference];
    double[] penaltyMinus = new double[nbReference];
    for (int loopref = 0; loopref < nbReference; loopref++) { // Shift on each quantity
      ParameterSensitivity psPertPlus = new ParameterSensitivity();
      psPertPlus = psPertPlus.plus(psMin);
      psPertPlus = psPertPlus.plus(rs[loopref].multiplyBy(shift));
      DoubleMatrix1D psPertPlusMat = psPertPlus.convert(FX_MATRIX, EUR).toMatrix();
      DoubleMatrix2D psPertPlusMatT = new DoubleMatrix2D(new double[][] {psPertPlusMat.getData()});
      penaltyPlus[loopref] = ((DoubleMatrix2D) MATRIX.multiply(psPertPlusMatT, MATRIX.multiply(wtW, psPertPlusMat))).getEntry(0, 0);
      assertTrue("PortfolioHedgingCalculator: minimum", penalty < penaltyPlus[loopref]);

      ParameterSensitivity psPertMinus = new ParameterSensitivity();
      psPertMinus = psPertMinus.plus(psMin);
      psPertMinus = psPertMinus.plus(rs[loopref].multiplyBy(-shift));
      DoubleMatrix1D psPertMinusMat = psPertMinus.convert(FX_MATRIX, EUR).toMatrix();
      DoubleMatrix2D psPertMinusMatT = new DoubleMatrix2D(new double[][] {psPertMinusMat.getData()});
      penaltyMinus[loopref] = ((DoubleMatrix2D) MATRIX.multiply(psPertMinusMatT, MATRIX.multiply(wtW, psPertMinusMat))).getEntry(0, 0);
      assertTrue("PortfolioHedgingCalculator: minimum " + loopref, penalty < penaltyMinus[loopref]);
    }

  }

}

/**
 * Copyright (C) 2009 - 2009 by OpenGamma Inc.
 *
 * Please see distribution for license.
 */
package com.opengamma.financial.model.option.definition;

import com.opengamma.financial.model.tree.RecombiningBinomialTree;

/**
 * 
 * @author emcleod
 */
public class RendlemanBartterBinomialOptionModelDefinition extends BinomialOptionModelDefinition<OptionDefinition, StandardOptionDataBundle> {

  @Override
  public double getDownFactor(final OptionDefinition option, final StandardOptionDataBundle data, final int n, final int j) {
    final double b = data.getCostOfCarry();
    final double t = option.getTimeToExpiry(data.getDate());
    final double k = option.getStrike();
    final double sigma = data.getVolatility(t, k);
    final double dt = t / n;
    return Math.exp((b - sigma * sigma / 2) * dt - sigma * Math.sqrt(dt));
  }

  @Override
  public RecombiningBinomialTree<Double> getUpProbabilityTree(final OptionDefinition option, final StandardOptionDataBundle data, final int n, final int j) {
    final Double[][] tree = new Double[n + 1][j];
    for (int i = 0; i <= n; i++) {
      for (int ii = 0; ii < j; ii++) {
        tree[i][ii] = 0.5;
      }
    }
    return new RecombiningBinomialTree<Double>(tree);
  }

  @Override
  public double getUpFactor(final OptionDefinition option, final StandardOptionDataBundle data, final int n, final int j) {
    final double b = data.getCostOfCarry();
    final double t = option.getTimeToExpiry(data.getDate());
    final double k = option.getStrike();
    final double sigma = data.getVolatility(t, k);
    final double dt = t / n;
    return Math.exp((b - sigma * sigma / 2) * dt + sigma * Math.sqrt(dt));
  }

}

/**
 * Copyright (C) 2009 - 2010 by OpenGamma Inc.
 *
 * Please see distribution for license.
 */
package com.opengamma.math.rootfinding;

import static com.opengamma.math.matrix.MatrixAlgebraFactory.OG_ALGEBRA;

import org.apache.commons.lang.Validate;

import com.opengamma.math.linearalgebra.Decomposition;
import com.opengamma.math.linearalgebra.DecompositionResult;
import com.opengamma.math.linearalgebra.LUDecompositionCommons;
import com.opengamma.math.matrix.DoubleMatrix1D;
import com.opengamma.math.matrix.DoubleMatrix2D;

/**
 * Uses the Sherman-Morrison formula to invert Broyden's Jacobian update formula, thus providing a direct update formula for the inverse Jacobian 
 */
public class ShermanMorrisonVectorRootFinder extends NewtonRootFinderImpl {

  private static final double DEF_TOL = 1e-7;
  private static final int MAX_STEPS = 100;
  private DoubleMatrix2D _invJacobianEst;
  private final Decomposition _decomp;

  public ShermanMorrisonVectorRootFinder() {
    this(DEF_TOL, DEF_TOL, MAX_STEPS);
  }

  /**
   * @param absoluteTol
   * @param relativeTol
   * @param maxSteps
   */
  public ShermanMorrisonVectorRootFinder(double absoluteTol, double relativeTol, int maxSteps) {
    super(absoluteTol, relativeTol, maxSteps);
    _decomp = new LUDecompositionCommons();
  }

  public ShermanMorrisonVectorRootFinder(double absoluteTol, double relativeTol, int maxSteps, Decomposition decomp) {
    super(absoluteTol, relativeTol, maxSteps);
    Validate.notNull(decomp);
    _decomp = decomp;
  }

  /* (non-Javadoc)
   * @see com.opengamma.math.rootfinding.NewtonRootFinderImpl#getDirection()
   */
  @Override
  protected DoubleMatrix1D getDirection() {
    return OG_ALGEBRA.multiply(_invJacobianEst, _y);
  }

  /* (non-Javadoc)
   * @see com.opengamma.math.rootfinding.NewtonRootFinderImpl#initializeMatrices()
   */
  @Override
  protected void initializeMatrices() {
    DoubleMatrix2D jacobianEst = _jacobian.evaluate(_x);
    DecompositionResult deconResult = _decomp.evaluate(jacobianEst);
    _invJacobianEst = deconResult.solve(DoubleMatrix2D.Identity(_x.getNumberOfElements()));
  }

  /* (non-Javadoc)
   * @see com.opengamma.math.rootfinding.NewtonRootFinderImpl#updateMatrices()
   */
  @Override
  protected void updateMatrices() {
    DoubleMatrix1D vtemp1 = OG_ALGEBRA.multiply(_deltax, _invJacobianEst);
    double length2 = OG_ALGEBRA.getInnerProduct(vtemp1, _deltay);
    vtemp1 = (DoubleMatrix1D) OG_ALGEBRA.scale(vtemp1, 1.0 / length2);
    DoubleMatrix1D vtemp2 = (DoubleMatrix1D) OG_ALGEBRA
        .subtract(_deltax, OG_ALGEBRA.multiply(_invJacobianEst, _deltay));
    DoubleMatrix2D mtemp = OG_ALGEBRA.getOuterProduct(vtemp2, vtemp1);
    _invJacobianEst = (DoubleMatrix2D) OG_ALGEBRA.add(_invJacobianEst, mtemp);
  }

}

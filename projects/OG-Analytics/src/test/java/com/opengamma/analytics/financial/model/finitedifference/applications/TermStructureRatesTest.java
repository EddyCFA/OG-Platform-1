/**
 * Copyright (C) 2013 - present by OpenGamma Inc. and the OpenGamma group of companies
 * 
 * Please see distribution for license.
 */
package com.opengamma.analytics.financial.model.finitedifference.applications;

import static org.testng.AssertJUnit.assertEquals;

import java.util.Arrays;

import org.testng.annotations.Test;

import com.opengamma.analytics.financial.model.finitedifference.BoundaryCondition;
import com.opengamma.analytics.financial.model.finitedifference.ConvectionDiffusionPDE1DCoefficients;
import com.opengamma.analytics.financial.model.finitedifference.ConvectionDiffusionPDE1DStandardCoefficients;
import com.opengamma.analytics.financial.model.finitedifference.ExponentialMeshing;
import com.opengamma.analytics.financial.model.finitedifference.MeshingFunction;
import com.opengamma.analytics.financial.model.finitedifference.NeumannBoundaryCondition;
import com.opengamma.analytics.financial.model.finitedifference.PDE1DDataBundle;
import com.opengamma.analytics.financial.model.finitedifference.PDEGrid1D;
import com.opengamma.analytics.financial.model.finitedifference.PDEResults1D;
import com.opengamma.analytics.financial.model.finitedifference.ThetaMethodFiniteDifference;
import com.opengamma.analytics.financial.model.interestrate.curve.ForwardCurve;
import com.opengamma.analytics.financial.model.option.pricing.analytic.formula.EuropeanVanillaOption;
import com.opengamma.analytics.financial.model.volatility.BlackFormulaRepository;
import com.opengamma.analytics.financial.model.volatility.local.LocalVolatilitySurfaceStrike;
import com.opengamma.analytics.math.curve.ConstantDoublesCurve;
import com.opengamma.analytics.math.curve.Curve;
import com.opengamma.analytics.math.curve.FunctionalDoublesCurve;
import com.opengamma.analytics.math.function.Function;
import com.opengamma.analytics.math.function.Function1D;
import com.opengamma.analytics.math.integration.Integrator1D;
import com.opengamma.analytics.math.integration.RungeKuttaIntegrator1D;
import com.opengamma.analytics.math.surface.ConstantDoublesSurface;

/**
 * Check that the pde solver correctly prices the forward 
 */
public class TermStructureRatesTest {
  private static final PDE1DCoefficientsProvider PDE = new PDE1DCoefficientsProvider();
  
  private static final LocalVolatilityBackwardsPDEPricer PRICER = new LocalVolatilityBackwardsPDEPricer();
  private static final ForwardCurve FWD_CURVE;
 // private static final YieldAndDiscountCurve DIS_CURVE;
  private static final Curve<Double, Double> RISK_FREE_CURVE;
  private static final LocalVolatilitySurfaceStrike LOCAL_VOL_SUR;
  private static final double S0 = 10.0;
  private static final double T = 5.0;
  private static final double SIGMA = 0.3;
  private static final double DF;

  static {
    
    Function1D<Double,Double> r = new Function1D<Double, Double>() {
      @Override
      public Double evaluate(Double t) {
        return (-0.04 + 0.1*t)*Math.exp(-0.5*t) + 0.08;
      }
    };
    
    Function<Double,Double> b = new Function1D<Double, Double>() {
      @Override
      public Double evaluate(Double t) {
        return (-0.04 + 0.1*t)*Math.exp(-0.5*t) - 0.08*Math.exp(-0.3*t) - 0.05 ;
      }
    };
    
    RISK_FREE_CURVE = FunctionalDoublesCurve.from(r);
    FWD_CURVE = new ForwardCurve(S0, FunctionalDoublesCurve.from(b));
    LOCAL_VOL_SUR = new LocalVolatilitySurfaceStrike(ConstantDoublesSurface.from(SIGMA));
    
    Integrator1D<Double, Double> integrator = new RungeKuttaIntegrator1D();
    DF  = Math.exp(-integrator.integrate(r, 0.0, T));
  }
  
  /**
   * Check a forward is priced correctly by the PDE solver 
   */
  @Test
  public void forwardTest() {
    //forward (how much would you pay now to receive the asset at time T) is just a zero strike call
    EuropeanVanillaOption option = new EuropeanVanillaOption(0.0, T, true);
    
    int tNodes = 100; 
    int nu = 10;
    int xNodes = nu*tNodes;
    
    double pvFwd = DF*FWD_CURVE.getForward(T);
    
    double pdePrice = PRICER.price(FWD_CURVE, RISK_FREE_CURVE, option, LOCAL_VOL_SUR, false, xNodes, tNodes);
    System.out.println(pdePrice);
    
    //can recover accurate forward price with moderate grid     
    assertEquals(pvFwd,pdePrice,pvFwd*5e-6);
  }
  
  /**
   * Check a zero coupon bond is priced correctly by the PDE solver. Note, as this is not something one normally prices this way, there 
   * is no helper class for the set up  
   */
  @Test
  public void zeroCouponBondTest() {
    final ConvectionDiffusionPDE1DStandardCoefficients coef = PDE.getBackwardsLocalVol(RISK_FREE_CURVE,FWD_CURVE.getDriftCurve(),T,LOCAL_VOL_SUR);
    final Function1D<Double, Double> payoff = new Function1D<Double, Double>() {
      @Override
      public Double evaluate(Double x) {
        return 1.0;
      }
    };
    
    int tNodes = 100;
    int nu = 5;
    int xNodes = nu*tNodes;
    double sMin = S0/5.0;
    double sMax = 5*S0;
    MeshingFunction xMesh = new ExponentialMeshing(sMin, sMax, xNodes, 0.0, new double[] {S0});
    MeshingFunction tMesh = new ExponentialMeshing(0, T, tNodes, 0.0);
    PDEGrid1D grid = new PDEGrid1D(tMesh, xMesh);
    
    BoundaryCondition lower = new NeumannBoundaryCondition(0.0, sMin, true);
    BoundaryCondition upper = new NeumannBoundaryCondition(0.0, sMax, true);
    PDE1DDataBundle<ConvectionDiffusionPDE1DCoefficients> data = new PDE1DDataBundle<ConvectionDiffusionPDE1DCoefficients>(coef, payoff, lower, upper, grid);
    ThetaMethodFiniteDifference solver = new ThetaMethodFiniteDifference();
    PDEResults1D res = solver.solve(data);
    final int index = Arrays.binarySearch(grid.getSpaceNodes(), S0);
    double pdePrice = res.getFunctionValue(index);
    
    System.out.println(DF+"\t"+pdePrice);
    assertEquals(DF,pdePrice,DF*5e-5);
  }
  
  @Test
  public void OptionTest() {
    double k = 6.0;
    boolean isCall = false;
    
    EuropeanVanillaOption option = new EuropeanVanillaOption(k, T, isCall);
    
    int tNodes = 120; 
    int nu = 10;
    int xNodes = nu*tNodes;
    
    double  bsPrice = DF*BlackFormulaRepository.price(FWD_CURVE.getForward(T), k, T, SIGMA, isCall);
    
    double pdePrice = PRICER.price(FWD_CURVE, RISK_FREE_CURVE, option, LOCAL_VOL_SUR, false, xNodes, tNodes,0.1,0.0,5.0);
 //   double resErr = Math.abs((pdePrice-bsPrice)/bsPrice);
  //  System.out.println(bsPrice +"\t"+pdePrice+"\t"+resErr);
       
    assertEquals(bsPrice,pdePrice,bsPrice*1e-4);
    
    
    //now price in terms of the forward - gives around 3 times improvement in accuracy 
    ForwardCurve fc = new ForwardCurve(FWD_CURVE.getForward(T));
    ConstantDoublesCurve r = ConstantDoublesCurve.from(0.0);
    pdePrice = DF*PRICER.price(fc, r, option, LOCAL_VOL_SUR, false, xNodes, tNodes,0.1,0.0,5.0);
    
   // resErr = Math.abs((pdePrice-bsPrice)/bsPrice);
   // System.out.println(bsPrice +"\t"+pdePrice+"\t"+resErr);
    assertEquals(bsPrice,pdePrice,bsPrice*3e-5);
  }


}
/**
 * Copyright (C) 2012 - present by OpenGamma Inc. and the OpenGamma group of companies
 * 
 * Please see distribution for license.
 */
package com.opengamma.maths.highlevelapi.functions.DOGMAFunctions.DOGMATrigonometry.acos;

import com.opengamma.maths.highlevelapi.datatypes.primitive.OGArraySuper;

/**
 * Math.acos overload
 * @param <T> An OGArray type
 */
public interface AcosAbstract<T extends OGArraySuper<? extends Number>> {
  
  OGArraySuper<? extends Number> acos(T array1);
}
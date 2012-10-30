/**
 * Copyright (C) 2012 - present by OpenGamma Inc. and the OpenGamma group of companies
 * 
 * Please see distribution for license.
 */
package com.opengamma.maths.highlevelapi.functions.DOGMAFunctions.DOGMASpecialFunctions.erf;

import com.opengamma.maths.highlevelapi.datatypes.primitive.OGArraySuper;

/**
 * erf
 * @param <T> An OGArray type
 */
public interface ErfAbstract<T extends OGArraySuper<? extends Number>> {
  
  OGArraySuper<? extends Number> erf(T array1);
}
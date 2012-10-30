/**
 * Copyright (C) 2009 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.financial.security.swap;

import java.io.Serializable;
import java.util.Map;

import org.joda.beans.BeanBuilder;
import org.joda.beans.BeanDefinition;
import org.joda.beans.JodaBeanUtils;
import org.joda.beans.MetaProperty;
import org.joda.beans.impl.direct.DirectBean;
import org.joda.beans.impl.direct.DirectMetaBean;
import org.joda.beans.impl.direct.DirectMetaPropertyMap;

/**
 * Abstract base class for the notional in a swap leg.
 */
@BeanDefinition
public abstract class Notional extends DirectBean implements Serializable {

  /** Serialization version. */
  private static final long serialVersionUID = 1L;

  /**
   * Creates an instance.
   */
  protected Notional() {
  }

  //-------------------------------------------------------------------------
  /**
   * Accepts a visitor to manage traversal of the hierarchy.
   * 
   * @param <T> the result type of the visitor
   * @param visitor  the visitor, not null
   * @return the result
   */
  public abstract <T> T accept(NotionalVisitor<T> visitor);

  //------------------------- AUTOGENERATED START -------------------------
  ///CLOVER:OFF
  /**
   * The meta-bean for {@code Notional}.
   * @return the meta-bean, not null
   */
  public static Notional.Meta meta() {
    return Notional.Meta.INSTANCE;
  }
  static {
    JodaBeanUtils.registerMetaBean(Notional.Meta.INSTANCE);
  }

  @Override
  public Notional.Meta metaBean() {
    return Notional.Meta.INSTANCE;
  }

  @Override
  protected Object propertyGet(String propertyName, boolean quiet) {
    return super.propertyGet(propertyName, quiet);
  }

  @Override
  protected void propertySet(String propertyName, Object newValue, boolean quiet) {
    super.propertySet(propertyName, newValue, quiet);
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj != null && obj.getClass() == this.getClass()) {
      return true;
    }
    return false;
  }

  @Override
  public int hashCode() {
    int hash = getClass().hashCode();
    return hash;
  }

  //-----------------------------------------------------------------------
  /**
   * The meta-bean for {@code Notional}.
   */
  public static class Meta extends DirectMetaBean {
    /**
     * The singleton instance of the meta-bean.
     */
    static final Meta INSTANCE = new Meta();

    /**
     * The meta-properties.
     */
    private final Map<String, MetaProperty<?>> _metaPropertyMap$ = new DirectMetaPropertyMap(
        this, null);

    /**
     * Restricted constructor.
     */
    protected Meta() {
    }

    @Override
    public BeanBuilder<? extends Notional> builder() {
      throw new UnsupportedOperationException("Notional is an abstract class");
    }

    @Override
    public Class<? extends Notional> beanType() {
      return Notional.class;
    }

    @Override
    public Map<String, MetaProperty<?>> metaPropertyMap() {
      return _metaPropertyMap$;
    }

    //-----------------------------------------------------------------------
  }

  ///CLOVER:ON
  //-------------------------- AUTOGENERATED END --------------------------
}
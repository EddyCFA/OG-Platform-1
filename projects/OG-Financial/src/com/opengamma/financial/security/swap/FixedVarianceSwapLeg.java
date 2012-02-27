/**
 * Copyright (C) 2012 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.financial.security.swap;

import com.opengamma.financial.convention.businessday.BusinessDayConvention;
import com.opengamma.financial.convention.daycount.DayCount;
import com.opengamma.financial.convention.frequency.Frequency;
import com.opengamma.id.ExternalId;
import org.joda.beans.BeanBuilder;
import org.joda.beans.BeanDefinition;
import org.joda.beans.JodaBeanUtils;
import org.joda.beans.MetaProperty;
import org.joda.beans.Property;
import org.joda.beans.PropertyDefinition;
import org.joda.beans.impl.direct.DirectBeanBuilder;
import org.joda.beans.impl.direct.DirectMetaProperty;
import org.joda.beans.impl.direct.DirectMetaPropertyMap;

import java.util.Map;

/**
 * Fixed leg for variance swaps
 */
@BeanDefinition
public class FixedVarianceSwapLeg extends VarianceSwapLeg {

  /** Serialization version. */
  private static final long serialVersionUID = 1L;

  /**
   * @param dayCount The day count convention, not null
   * @param frequency The frequency, not null
   * @param regionId The region ID, not null
   * @param businessDayConvention The business day convention, not null
   * @param notional The notional, not null
   * @param eom The end-of-month flag
   * @param strike The strike
   * @param type The variance swap type, not null
   */
  public FixedVarianceSwapLeg(DayCount dayCount,
                              Frequency frequency,
                              ExternalId regionId,
                              BusinessDayConvention businessDayConvention,
                              Notional notional,
                              boolean eom,
                              double strike,
                              VarianceSwapType type) {
    super(dayCount, frequency, regionId, businessDayConvention, notional, eom);
    setStrike(strike);
    setType(type);
  }

  /** For the builder */
  FixedVarianceSwapLeg() {
  }

  @Override
  public <T> T accept(SwapLegVisitor<T> visitor) {
    return visitor.visitFixedVarianceSwapLeg(this);
  }

  @PropertyDefinition
  private double _strike;

  @PropertyDefinition(validate = "notNull")
  private VarianceSwapType _type;
  //------------------------- AUTOGENERATED START -------------------------
  ///CLOVER:OFF
  /**
   * The meta-bean for {@code FixedVarianceSwapLeg}.
   * @return the meta-bean, not null
   */
  public static FixedVarianceSwapLeg.Meta meta() {
    return FixedVarianceSwapLeg.Meta.INSTANCE;
  }
  static {
    JodaBeanUtils.registerMetaBean(FixedVarianceSwapLeg.Meta.INSTANCE);
  }

  @Override
  public FixedVarianceSwapLeg.Meta metaBean() {
    return FixedVarianceSwapLeg.Meta.INSTANCE;
  }

  @Override
  protected Object propertyGet(String propertyName, boolean quiet) {
    switch (propertyName.hashCode()) {
      case -891985998:  // strike
        return getStrike();
      case 3575610:  // type
        return getType();
    }
    return super.propertyGet(propertyName, quiet);
  }

  @Override
  protected void propertySet(String propertyName, Object newValue, boolean quiet) {
    switch (propertyName.hashCode()) {
      case -891985998:  // strike
        setStrike((Double) newValue);
        return;
      case 3575610:  // type
        setType((VarianceSwapType) newValue);
        return;
    }
    super.propertySet(propertyName, newValue, quiet);
  }

  @Override
  protected void validate() {
    JodaBeanUtils.notNull(_type, "type");
    super.validate();
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj != null && obj.getClass() == this.getClass()) {
      FixedVarianceSwapLeg other = (FixedVarianceSwapLeg) obj;
      return JodaBeanUtils.equal(getStrike(), other.getStrike()) &&
          JodaBeanUtils.equal(getType(), other.getType()) &&
          super.equals(obj);
    }
    return false;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash += hash * 31 + JodaBeanUtils.hashCode(getStrike());
    hash += hash * 31 + JodaBeanUtils.hashCode(getType());
    return hash ^ super.hashCode();
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the strike.
   * @return the value of the property
   */
  public double getStrike() {
    return _strike;
  }

  /**
   * Sets the strike.
   * @param strike  the new value of the property
   */
  public void setStrike(double strike) {
    this._strike = strike;
  }

  /**
   * Gets the the {@code strike} property.
   * @return the property, not null
   */
  public final Property<Double> strike() {
    return metaBean().strike().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the type.
   * @return the value of the property, not null
   */
  public VarianceSwapType getType() {
    return _type;
  }

  /**
   * Sets the type.
   * @param type  the new value of the property, not null
   */
  public void setType(VarianceSwapType type) {
    JodaBeanUtils.notNull(type, "type");
    this._type = type;
  }

  /**
   * Gets the the {@code type} property.
   * @return the property, not null
   */
  public final Property<VarianceSwapType> type() {
    return metaBean().type().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * The meta-bean for {@code FixedVarianceSwapLeg}.
   */
  public static class Meta extends VarianceSwapLeg.Meta {
    /**
     * The singleton instance of the meta-bean.
     */
    static final Meta INSTANCE = new Meta();

    /**
     * The meta-property for the {@code strike} property.
     */
    private final MetaProperty<Double> _strike = DirectMetaProperty.ofReadWrite(
        this, "strike", FixedVarianceSwapLeg.class, Double.TYPE);
    /**
     * The meta-property for the {@code type} property.
     */
    private final MetaProperty<VarianceSwapType> _type = DirectMetaProperty.ofReadWrite(
        this, "type", FixedVarianceSwapLeg.class, VarianceSwapType.class);
    /**
     * The meta-properties.
     */
    private final Map<String, MetaProperty<Object>> _map = new DirectMetaPropertyMap(
      this, (DirectMetaPropertyMap) super.metaPropertyMap(),
        "strike",
        "type");

    /**
     * Restricted constructor.
     */
    protected Meta() {
    }

    @Override
    protected MetaProperty<?> metaPropertyGet(String propertyName) {
      switch (propertyName.hashCode()) {
        case -891985998:  // strike
          return _strike;
        case 3575610:  // type
          return _type;
      }
      return super.metaPropertyGet(propertyName);
    }

    @Override
    public BeanBuilder<? extends FixedVarianceSwapLeg> builder() {
      return new DirectBeanBuilder<FixedVarianceSwapLeg>(new FixedVarianceSwapLeg());
    }

    @Override
    public Class<? extends FixedVarianceSwapLeg> beanType() {
      return FixedVarianceSwapLeg.class;
    }

    @Override
    public Map<String, MetaProperty<Object>> metaPropertyMap() {
      return _map;
    }

    //-----------------------------------------------------------------------
    /**
     * The meta-property for the {@code strike} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<Double> strike() {
      return _strike;
    }

    /**
     * The meta-property for the {@code type} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<VarianceSwapType> type() {
      return _type;
    }

  }

  ///CLOVER:ON
  //-------------------------- AUTOGENERATED END --------------------------
}

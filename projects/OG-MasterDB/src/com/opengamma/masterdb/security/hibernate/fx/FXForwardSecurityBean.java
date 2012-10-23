/**
 * Copyright (C) 2009 - present by OpenGamma Inc. and the OpenGamma group of companies
 * 
 * Please see distribution for license.
 */

package com.opengamma.masterdb.security.hibernate.fx;

import java.util.Map;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.joda.beans.BeanBuilder;
import org.joda.beans.BeanDefinition;
import org.joda.beans.JodaBeanUtils;
import org.joda.beans.MetaProperty;
import org.joda.beans.Property;
import org.joda.beans.PropertyDefinition;
import org.joda.beans.impl.direct.DirectBeanBuilder;
import org.joda.beans.impl.direct.DirectMetaProperty;
import org.joda.beans.impl.direct.DirectMetaPropertyMap;

import com.opengamma.financial.security.fx.FXForwardSecurity;
import com.opengamma.masterdb.security.hibernate.CurrencyBean;
import com.opengamma.masterdb.security.hibernate.ExternalIdBean;
import com.opengamma.masterdb.security.hibernate.SecurityBean;
import com.opengamma.masterdb.security.hibernate.ZonedDateTimeBean;

/**
 * A Hibernate bean representation of {@link FXForwardSecurity}.
 */
@BeanDefinition
public class FXForwardSecurityBean extends SecurityBean {

  @PropertyDefinition
  private ZonedDateTimeBean _forwardDate;
  @PropertyDefinition
  private ExternalIdBean _region;
  @PropertyDefinition
  private CurrencyBean _payCurrency;
  @PropertyDefinition
  private double _payAmount;
  @PropertyDefinition
  private CurrencyBean _receiveCurrency;
  @PropertyDefinition
  private double _receiveAmount;

  @Override
  public boolean equals(final Object other) {
    if (!(other instanceof FXForwardSecurityBean)) {
      return false;
    }
    FXForwardSecurityBean fxForward = (FXForwardSecurityBean) other;
    return new EqualsBuilder()
      .append(getId(), fxForward.getId())
      .append(getForwardDate(), fxForward.getForwardDate())
      .append(getPayCurrency(), fxForward.getPayCurrency())
      .append(getPayAmount(), fxForward.getPayAmount())
      .append(getReceiveCurrency(), fxForward.getReceiveCurrency())
      .append(getReceiveAmount(), fxForward.getReceiveAmount())
      .append(getRegion(), fxForward.getRegion())
      .isEquals();
  }
  
  @Override
  public int hashCode() {
    return new HashCodeBuilder()
      .append(getForwardDate())
      .append(getPayCurrency())
      .append(getPayAmount())
      .append(getReceiveCurrency())
      .append(getReceiveAmount())
      .toHashCode();
  }

  //------------------------- AUTOGENERATED START -------------------------
  ///CLOVER:OFF
  /**
   * The meta-bean for {@code FXForwardSecurityBean}.
   * @return the meta-bean, not null
   */
  public static FXForwardSecurityBean.Meta meta() {
    return FXForwardSecurityBean.Meta.INSTANCE;
  }
  static {
    JodaBeanUtils.registerMetaBean(FXForwardSecurityBean.Meta.INSTANCE);
  }

  @Override
  public FXForwardSecurityBean.Meta metaBean() {
    return FXForwardSecurityBean.Meta.INSTANCE;
  }

  @Override
  protected Object propertyGet(String propertyName, boolean quiet) {
    switch (propertyName.hashCode()) {
      case 1652755475:  // forwardDate
        return getForwardDate();
      case -934795532:  // region
        return getRegion();
      case -295641895:  // payCurrency
        return getPayCurrency();
      case -1338781920:  // payAmount
        return getPayAmount();
      case -1228590060:  // receiveCurrency
        return getReceiveCurrency();
      case 984267035:  // receiveAmount
        return getReceiveAmount();
    }
    return super.propertyGet(propertyName, quiet);
  }

  @Override
  protected void propertySet(String propertyName, Object newValue, boolean quiet) {
    switch (propertyName.hashCode()) {
      case 1652755475:  // forwardDate
        setForwardDate((ZonedDateTimeBean) newValue);
        return;
      case -934795532:  // region
        setRegion((ExternalIdBean) newValue);
        return;
      case -295641895:  // payCurrency
        setPayCurrency((CurrencyBean) newValue);
        return;
      case -1338781920:  // payAmount
        setPayAmount((Double) newValue);
        return;
      case -1228590060:  // receiveCurrency
        setReceiveCurrency((CurrencyBean) newValue);
        return;
      case 984267035:  // receiveAmount
        setReceiveAmount((Double) newValue);
        return;
    }
    super.propertySet(propertyName, newValue, quiet);
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the forwardDate.
   * @return the value of the property
   */
  public ZonedDateTimeBean getForwardDate() {
    return _forwardDate;
  }

  /**
   * Sets the forwardDate.
   * @param forwardDate  the new value of the property
   */
  public void setForwardDate(ZonedDateTimeBean forwardDate) {
    this._forwardDate = forwardDate;
  }

  /**
   * Gets the the {@code forwardDate} property.
   * @return the property, not null
   */
  public final Property<ZonedDateTimeBean> forwardDate() {
    return metaBean().forwardDate().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the region.
   * @return the value of the property
   */
  public ExternalIdBean getRegion() {
    return _region;
  }

  /**
   * Sets the region.
   * @param region  the new value of the property
   */
  public void setRegion(ExternalIdBean region) {
    this._region = region;
  }

  /**
   * Gets the the {@code region} property.
   * @return the property, not null
   */
  public final Property<ExternalIdBean> region() {
    return metaBean().region().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the payCurrency.
   * @return the value of the property
   */
  public CurrencyBean getPayCurrency() {
    return _payCurrency;
  }

  /**
   * Sets the payCurrency.
   * @param payCurrency  the new value of the property
   */
  public void setPayCurrency(CurrencyBean payCurrency) {
    this._payCurrency = payCurrency;
  }

  /**
   * Gets the the {@code payCurrency} property.
   * @return the property, not null
   */
  public final Property<CurrencyBean> payCurrency() {
    return metaBean().payCurrency().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the payAmount.
   * @return the value of the property
   */
  public double getPayAmount() {
    return _payAmount;
  }

  /**
   * Sets the payAmount.
   * @param payAmount  the new value of the property
   */
  public void setPayAmount(double payAmount) {
    this._payAmount = payAmount;
  }

  /**
   * Gets the the {@code payAmount} property.
   * @return the property, not null
   */
  public final Property<Double> payAmount() {
    return metaBean().payAmount().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the receiveCurrency.
   * @return the value of the property
   */
  public CurrencyBean getReceiveCurrency() {
    return _receiveCurrency;
  }

  /**
   * Sets the receiveCurrency.
   * @param receiveCurrency  the new value of the property
   */
  public void setReceiveCurrency(CurrencyBean receiveCurrency) {
    this._receiveCurrency = receiveCurrency;
  }

  /**
   * Gets the the {@code receiveCurrency} property.
   * @return the property, not null
   */
  public final Property<CurrencyBean> receiveCurrency() {
    return metaBean().receiveCurrency().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the receiveAmount.
   * @return the value of the property
   */
  public double getReceiveAmount() {
    return _receiveAmount;
  }

  /**
   * Sets the receiveAmount.
   * @param receiveAmount  the new value of the property
   */
  public void setReceiveAmount(double receiveAmount) {
    this._receiveAmount = receiveAmount;
  }

  /**
   * Gets the the {@code receiveAmount} property.
   * @return the property, not null
   */
  public final Property<Double> receiveAmount() {
    return metaBean().receiveAmount().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * The meta-bean for {@code FXForwardSecurityBean}.
   */
  public static class Meta extends SecurityBean.Meta {
    /**
     * The singleton instance of the meta-bean.
     */
    static final Meta INSTANCE = new Meta();

    /**
     * The meta-property for the {@code forwardDate} property.
     */
    private final MetaProperty<ZonedDateTimeBean> _forwardDate = DirectMetaProperty.ofReadWrite(
        this, "forwardDate", FXForwardSecurityBean.class, ZonedDateTimeBean.class);
    /**
     * The meta-property for the {@code region} property.
     */
    private final MetaProperty<ExternalIdBean> _region = DirectMetaProperty.ofReadWrite(
        this, "region", FXForwardSecurityBean.class, ExternalIdBean.class);
    /**
     * The meta-property for the {@code payCurrency} property.
     */
    private final MetaProperty<CurrencyBean> _payCurrency = DirectMetaProperty.ofReadWrite(
        this, "payCurrency", FXForwardSecurityBean.class, CurrencyBean.class);
    /**
     * The meta-property for the {@code payAmount} property.
     */
    private final MetaProperty<Double> _payAmount = DirectMetaProperty.ofReadWrite(
        this, "payAmount", FXForwardSecurityBean.class, Double.TYPE);
    /**
     * The meta-property for the {@code receiveCurrency} property.
     */
    private final MetaProperty<CurrencyBean> _receiveCurrency = DirectMetaProperty.ofReadWrite(
        this, "receiveCurrency", FXForwardSecurityBean.class, CurrencyBean.class);
    /**
     * The meta-property for the {@code receiveAmount} property.
     */
    private final MetaProperty<Double> _receiveAmount = DirectMetaProperty.ofReadWrite(
        this, "receiveAmount", FXForwardSecurityBean.class, Double.TYPE);
    /**
     * The meta-properties.
     */
    private final Map<String, MetaProperty<?>> _metaPropertyMap$ = new DirectMetaPropertyMap(
      this, (DirectMetaPropertyMap) super.metaPropertyMap(),
        "forwardDate",
        "region",
        "payCurrency",
        "payAmount",
        "receiveCurrency",
        "receiveAmount");

    /**
     * Restricted constructor.
     */
    protected Meta() {
    }

    @Override
    protected MetaProperty<?> metaPropertyGet(String propertyName) {
      switch (propertyName.hashCode()) {
        case 1652755475:  // forwardDate
          return _forwardDate;
        case -934795532:  // region
          return _region;
        case -295641895:  // payCurrency
          return _payCurrency;
        case -1338781920:  // payAmount
          return _payAmount;
        case -1228590060:  // receiveCurrency
          return _receiveCurrency;
        case 984267035:  // receiveAmount
          return _receiveAmount;
      }
      return super.metaPropertyGet(propertyName);
    }

    @Override
    public BeanBuilder<? extends FXForwardSecurityBean> builder() {
      return new DirectBeanBuilder<FXForwardSecurityBean>(new FXForwardSecurityBean());
    }

    @Override
    public Class<? extends FXForwardSecurityBean> beanType() {
      return FXForwardSecurityBean.class;
    }

    @Override
    public Map<String, MetaProperty<?>> metaPropertyMap() {
      return _metaPropertyMap$;
    }

    //-----------------------------------------------------------------------
    /**
     * The meta-property for the {@code forwardDate} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<ZonedDateTimeBean> forwardDate() {
      return _forwardDate;
    }

    /**
     * The meta-property for the {@code region} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<ExternalIdBean> region() {
      return _region;
    }

    /**
     * The meta-property for the {@code payCurrency} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<CurrencyBean> payCurrency() {
      return _payCurrency;
    }

    /**
     * The meta-property for the {@code payAmount} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<Double> payAmount() {
      return _payAmount;
    }

    /**
     * The meta-property for the {@code receiveCurrency} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<CurrencyBean> receiveCurrency() {
      return _receiveCurrency;
    }

    /**
     * The meta-property for the {@code receiveAmount} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<Double> receiveAmount() {
      return _receiveAmount;
    }

  }

  ///CLOVER:ON
  //-------------------------- AUTOGENERATED END --------------------------
}

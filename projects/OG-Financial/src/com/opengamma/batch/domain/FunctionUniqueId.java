/**
 * Copyright (C) 2009 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.batch.domain;

import java.util.Map;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.joda.beans.BeanBuilder;
import org.joda.beans.BeanDefinition;
import org.joda.beans.JodaBeanUtils;
import org.joda.beans.MetaProperty;
import org.joda.beans.Property;
import org.joda.beans.PropertyDefinition;
import org.joda.beans.impl.direct.DirectBean;
import org.joda.beans.impl.direct.DirectBeanBuilder;
import org.joda.beans.impl.direct.DirectMetaBean;
import org.joda.beans.impl.direct.DirectMetaProperty;
import org.joda.beans.impl.direct.DirectMetaPropertyMap;

@BeanDefinition
public class FunctionUniqueId extends DirectBean {
  
  @PropertyDefinition
  private long _id;
  
  @PropertyDefinition
  private String _uniqueId;

  @Override
  public int hashCode() {
    return HashCodeBuilder.reflectionHashCode(this);
  }

  @Override
  public boolean equals(Object obj) {
    return EqualsBuilder.reflectionEquals(this, obj);
  }
  
  @Override
  public String toString() {
    return ToStringBuilder.reflectionToString(this);
  }

  //------------------------- AUTOGENERATED START -------------------------
  ///CLOVER:OFF
  /**
   * The meta-bean for {@code FunctionUniqueId}.
   * @return the meta-bean, not null
   */
  public static FunctionUniqueId.Meta meta() {
    return FunctionUniqueId.Meta.INSTANCE;
  }
  static {
    JodaBeanUtils.registerMetaBean(FunctionUniqueId.Meta.INSTANCE);
  }

  @Override
  public FunctionUniqueId.Meta metaBean() {
    return FunctionUniqueId.Meta.INSTANCE;
  }

  @Override
  protected Object propertyGet(String propertyName, boolean quiet) {
    switch (propertyName.hashCode()) {
      case 3355:  // id
        return getId();
      case -294460212:  // uniqueId
        return getUniqueId();
    }
    return super.propertyGet(propertyName, quiet);
  }

  @Override
  protected void propertySet(String propertyName, Object newValue, boolean quiet) {
    switch (propertyName.hashCode()) {
      case 3355:  // id
        setId((Long) newValue);
        return;
      case -294460212:  // uniqueId
        setUniqueId((String) newValue);
        return;
    }
    super.propertySet(propertyName, newValue, quiet);
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the id.
   * @return the value of the property
   */
  public long getId() {
    return _id;
  }

  /**
   * Sets the id.
   * @param id  the new value of the property
   */
  public void setId(long id) {
    this._id = id;
  }

  /**
   * Gets the the {@code id} property.
   * @return the property, not null
   */
  public final Property<Long> id() {
    return metaBean().id().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the uniqueId.
   * @return the value of the property
   */
  public String getUniqueId() {
    return _uniqueId;
  }

  /**
   * Sets the uniqueId.
   * @param uniqueId  the new value of the property
   */
  public void setUniqueId(String uniqueId) {
    this._uniqueId = uniqueId;
  }

  /**
   * Gets the the {@code uniqueId} property.
   * @return the property, not null
   */
  public final Property<String> uniqueId() {
    return metaBean().uniqueId().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * The meta-bean for {@code FunctionUniqueId}.
   */
  public static class Meta extends DirectMetaBean {
    /**
     * The singleton instance of the meta-bean.
     */
    static final Meta INSTANCE = new Meta();

    /**
     * The meta-property for the {@code id} property.
     */
    private final MetaProperty<Long> _id = DirectMetaProperty.ofReadWrite(
        this, "id", FunctionUniqueId.class, Long.TYPE);
    /**
     * The meta-property for the {@code uniqueId} property.
     */
    private final MetaProperty<String> _uniqueId = DirectMetaProperty.ofReadWrite(
        this, "uniqueId", FunctionUniqueId.class, String.class);
    /**
     * The meta-properties.
     */
    private final Map<String, MetaProperty<?>> _metaPropertyMap$ = new DirectMetaPropertyMap(
        this, null,
        "id",
        "uniqueId");

    /**
     * Restricted constructor.
     */
    protected Meta() {
    }

    @Override
    protected MetaProperty<?> metaPropertyGet(String propertyName) {
      switch (propertyName.hashCode()) {
        case 3355:  // id
          return _id;
        case -294460212:  // uniqueId
          return _uniqueId;
      }
      return super.metaPropertyGet(propertyName);
    }

    @Override
    public BeanBuilder<? extends FunctionUniqueId> builder() {
      return new DirectBeanBuilder<FunctionUniqueId>(new FunctionUniqueId());
    }

    @Override
    public Class<? extends FunctionUniqueId> beanType() {
      return FunctionUniqueId.class;
    }

    @Override
    public Map<String, MetaProperty<?>> metaPropertyMap() {
      return _metaPropertyMap$;
    }

    //-----------------------------------------------------------------------
    /**
     * The meta-property for the {@code id} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<Long> id() {
      return _id;
    }

    /**
     * The meta-property for the {@code uniqueId} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<String> uniqueId() {
      return _uniqueId;
    }

  }

  ///CLOVER:ON
  //-------------------------- AUTOGENERATED END --------------------------
}

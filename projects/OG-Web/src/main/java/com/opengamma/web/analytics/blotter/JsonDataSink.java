/**
 * Copyright (C) 2012 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.web.analytics.blotter;

import java.util.Collection;
import java.util.Map;

import org.joda.beans.Bean;
import org.joda.beans.MetaBean;
import org.json.JSONObject;

import com.google.common.collect.Maps;

/**
 * Receives data from a Joda bean and writes it into a JSON object.
 */
/* package */ class JsonDataSink implements BeanDataSink<JSONObject> {

  private final Map<String, Object> _json = Maps.newHashMap();

  @Override
  public void setBeanData(MetaBean metaBean, Bean bean) {
    _json.put("type", metaBean.beanType().getSimpleName());
  }

  @Override
  public void setValue(String propertyName, String value) {
    _json.put(propertyName, value);
  }

  @Override
  public void setCollectionValues(String propertyName, Collection<String> values) {
    _json.put(propertyName, values);
  }

  @Override
  public void setMapValues(String propertyName, Map<String, String> values) {
    _json.put(propertyName, values);
  }

  @Override
  public void setBeanValue(String propertyName, Bean bean, BeanTraverser traverser) {
    Object value;
    if (bean == null) {
      value = null;
    } else {
      BuildingBeanVisitor<JSONObject> visitor = new BuildingBeanVisitor<JSONObject>(bean, new JsonDataSink());
      value = traverser.traverse(bean.metaBean(), visitor);
    }
    _json.put(propertyName, value);
  }

  @Override
  public JSONObject finish() {
    return new JSONObject(_json);
  }
}

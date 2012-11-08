/**
 * Copyright (C) 2012 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.web.analytics.json;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

import org.json.JSONObject;

import com.google.common.collect.ImmutableMap;
import com.opengamma.web.analytics.ViewportResultsJsonWriter;
import com.opengamma.web.analytics.rest.ViewportVersion;

/**
 *
 */
@Provider
@Produces(MediaType.APPLICATION_JSON)
public class ViewportVersionMessageBodyWriter implements MessageBodyWriter<ViewportVersion> {

  @Override
  public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
    return type.equals(ViewportVersion.class);
  }

  @Override
  public long getSize(ViewportVersion version,
                      Class<?> type,
                      Type genericType,
                      Annotation[] annotations,
                      MediaType mediaType) {
    return -1;
  }

  @Override
  public void writeTo(ViewportVersion version,
                      Class<?> type,
                      Type genericType,
                      Annotation[] annotations,
                      MediaType mediaType,
                      MultivaluedMap<String, Object> httpHeaders,
                      OutputStream entityStream) throws IOException, WebApplicationException {
    ImmutableMap<String, Long> map = ImmutableMap.of(ViewportResultsJsonWriter.VERSION, version.getVersion());
    entityStream.write(new JSONObject(map).toString().getBytes());
  }
}
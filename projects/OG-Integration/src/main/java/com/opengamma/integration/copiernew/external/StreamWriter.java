/**
 * Copyright (C) 2012 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.integration.copiernew.external;

import com.opengamma.OpenGammaRuntimeException;
import com.opengamma.id.UniqueId;
import com.opengamma.id.UniqueIdentifiable;
import com.opengamma.integration.copiernew.Writeable;

import com.opengamma.util.ArgumentChecker;
import com.thoughtworks.xstream.XStream;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamDriver;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.xml.StaxDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;

public class StreamWriter<T> implements Writeable<T> {

  private static final Logger s_logger = LoggerFactory.getLogger(StreamWriter.class);

  XStream _xStream;
  ObjectOutputStream _objectOutputStream;

  public StreamWriter(OutputStream outputStream) {
    this(outputStream, new StaxDriver()
//      {
//        public HierarchicalStreamWriter createWriter(OutputStream out) {
//          try {
//            return new StaxWriter(
//              getQnameMap(),
//              new IndentingXMLStreamWriter(getOutputFactory().createXMLStreamWriter(out)),
//              true,
//              isRepairingNamespace()
//            );
//          } catch (XMLStreamException e) {
//            throw new OpenGammaRuntimeException(e.getMessage(), e);
//          }
//        }
//      }
    );
  }

  public StreamWriter(OutputStream outputStream, HierarchicalStreamDriver driver) {
    ArgumentChecker.notNull(outputStream, "outputStream");
    ArgumentChecker.notNull(driver, "driver");
    _xStream = new XStream(driver);
    //_xStream.omitField(Object.class, "_uniqueId");
    _xStream.registerConverter(new Converter() {
      @Override
      public void marshal(Object o, HierarchicalStreamWriter hierarchicalStreamWriter, MarshallingContext marshallingContext) {
        // TODO
      }

      @Override
      public Object unmarshal(HierarchicalStreamReader hierarchicalStreamReader, UnmarshallingContext unmarshallingContext) {
        return null;  // TODO
      }

      @Override
      public boolean canConvert(Class aClass) {
        return aClass.equals(UniqueId.class);  // TODO
      }
    });

    try {
      _objectOutputStream = _xStream.createObjectOutputStream(outputStream);
    } catch (IOException e) {
      throw new OpenGammaRuntimeException("Could not create an ObjectOutputStream", e);
    }
  }

  @Override
  public void addOrUpdate(T datum) {
    try {
      datum.getClass().getMethod("setUniqueId", UniqueId.class).invoke(datum, new Object[] {null});
    } catch (Exception e) {
      // do nothing
    }
    try {
      _objectOutputStream.writeObject(datum);
    } catch (IOException e) {
      s_logger.error("Could not addOrUpdate: " + e.getMessage());
    }
  }

  @Override
  public void addOrUpdate(Iterable<T> data) {
    for (T datum : data) {
      addOrUpdate(datum);
    }
  }

  @Override
  public void flush() throws IOException {
    _objectOutputStream.flush();
  }

  public void close() throws IOException {
    _objectOutputStream.close();
  }
}
/**
 * Copyright (C) 2012 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.web.analytics.formatting;

import java.util.List;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.opengamma.engine.value.ValueSpecification;
import com.opengamma.financial.analytics.LabelledMatrix1D;
import com.opengamma.id.ExternalId;
import com.opengamma.util.ArgumentChecker;

/**
 *
 */
/* package */ class LabelledMatrix1DFormatter extends AbstractFormatter<LabelledMatrix1D> {

  private final DoubleFormatter _doubleFormatter;

  LabelledMatrix1DFormatter(DoubleFormatter doubleFormatter) {
    super(LabelledMatrix1D.class);
    ArgumentChecker.notNull(doubleFormatter, "doubleFormatter");
    _doubleFormatter = doubleFormatter;
    addFormatter(new Formatter<LabelledMatrix1D>(Format.EXPANDED) {
      @Override
      Object format(LabelledMatrix1D value, ValueSpecification valueSpec) {
        return formatExpanded(value, valueSpec);
      }
    });
  }

  @Override
  public String formatCell(LabelledMatrix1D value, ValueSpecification valueSpec) {
    return "Vector (" + value.getKeys().length + ")";
  }

  private List<List<String>> formatExpanded(LabelledMatrix1D value, ValueSpecification valueSpec) {
    int length = value.getKeys().length;
    List<List<String>> results = Lists.newArrayListWithCapacity(length);
    for (int i = 0; i < length; i++) {
      Object labelObject = value.getLabels()[i];
      String label = labelObject instanceof ExternalId ? ((ExternalId) labelObject).getValue() : labelObject.toString();
      String formattedValue = _doubleFormatter.formatCell(value.getValues()[i], valueSpec);
      List<String> rowResults = ImmutableList.of(label, formattedValue);
      results.add(rowResults);
    }
    return results;
  }

  @Override
  public DataType getDataType() {
    return DataType.LABELLED_MATRIX_1D;
  }
}

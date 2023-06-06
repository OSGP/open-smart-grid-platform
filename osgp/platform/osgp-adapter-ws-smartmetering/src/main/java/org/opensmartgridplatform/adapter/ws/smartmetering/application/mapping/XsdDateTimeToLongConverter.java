// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.ws.smartmetering.application.mapping;

import java.util.GregorianCalendar;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** convert a xsd datetime string to a date and back */
public class XsdDateTimeToLongConverter extends BidirectionalConverter<String, Long> {

  private static final Logger LOGGER = LoggerFactory.getLogger(XsdDateTimeToLongConverter.class);

  @Override
  public Long convertTo(
      final String source, final Type<Long> destinationType, final MappingContext context) {
    if (source == null || source.isEmpty()) {
      return null;
    }
    try {
      return DatatypeFactory.newInstance()
          .newXMLGregorianCalendar(source)
          .toGregorianCalendar()
          .getTimeInMillis();
    } catch (final DatatypeConfigurationException e) {
      LOGGER.warn("wrong datetime " + source, e);
      return null;
    }
  }

  @Override
  public String convertFrom(
      final Long source, final Type<String> destinationType, final MappingContext context) {
    if (source == null) {
      return null;
    }
    final GregorianCalendar cal = new GregorianCalendar();
    cal.setTimeInMillis(source);
    try {
      return DatatypeFactory.newInstance().newXMLGregorianCalendar(cal).toXMLFormat();
    } catch (final DatatypeConfigurationException e) {
      LOGGER.warn("datetime conversion problem" + source, e);
      return null;
    }
  }
}

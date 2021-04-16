/*
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.ws.smartmetering.application.mapping;

import java.util.Date;
import javax.xml.datatype.XMLGregorianCalendar;
import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.metadata.Type;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.monitoring.PowerQualityValue;

public class PowerQualityValueConverter
    extends CustomConverter<
        org.opensmartgridplatform.domain.core.valueobjects.smartmetering.PowerQualityValue,
        PowerQualityValue> {

  @Override
  public PowerQualityValue convert(
      final org.opensmartgridplatform.domain.core.valueobjects.smartmetering.PowerQualityValue
          source,
      final Type<? extends PowerQualityValue> destinationType,
      final MappingContext context) {
    if (source == null) {
      return null;
    }

    final PowerQualityValue result = new PowerQualityValue();
    if (source.getValue() instanceof Date) {
      final XMLGregorianCalendar xmlGregCal =
          this.mapperFacade.map(source.getValue(), XMLGregorianCalendar.class);
      result.setStringValueOrDateValueOrFloatValue(xmlGregCal);
    } else {
      result.setStringValueOrDateValueOrFloatValue(source.getValue());
    }
    return result;
  }
}

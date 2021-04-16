/*
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.ws.smartmetering.application.mapping;

import java.util.Date;
import javax.xml.datatype.XMLGregorianCalendar;
import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.metadata.Type;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.common.ProfileEntryValue;

public class ProfileEntryValueConverter
    extends CustomConverter<
        org.opensmartgridplatform.domain.core.valueobjects.smartmetering.ProfileEntryValue,
        ProfileEntryValue> {

  @Override
  public ProfileEntryValue convert(
      final org.opensmartgridplatform.domain.core.valueobjects.smartmetering.ProfileEntryValue
          source,
      final Type<? extends ProfileEntryValue> destinationType,
      final MappingContext context) {

    final ProfileEntryValue result = new ProfileEntryValue();
    if (source.getValue() instanceof Date) {
      final XMLGregorianCalendar xmlGregCal =
          this.mapperFacade.map(source.getValue(), XMLGregorianCalendar.class);
      result.getStringValueOrDateValueOrFloatValue().add(xmlGregCal);
    } else {
      result.getStringValueOrDateValueOrFloatValue().add(source.getValue());
    }
    return result;
  }
}

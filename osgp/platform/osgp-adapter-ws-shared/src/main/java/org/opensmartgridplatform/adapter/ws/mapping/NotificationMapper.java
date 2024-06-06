// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.ws.mapping;

import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.ConfigurableMapper;
import org.opensmartgridplatform.shared.mappers.XMLGregorianCalendarToDateTimeConverter;
import org.springframework.stereotype.Component;

@Component(value = "notificationMapper")
public class NotificationMapper extends ConfigurableMapper {
  @Override
  public void configure(final MapperFactory mapperFactory) {

    // This converter is needed to ensure correct mapping of dates and
    // times.
    mapperFactory
        .getConverterFactory()
        .registerConverter(new XMLGregorianCalendarToDateTimeConverter());
  }
}

// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.ws.smartmetering.application.mapping;

import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.ConfigurableMapper;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.management.MessageLog;
import org.opensmartgridplatform.logging.domain.entities.DeviceLogItem;
import org.opensmartgridplatform.shared.mappers.XMLGregorianCalendarToDateTimeConverter;
import org.springframework.stereotype.Component;

@Component(value = "smartMeteringManagementMapper")
public class ManagementMapper extends ConfigurableMapper {
  @Override
  public void configure(final MapperFactory mapperFactory) {

    // This converter is needed because Orika can't map byDefault from
    // object to enum or vice versa.
    mapperFactory.getConverterFactory().registerConverter(new EventConverter());

    // The following 2 converters are needed to ensure correct mapping of
    // dates and times.
    mapperFactory
        .getConverterFactory()
        .registerConverter(new XMLGregorianCalendarToDateTimeConverter());
    mapperFactory.getConverterFactory().registerConverter(new XsdDateTimeToLongConverter());

    mapperFactory.getConverterFactory().registerConverter(new EventMessageDataContainerConverter());

    mapperFactory
        .classMap(DeviceLogItem.class, MessageLog.class)
        .field("creationTime", "timestamp")
        .byDefault()
        .register();
  }
}

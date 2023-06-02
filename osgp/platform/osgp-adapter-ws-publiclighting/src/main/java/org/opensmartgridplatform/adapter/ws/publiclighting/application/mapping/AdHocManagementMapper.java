//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.adapter.ws.publiclighting.application.mapping;

import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.ConfigurableMapper;
import org.opensmartgridplatform.adapter.ws.publiclighting.application.mapping.converters.LightValueConverter;
import org.opensmartgridplatform.adapter.ws.publiclighting.application.mapping.converters.LmdConverter;
import org.opensmartgridplatform.adapter.ws.publiclighting.application.mapping.converters.SsldConverter;
import org.opensmartgridplatform.domain.core.valueobjects.DeviceStatus;
import org.opensmartgridplatform.domain.core.valueobjects.DeviceStatusMapped;
import org.opensmartgridplatform.domain.core.valueobjects.LightSensorStatus;
import org.opensmartgridplatform.shared.mappers.XMLGregorianCalendarToDateTimeConverter;
import org.springframework.stereotype.Component;

@Component(value = "publicLightingAdhocManagementMapper")
public class AdHocManagementMapper extends ConfigurableMapper {

  @Override
  public void configure(final MapperFactory mapperFactory) {
    mapperFactory
        .getConverterFactory()
        .registerConverter(new XMLGregorianCalendarToDateTimeConverter());
    mapperFactory.getConverterFactory().registerConverter(new SsldConverter());
    mapperFactory.getConverterFactory().registerConverter(new LmdConverter());
    mapperFactory.getConverterFactory().registerConverter(new LightValueConverter());

    mapperFactory
        .classMap(
            DeviceStatus.class,
            org.opensmartgridplatform.adapter.ws.schema.publiclighting.adhocmanagement.DeviceStatus
                .class)
        .byDefault()
        .register();
    mapperFactory
        .classMap(
            DeviceStatusMapped.class,
            org.opensmartgridplatform.adapter.ws.schema.publiclighting.adhocmanagement.DeviceStatus
                .class)
        .byDefault()
        .register();
    mapperFactory
        .classMap(
            LightSensorStatus.class,
            org.opensmartgridplatform.adapter.ws.schema.publiclighting.adhocmanagement
                .LightSensorStatus.class)
        .byDefault()
        .register();
  }
}

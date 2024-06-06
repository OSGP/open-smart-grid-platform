// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.ws.core.application.mapping;

import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.ConfigurableMapper;
import org.opensmartgridplatform.adapter.ws.core.application.mapping.ws.DaliConfigurationConverter;
import org.opensmartgridplatform.shared.mappers.XMLGregorianCalendarToDateTimeConverter;
import org.opensmartgridplatform.shared.mappers.XMLGregorianCalendarToZonedDateTimeConverter;
import org.springframework.stereotype.Component;

@Component(value = "coreConfigurationManagementMapper")
public class ConfigurationManagementMapper extends ConfigurableMapper {
  @Override
  public void configure(final MapperFactory mapperFactory) {
    mapperFactory.getConverterFactory().registerConverter(new DaliConfigurationConverter());
    // TODO remove after refactor of FDP-1558
    mapperFactory
        .getConverterFactory()
        .registerConverter(new XMLGregorianCalendarToDateTimeConverter());
    mapperFactory
        .getConverterFactory()
        .registerConverter(new XMLGregorianCalendarToZonedDateTimeConverter());
    mapperFactory.getConverterFactory().registerConverter(new ConfigurationConverter());
  }
}

// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.application.mapping;

import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.ConfigurableMapper;
import org.springframework.stereotype.Component;

@Component(value = "configurationMapper")
public class ConfigurationMapper extends ConfigurableMapper {

  @Override
  public void configure(final MapperFactory mapperFactory) {
    mapperFactory.getConverterFactory().registerConverter(new AdministrativeStatusTypeConverter());
    mapperFactory.getConverterFactory().registerConverter(new SeasonProfileConverter());
    mapperFactory.getConverterFactory().registerConverter(new WeekProfileConverter());
    mapperFactory.getConverterFactory().registerConverter(new DayProfileConverter());
    mapperFactory.getConverterFactory().registerConverter(new CosemDateTimeConverter());
    mapperFactory.getConverterFactory().registerConverter(new CosemTimeConverter());
    mapperFactory.getConverterFactory().registerConverter(new CaptureObjectDefinitionConverter());
  }
}

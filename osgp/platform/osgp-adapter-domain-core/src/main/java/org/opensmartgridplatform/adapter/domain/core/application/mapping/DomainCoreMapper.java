/*
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.domain.core.application.mapping;

import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.ConfigurableMapper;
import org.opensmartgridplatform.domain.core.valueobjects.FirmwareModuleType;
import org.opensmartgridplatform.domain.core.valueobjects.FirmwareVersion;
import org.opensmartgridplatform.dto.valueobjects.FirmwareVersionDto;
import org.springframework.stereotype.Component;

@Component
public class DomainCoreMapper extends ConfigurableMapper {

  @Override
  protected void configure(final MapperFactory factory) {
    factory.getConverterFactory().registerConverter(new ConfigurationConverter());
    factory.getConverterFactory().registerConverter(new DaliConfigurationConverter());
    factory.getConverterFactory().registerConverter(new LightTypeConverter());
    factory.getConverterFactory().registerConverter(new LinkTypeConverter());
    factory
        .classMap(
            FirmwareModuleType.class,
            org.opensmartgridplatform.dto.valueobjects.FirmwareModuleType.class)
        .byDefault()
        .register();
    factory.classMap(FirmwareVersion.class, FirmwareVersionDto.class).byDefault().register();
  }
}

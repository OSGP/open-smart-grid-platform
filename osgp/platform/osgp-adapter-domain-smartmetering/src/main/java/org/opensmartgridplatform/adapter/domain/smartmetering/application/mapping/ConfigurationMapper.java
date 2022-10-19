/*
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.domain.smartmetering.application.mapping;

import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.ConfigurableMapper;
import org.opensmartgridplatform.adapter.domain.smartmetering.application.mapping.customconverters.AdministrativeStatusResponseConverter;
import org.opensmartgridplatform.adapter.domain.smartmetering.application.mapping.customconverters.CosemDateTimeConverter;
import org.opensmartgridplatform.adapter.domain.smartmetering.application.mapping.customconverters.CosemObisCodeConverter;
import org.opensmartgridplatform.adapter.domain.smartmetering.application.mapping.customconverters.FirmwareVersionConverter;
import org.opensmartgridplatform.adapter.domain.smartmetering.application.mapping.customconverters.FirmwareVersionGasConverter;
import org.opensmartgridplatform.adapter.domain.smartmetering.application.mapping.customconverters.GetAllAttributeValuesResponseConverter;
import org.opensmartgridplatform.adapter.domain.smartmetering.application.mapping.customconverters.PushSetupAlarmDtoConverter;
import org.opensmartgridplatform.adapter.domain.smartmetering.application.mapping.customconverters.PushSetupLastGaspDtoConverter;
import org.opensmartgridplatform.adapter.domain.smartmetering.application.mapping.customconverters.PushSetupSmsDtoConverter;
import org.opensmartgridplatform.adapter.domain.smartmetering.application.mapping.customconverters.WeekProfileConverter;
import org.springframework.stereotype.Component;

@Component(value = "configurationMapper")
public class ConfigurationMapper extends ConfigurableMapper {

  @Override
  public void configure(final MapperFactory mapperFactory) {

    // This mapper needs a converter for CosemDateTime objects because
    // Orika sometimes throws an exception if mapping by default is tried
    mapperFactory.getConverterFactory().registerConverter(new CosemDateTimeConverter(this));
    mapperFactory
        .getConverterFactory()
        .registerConverter(new AdministrativeStatusResponseConverter());
    mapperFactory.getConverterFactory().registerConverter(new FirmwareVersionConverter());
    mapperFactory.getConverterFactory().registerConverter(new FirmwareVersionGasConverter());
    mapperFactory
        .getConverterFactory()
        .registerConverter(new GetAllAttributeValuesResponseConverter());
    mapperFactory.getConverterFactory().registerConverter(new WeekProfileConverter(this));
    mapperFactory.getConverterFactory().registerConverter(new PushSetupAlarmDtoConverter(this));
    mapperFactory.getConverterFactory().registerConverter(new PushSetupLastGaspDtoConverter(this));
    mapperFactory.getConverterFactory().registerConverter(new PushSetupSmsDtoConverter(this));
    mapperFactory.getConverterFactory().registerConverter(new CosemObisCodeConverter());
  }
}

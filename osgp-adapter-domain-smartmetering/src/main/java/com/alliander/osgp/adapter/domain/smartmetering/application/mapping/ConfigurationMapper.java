/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.domain.smartmetering.application.mapping;

import org.springframework.stereotype.Component;

import com.alliander.osgp.adapter.domain.smartmetering.application.mapping.customconverters.AdministrativeStatusResponseConverter;
import com.alliander.osgp.adapter.domain.smartmetering.application.mapping.customconverters.CosemDateTimeConverter;
import com.alliander.osgp.adapter.domain.smartmetering.application.mapping.customconverters.FirmwareVersionConverter;
import com.alliander.osgp.adapter.domain.smartmetering.application.mapping.customconverters.GetAllAttributeValuesResponseConverter;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.GetMBusEncryptionKeyStatusRequestData;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.GetMBusEncryptionKeyStatusResponseData;
import com.alliander.osgp.dto.valueobjects.smartmetering.GetMBusEncryptionKeyStatusRequestDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.GetMBusEncryptionKeyStatusResponseDto;

import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.ConfigurableMapper;

@Component(value = "configurationMapper")
public class ConfigurationMapper extends ConfigurableMapper {

    @Override
    public void configure(final MapperFactory mapperFactory) {

        // This mapper needs a converter for CosemDateTime objects because
        // Orika sometimes throws an exception if mapping by default is tried
        mapperFactory.getConverterFactory().registerConverter(new CosemDateTimeConverter(this));
        mapperFactory.getConverterFactory().registerConverter(new AdministrativeStatusResponseConverter());
        mapperFactory.getConverterFactory().registerConverter(new FirmwareVersionConverter());
        mapperFactory.getConverterFactory().registerConverter(new GetAllAttributeValuesResponseConverter());

        mapperFactory.classMap(GetMBusEncryptionKeyStatusRequestData.class, GetMBusEncryptionKeyStatusRequestDto.class)
                .field("MBusDeviceIdentification", "mBusDeviceIdentification").byDefault().register();
        mapperFactory
                .classMap(GetMBusEncryptionKeyStatusResponseDto.class, GetMBusEncryptionKeyStatusResponseData.class)
                .field("MBusDeviceIdentification", "mBusDeviceIdentification").byDefault().register();
    }
}

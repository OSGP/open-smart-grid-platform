/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.osgpfoundation.osgp.adapter.domain.da.application.mapping;

import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.converter.builtin.PassThroughConverter;
import ma.glasnost.orika.impl.ConfigurableMapper;
import org.joda.time.DateTime;
import org.osgpfoundation.osgp.domain.da.valueobjects.GetDeviceModelRequest;
import org.osgpfoundation.osgp.domain.da.valueobjects.GetHealthStatusRequest;
import org.osgpfoundation.osgp.domain.da.valueobjects.GetPQValuesPeriodicRequest;
import org.osgpfoundation.osgp.domain.da.valueobjects.GetPQValuesRequest;
import org.osgpfoundation.osgp.dto.da.GetDeviceModelRequestDto;
import org.osgpfoundation.osgp.dto.da.GetHealthStatusRequestDto;
import org.osgpfoundation.osgp.dto.da.GetPQValuesPeriodicRequestDto;
import org.osgpfoundation.osgp.dto.da.GetPQValuesRequestDto;
import org.springframework.stereotype.Component;

@Component
public class DomainDistributionAutomationMapper extends ConfigurableMapper {

    @Override
    protected void configure(final MapperFactory factory) {
        factory.getConverterFactory().registerConverter(new PassThroughConverter(DateTime.class));
        factory.classMap(GetDeviceModelRequest.class, GetDeviceModelRequestDto.class).byDefault().register();
        factory.classMap(GetHealthStatusRequest.class, GetHealthStatusRequestDto.class).byDefault().register();
        factory.classMap(GetPQValuesRequest.class, GetPQValuesRequestDto.class).byDefault().register();
        factory.classMap(GetPQValuesPeriodicRequest.class, GetPQValuesPeriodicRequestDto.class).byDefault().register();
    }
}

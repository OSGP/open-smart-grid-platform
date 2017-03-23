/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.smartsocietyservices.osgp.adapter.domain.da.application.mapping;

import com.smartsocietyservices.osgp.domain.da.valueobjects.MeasurementFilter;
import com.smartsocietyservices.osgp.domain.da.valueobjects.ProfileFilter;
import com.smartsocietyservices.osgp.domain.da.valueobjects.SystemFilter;
import com.smartsocietyservices.osgp.dto.da.MeasurementFilterDto;
import com.smartsocietyservices.osgp.dto.da.ProfileFilterDto;
import com.smartsocietyservices.osgp.dto.da.SystemFilterDto;
import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;

import java.util.List;

public class SystemFilterConverter extends BidirectionalConverter<SystemFilter, SystemFilterDto>
{

    @Override
    public SystemFilterDto convertTo( final SystemFilter source, final Type<SystemFilterDto> destinationType )
    {
        final List<MeasurementFilterDto> measurementFilters = this.mapperFacade
                .mapAsList( source.getMeasurementFilters(), MeasurementFilterDto.class );
        final List<ProfileFilterDto> profileFilters = this.mapperFacade.mapAsList( source.getProfileFilters(),
                ProfileFilterDto.class );

        return new SystemFilterDto( source.getId(), source.getSystemType(), measurementFilters, profileFilters,
                source.isAll() );
    }

    @Override
    public SystemFilter convertFrom( final SystemFilterDto source, final Type<SystemFilter> destinationType )
    {
        final List<MeasurementFilter> measurementFilters = this.mapperFacade.mapAsList( source.getMeasurementFilters(),
                MeasurementFilter.class );
        final List<ProfileFilter> profileFilters = this.mapperFacade.mapAsList( source.getProfileFilters(),
                ProfileFilter.class );

        return new SystemFilter( source.getId(), source.getSystemType(), measurementFilters, profileFilters,
                source.isAll() );
    }

}

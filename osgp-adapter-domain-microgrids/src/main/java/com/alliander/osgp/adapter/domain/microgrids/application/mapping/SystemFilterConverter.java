package com.alliander.osgp.adapter.domain.microgrids.application.mapping;

import java.util.List;

import com.alliander.osgp.domain.microgrids.valueobjects.MeasurementFilter;
import com.alliander.osgp.domain.microgrids.valueobjects.ProfileFilter;
import com.alliander.osgp.domain.microgrids.valueobjects.SystemFilter;
import com.alliander.osgp.dto.valueobjects.microgrids.MeasurementFilterDto;
import com.alliander.osgp.dto.valueobjects.microgrids.ProfileFilterDto;
import com.alliander.osgp.dto.valueobjects.microgrids.SystemFilterDto;

import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;

public class SystemFilterConverter extends BidirectionalConverter<SystemFilter, SystemFilterDto> {

    @Override
    public SystemFilterDto convertTo(final SystemFilter source, final Type<SystemFilterDto> destinationType) {
        final List<MeasurementFilterDto> measurementFilters = this.mapperFacade
                .mapAsList(source.getMeasurementFilters(), MeasurementFilterDto.class);
        final List<ProfileFilterDto> profileFilters = this.mapperFacade.mapAsList(source.getProfileFilters(),
                ProfileFilterDto.class);

        return new SystemFilterDto(source.getId(), source.getSystemType(), measurementFilters, profileFilters,
                source.isAll());
    }

    @Override
    public SystemFilter convertFrom(final SystemFilterDto source, final Type<SystemFilter> destinationType) {
        final List<MeasurementFilter> measurementFilters = this.mapperFacade.mapAsList(source.getMeasurementFilters(),
                MeasurementFilter.class);
        final List<ProfileFilter> profileFilters = this.mapperFacade.mapAsList(source.getProfileFilters(),
                ProfileFilter.class);

        return new SystemFilter(source.getId(), source.getSystemType(), measurementFilters, profileFilters,
                source.isAll());
    }

}

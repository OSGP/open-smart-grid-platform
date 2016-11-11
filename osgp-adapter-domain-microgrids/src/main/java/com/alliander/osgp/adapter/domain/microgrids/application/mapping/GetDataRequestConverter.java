package com.alliander.osgp.adapter.domain.microgrids.application.mapping;

import java.util.List;

import com.alliander.osgp.domain.microgrids.valueobjects.GetDataRequest;
import com.alliander.osgp.domain.microgrids.valueobjects.SystemFilter;
import com.alliander.osgp.dto.valueobjects.microgrids.GetDataRequestDto;
import com.alliander.osgp.dto.valueobjects.microgrids.SystemFilterDto;

import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;

public class GetDataRequestConverter extends BidirectionalConverter<GetDataRequest, GetDataRequestDto> {

    @Override
    public GetDataRequestDto convertTo(final GetDataRequest source, final Type<GetDataRequestDto> destinationType) {
        final List<SystemFilterDto> systemFilters = this.mapperFacade.mapAsList(source.getSystemFilters(),
                SystemFilterDto.class);

        return new GetDataRequestDto(systemFilters);
    }

    @Override
    public GetDataRequest convertFrom(final GetDataRequestDto source, final Type<GetDataRequest> destinationType) {
        final List<SystemFilter> systemFilters = this.mapperFacade.mapAsList(source.getSystemFilters(),
                SystemFilter.class);

        return new GetDataRequest(systemFilters);
    }

}

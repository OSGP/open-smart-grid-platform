package com.alliander.osgp.adapter.domain.smartmetering.application.mapping;

import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;

import com.alliander.osgp.dto.valueobjects.smartmetering.GetAdministration;

public class GetAdministrationConverter extends
        BidirectionalConverter<com.alliander.osgp.dto.valueobjects.smartmetering.GetAdministration, GetAdministration> {

    @Override
    public GetAdministration convertTo(
            final com.alliander.osgp.dto.valueobjects.smartmetering.GetAdministration source,
            final Type<GetAdministration> destinationType) {

        if (source == null) {
            return null;
        }

        final GetAdministration result = new GetAdministration();
        result.setDeviceIdentification(source.getDeviceIdentification());

        return result;
    }

    @Override
    public com.alliander.osgp.dto.valueobjects.smartmetering.GetAdministration convertFrom(
            final GetAdministration source,
            final Type<com.alliander.osgp.dto.valueobjects.smartmetering.GetAdministration> destinationType) {

        if (source == null) {
            return null;
        }

        final com.alliander.osgp.dto.valueobjects.smartmetering.GetAdministration result = new com.alliander.osgp.dto.valueobjects.smartmetering.GetAdministration();
        result.setDeviceIdentification(source.getDeviceIdentification());

        return result;
    }

}
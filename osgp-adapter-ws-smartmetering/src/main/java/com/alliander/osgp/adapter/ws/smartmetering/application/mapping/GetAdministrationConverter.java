package com.alliander.osgp.adapter.ws.smartmetering.application.mapping;

import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;

import com.alliander.osgp.domain.core.valueobjects.smartmetering.GetAdministration;

public class GetAdministrationConverter
extends
BidirectionalConverter<GetAdministration, com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.GetAdministrationRequest> {

    @Override
    public com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.GetAdministrationRequest convertTo(
            final GetAdministration source,
            final Type<com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.GetAdministrationRequest> destinationType) {

        if (source == null) {
            return null;
        }

        final com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.GetAdministrationRequest result = new com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.GetAdministrationRequest();
        result.setDeviceIdentification(source.getDeviceIdentification());

        return result;
    }

    @Override
    public GetAdministration convertFrom(
            final com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.GetAdministrationRequest source,
            final Type<GetAdministration> destinationType) {

        if (source == null) {
            return null;
        }

        final GetAdministration result = new GetAdministration();
        result.setDeviceIdentification(source.getDeviceIdentification());

        return result;
    }

}

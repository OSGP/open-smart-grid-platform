package com.alliander.osgp.adapter.ws.smartmetering.application.mapping;

import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;

import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.GetAdministrationRequest;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.GetAdministration;

public class GetAdministrationConverter extends BidirectionalConverter<GetAdministration, GetAdministrationRequest> {

    @Override
    public GetAdministrationRequest convertTo(final GetAdministration source,
            final Type<GetAdministrationRequest> destinationType) {

        if (source == null) {
            return null;
        }

        final GetAdministrationRequest result = new GetAdministrationRequest();
        result.setDeviceIdentification(source.getDeviceIdentification());

        return result;
    }

    @Override
    public GetAdministration convertFrom(final GetAdministrationRequest source,
            final Type<GetAdministration> destinationType) {

        if (source == null) {
            return null;
        }

        final GetAdministration result = new GetAdministration();
        result.setDeviceIdentification(source.getDeviceIdentification());

        return null;
    }

}

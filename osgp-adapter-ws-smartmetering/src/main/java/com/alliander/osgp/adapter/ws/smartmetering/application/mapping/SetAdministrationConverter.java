package com.alliander.osgp.adapter.ws.smartmetering.application.mapping;

import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;

import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.SetAdministrationRequest;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.SetAdministration;

public class SetAdministrationConverter extends BidirectionalConverter<SetAdministration, SetAdministrationRequest> {

    @Override
    public SetAdministrationRequest convertTo(final SetAdministration source,
            final Type<SetAdministrationRequest> destinationType) {

        if (source == null) {
            return null;
        }

        final SetAdministrationRequest result = new SetAdministrationRequest();

        result.setDeviceIdentification(source.getDeviceIdentification());
        result.setEnabled(source.isEnabled());

        return result;
    }

    @Override
    public SetAdministration convertFrom(final SetAdministrationRequest source,
            final Type<SetAdministration> destinationType) {

        if (source == null) {
            return null;
        }

        final SetAdministration result = new SetAdministration();
        result.setDeviceIdentification(source.getDeviceIdentification());
        result.setEnabled(source.isEnabled());

        return result;
    }

}

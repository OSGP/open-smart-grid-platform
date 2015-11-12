package com.alliander.osgp.adapter.domain.smartmetering.application.mapping;

import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;

import com.alliander.osgp.domain.core.valueobjects.smartmetering.SetAdministration;

public class SetAdministrationConverter extends
        BidirectionalConverter<com.alliander.osgp.dto.valueobjects.smartmetering.SetAdministration, SetAdministration> {

    @Override
    public SetAdministration convertTo(
            final com.alliander.osgp.dto.valueobjects.smartmetering.SetAdministration source,
            final Type<SetAdministration> destinationType) {

        if (source == null) {
            return null;
        }

        final SetAdministration result = new SetAdministration();
        result.setDeviceIdentification(source.getDeviceIdentification());
        result.setEnabled(source.isEnabled());

        return result;
    }

    @Override
    public com.alliander.osgp.dto.valueobjects.smartmetering.SetAdministration convertFrom(
            final SetAdministration source,
            final Type<com.alliander.osgp.dto.valueobjects.smartmetering.SetAdministration> destinationType) {

        if (source == null) {
            return null;
        }

        final com.alliander.osgp.dto.valueobjects.smartmetering.SetAdministration result = new com.alliander.osgp.dto.valueobjects.smartmetering.SetAdministration();
        result.setDeviceIdentification(source.getDeviceIdentification());
        result.setEnabled(source.isEnabled());

        return result;
    }

}

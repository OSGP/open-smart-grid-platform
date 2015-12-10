package com.alliander.osgp.adapter.domain.smartmetering.application.mapping;

import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;

import com.alliander.osgp.dto.valueobjects.smartmetering.GetAdministrationState;

public class GetAdministrationStateConverter
        extends
BidirectionalConverter<com.alliander.osgp.dto.valueobjects.smartmetering.GetAdministrationState, GetAdministrationState> {

    @Override
    public GetAdministrationState convertTo(
            final com.alliander.osgp.dto.valueobjects.smartmetering.GetAdministrationState source,
            final Type<GetAdministrationState> destinationType) {

        if (source == null) {
            return null;
        }

        GetAdministrationState result = null;

        switch (source.getStatus()) {
        case UNDEFINED:
            result = new GetAdministrationState(source.getDeviceIdentification(),
                    com.alliander.osgp.dto.valueobjects.smartmetering.AdministrationStateType.UNDEFINED);
            break;
        case OFF:
            result = new GetAdministrationState(source.getDeviceIdentification(),
                    com.alliander.osgp.dto.valueobjects.smartmetering.AdministrationStateType.OFF);
            break;
        case ON:
            result = new GetAdministrationState(source.getDeviceIdentification(),
                    com.alliander.osgp.dto.valueobjects.smartmetering.AdministrationStateType.ON);
            break;
        }

        return result;
    }

    @Override
    public com.alliander.osgp.dto.valueobjects.smartmetering.GetAdministrationState convertFrom(
            final GetAdministrationState source,
            final Type<com.alliander.osgp.dto.valueobjects.smartmetering.GetAdministrationState> destinationType) {

        if (source == null) {
            return null;
        }

        com.alliander.osgp.dto.valueobjects.smartmetering.GetAdministrationState result = null;

        switch (source.getStatus()) {
        case UNDEFINED:
            result = new com.alliander.osgp.dto.valueobjects.smartmetering.GetAdministrationState(
                    source.getDeviceIdentification(),
                    com.alliander.osgp.dto.valueobjects.smartmetering.AdministrationStateType.UNDEFINED);
            break;
        case OFF:
            result = new com.alliander.osgp.dto.valueobjects.smartmetering.GetAdministrationState(
                    source.getDeviceIdentification(),
                    com.alliander.osgp.dto.valueobjects.smartmetering.AdministrationStateType.OFF);
            break;
        case ON:
            result = new com.alliander.osgp.dto.valueobjects.smartmetering.GetAdministrationState(
                    source.getDeviceIdentification(),
                    com.alliander.osgp.dto.valueobjects.smartmetering.AdministrationStateType.ON);
            break;
        }

        return result;
    }

}
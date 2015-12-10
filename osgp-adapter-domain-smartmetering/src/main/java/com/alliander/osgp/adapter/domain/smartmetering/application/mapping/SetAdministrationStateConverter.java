/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.domain.smartmetering.application.mapping;

import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;

import com.alliander.osgp.domain.core.valueobjects.smartmetering.SetAdministrationState;

public class SetAdministrationStateConverter
        extends
        BidirectionalConverter<com.alliander.osgp.dto.valueobjects.smartmetering.SetAdministrationState, SetAdministrationState> {

    @Override
    public SetAdministrationState convertTo(
            final com.alliander.osgp.dto.valueobjects.smartmetering.SetAdministrationState source,
            final Type<SetAdministrationState> destinationType) {

        if (source == null) {
            return null;
        }

        SetAdministrationState result = null;

        switch (source.getStatus()) {
        case UNDEFINED:
            result = new SetAdministrationState(
                    com.alliander.osgp.domain.core.valueobjects.smartmetering.AdministrativeStateType.UNDEFINED,
                    source.getDeviceIdentification());
            break;

        case OFF:
            result = new SetAdministrationState(
                    com.alliander.osgp.domain.core.valueobjects.smartmetering.AdministrativeStateType.OFF,
                    source.getDeviceIdentification());

            break;
        case ON:
            result = new SetAdministrationState(
                    com.alliander.osgp.domain.core.valueobjects.smartmetering.AdministrativeStateType.ON,
                    source.getDeviceIdentification());

            break;
        }

        return result;
    }

    @Override
    public com.alliander.osgp.dto.valueobjects.smartmetering.SetAdministrationState convertFrom(
            final SetAdministrationState source,
            final Type<com.alliander.osgp.dto.valueobjects.smartmetering.SetAdministrationState> destinationType) {

        if (source == null) {
            return null;
        }

        com.alliander.osgp.dto.valueobjects.smartmetering.SetAdministrationState result = null;

        switch (source.getStatus()) {
        case UNDEFINED:
            result = new com.alliander.osgp.dto.valueobjects.smartmetering.SetAdministrationState(
                    com.alliander.osgp.dto.valueobjects.smartmetering.AdministrationStateType.UNDEFINED,
                    source.getDeviceIdentification());
            break;
        case OFF:
            result = new com.alliander.osgp.dto.valueobjects.smartmetering.SetAdministrationState(
                    com.alliander.osgp.dto.valueobjects.smartmetering.AdministrationStateType.OFF,
                    source.getDeviceIdentification());
            break;
        case ON:
            result = new com.alliander.osgp.dto.valueobjects.smartmetering.SetAdministrationState(
                    com.alliander.osgp.dto.valueobjects.smartmetering.AdministrationStateType.ON,
                    source.getDeviceIdentification());
            break;
        }

        return result;
    }

}

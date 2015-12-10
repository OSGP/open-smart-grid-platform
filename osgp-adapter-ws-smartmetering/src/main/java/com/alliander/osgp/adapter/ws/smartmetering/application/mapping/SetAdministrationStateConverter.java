/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.ws.smartmetering.application.mapping;

import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;

import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.SetAdministrationStateRequest;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.SetAdministrationState;

public class SetAdministrationStateConverter extends
BidirectionalConverter<SetAdministrationState, SetAdministrationStateRequest> {

    @Override
    public SetAdministrationStateRequest convertTo(final SetAdministrationState source,
            final Type<SetAdministrationStateRequest> destinationType) {

        if (source == null) {
            return null;
        }

        final SetAdministrationStateRequest result = new SetAdministrationStateRequest();

        result.setDeviceIdentification(source.getDeviceIdentification());
        // result.setEnabled(source.isEnabled());

        return result;
    }

    @Override
    public SetAdministrationState convertFrom(final SetAdministrationStateRequest source,
            final Type<SetAdministrationState> destinationType) {

        if (source == null) {
            return null;
        }

        // final SetAdministrationState result = new
        // SetAdministrationState(source.isEnabled(),
        // source.getDeviceIdentification());

        return null;
    }

}

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

import com.alliander.osgp.domain.core.valueobjects.smartmetering.AdministrativeState;

public class GetAdministrationStateConverter
extends
BidirectionalConverter<AdministrativeState, com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.GetAdministrationStateRequest> {

    @Override
    public com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.GetAdministrationStateRequest convertTo(
            final AdministrativeState source,
            final Type<com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.GetAdministrationStateRequest> destinationType) {

        if (source == null) {
            return null;
        }

        final com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.GetAdministrationStateRequest result = new com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.GetAdministrationStateRequest();
        result.setDeviceIdentification(source.getDeviceIdentification());

        return result;
    }

    @Override
    public AdministrativeState convertFrom(
            final com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.GetAdministrationStateRequest source,
            final Type<AdministrativeState> destinationType) {

        if (source == null) {
            return null;
        }

        return new AdministrativeState(source.getDeviceIdentification());
    }

}

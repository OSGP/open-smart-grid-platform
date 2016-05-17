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

import com.alliander.osgp.domain.core.valueobjects.smartmetering.SetKeysRequestData;

public class SetKeysRequestConverter extends
        BidirectionalConverter<SetKeysRequestData, com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.SetKeysRequestData> {

    @Override
    public com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.SetKeysRequestData convertTo(final SetKeysRequestData source,
            final Type<com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.SetKeysRequestData> destinationType) {
        if (source == null) {
            return null;
        }

        final com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.SetKeysRequestData destination = new com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.SetKeysRequestData();
        destination.setAuthenticationKey(source.getAuthenticationKey());
        destination.setEncryptionKey(source.getEncryptionKey());

        return destination;
    }

    @Override
    public SetKeysRequestData convertFrom(final com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.SetKeysRequestData source,
            final Type<SetKeysRequestData> destinationType) {
        if (source == null) {
            return null;
        }

        return new SetKeysRequestData(source.getAuthenticationKey(), source.getEncryptionKey());
    }
}

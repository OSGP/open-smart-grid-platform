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

import com.alliander.osgp.domain.core.valueobjects.smartmetering.KeySet;

public class KeySetConverter extends
        BidirectionalConverter<KeySet, com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.KeySet> {

    @Override
    public com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.KeySet convertTo(final KeySet source,
            final Type<com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.KeySet> destinationType) {
        if (source == null) {
            return null;
        }

        final com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.KeySet destination = new com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.KeySet();
        destination.setAuthenticationKey(source.getAuthenticationKey());
        destination.setEncryptionKey(source.getEncryptionKey());

        return destination;
    }

    @Override
    public KeySet convertFrom(final com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.KeySet source,
            final Type<KeySet> destinationType) {
        if (source == null) {
            return null;
        }

        return new KeySet(source.getAuthenticationKey(), source.getEncryptionKey());
    }
}

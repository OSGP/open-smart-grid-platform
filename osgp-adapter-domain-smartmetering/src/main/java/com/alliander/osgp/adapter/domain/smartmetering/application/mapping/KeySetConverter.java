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

import com.alliander.osgp.domain.core.valueobjects.smartmetering.KeySet;
import com.alliander.osgp.dto.valueobjects.smartmetering.KeySetDto;

public class KeySetConverter extends BidirectionalConverter<KeySetDto, KeySet> {

    @Override
    public KeySet convertTo(final KeySetDto source, final Type<KeySet> destinationType) {
        if (source == null) {
            return null;
        }

        return new KeySet(source.getAuthenticationKey(), source.getEncryptionKey());
    }

    @Override
    public KeySetDto convertFrom(final KeySet source, final Type<KeySetDto> destinationType) {
        if (source == null) {
            return null;
        }

        return new KeySetDto(source.getAuthenticationKey(), source.getEncryptionKey());
    }
}

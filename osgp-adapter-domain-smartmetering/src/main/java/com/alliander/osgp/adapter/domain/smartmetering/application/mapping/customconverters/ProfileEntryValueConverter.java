/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.domain.smartmetering.application.mapping.customconverters;

import java.math.BigDecimal;
import java.util.Date;

import com.alliander.osgp.domain.core.valueobjects.smartmetering.ProfileEntryValue;
import com.alliander.osgp.dto.valueobjects.smartmetering.ProfileEntryValueDto;

import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.metadata.Type;

public class ProfileEntryValueConverter extends CustomConverter<ProfileEntryValueDto, ProfileEntryValue> {

    @Override
    public ProfileEntryValue convert(final ProfileEntryValueDto source,
            final Type<? extends ProfileEntryValue> destinationType, final MappingContext context) {
        final Object value = source.getValue();
        if (value != null) {
            if (value instanceof Long) {
                return new ProfileEntryValue((Long) value);
            } else if (value instanceof Date) {
                return new ProfileEntryValue((Date) value);
            } else if (value instanceof BigDecimal) {
                return new ProfileEntryValue((BigDecimal) value);
            } else {
                return new ProfileEntryValue((String) value);
            }
        } else {
            return new ProfileEntryValue((String) null);
        }
    }

}

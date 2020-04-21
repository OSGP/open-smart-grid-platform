/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.domain.smartmetering.application.mapping.customconverters;

import java.math.BigDecimal;
import java.util.Date;

import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.metadata.Type;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.ProfileEntryValue;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ProfileEntryValueDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProfileEntryValueConverter extends CustomConverter<ProfileEntryValueDto, ProfileEntryValue> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProfileEntryValueConverter.class);

    @Override
    public ProfileEntryValue convert(final ProfileEntryValueDto source,
            final Type<? extends ProfileEntryValue> destinationType, final MappingContext context) {
        final Object value = source.getValue();
        if (value != null) {
            if (value instanceof BigDecimal) {

                LOGGER.info("-------------------- Found BigDecimal");

                return new ProfileEntryValue((BigDecimal) value);
            } else if (value instanceof Float) {

                LOGGER.info("-------------------- Found Float");

                return new ProfileEntryValue((Float) value);
            } else if (value instanceof Long) {

                LOGGER.info("-------------------- Found Long");

                return new ProfileEntryValue((Long) value);
            } else if (value instanceof Date) {

                LOGGER.info("-------------------- Found Date");

                return new ProfileEntryValue((Date) value);
            } else {

                LOGGER.info("-------------------- Found String");
                return new ProfileEntryValue((String) value);
            }
        } else {
            return new ProfileEntryValue(null);
        }
    }

}

/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.domain.smartmetering.application.mapping;

import java.util.HashSet;
import java.util.Set;

import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;

import org.springframework.stereotype.Component;

import com.alliander.osgp.domain.core.valueobjects.smartmetering.AmrProfileStatusCode;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.AmrProfileStatusCodeFlag;

@Component
public class AmrProfileStatusCodeConverter
        extends
        BidirectionalConverter<com.alliander.osgp.dto.valueobjects.smartmetering.AmrProfileStatusCodeDto, AmrProfileStatusCode> {

    @Override
    public AmrProfileStatusCode convertTo(
            final com.alliander.osgp.dto.valueobjects.smartmetering.AmrProfileStatusCodeDto source,
            final Type<AmrProfileStatusCode> destinationType) {

        final Set<AmrProfileStatusCodeFlag> resultStatusCodeFlags = new HashSet<>();
        final Set<com.alliander.osgp.dto.valueobjects.smartmetering.AmrProfileStatusCodeFlagDto> sourceStatusCodeFlags = source
                .getAmrProfileStatusCodeFlags();
        for (final com.alliander.osgp.dto.valueobjects.smartmetering.AmrProfileStatusCodeFlagDto sourceStatusCodeFlag : sourceStatusCodeFlags) {
            resultStatusCodeFlags.add(AmrProfileStatusCodeFlag.valueOf(sourceStatusCodeFlag.value()));
        }

        return new AmrProfileStatusCode(resultStatusCodeFlags);
    }

    @Override
    public com.alliander.osgp.dto.valueobjects.smartmetering.AmrProfileStatusCodeDto convertFrom(
            final AmrProfileStatusCode source,
            final Type<com.alliander.osgp.dto.valueobjects.smartmetering.AmrProfileStatusCodeDto> destinationType) {

        final Set<com.alliander.osgp.dto.valueobjects.smartmetering.AmrProfileStatusCodeFlagDto> resultStatusCodeFlags = new HashSet<>();
        final Set<AmrProfileStatusCodeFlag> sourceStatusCodeFlags = source.getAmrProfileStatusCodeFlags();
        for (final AmrProfileStatusCodeFlag sourceStatusCodeFlag : sourceStatusCodeFlags) {
            resultStatusCodeFlags.add(com.alliander.osgp.dto.valueobjects.smartmetering.AmrProfileStatusCodeFlagDto
                    .valueOf(sourceStatusCodeFlag.value()));
        }

        return new com.alliander.osgp.dto.valueobjects.smartmetering.AmrProfileStatusCodeDto(resultStatusCodeFlags);
    }

}

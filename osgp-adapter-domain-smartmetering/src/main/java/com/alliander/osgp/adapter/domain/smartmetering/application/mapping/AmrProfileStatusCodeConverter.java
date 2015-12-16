package com.alliander.osgp.adapter.domain.smartmetering.application.mapping;

import java.util.HashSet;
import java.util.Set;

import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;

import com.alliander.osgp.domain.core.valueobjects.smartmetering.AmrProfileStatusCodeFlag;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.AmrProfileStatusCode;

public class AmrProfileStatusCodeConverter
extends
BidirectionalConverter<com.alliander.osgp.dto.valueobjects.smartmetering.AmrProfileStatusCode, AmrProfileStatusCode> {

    @Override
    public AmrProfileStatusCode convertTo(
            final com.alliander.osgp.dto.valueobjects.smartmetering.AmrProfileStatusCode source,
            final Type<AmrProfileStatusCode> destinationType) {

        final Set<AmrProfileStatusCodeFlag> resultStatusCodeFlags = new HashSet<>();
        final Set<com.alliander.osgp.dto.valueobjects.smartmetering.AmrProfileStatusCodeFlag> sourceStatusCodeFlags = source
                .getAmrProfileStatusCodeFlags();
        for (final com.alliander.osgp.dto.valueobjects.smartmetering.AmrProfileStatusCodeFlag sourceStatusCodeFlag : sourceStatusCodeFlags) {
            resultStatusCodeFlags.add(AmrProfileStatusCodeFlag.valueOf(sourceStatusCodeFlag.value()));
        }

        return new AmrProfileStatusCode(resultStatusCodeFlags);
    }

    @Override
    public com.alliander.osgp.dto.valueobjects.smartmetering.AmrProfileStatusCode convertFrom(
            final AmrProfileStatusCode source,
            final Type<com.alliander.osgp.dto.valueobjects.smartmetering.AmrProfileStatusCode> destinationType) {

        final Set<com.alliander.osgp.dto.valueobjects.smartmetering.AmrProfileStatusCodeFlag> resultStatusCodeFlags = new HashSet<>();
        final Set<AmrProfileStatusCodeFlag> sourceStatusCodeFlags = source.getAmrProfileStatusCodeFlags();
        for (final AmrProfileStatusCodeFlag sourceStatusCodeFlag : sourceStatusCodeFlags) {
            resultStatusCodeFlags.add(com.alliander.osgp.dto.valueobjects.smartmetering.AmrProfileStatusCodeFlag.valueOf(sourceStatusCodeFlag
                    .value()));
        }

        return new com.alliander.osgp.dto.valueobjects.smartmetering.AmrProfileStatusCode(resultStatusCodeFlags);
    }

}

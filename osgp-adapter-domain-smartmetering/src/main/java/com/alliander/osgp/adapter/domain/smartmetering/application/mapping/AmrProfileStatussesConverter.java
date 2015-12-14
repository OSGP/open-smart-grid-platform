package com.alliander.osgp.adapter.domain.smartmetering.application.mapping;

import java.util.HashSet;
import java.util.Set;

import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;

import com.alliander.osgp.domain.core.valueobjects.smartmetering.AmrProfileStatus;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.AmrProfileStatusses;

public class AmrProfileStatussesConverter
extends
BidirectionalConverter<com.alliander.osgp.dto.valueobjects.smartmetering.AmrProfileStatusses, AmrProfileStatusses> {

    @Override
    public AmrProfileStatusses convertTo(
            final com.alliander.osgp.dto.valueobjects.smartmetering.AmrProfileStatusses source,
            final Type<AmrProfileStatusses> destinationType) {

        final Set<AmrProfileStatus> resultStatusses = new HashSet<>();
        final Set<com.alliander.osgp.dto.valueobjects.smartmetering.AmrProfileStatus> sourceStatusses = source
                .getAmrProfileStatuss();
        for (final com.alliander.osgp.dto.valueobjects.smartmetering.AmrProfileStatus sourceStatus : sourceStatusses) {
            resultStatusses.add(AmrProfileStatus.valueOf(sourceStatus.value()));
        }

        return new AmrProfileStatusses(resultStatusses);
    }

    @Override
    public com.alliander.osgp.dto.valueobjects.smartmetering.AmrProfileStatusses convertFrom(
            final AmrProfileStatusses source,
            final Type<com.alliander.osgp.dto.valueobjects.smartmetering.AmrProfileStatusses> destinationType) {

        final Set<com.alliander.osgp.dto.valueobjects.smartmetering.AmrProfileStatus> resultStatusses = new HashSet<>();
        final Set<AmrProfileStatus> sourceStatusses = source.getAmrProfileStatusses();
        for (final AmrProfileStatus sourceStatus : sourceStatusses) {
            resultStatusses.add(com.alliander.osgp.dto.valueobjects.smartmetering.AmrProfileStatus.valueOf(sourceStatus
                    .value()));
        }

        return new com.alliander.osgp.dto.valueobjects.smartmetering.AmrProfileStatusses(resultStatusses);
    }

}

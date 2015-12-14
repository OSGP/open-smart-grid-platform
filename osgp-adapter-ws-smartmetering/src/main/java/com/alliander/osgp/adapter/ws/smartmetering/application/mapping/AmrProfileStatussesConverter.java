package com.alliander.osgp.adapter.ws.smartmetering.application.mapping;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;

import com.alliander.osgp.domain.core.valueobjects.smartmetering.AmrProfileStatus;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.AmrProfileStatusses;

public class AmrProfileStatussesConverter
extends
BidirectionalConverter<AmrProfileStatusses, com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.AmrProfileStatusses> {

    @Override
    public com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.AmrProfileStatusses convertTo(
            final AmrProfileStatusses source,
            final Type<com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.AmrProfileStatusses> destinationType) {

        final com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.AmrProfileStatusses result = new com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.AmrProfileStatusses();
        final List<com.alliander.osgp.adapter.ws.schema.smartmetering.common.AmrProfileStatus> resultStatusses = result
                .getAmrProfileStatus();

        final Set<AmrProfileStatus> sourceStatusses = source.getAmrProfileStatusses();
        for (final AmrProfileStatus sourceStatus : sourceStatusses) {
            resultStatusses.add(com.alliander.osgp.adapter.ws.schema.smartmetering.common.AmrProfileStatus
                    .valueOf(sourceStatus.value()));
        }

        return result;
    }

    @Override
    public AmrProfileStatusses convertFrom(
            final com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.AmrProfileStatusses source,
            final Type<AmrProfileStatusses> destinationType) {

        final Set<AmrProfileStatus> resultStatusses = new HashSet<>();
        final List<com.alliander.osgp.adapter.ws.schema.smartmetering.common.AmrProfileStatus> sourceStatusses = source
                .getAmrProfileStatus();

        for (final com.alliander.osgp.adapter.ws.schema.smartmetering.common.AmrProfileStatus sourceStatus : sourceStatusses) {
            resultStatusses.add(AmrProfileStatus.valueOf(sourceStatus.value()));
        }

        return new AmrProfileStatusses(resultStatusses);
    }

}

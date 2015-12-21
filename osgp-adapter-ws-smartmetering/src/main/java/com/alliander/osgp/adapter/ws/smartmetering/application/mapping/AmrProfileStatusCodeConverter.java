/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.ws.smartmetering.application.mapping;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;

import com.alliander.osgp.domain.core.valueobjects.smartmetering.AmrProfileStatusCodeFlag;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.AmrProfileStatusCode;

public class AmrProfileStatusCodeConverter
        extends
        BidirectionalConverter<AmrProfileStatusCode, com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.AmrProfileStatusCode> {

    @Override
    public com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.AmrProfileStatusCode convertTo(
            final AmrProfileStatusCode source,
            final Type<com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.AmrProfileStatusCode> destinationType) {

        final com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.AmrProfileStatusCode result = new com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.AmrProfileStatusCode();
        final List<com.alliander.osgp.adapter.ws.schema.smartmetering.common.AmrProfileStatusCodeFlag> resultStatusCodeFlags = result
                .getAmrProfileStatusCodeFlag();

        final Set<AmrProfileStatusCodeFlag> sourceStatusCodeFlags = source.getAmrProfileStatusCodeFlags();
        for (final AmrProfileStatusCodeFlag sourceStatusCodeFlag : sourceStatusCodeFlags) {
            resultStatusCodeFlags.add(com.alliander.osgp.adapter.ws.schema.smartmetering.common.AmrProfileStatusCodeFlag
                    .valueOf(sourceStatusCodeFlag.value()));
        }

        return result;
    }

    @Override
    public AmrProfileStatusCode convertFrom(
            final com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.AmrProfileStatusCode source,
            final Type<AmrProfileStatusCode> destinationType) {

        final Set<AmrProfileStatusCodeFlag> resultStatusCodeFlags = new HashSet<>();
        final List<com.alliander.osgp.adapter.ws.schema.smartmetering.common.AmrProfileStatusCodeFlag> sourceStatusCodeFlags = source
                .getAmrProfileStatusCodeFlag();

        for (final com.alliander.osgp.adapter.ws.schema.smartmetering.common.AmrProfileStatusCodeFlag sourceStatusCodeFlag : sourceStatusCodeFlags) {
            resultStatusCodeFlags.add(AmrProfileStatusCodeFlag.valueOf(sourceStatusCodeFlag.value()));
        }

        return new AmrProfileStatusCode(resultStatusCodeFlags);
    }

}

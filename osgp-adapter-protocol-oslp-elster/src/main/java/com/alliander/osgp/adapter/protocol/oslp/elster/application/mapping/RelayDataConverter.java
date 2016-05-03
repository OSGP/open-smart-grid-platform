/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.protocol.oslp.elster.application.mapping;

import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;

import com.alliander.osgp.dto.valueobjects.RelayDataDto;
import com.alliander.osgp.oslp.Oslp;
import com.google.protobuf.ByteString;

public class RelayDataConverter extends BidirectionalConverter<RelayDataDto, Oslp.RelayData> {

    @Override
    public RelayDataDto convertFrom(final Oslp.RelayData source, final Type<RelayDataDto> destinationType) {

        int index = 0;
        if (source.hasIndex()) {
            index = source.getIndex().byteAt(0);
        }

        int totalLightingMinutes = 0;
        if (source.hasTotalLightingMinutes()) {
            totalLightingMinutes = source.getTotalLightingMinutes();
        }

        return new RelayDataDto(index, totalLightingMinutes);
    }

    @Override
    public Oslp.RelayData convertTo(final RelayDataDto source, final Type<Oslp.RelayData> destinationType) {

        final ByteString index = this.mapperFacade.map(Integer.valueOf(source.getIndex()), ByteString.class);
        final int totalLightingMinutes = source.getTotalLightingMinutes();

        return Oslp.RelayData.newBuilder().setIndex(index).setTotalLightingMinutes(totalLightingMinutes).build();
    }
}

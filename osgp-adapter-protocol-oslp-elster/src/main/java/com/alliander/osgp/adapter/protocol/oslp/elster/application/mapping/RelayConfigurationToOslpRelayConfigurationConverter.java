/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.protocol.oslp.elster.application.mapping;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.alliander.osgp.dto.valueobjects.RelayConfigurationDto;
import com.alliander.osgp.dto.valueobjects.RelayMapDto;
import com.alliander.osgp.dto.valueobjects.RelayTypeDto;
import com.alliander.osgp.oslp.Oslp;
import com.alliander.osgp.oslp.Oslp.IndexAddressMap;
import com.google.protobuf.ByteString;

import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;

public class RelayConfigurationToOslpRelayConfigurationConverter
        extends BidirectionalConverter<RelayConfigurationDto, Oslp.RelayConfiguration> {
    @Override
    public com.alliander.osgp.oslp.Oslp.RelayConfiguration convertTo(final RelayConfigurationDto source,
            final Type<com.alliander.osgp.oslp.Oslp.RelayConfiguration> destinationType, final MappingContext context) {
        final Oslp.RelayConfiguration.Builder relayConfiguration = Oslp.RelayConfiguration.newBuilder();

        if (source.getRelayMap() != null) {
            // Not very pretty, this could (presumably) be cleaned up by proper
            // use of Orika.
            for (final RelayMapDto entry : source.getRelayMap()) {
                // Map null to OSLP RT_NOT_SET
                relayConfiguration.addAddressMap(Oslp.IndexAddressMap.newBuilder()
                        .setIndex(this.mapperFacade.map(entry.getIndex(), ByteString.class))
                        .setAddress(this.mapperFacade.map(entry.getAddress(), ByteString.class))
                        .setRelayType(entry.getRelayType() != null
                                ? this.mapperFacade.map(entry.getRelayType(), Oslp.RelayType.class)
                                : Oslp.RelayType.RT_NOT_SET));
            }
        }

        return relayConfiguration.build();
    }

    @Override
    public RelayConfigurationDto convertFrom(final com.alliander.osgp.oslp.Oslp.RelayConfiguration source,
            final Type<RelayConfigurationDto> destinationType, final MappingContext context) {

        if (source == null) {
            return null;
        }

        // Map the relay configuration.
        final List<RelayMapDto> indexAddressMap = new ArrayList<>();
        for (final IndexAddressMap entry : source.getAddressMapList()) {
            // Map OSLP RT_NOT_SET to null
            indexAddressMap.add(new RelayMapDto(this.mapperFacade.map(entry.getIndex(), Integer.class),
                    this.mapperFacade.map(entry.getAddress(), Integer.class),
                    entry.hasRelayType() && entry.getRelayType() != Oslp.RelayType.RT_NOT_SET
                            ? this.mapperFacade.map(entry.getRelayType(), RelayTypeDto.class)
                            : null,
                    null));
        }

        // Sort the relay configuration on index.
        Collections.sort(indexAddressMap, new Comparator<RelayMapDto>() {
            @Override
            public int compare(final RelayMapDto o1, final RelayMapDto o2) {
                return o1.getIndex().compareTo(o2.getIndex());
            }
        });

        return new RelayConfigurationDto(indexAddressMap);
    }
}

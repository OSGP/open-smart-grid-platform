/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.protocol.oslp.application.mapping;

import java.util.ArrayList;
import java.util.List;

import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;

import com.alliander.osgp.dto.valueobjects.RelayConfiguration;
import com.alliander.osgp.dto.valueobjects.RelayMap;
import com.alliander.osgp.dto.valueobjects.RelayType;
import com.alliander.osgp.oslp.Oslp;
import com.alliander.osgp.oslp.Oslp.IndexAddressMap;
import com.google.protobuf.ByteString;

public class RelayConfigurationToOslpRelayConfigurationConverter extends
        BidirectionalConverter<RelayConfiguration, Oslp.RelayConfiguration> {
    @Override
    public com.alliander.osgp.oslp.Oslp.RelayConfiguration convertTo(final RelayConfiguration source,
            final Type<com.alliander.osgp.oslp.Oslp.RelayConfiguration> destinationType) {
        final Oslp.RelayConfiguration.Builder relayConfiguration = Oslp.RelayConfiguration.newBuilder();

        if (source.getRelayMap() != null) {
            // Not very pretty, this could (presumably) be cleaned up by proper
            // use of Orika.
            for (final RelayMap entry : source.getRelayMap()) {
                // Map null to OSLP RT_NOT_SET
                relayConfiguration.addAddressMap(Oslp.IndexAddressMap
                        .newBuilder()
                        .setIndex(this.mapperFacade.map(entry.getIndex(), ByteString.class))
                        .setAddress(this.mapperFacade.map(entry.getAddress(), ByteString.class))
                        .setRelayType(
                                entry.getRelayType() != null ? this.mapperFacade.map(entry.getRelayType(),
                                        Oslp.RelayType.class) : Oslp.RelayType.RT_NOT_SET));
            }
        }

        return relayConfiguration.build();
    }

    @Override
    public RelayConfiguration convertFrom(final com.alliander.osgp.oslp.Oslp.RelayConfiguration source,
            final Type<RelayConfiguration> destinationType) {

        if (source == null) {
            return null;
        }

        final List<RelayMap> indexAddressMap = new ArrayList<RelayMap>();
        for (final IndexAddressMap entry : source.getAddressMapList()) {
            // Map OSLP RT_NOT_SET to null
            indexAddressMap.add(new RelayMap(this.mapperFacade.map(entry.getIndex(), Integer.class), this.mapperFacade
                    .map(entry.getAddress(), Integer.class), entry.hasRelayType()
                    && entry.getRelayType() != Oslp.RelayType.RT_NOT_SET ? this.mapperFacade.map(entry.getRelayType(),
                    RelayType.class) : null));
        }

        final List<RelayMap> relayMaps = new ArrayList<RelayMap>();
        return new RelayConfiguration(!indexAddressMap.isEmpty() ? indexAddressMap : relayMaps);
    }
}

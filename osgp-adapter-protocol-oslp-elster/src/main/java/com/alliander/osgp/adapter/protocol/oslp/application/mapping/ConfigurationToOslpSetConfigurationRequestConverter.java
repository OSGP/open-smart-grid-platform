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

import com.alliander.osgp.dto.valueobjects.Configuration;
import com.alliander.osgp.dto.valueobjects.RelayMatrix;
import com.alliander.osgp.oslp.Oslp;
import com.alliander.osgp.oslp.Oslp.SetConfigurationRequest;
import com.google.protobuf.ByteString;

public class ConfigurationToOslpSetConfigurationRequestConverter extends BidirectionalConverter<Configuration, Oslp.SetConfigurationRequest> {

    @Override
    public SetConfigurationRequest convertTo(final Configuration source, final Type<SetConfigurationRequest> destinationType) {

        final Oslp.SetConfigurationRequest.Builder setConfigurationRequest = Oslp.SetConfigurationRequest.newBuilder();

        if (source.getLightType() != null) {
            setConfigurationRequest.setLightType(this.mapperFacade.map(source.getLightType(), Oslp.LightType.class));
        }

        if (source.getDaliConfiguration() != null) {
            setConfigurationRequest.setDaliConfiguration(this.mapperFacade.map(source.getDaliConfiguration(),
                    Oslp.DaliConfiguration.class));
        }

        if (source.getRelayConfiguration() != null) {
            setConfigurationRequest.setRelayConfiguration(this.mapperFacade.map(source.getRelayConfiguration(),
                    Oslp.RelayConfiguration.class));
        }

        if (source.getShortTermHistoryIntervalMinutes() != null) {
            setConfigurationRequest.setShortTermHistoryIntervalMinutes(this.mapperFacade.map(
                    source.getShortTermHistoryIntervalMinutes(), Integer.class));
        }

        if (source.getLongTermHistoryInterval() != null) {
            setConfigurationRequest.setLongTermHistoryInterval(this.mapperFacade.map(
                    source.getLongTermHistoryInterval(), Integer.class));
        }

        if (source.getLongTermHistoryIntervalType() != null) {
            setConfigurationRequest.setLongTermHistoryIntervalType(this.mapperFacade.map(
                    source.getLongTermHistoryIntervalType(), Oslp.LongTermIntervalType.class));
        }

        if (source.getPreferredLinkType() != null) {
            setConfigurationRequest.setPreferredLinkType(this.mapperFacade.map(source.getPreferredLinkType(),
                    Oslp.LinkType.class));
        }

        if (source.getMeterType() != null) {
            setConfigurationRequest.setMeterType(this.mapperFacade.map(source.getMeterType(), Oslp.MeterType.class));
        }

        setConfigurationRequest.setAstroGateSunRiseOffset(source.getAstroGateSunRiseOffset());
        setConfigurationRequest.setAstroGateSunSetOffset(source.getAstroGateSunSetOffset());
        setConfigurationRequest.setIsAutomaticSummerTimingEnabled(source.isAutomaticSummerTimingEnabled());
        setConfigurationRequest.setCommunicationNumberOfRetries(source.getCommunicationNumberOfRetries());
        setConfigurationRequest.setCommunicationPauseTimeBetweenConnectionTrials(source.getCommunicationPauseTimeBetweenConnectionTrials());
        setConfigurationRequest.setCommunicationTimeout(source.getCommunicationTimeout());
        setConfigurationRequest.setDeviceFixIpValue(ByteString.copyFromUtf8(source.getDeviceFixIpValue()));
        setConfigurationRequest.setIsDhcpEnabled(source.isDhcpEnabled());
        setConfigurationRequest.setOsgpPortNumber(source.getOsgpPortNumber());
        setConfigurationRequest.setOspgIpAddress(ByteString.copyFromUtf8(source.getOspgIpAddress()));
        setConfigurationRequest.setRelayRefreshing(source.isRelayRefreshing());
        setConfigurationRequest.setSummerTimeDetails(source.getSummerTimeDetails());
        setConfigurationRequest.setIsTestButtonEnabled(source.isTestButtonEnabled());
        setConfigurationRequest.setTimeSyncFrequency(source.getTimeSyncFrequency());
        setConfigurationRequest.setWinterTimeDetails(source.getWinterTimeDetails());
        setConfigurationRequest.addAllSwitchingDelay(source.getSwitchingDelay());

        final List<com.alliander.osgp.oslp.Oslp.RelayMatrix> oslpRelayMatrix = new ArrayList<>();
        for (final RelayMatrix relayMatrix : source.getRelayLinking()) {

            final Oslp.RelayMatrix newRelayMatrix = Oslp.RelayMatrix.newBuilder()
                    .setIndicesOfControlledRelaysOff(ByteString.copyFromUtf8(relayMatrix.getIndicesOfControlledRelaysOff()))
                    .setIndicesOfControlledRelaysOn(ByteString.copyFromUtf8(relayMatrix.getIndicesOfControlledRelaysOn()))
                    .setMasterRelayIndex(ByteString.copyFromUtf8(relayMatrix.getMasterRelayIndex()))
                    .setMasterRelayOn(relayMatrix.isMasterRelayOn()).build();

            oslpRelayMatrix.add(newRelayMatrix);
        }
        setConfigurationRequest.addAllRelayLinking(oslpRelayMatrix);

        return setConfigurationRequest.build();
    }

    @Override
    public Configuration convertFrom(final SetConfigurationRequest source, final Type<Configuration> destinationType) {

        final Configuration configuration = new Configuration(com.alliander.osgp.dto.valueobjects.LightType.valueOf(source.getLightType().name())
                , this.mapperFacade.map(source.getDaliConfiguration(), com.alliander.osgp.dto.valueobjects.DaliConfiguration.class)
                , this.mapperFacade.map(source.getRelayConfiguration(), com.alliander.osgp.dto.valueobjects.RelayConfiguration.class)
                , source.getShortTermHistoryIntervalMinutes()
                , this.mapperFacade.map(source.getPreferredLinkType(), com.alliander.osgp.dto.valueobjects.LinkType.class)
                , this.mapperFacade.map(source.getMeterType(), com.alliander.osgp.dto.valueobjects.MeterType.class)
                , source.getLongTermHistoryInterval()
                , this.mapperFacade.map(source.getLongTermHistoryIntervalType(), com.alliander.osgp.dto.valueobjects.LongTermIntervalType.class));

        configuration.setAstroGateSunRiseOffset(source.getAstroGateSunRiseOffset());
        configuration.setAstroGateSunSetOffset(source.getAstroGateSunSetOffset());
        configuration.setAutomaticSummerTimingEnabled(source.getIsAutomaticSummerTimingEnabled());
        configuration.setCommunicationNumberOfRetries(source.getCommunicationNumberOfRetries());
        configuration.setCommunicationPauseTimeBetweenConnectionTrials(source.getCommunicationPauseTimeBetweenConnectionTrials());
        configuration.setCommunicationTimeout(source.getCommunicationTimeout());
        configuration.setDeviceFixIpValue(source.getDeviceFixIpValue().toStringUtf8());
        configuration.setDhcpEnabled(source.getIsDhcpEnabled());
        configuration.setOsgpPortNumber(source.getOsgpPortNumber());
        configuration.setOspgIpAddress(source.getOspgIpAddress().toStringUtf8());
        configuration.setRelayRefreshing(source.getRelayRefreshing());
        configuration.setSummerTimeDetails(source.getSummerTimeDetails());
        configuration.setTestButtonEnabled(source.getIsTestButtonEnabled());
        configuration.setTimeSyncFrequency(source.getTimeSyncFrequency());
        configuration.setWinterTimeDetails(source.getWinterTimeDetails());
        configuration.setSwitchingDelay(source.getSwitchingDelayList());

        final List<RelayMatrix> relayMatrix = new ArrayList<RelayMatrix>();
        for (final com.alliander.osgp.oslp.Oslp.RelayMatrix matrix : source.getRelayLinkingList()) {
            final RelayMatrix newRelayMatrix = new RelayMatrix(matrix.getMasterRelayIndex().toStringUtf8(),
                    matrix.getMasterRelayOn());

            newRelayMatrix.setIndicesOfControlledRelaysOff(matrix.getIndicesOfControlledRelaysOff().toStringUtf8());
            newRelayMatrix.setIndicesOfControlledRelaysOn(matrix.getIndicesOfControlledRelaysOn().toStringUtf8());
            relayMatrix.add(newRelayMatrix);
        }
        configuration.setRelayLinking(relayMatrix);

        return configuration;
    }

}

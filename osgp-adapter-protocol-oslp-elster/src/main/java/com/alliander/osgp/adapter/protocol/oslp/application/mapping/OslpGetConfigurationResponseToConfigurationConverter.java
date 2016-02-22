/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.protocol.oslp.application.mapping;

import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.metadata.Type;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alliander.osgp.dto.valueobjects.Configuration;
import com.alliander.osgp.dto.valueobjects.DaliConfiguration;
import com.alliander.osgp.dto.valueobjects.LightType;
import com.alliander.osgp.dto.valueobjects.LinkType;
import com.alliander.osgp.dto.valueobjects.LongTermIntervalType;
import com.alliander.osgp.dto.valueobjects.MeterType;
import com.alliander.osgp.dto.valueobjects.RelayConfiguration;
import com.alliander.osgp.dto.valueobjects.RelayMatrix;
import com.alliander.osgp.oslp.Oslp;
import com.google.protobuf.ByteString;

public class OslpGetConfigurationResponseToConfigurationConverter extends
CustomConverter<Oslp.GetConfigurationResponse, Configuration> {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(OslpGetConfigurationResponseToConfigurationConverter.class);

    @Override
    public Configuration convert(final Oslp.GetConfigurationResponse source,
            final Type<? extends Configuration> destinationType) {
        final Configuration configuration = new Configuration(source.hasLightType() ? this.mapperFacade.map(
                source.getLightType(), LightType.class) : null, source.hasDaliConfiguration() ? this.mapperFacade.map(
                        source.getDaliConfiguration(), DaliConfiguration.class) : null,
                        source.hasRelayConfiguration() ? this.mapperFacade.map(source.getRelayConfiguration(),
                                RelayConfiguration.class) : null,
                                source.hasShortTermHistoryIntervalMinutes() ? this.mapperFacade.map(
                                        source.getShortTermHistoryIntervalMinutes(), Integer.class) : null,
                                        source.hasPreferredLinkType() ? this.mapperFacade.map(source.getPreferredLinkType(), LinkType.class)
                                                : null, source.hasMeterType() ? this.mapperFacade.map(source.getMeterType(), MeterType.class)
                                                        : null, source.hasLongTermHistoryInterval() ? this.mapperFacade.map(
                                                                source.getLongTermHistoryInterval(), Integer.class) : null,
                                                                source.hasLongTermHistoryIntervalType() ? this.mapperFacade.map(
                                                                        source.getLongTermHistoryIntervalType(), LongTermIntervalType.class) : null);

        configuration.setTimeSyncFrequency(source.getTimeSyncFrequency());
        if (source.getDeviceFixIpValue() != null && !source.getDeviceFixIpValue().isEmpty()) {
            configuration.setDeviceFixIpValue(this.convertIpAddress(source.getDeviceFixIpValue()));
        }
        configuration.setDhcpEnabled(source.getIsDhcpEnabled());
        configuration.setCommunicationTimeout(source.getCommunicationTimeout());
        configuration.setCommunicationNumberOfRetries(source.getCommunicationNumberOfRetries());
        configuration.setCommunicationPauseTimeBetweenConnectionTrials(source
                .getCommunicationPauseTimeBetweenConnectionTrials());
        if (source.getOspgIpAddress() != null && !source.getOspgIpAddress().isEmpty()) {
            configuration.setOspgIpAddress(this.convertIpAddress(source.getOspgIpAddress()));
        }
        configuration.setOsgpPortNumber(source.getOsgpPortNumber());
        configuration.setTestButtonEnabled(source.getIsTestButtonEnabled());
        configuration.setAutomaticSummerTimingEnabled(source.getIsAutomaticSummerTimingEnabled());
        configuration.setAstroGateSunRiseOffset(source.getAstroGateSunRiseOffset());
        configuration.setAstroGateSunSetOffset(source.getAstroGateSunSetOffset());
        configuration.setSwitchingDelays(source.getSwitchingDelayList());
        if (source.getRelayLinkingList() != null) {
            configuration.setRelayLinking(this.mapperFacade.mapAsList(source.getRelayLinkingList(), RelayMatrix.class));
        }
        configuration.setRelayRefreshing(source.getRelayRefreshing());
        configuration.setSummerTimeDetails(source.getSummerTimeDetails());
        configuration.setWinterTimeDetails(source.getWinterTimeDetails());

        return configuration;
    }

    private String convertIpAddress(final ByteString byteString) {
        LOGGER.debug("byteString.toByteArray().length(): {}", byteString.toByteArray().length);

        final StringBuilder stringBuilder = new StringBuilder();
        for (final byte number : byteString.toByteArray()) {
            int convertedNumber = number;
            if (number < 0) {
                convertedNumber = 256 + number;
            }
            final String str = String.valueOf(convertedNumber);
            stringBuilder.append(str).append(".");
        }
        final String ipValue = stringBuilder.toString();
        return ipValue.substring(0, ipValue.length() - 1);
        // try {
        // return new String(ipAddress.getBytes("UTF-8"));
        // } catch (final UnsupportedEncodingException e) {
        // LOGGER.error("UnsupportedEncodingException", e);
        // return null;
        // }
    }
}

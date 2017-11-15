/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.domain.core.application.mapping;

import com.alliander.osgp.domain.core.valueobjects.Configuration;
import com.alliander.osgp.domain.core.valueobjects.DaliConfiguration;
import com.alliander.osgp.domain.core.valueobjects.DeviceFixedIp;
import com.alliander.osgp.domain.core.valueobjects.LightType;
import com.alliander.osgp.domain.core.valueobjects.LinkType;
import com.alliander.osgp.domain.core.valueobjects.LongTermIntervalType;
import com.alliander.osgp.domain.core.valueobjects.MeterType;
import com.alliander.osgp.domain.core.valueobjects.RelayConfiguration;
import com.alliander.osgp.dto.valueobjects.DeviceFixedIpDto;

import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;

public class ConfigurationConverter
        extends BidirectionalConverter<com.alliander.osgp.dto.valueobjects.ConfigurationDto, Configuration> {

    @Override
    public Configuration convertTo(final com.alliander.osgp.dto.valueobjects.ConfigurationDto source,
            final Type<Configuration> destinationType, final MappingContext context) {

        final LightType lightType = this.mapperFacade.map(source.getLightType(), LightType.class);

        final DaliConfiguration daliConfiguration = this.mapperFacade.map(source.getDaliConfiguration(),
                DaliConfiguration.class);

        final RelayConfiguration relayConfiguration = this.mapperFacade.map(source.getRelayConfiguration(),
                RelayConfiguration.class);

        final Integer shortTermHistoryIntervalMinutes = this.mapperFacade
                .map(source.getShortTermHistoryIntervalMinutes(), Integer.class);

        final LinkType preferredLinkType = this.mapperFacade.map(source.getPreferredLinkType(), LinkType.class);

        final MeterType meterType = this.mapperFacade.map(source.getMeterType(), MeterType.class);

        final Integer longTermHistoryInterval = this.mapperFacade.map(source.getLongTermHistoryInterval(),
                Integer.class);

        final LongTermIntervalType longTermHistoryIntervalType = this.mapperFacade
                .map(source.getLongTermHistoryIntervalType(), LongTermIntervalType.class);

        final Configuration configuration = new Configuration(lightType, daliConfiguration, relayConfiguration,
                shortTermHistoryIntervalMinutes, preferredLinkType, meterType, longTermHistoryInterval,
                longTermHistoryIntervalType);

        configuration.setAstroGateSunRiseOffset(source.getAstroGateSunRiseOffset());
        configuration.setAstroGateSunSetOffset(source.getAstroGateSunSetOffset());
        configuration.setAutomaticSummerTimingEnabled(source.isAutomaticSummerTimingEnabled());
        configuration.setCommunicationNumberOfRetries(source.getCommunicationNumberOfRetries());
        configuration.setCommunicationPauseTimeBetweenConnectionTrials(
                source.getCommunicationPauseTimeBetweenConnectionTrials());
        configuration.setCommunicationTimeout(source.getCommunicationTimeout());
        configuration.setDhcpEnabled(source.isDhcpEnabled());
        configuration.setTlsEnabled(source.isTlsEnabled());
        configuration.setTlsPortNumber(source.getTlsPortNumber());
        configuration.setCommonNameString(source.getCommonNameString());
        configuration.setOsgpPortNumber(source.getOsgpPortNumber());
        configuration.setOsgpIpAddress(source.getOsgpIpAddres());
        configuration.setNtpHost(source.getNtpHost());
        configuration.setNtpEnabled(source.getNtpEnabled());
        configuration.setNtpSyncInterval(source.getNtpSyncInterval());
        configuration.setRelayRefreshing(source.isRelayRefreshing());
        configuration.setSummerTimeDetails(source.getSummerTimeDetails());
        configuration.setSwitchingDelays(source.getSwitchingDelays());
        configuration.setTestButtonEnabled(source.isTestButtonEnabled());
        configuration.setTimeSyncFrequency(source.getTimeSyncFrequency());
        configuration.setWinterTimeDetails(source.getWinterTimeDetails());
        if (source.getRelayLinking() != null) {
            configuration.setRelayLinking(this.mapperFacade.mapAsList(source.getRelayLinking(),
                    com.alliander.osgp.domain.core.valueobjects.RelayMatrix.class));
        }

        if (source.getDeviceFixedIp() != null) {
            configuration.setDeviceFixedIp(this.mapperFacade.map(source.getDeviceFixedIp(), DeviceFixedIp.class));
        }

        return configuration;
    }

    @Override
    public com.alliander.osgp.dto.valueobjects.ConfigurationDto convertFrom(final Configuration source,
            final Type<com.alliander.osgp.dto.valueobjects.ConfigurationDto> destinationType,
            final MappingContext context) {

        final com.alliander.osgp.dto.valueobjects.LightTypeDto lightType = this.mapperFacade.map(source.getLightType(),
                com.alliander.osgp.dto.valueobjects.LightTypeDto.class);

        final com.alliander.osgp.dto.valueobjects.DaliConfigurationDto daliConfiguration = this.mapperFacade
                .map(source.getDaliConfiguration(), com.alliander.osgp.dto.valueobjects.DaliConfigurationDto.class);

        final com.alliander.osgp.dto.valueobjects.RelayConfigurationDto relayConfiguration = this.mapperFacade
                .map(source.getRelayConfiguration(), com.alliander.osgp.dto.valueobjects.RelayConfigurationDto.class);

        final Integer shortTermHistoryIntervalMinutes = this.mapperFacade
                .map(source.getShortTermHistoryIntervalMinutes(), Integer.class);

        final com.alliander.osgp.dto.valueobjects.LinkTypeDto preferredLinkType = this.mapperFacade
                .map(source.getPreferredLinkType(), com.alliander.osgp.dto.valueobjects.LinkTypeDto.class);

        final com.alliander.osgp.dto.valueobjects.MeterTypeDto meterType = this.mapperFacade.map(source.getMeterType(),
                com.alliander.osgp.dto.valueobjects.MeterTypeDto.class);

        final Integer longTermHistoryInterval = this.mapperFacade.map(source.getLongTermHistoryInterval(),
                Integer.class);

        final com.alliander.osgp.dto.valueobjects.LongTermIntervalTypeDto longTermHistoryIntervalType = this.mapperFacade
                .map(source.getLongTermHistoryIntervalType(),
                        com.alliander.osgp.dto.valueobjects.LongTermIntervalTypeDto.class);

        final com.alliander.osgp.dto.valueobjects.ConfigurationDto configuration = new com.alliander.osgp.dto.valueobjects.ConfigurationDto(
                lightType, daliConfiguration, relayConfiguration, shortTermHistoryIntervalMinutes, preferredLinkType,
                meterType, longTermHistoryInterval, longTermHistoryIntervalType);

        configuration.setAstroGateSunRiseOffset(source.getAstroGateSunRiseOffset());
        configuration.setAstroGateSunSetOffset(source.getAstroGateSunSetOffset());
        configuration.setAutomaticSummerTimingEnabled(source.isAutomaticSummerTimingEnabled());
        configuration.setCommunicationNumberOfRetries(source.getCommunicationNumberOfRetries());
        configuration.setCommunicationPauseTimeBetweenConnectionTrials(
                source.getCommunicationPauseTimeBetweenConnectionTrials());
        configuration.setCommunicationTimeout(source.getCommunicationTimeout());
        configuration.setDhcpEnabled(source.isDhcpEnabled());
        configuration.setTlsEnabled(source.isTlsEnabled());
        configuration.setTlsPortNumber(source.getTlsPortNumber());
        configuration.setCommonNameString(source.getCommonNameString());
        configuration.setOsgpPortNumber(source.getOsgpPortNumber());
        configuration.setOsgpIpAddress(source.getOsgpIpAddress());
        configuration.setNtpHost(source.getNtpHost());
        configuration.setNtpEnabled(source.getNtpEnabled());
        configuration.setNtpSyncInterval(source.getNtpSyncInterval());
        configuration.setRelayRefreshing(source.isRelayRefreshing());
        configuration.setSummerTimeDetails(source.getSummerTimeDetails());
        configuration.setSwitchingDelays(source.getSwitchingDelays());
        configuration.setTestButtonEnabled(source.isTestButtonEnabled());
        configuration.setTimeSyncFrequency(source.getTimeSyncFrequency());
        configuration.setWinterTimeDetails(source.getWinterTimeDetails());
        if (source.getRelayLinking() != null) {
            configuration.setRelayLinking(this.mapperFacade.mapAsList(source.getRelayLinking(),
                    com.alliander.osgp.dto.valueobjects.RelayMatrixDto.class));
        }

        if (source.getDeviceFixedIp() != null) {
            configuration.setDeviceFixedIp(this.mapperFacade.map(source.getDeviceFixedIp(), DeviceFixedIpDto.class));
        }

        return configuration;
    }

}

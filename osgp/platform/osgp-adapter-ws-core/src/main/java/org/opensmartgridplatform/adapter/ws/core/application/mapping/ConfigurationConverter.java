/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.ws.core.application.mapping;

import javax.xml.datatype.XMLGregorianCalendar;

import org.joda.time.DateTime;
import org.opensmartgridplatform.adapter.ws.schema.core.configurationmanagement.Configuration;
import org.opensmartgridplatform.domain.core.valueobjects.DaliConfiguration;
import org.opensmartgridplatform.domain.core.valueobjects.DeviceFixedIp;
import org.opensmartgridplatform.domain.core.valueobjects.LightType;
import org.opensmartgridplatform.domain.core.valueobjects.LinkType;
import org.opensmartgridplatform.domain.core.valueobjects.RelayConfiguration;
import org.opensmartgridplatform.domain.core.valueobjects.RelayMatrix;

import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;

public class ConfigurationConverter extends
        BidirectionalConverter<Configuration, org.opensmartgridplatform.domain.core.valueobjects.Configuration> {

    @Override
    public org.opensmartgridplatform.domain.core.valueobjects.Configuration convertTo(final Configuration source,
            final Type<org.opensmartgridplatform.domain.core.valueobjects.Configuration> destinationType,
            final MappingContext mappingContext) {
        if (source == null) {
            return null;
        }

        return new org.opensmartgridplatform.domain.core.valueobjects.Configuration.Builder()
                .withLightType(this.mapperFacade.map(source.getLightType(), LightType.class))
                .withDaliConfiguration(this.mapperFacade.map(source.getDaliConfiguration(), DaliConfiguration.class))
                .withRelayConfiguration(this.mapperFacade.map(source.getRelayConfiguration(), RelayConfiguration.class))
                .withPreferredLinkType(this.mapperFacade.map(source.getPreferredLinkType(), LinkType.class))
                .withTimeSyncFrequency(source.getTimeSyncFrequency())
                .withDeviceFixedIp(this.mapperFacade.map(source.getDeviceFixedIp(), DeviceFixedIp.class))
                .withDhcpEnabled(source.isDhcpEnabled())
                .withCommunicationTimeout(source.getCommunicationTimeout())
                .withCommunicationNumberOfRetries(source.getCommunicationNumberOfRetries())
                .withCommunicationPauseTimeBetweenConnectionTrials(
                        source.getCommunicationPauseTimeBetweenConnectionTrials())
                .withOsgpIpAddress(source.getOsgpIpAddress())
                .withOsgpPortNumber(source.getOsgpPortNumber())
                .withNtpHost(source.getNtpHost())
                .withNtpEnabled(source.isNtpEnabled())
                .withNtpSyncInterval(source.getNtpSyncInterval())
                .withTestButtonEnabled(source.isTestButtonEnabled())
                .withAutomaticSummerTimingEnabled(source.isAutomaticSummerTimingEnabled())
                .withAstroGateSunRiseOffset(source.getAstroGateSunRiseOffset())
                .withAstroGateSunSetOffset(source.getAstroGateSunSetOffset())
                .withSwitchingDelays(source.getSwitchingDelays())
                .withRelayLinking(this.mapperFacade.mapAsList(source.getRelayLinking(), RelayMatrix.class))
                .withRelayRefreshing(source.isRelayRefreshing())
                .withSummerTimeDetails(this.mapperFacade.map(source.getSummerTimeDetails(), DateTime.class))
                .withWinterTimeDetails(this.mapperFacade.map(source.getWinterTimeDetails(), DateTime.class))
                .build();
    }

    @Override
    public Configuration convertFrom(final org.opensmartgridplatform.domain.core.valueobjects.Configuration source,
            final Type<Configuration> destinationType, final MappingContext mappingContext) {
        if (source == null) {
            return null;
        }
        final Configuration dest = new Configuration();
        dest.setLightType(this.mapperFacade.map(source.getLightType(),
                org.opensmartgridplatform.adapter.ws.schema.core.configurationmanagement.LightType.class));
        dest.setDaliConfiguration(this.mapperFacade.map(source.getDaliConfiguration(),
                org.opensmartgridplatform.adapter.ws.schema.core.configurationmanagement.DaliConfiguration.class));
        dest.setRelayConfiguration(this.mapperFacade.map(source.getRelayConfiguration(),
                org.opensmartgridplatform.adapter.ws.schema.core.configurationmanagement.RelayConfiguration.class));
        dest.setPreferredLinkType(this.mapperFacade.map(source.getPreferredLinkType(),
                org.opensmartgridplatform.adapter.ws.schema.core.configurationmanagement.LinkType.class));
        dest.setTimeSyncFrequency(source.getTimeSyncFrequency());
        dest.setDeviceFixedIp(this.mapperFacade.map(source.getDeviceFixedIp(),
                org.opensmartgridplatform.adapter.ws.schema.core.configurationmanagement.DeviceFixedIp.class));
        dest.setDhcpEnabled(source.isDhcpEnabled());
        dest.setCommunicationTimeout(source.getCommunicationTimeout());
        dest.setCommunicationNumberOfRetries(source.getCommunicationNumberOfRetries());
        dest.setCommunicationPauseTimeBetweenConnectionTrials(
                source.getCommunicationPauseTimeBetweenConnectionTrials());
        dest.setOsgpIpAddress(source.getOsgpIpAddress());
        dest.setOsgpPortNumber(source.getOsgpPortNumber());
        dest.setNtpHost(source.getNtpHost());
        dest.setNtpEnabled(source.getNtpEnabled());
        dest.setNtpSyncInterval(source.getNtpSyncInterval());
        dest.setTestButtonEnabled(source.isTestButtonEnabled());
        dest.setAutomaticSummerTimingEnabled(source.isAutomaticSummerTimingEnabled());
        dest.setAstroGateSunRiseOffset(source.getAstroGateSunRiseOffset());
        dest.setAstroGateSunSetOffset(source.getAstroGateSunSetOffset());
        dest.getSwitchingDelays().addAll(source.getSwitchingDelays());
        dest.getRelayLinking()
                .addAll(this.mapperFacade.mapAsList(source.getRelayLinking(),
                        org.opensmartgridplatform.adapter.ws.schema.core.configurationmanagement.RelayMatrix.class));
        dest.setRelayRefreshing(source.isRelayRefreshing());
        dest.setSummerTimeDetails(this.mapperFacade.map(source.getSummerTimeDetails(), XMLGregorianCalendar.class));
        dest.setWinterTimeDetails(this.mapperFacade.map(source.getWinterTimeDetails(), XMLGregorianCalendar.class));
        return dest;
    }
}

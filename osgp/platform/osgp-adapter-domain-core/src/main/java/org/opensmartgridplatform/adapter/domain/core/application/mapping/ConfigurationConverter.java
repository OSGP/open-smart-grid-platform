/*
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.domain.core.application.mapping;

import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;
import org.opensmartgridplatform.domain.core.valueobjects.Configuration;
import org.opensmartgridplatform.domain.core.valueobjects.DaliConfiguration;
import org.opensmartgridplatform.domain.core.valueobjects.DeviceFixedIp;
import org.opensmartgridplatform.domain.core.valueobjects.LightType;
import org.opensmartgridplatform.domain.core.valueobjects.LinkType;
import org.opensmartgridplatform.domain.core.valueobjects.RelayConfiguration;
import org.opensmartgridplatform.dto.valueobjects.DeviceFixedIpDto;

public class ConfigurationConverter
    extends BidirectionalConverter<
        org.opensmartgridplatform.dto.valueobjects.ConfigurationDto, Configuration> {

  @Override
  public Configuration convertTo(
      final org.opensmartgridplatform.dto.valueobjects.ConfigurationDto source,
      final Type<Configuration> destinationType,
      final MappingContext context) {

    final LightType lightType = this.mapperFacade.map(source.getLightType(), LightType.class);

    final DaliConfiguration daliConfiguration =
        this.mapperFacade.map(source.getDaliConfiguration(), DaliConfiguration.class);

    final RelayConfiguration relayConfiguration =
        this.mapperFacade.map(source.getRelayConfiguration(), RelayConfiguration.class);

    final LinkType preferredLinkType =
        this.mapperFacade.map(source.getPreferredLinkType(), LinkType.class);

    final Configuration.Builder builder =
        new Configuration.Builder()
            .withLightType(lightType)
            .withDaliConfiguration(daliConfiguration)
            .withRelayConfiguration(relayConfiguration)
            .withPreferredLinkType(preferredLinkType)
            .withTimeSyncFrequency(source.getTimeSyncFrequency())
            .withDhcpEnabled(source.isDhcpEnabled())
            .withTlsEnabled(source.isTlsEnabled())
            .withTlsPortNumber(source.getTlsPortNumber())
            .withCommonNameString(source.getCommonNameString())
            .withCommunicationTimeout(source.getCommunicationTimeout())
            .withCommunicationNumberOfRetries(source.getCommunicationNumberOfRetries())
            .withCommunicationPauseTimeBetweenConnectionTrials(
                source.getCommunicationPauseTimeBetweenConnectionTrials())
            .withOsgpIpAddress(source.getOsgpIpAddres())
            .withOsgpPortNumber(source.getOsgpPortNumber())
            .withNtpHost(source.getNtpHost())
            .withNtpEnabled(source.getNtpEnabled())
            .withNtpSyncInterval(source.getNtpSyncInterval())
            .withTestButtonEnabled(source.isTestButtonEnabled())
            .withAutomaticSummerTimingEnabled(source.isAutomaticSummerTimingEnabled())
            .withAstroGateSunRiseOffset(source.getAstroGateSunRiseOffset())
            .withAstroGateSunSetOffset(source.getAstroGateSunSetOffset())
            .withSwitchingDelays(source.getSwitchingDelays())
            .withRelayRefreshing(source.isRelayRefreshing())
            .withSummerTimeDetails(source.getSummerTimeDetails())
            .withWinterTimeDetails(source.getWinterTimeDetails());

    if (source.getRelayLinking() != null) {
      builder.withRelayLinking(
          this.mapperFacade.mapAsList(
              source.getRelayLinking(),
              org.opensmartgridplatform.domain.core.valueobjects.RelayMatrix.class));
    }
    if (source.getDeviceFixedIp() != null) {
      builder.withDeviceFixedIp(
          this.mapperFacade.map(source.getDeviceFixedIp(), DeviceFixedIp.class));
    }

    return builder.build();
  }

  @Override
  public org.opensmartgridplatform.dto.valueobjects.ConfigurationDto convertFrom(
      final Configuration source,
      final Type<org.opensmartgridplatform.dto.valueobjects.ConfigurationDto> destinationType,
      final MappingContext context) {

    final org.opensmartgridplatform.dto.valueobjects.LightTypeDto lightType =
        this.mapperFacade.map(
            source.getLightType(), org.opensmartgridplatform.dto.valueobjects.LightTypeDto.class);

    final org.opensmartgridplatform.dto.valueobjects.DaliConfigurationDto daliConfiguration =
        this.mapperFacade.map(
            source.getDaliConfiguration(),
            org.opensmartgridplatform.dto.valueobjects.DaliConfigurationDto.class);

    final org.opensmartgridplatform.dto.valueobjects.RelayConfigurationDto relayConfiguration =
        this.mapperFacade.map(
            source.getRelayConfiguration(),
            org.opensmartgridplatform.dto.valueobjects.RelayConfigurationDto.class);

    final org.opensmartgridplatform.dto.valueobjects.LinkTypeDto preferredLinkType =
        this.mapperFacade.map(
            source.getPreferredLinkType(),
            org.opensmartgridplatform.dto.valueobjects.LinkTypeDto.class);

    final org.opensmartgridplatform.dto.valueobjects.ConfigurationDto configuration =
        org.opensmartgridplatform.dto.valueobjects.ConfigurationDto.newBuilder()
            .withLightType(lightType)
            .withDaliConfiguration(daliConfiguration)
            .withRelayConfiguration(relayConfiguration)
            .withPreferredLinkType(preferredLinkType)
            .build();

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
      configuration.setRelayLinking(
          this.mapperFacade.mapAsList(
              source.getRelayLinking(),
              org.opensmartgridplatform.dto.valueobjects.RelayMatrixDto.class));
    }

    if (source.getDeviceFixedIp() != null) {
      configuration.setDeviceFixedIp(
          this.mapperFacade.map(source.getDeviceFixedIp(), DeviceFixedIpDto.class));
    }

    return configuration;
  }
}

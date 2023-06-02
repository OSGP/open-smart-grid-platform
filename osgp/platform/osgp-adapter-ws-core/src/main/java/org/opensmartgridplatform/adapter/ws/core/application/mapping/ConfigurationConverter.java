//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.adapter.ws.core.application.mapping;

import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.metadata.Type;
import org.joda.time.DateTime;
import org.opensmartgridplatform.adapter.ws.schema.core.configurationmanagement.Configuration;
import org.opensmartgridplatform.domain.core.valueobjects.DaliConfiguration;
import org.opensmartgridplatform.domain.core.valueobjects.DeviceFixedIp;
import org.opensmartgridplatform.domain.core.valueobjects.LightType;
import org.opensmartgridplatform.domain.core.valueobjects.LinkType;
import org.opensmartgridplatform.domain.core.valueobjects.RelayConfiguration;
import org.opensmartgridplatform.domain.core.valueobjects.RelayMatrix;

public class ConfigurationConverter
    extends CustomConverter<
        Configuration, org.opensmartgridplatform.domain.core.valueobjects.Configuration> {
  @Override
  public org.opensmartgridplatform.domain.core.valueobjects.Configuration convert(
      final Configuration source,
      final Type<? extends org.opensmartgridplatform.domain.core.valueobjects.Configuration>
          destinationType,
      final MappingContext mappingContext) {
    return new org.opensmartgridplatform.domain.core.valueobjects.Configuration.Builder()
        .withLightType(this.mapperFacade.map(source.getLightType(), LightType.class))
        .withDaliConfiguration(
            this.mapperFacade.map(source.getDaliConfiguration(), DaliConfiguration.class))
        .withRelayConfiguration(
            this.mapperFacade.map(source.getRelayConfiguration(), RelayConfiguration.class))
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
}

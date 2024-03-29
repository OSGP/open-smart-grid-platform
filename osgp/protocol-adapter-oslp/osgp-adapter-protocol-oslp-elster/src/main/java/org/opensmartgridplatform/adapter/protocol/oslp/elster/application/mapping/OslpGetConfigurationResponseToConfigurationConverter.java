// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.oslp.elster.application.mapping;

import com.google.protobuf.ByteString;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.metadata.Type;
import org.joda.time.DateTime;
import org.joda.time.DateTimeFieldType;
import org.joda.time.MutableDateTime;
import org.opensmartgridplatform.dto.valueobjects.ConfigurationDto;
import org.opensmartgridplatform.dto.valueobjects.DaliConfigurationDto;
import org.opensmartgridplatform.dto.valueobjects.DeviceFixedIpDto;
import org.opensmartgridplatform.dto.valueobjects.LightTypeDto;
import org.opensmartgridplatform.dto.valueobjects.LinkTypeDto;
import org.opensmartgridplatform.dto.valueobjects.RelayConfigurationDto;
import org.opensmartgridplatform.dto.valueobjects.RelayMatrixDto;
import org.opensmartgridplatform.oslp.Oslp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OslpGetConfigurationResponseToConfigurationConverter
    extends CustomConverter<Oslp.GetConfigurationResponse, ConfigurationDto> {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(OslpGetConfigurationResponseToConfigurationConverter.class);

  private static final int SECONDS_PER_MINUTE = 60;

  @Override
  public ConfigurationDto convert(
      final Oslp.GetConfigurationResponse source,
      final Type<? extends ConfigurationDto> destinationType,
      final MappingContext context) {

    // Convert the required values for the constructor of Configuration.
    final LightTypeDto lightType = this.getLightType(source);
    final DaliConfigurationDto daliConfiguration = this.getDaliConfiguration(source);
    final RelayConfigurationDto relayConfiguration = this.getRelayConfiguration(source);
    final LinkTypeDto preferredLinkType = this.getPreferredLinkType(source);

    final ConfigurationDto configuration =
        ConfigurationDto.newBuilder()
            .withLightType(lightType)
            .withDaliConfiguration(daliConfiguration)
            .withRelayConfiguration(relayConfiguration)
            .withPreferredLinkType(preferredLinkType)
            .build();

    // Set the optional values using the set() functions.
    configuration.setTimeSyncFrequency(source.getTimeSyncFrequency());
    this.setFixedIpConfiguration(source, configuration);
    configuration.setDhcpEnabled(source.getIsDhcpEnabled());
    configuration.setCommunicationTimeout(source.getCommunicationTimeout());
    configuration.setCommunicationNumberOfRetries(source.getCommunicationNumberOfRetries());
    configuration.setCommunicationPauseTimeBetweenConnectionTrials(
        source.getCommunicationPauseTimeBetweenConnectionTrials());
    this.setOspgIpAddress(source, configuration);
    this.setOsgpPortNumber(source, configuration);
    configuration.setTestButtonEnabled(source.getIsTestButtonEnabled());
    configuration.setAutomaticSummerTimingEnabled(source.getIsAutomaticSummerTimingEnabled());
    configuration.setAstroGateSunRiseOffset(
        source.getAstroGateSunRiseOffset() / SECONDS_PER_MINUTE);
    configuration.setAstroGateSunSetOffset(source.getAstroGateSunSetOffset() / SECONDS_PER_MINUTE);
    configuration.setSwitchingDelays(source.getSwitchingDelayList());
    this.setRelayLinking(source, configuration);
    configuration.setRelayRefreshing(source.getRelayRefreshing());

    final ZonedDateTime summerTimeDetails =
        this.convertSummerTimeWinterTimeDetails(source.getSummerTimeDetails());
    configuration.setSummerTimeDetails(summerTimeDetails);
    final ZonedDateTime winterTimeDetails =
        this.convertSummerTimeWinterTimeDetails(source.getWinterTimeDetails());
    configuration.setWinterTimeDetails(winterTimeDetails);

    return configuration;
  }

  private LightTypeDto getLightType(final Oslp.GetConfigurationResponse source) {
    return source.hasLightType()
        ? this.mapperFacade.map(source.getLightType(), LightTypeDto.class)
        : null;
  }

  private DaliConfigurationDto getDaliConfiguration(final Oslp.GetConfigurationResponse source) {
    return source.hasDaliConfiguration()
        ? this.mapperFacade.map(source.getDaliConfiguration(), DaliConfigurationDto.class)
        : null;
  }

  private RelayConfigurationDto getRelayConfiguration(final Oslp.GetConfigurationResponse source) {
    return source.hasRelayConfiguration()
        ? this.mapperFacade.map(source.getRelayConfiguration(), RelayConfigurationDto.class)
        : new RelayConfigurationDto(new ArrayList<>());
  }

  private LinkTypeDto getPreferredLinkType(final Oslp.GetConfigurationResponse source) {
    return source.hasPreferredLinkType()
            && !source.getPreferredLinkType().equals(Oslp.LinkType.LINK_NOT_SET)
        ? this.mapperFacade.map(source.getPreferredLinkType(), LinkTypeDto.class)
        : null;
  }

  private void setRelayLinking(
      final Oslp.GetConfigurationResponse source, final ConfigurationDto configuration) {
    if (source.getRelayLinkingList() != null) {
      configuration.setRelayLinking(
          this.mapperFacade.mapAsList(source.getRelayLinkingList(), RelayMatrixDto.class));
    }
  }

  private void setOsgpPortNumber(
      final Oslp.GetConfigurationResponse source, final ConfigurationDto configuration) {
    if (source.getOsgpPortNumber() > 0 && source.getOsgpPortNumber() < 65536) {
      configuration.setOsgpPortNumber(source.getOsgpPortNumber());
    }
  }

  private void setOspgIpAddress(
      final Oslp.GetConfigurationResponse source, final ConfigurationDto configuration) {
    if (source.getOspgIpAddress() != null && !source.getOspgIpAddress().isEmpty()) {
      configuration.setOsgpIpAddress(this.convertIpAddress(source.getOspgIpAddress()));
    }
  }

  private void setFixedIpConfiguration(
      final Oslp.GetConfigurationResponse source, final ConfigurationDto configuration) {
    if (source.getDeviceFixIpValue() != null && !source.getDeviceFixIpValue().isEmpty()) {
      final String ipAddress = this.convertIpAddress(source.getDeviceFixIpValue());
      final String netMask = this.convertIpAddress(source.getNetMask());
      final String gateWay = this.convertIpAddress(source.getGateWay());
      configuration.setDeviceFixedIp(new DeviceFixedIpDto(ipAddress, netMask, gateWay));
    }
  }

  private String convertIpAddress(final ByteString byteString) {
    if (byteString == null || byteString.isEmpty()) {
      return "";
    }
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
  }

  /*-
   * SummerTimeDetails/WinterTimeDetails string: MMWHHmi
   *
   * where: (note, north hemisphere summer begins at the end of march) MM:
   * month W: day of the week (0- Monday, 6- Sunday) HH: hour of the changing
   * time mi: minutes of the changing time
   *
   * Default value for summer time: 0360100 Default value for summer time:
   * 1060200
   */
  private ZonedDateTime convertSummerTimeWinterTimeDetails(final String timeDetails) {
    final int month = Integer.parseInt(timeDetails.substring(0, 2));
    final int day = Integer.parseInt(timeDetails.substring(2, 3));
    final int hour = Integer.parseInt(timeDetails.substring(3, 5));
    final int minutes = Integer.parseInt(timeDetails.substring(5, 7));

    LOGGER.info("month: {}, day: {}, hour: {}, minutes: {}", month, day, hour, minutes);

    final int year = DateTime.now().getYear();
    final int dayOfMonth = this.getLastDayOfMonth(month, day);
    final ZonedDateTime dateTime =
        ZonedDateTime.of(year, month, dayOfMonth, hour, minutes, 0, 0, ZoneId.systemDefault());

    LOGGER.info("dateTime: {}", dateTime);

    return dateTime;
  }

  /** For a given Month of this year, find the date for the given weekday. */
  private int getLastDayOfMonth(final int month, final int day) {
    final DateTime dateTime = DateTime.now();
    MutableDateTime x = dateTime.toMutableDateTime();
    x.set(DateTimeFieldType.monthOfYear(), month);
    x.set(DateTimeFieldType.dayOfMonth(), 31);

    x = this.findLastDayOfOfMonth(day, x);
    return x.getDayOfMonth();
  }

  /**
   * Loop backwards through the days of the month until we find the given day of the month. For
   * example the last Sunday of the month March of this year.
   */
  private MutableDateTime findLastDayOfOfMonth(final int day, final MutableDateTime x) {
    final int yodaTimeDay = day + 1;
    while (true) {
      if (yodaTimeDay == x.getDayOfWeek()) {
        break;
      } else {
        final int dayOfMonth = x.getDayOfMonth() - 1;
        x.set(DateTimeFieldType.dayOfMonth(), dayOfMonth);
      }
    }
    return x;
  }
}

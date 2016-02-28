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

import org.joda.time.DateTime;
import org.joda.time.DateTimeFieldType;
import org.joda.time.MutableDateTime;
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

        final DateTime summerTimeDetails = this.convertSummerTimeWinterTimeDetails(source.getSummerTimeDetails());
        configuration.setSummerTimeDetails(summerTimeDetails);
        final DateTime winterTimeDetails = this.convertSummerTimeWinterTimeDetails(source.getWinterTimeDetails());
        configuration.setWinterTimeDetails(winterTimeDetails);

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
    }

    // @formatter:off
    /*
     * SummerTimeDetails/WinterTimeDetails string: MMWHHmi
     *
     * where: (note, north hemisphere summer begins at the end of march)
     * MM: month
     * W: day of the week (0- Monday, 6- Sunday)
     * HH: hour of the changing time
     * mi: minutes of the changing time
     *
     * Default value for summer time: 0360100
     * Default value for summer time: 1060200
     */
    // @formatter:on
    private DateTime convertSummerTimeWinterTimeDetails(final String timeDetails) {
        final int month = Integer.parseInt(timeDetails.substring(0, 2));
        final int day = Integer.parseInt(timeDetails.substring(2, 3));
        final int hour = Integer.parseInt(timeDetails.substring(3, 5));
        final int minutes = Integer.parseInt(timeDetails.substring(5, 7));

        LOGGER.info("month: {}, day: {}, hour: {}, minutes: {}", month, day, hour, minutes);

        final int year = DateTime.now().getYear();
        final int dayOfMonth = this.getLastDayOfMonth(month, day);
        final DateTime dateTime = new DateTime(year, month, dayOfMonth, hour, minutes);

        LOGGER.info("dateTime: {}", dateTime.toString());

        return dateTime;
    }

    /**
     * For a given Month of this year, find the date for the weekday {@link day}
     * .
     */
    private int getLastDayOfMonth(final int month, final int day) {
        final DateTime dateTime = DateTime.now();
        MutableDateTime x = dateTime.toMutableDateTime();
        x.set(DateTimeFieldType.monthOfYear(), month);
        x.set(DateTimeFieldType.dayOfMonth(), 31);

        x = this.findLastDayOfOfMonth(day, x);
        return x.getDayOfMonth();
    }

    /**
     * Loop backwards through the days of the month until we find {@link day} of
     * the month. For example the last Sunday of the month March of this year.
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

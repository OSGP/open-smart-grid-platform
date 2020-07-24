/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.oslp.elster.application.mapping;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.joda.time.DateTime;
import org.opensmartgridplatform.dto.valueobjects.ConfigurationDto;
import org.opensmartgridplatform.oslp.Oslp;
import org.opensmartgridplatform.oslp.Oslp.SetConfigurationRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.ByteString;

import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.metadata.Type;

public class ConfigurationToOslpSetConfigurationRequestConverter
        extends CustomConverter<ConfigurationDto, Oslp.SetConfigurationRequest> {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(ConfigurationToOslpSetConfigurationRequestConverter.class);

    private static final int SECONDS_PER_MINUTE = 60;

    @Override
    public SetConfigurationRequest convert(final ConfigurationDto source,
            final Type<? extends Oslp.SetConfigurationRequest> destinationType, final MappingContext context) {

        final Oslp.SetConfigurationRequest.Builder setConfigurationRequest = Oslp.SetConfigurationRequest.newBuilder();

        this.setLightType(source, setConfigurationRequest);
        this.setDaliConfiguration(source, setConfigurationRequest);
        this.setRelayConfiguration(source, setConfigurationRequest);
        this.setShortTermHistoryIntervalMinutes(source, setConfigurationRequest);
        this.setLongTermHistoryInterval(source, setConfigurationRequest);
        this.setLongTermHistoryIntervalType(source, setConfigurationRequest);
        this.setPreferredLinkType(source, setConfigurationRequest);
        this.setMeterType(source, setConfigurationRequest);
        this.setAstroGateSunRiseOffset(source, setConfigurationRequest);
        this.setAstroGateSunSetOffset(source, setConfigurationRequest);
        this.setIsAutomaticSummerTimingEnabled(source, setConfigurationRequest);
        this.setCommunicationNumberOfRetries(source, setConfigurationRequest);
        this.setCommunicationPauseTimeBetweenConnectionTrials(source, setConfigurationRequest);
        this.setCommunicationTimeout(source, setConfigurationRequest);
        this.setFixedIpConfiguration(source, setConfigurationRequest);
        this.setIsDhcpEnabled(source, setConfigurationRequest);
        this.setOsgpPortNumber(source, setConfigurationRequest);
        this.setOsgpIpAddress(source, setConfigurationRequest);
        this.setRelayRefreshing(source, setConfigurationRequest);
        this.setSummerTimeDetails(source, setConfigurationRequest);
        this.setIsTestButtonEnabled(source, setConfigurationRequest);
        this.setTimeSyncFrequency(source, setConfigurationRequest);
        this.setWinterTimeDetails(source, setConfigurationRequest);
        this.setSwitchingDelays(source, setConfigurationRequest);
        this.setRelayLinking(source, setConfigurationRequest);

        return setConfigurationRequest.build();
    }

    private void setLightType(final ConfigurationDto source,
            final Oslp.SetConfigurationRequest.Builder setConfigurationRequest) {
        if (source.getLightType() != null) {
            setConfigurationRequest.setLightType(this.mapperFacade.map(source.getLightType(), Oslp.LightType.class));
        }
    }

    private void setDaliConfiguration(final ConfigurationDto source,
            final Oslp.SetConfigurationRequest.Builder setConfigurationRequest) {
        if (source.getDaliConfiguration() != null) {
            setConfigurationRequest.setDaliConfiguration(
                    this.mapperFacade.map(source.getDaliConfiguration(), Oslp.DaliConfiguration.class));
        }
    }

    private void setRelayConfiguration(final ConfigurationDto source,
            final Oslp.SetConfigurationRequest.Builder setConfigurationRequest) {
        if (source.getRelayConfiguration() != null) {
            setConfigurationRequest.setRelayConfiguration(
                    this.mapperFacade.map(source.getRelayConfiguration(), Oslp.RelayConfiguration.class));
        }
    }

    private void setShortTermHistoryIntervalMinutes(final ConfigurationDto source,
            final Oslp.SetConfigurationRequest.Builder setConfigurationRequest) {
        if (source.getShortTermHistoryIntervalMinutes() != null) {
            setConfigurationRequest.setShortTermHistoryIntervalMinutes(
                    this.mapperFacade.map(source.getShortTermHistoryIntervalMinutes(), Integer.class));
        }
    }

    private void setLongTermHistoryInterval(final ConfigurationDto source,
            final Oslp.SetConfigurationRequest.Builder setConfigurationRequest) {
        if (source.getLongTermHistoryInterval() != null) {
            setConfigurationRequest.setLongTermHistoryInterval(
                    this.mapperFacade.map(source.getLongTermHistoryInterval(), Integer.class));
        }
    }

    private void setLongTermHistoryIntervalType(final ConfigurationDto source,
            final Oslp.SetConfigurationRequest.Builder setConfigurationRequest) {
        if (source.getLongTermHistoryIntervalType() != null) {
            setConfigurationRequest.setLongTermHistoryIntervalType(
                    this.mapperFacade.map(source.getLongTermHistoryIntervalType(), Oslp.LongTermIntervalType.class));
        }
    }

    private void setPreferredLinkType(final ConfigurationDto source,
            final Oslp.SetConfigurationRequest.Builder setConfigurationRequest) {
        if (source.getPreferredLinkType() != null) {
            setConfigurationRequest
                    .setPreferredLinkType(this.mapperFacade.map(source.getPreferredLinkType(), Oslp.LinkType.class));
        }
    }

    private void setMeterType(final ConfigurationDto source,
            final Oslp.SetConfigurationRequest.Builder setConfigurationRequest) {
        if (source.getMeterType() != null) {
            setConfigurationRequest.setMeterType(this.mapperFacade.map(source.getMeterType(), Oslp.MeterType.class));
        }
    }

    private void setAstroGateSunRiseOffset(final ConfigurationDto source,
            final Oslp.SetConfigurationRequest.Builder setConfigurationRequest) {
        if (source.getAstroGateSunRiseOffset() != null) {
            setConfigurationRequest.setAstroGateSunRiseOffset(source.getAstroGateSunRiseOffset() * SECONDS_PER_MINUTE);
        }
    }

    private void setAstroGateSunSetOffset(final ConfigurationDto source,
            final Oslp.SetConfigurationRequest.Builder setConfigurationRequest) {
        if (source.getAstroGateSunSetOffset() != null) {
            setConfigurationRequest.setAstroGateSunSetOffset(source.getAstroGateSunSetOffset() * SECONDS_PER_MINUTE);
        }
    }

    private void setIsAutomaticSummerTimingEnabled(final ConfigurationDto source,
            final Oslp.SetConfigurationRequest.Builder setConfigurationRequest) {
        if (source.isAutomaticSummerTimingEnabled() != null) {
            setConfigurationRequest.setIsAutomaticSummerTimingEnabled(source.isAutomaticSummerTimingEnabled());
        }
    }

    private void setCommunicationNumberOfRetries(final ConfigurationDto source,
            final Oslp.SetConfigurationRequest.Builder setConfigurationRequest) {
        if (source.getCommunicationNumberOfRetries() != null) {
            setConfigurationRequest.setCommunicationNumberOfRetries(source.getCommunicationNumberOfRetries());
        }
    }

    private void setCommunicationPauseTimeBetweenConnectionTrials(final ConfigurationDto source,
            final Oslp.SetConfigurationRequest.Builder setConfigurationRequest) {
        if (source.getCommunicationPauseTimeBetweenConnectionTrials() != null) {
            setConfigurationRequest.setCommunicationPauseTimeBetweenConnectionTrials(
                    source.getCommunicationPauseTimeBetweenConnectionTrials());
        }
    }

    private void setCommunicationTimeout(final ConfigurationDto source,
            final Oslp.SetConfigurationRequest.Builder setConfigurationRequest) {
        if (source.getCommunicationTimeout() != null) {
            setConfigurationRequest.setCommunicationTimeout(source.getCommunicationTimeout());
        }
    }

    private void setFixedIpConfiguration(final ConfigurationDto source,
            final Oslp.SetConfigurationRequest.Builder setConfigurationRequest) {
        if (source.getDeviceFixedIp() != null) {
            setConfigurationRequest.setDeviceFixIpValue(
                    this.convertTextualIpAddressToByteString(source.getDeviceFixedIp().getIpAddress()));
            setConfigurationRequest
                    .setNetMask(this.convertTextualIpAddressToByteString(source.getDeviceFixedIp().getNetMask()));
            setConfigurationRequest
                    .setGateWay(this.convertTextualIpAddressToByteString(source.getDeviceFixedIp().getGateWay()));
        }
    }

    private void setIsDhcpEnabled(final ConfigurationDto source,
            final Oslp.SetConfigurationRequest.Builder setConfigurationRequest) {
        if (source.isDhcpEnabled() != null) {
            setConfigurationRequest.setIsDhcpEnabled(source.isDhcpEnabled());
        }
    }

    private void setOsgpPortNumber(final ConfigurationDto source,
            final Oslp.SetConfigurationRequest.Builder setConfigurationRequest) {
        if (source.getOsgpPortNumber() != null) {
            setConfigurationRequest.setOsgpPortNumber(source.getOsgpPortNumber());
        }
    }

    private void setOsgpIpAddress(final ConfigurationDto source,
            final Oslp.SetConfigurationRequest.Builder setConfigurationRequest) {
        if (source.getOsgpIpAddres() != null) {
            setConfigurationRequest
                    .setOspgIpAddress(this.convertTextualIpAddressToByteString(source.getOsgpIpAddres()));
        }
    }

    private void setRelayRefreshing(final ConfigurationDto source,
            final Oslp.SetConfigurationRequest.Builder setConfigurationRequest) {
        if (source.isRelayRefreshing() != null) {
            setConfigurationRequest.setRelayRefreshing(source.isRelayRefreshing());
        }
    }

    private void setSummerTimeDetails(final ConfigurationDto source,
            final Oslp.SetConfigurationRequest.Builder setConfigurationRequest) {
        if (source.getSummerTimeDetails() != null) {
            final String summerTimeDetails = this.convertSummerTimeWinterTimeDetails(source.getSummerTimeDetails());
            setConfigurationRequest.setSummerTimeDetails(summerTimeDetails);
        }
    }

    private void setIsTestButtonEnabled(final ConfigurationDto source,
            final Oslp.SetConfigurationRequest.Builder setConfigurationRequest) {
        if (source.isTestButtonEnabled() != null) {
            setConfigurationRequest.setIsTestButtonEnabled(source.isTestButtonEnabled());
        }
    }

    private void setTimeSyncFrequency(final ConfigurationDto source,
            final Oslp.SetConfigurationRequest.Builder setConfigurationRequest) {
        if (source.getTimeSyncFrequency() != null) {
            setConfigurationRequest.setTimeSyncFrequency(source.getTimeSyncFrequency());
        }
    }

    private void setWinterTimeDetails(final ConfigurationDto source,
            final Oslp.SetConfigurationRequest.Builder setConfigurationRequest) {
        if (source.getWinterTimeDetails() != null) {
            final String winterTimeDetails = this.convertSummerTimeWinterTimeDetails(source.getWinterTimeDetails());
            setConfigurationRequest.setWinterTimeDetails(winterTimeDetails);
        }
    }

    private void setSwitchingDelays(final ConfigurationDto source,
            final Oslp.SetConfigurationRequest.Builder setConfigurationRequest) {
        if (source.getSwitchingDelays() != null) {
            setConfigurationRequest.addAllSwitchingDelay(source.getSwitchingDelays());
        }
    }

    private void setRelayLinking(final ConfigurationDto source,
            final Oslp.SetConfigurationRequest.Builder setConfigurationRequest) {
        if (source.getRelayLinking() != null) {
            setConfigurationRequest
                    .addAllRelayLinking(this.mapperFacade.mapAsList(source.getRelayLinking(), Oslp.RelayMatrix.class));
        }
    }

    private ByteString convertTextualIpAddressToByteString(final String ipAddress) {
        try {
            LOGGER.info("textual IP address or netmask: {}", ipAddress);
            final InetAddress inetAddress = InetAddress.getByName(ipAddress);
            final byte[] bytes = inetAddress.getAddress();
            LOGGER.info("bytes.length: {}", bytes.length);
            for (final byte b : bytes) {
                LOGGER.info("byte: {}", b);
            }
            return ByteString.copyFrom(bytes);
        } catch (final UnknownHostException e) {
            LOGGER.error("UnknownHostException", e);
            return null;
        }
    }

    // @formatter:off
    /*
     * SummerTimeDetails/WinterTimeDetails string: MMWHHmi
     *
     * where: (note, north hemisphere summer begins at the end of march) MM:
     * month W: day of the week (0- Monday, 6- Sunday) HH: hour of the changing
     * time mi: minutes of the changing time
     *
     * Default value for summer time: 0360100 Default value for summer time:
     * 1060200
     */
    // @formatter:on
    private String convertSummerTimeWinterTimeDetails(final DateTime dateTime) {
        LOGGER.info("dateTime: {}", dateTime);

        final String formattedTimeDetails = String.format("%02d", dateTime.getMonthOfYear())
                + (dateTime.getDayOfWeek() - 1) + String.format("%02d", dateTime.getHourOfDay())
                + String.format("%02d", dateTime.getMinuteOfHour());

        LOGGER.info("formattedTimeDetails: {}", formattedTimeDetails);

        return formattedTimeDetails;
    }
}

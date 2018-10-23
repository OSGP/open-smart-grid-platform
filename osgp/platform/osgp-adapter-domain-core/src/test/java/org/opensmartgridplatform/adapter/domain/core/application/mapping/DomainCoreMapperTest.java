/**
 * Copyright 2018 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.domain.core.application.mapping;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.List;

import org.joda.time.DateTime;
import org.junit.Test;
import org.opensmartgridplatform.domain.core.valueobjects.Configuration;
import org.opensmartgridplatform.domain.core.valueobjects.DaliConfiguration;
import org.opensmartgridplatform.domain.core.valueobjects.DeviceFixedIp;
import org.opensmartgridplatform.domain.core.valueobjects.LightType;
import org.opensmartgridplatform.domain.core.valueobjects.LinkType;
import org.opensmartgridplatform.domain.core.valueobjects.LongTermIntervalType;
import org.opensmartgridplatform.domain.core.valueobjects.MeterType;
import org.opensmartgridplatform.domain.core.valueobjects.RelayConfiguration;
import org.opensmartgridplatform.domain.core.valueobjects.RelayMap;
import org.opensmartgridplatform.domain.core.valueobjects.RelayMatrix;
import org.opensmartgridplatform.domain.core.valueobjects.RelayType;
import org.opensmartgridplatform.dto.valueobjects.ConfigurationDto;
import org.opensmartgridplatform.dto.valueobjects.DaliConfigurationDto;
import org.opensmartgridplatform.dto.valueobjects.DeviceFixedIpDto;
import org.opensmartgridplatform.dto.valueobjects.LightTypeDto;
import org.opensmartgridplatform.dto.valueobjects.LinkTypeDto;
import org.opensmartgridplatform.dto.valueobjects.LongTermIntervalTypeDto;
import org.opensmartgridplatform.dto.valueobjects.MeterTypeDto;
import org.opensmartgridplatform.dto.valueobjects.RelayConfigurationDto;
import org.opensmartgridplatform.dto.valueobjects.RelayMapDto;
import org.opensmartgridplatform.dto.valueobjects.RelayMatrixDto;
import org.opensmartgridplatform.dto.valueobjects.RelayTypeDto;

public class DomainCoreMapperTest {
    private final DomainCoreMapper mapper = new DomainCoreMapper();

    @Test
    public void convertsConfigurationDtoToConfiguration() {
        final ConfigurationDto source = this.aConfigurationDto();

        final Configuration result = this.mapper.map(source, Configuration.class);

        assertThat(result).isEqualToComparingFieldByFieldRecursively(this.toConfiguration(source));
    }

    private ConfigurationDto aConfigurationDto() {
        final ConfigurationDto source = new ConfigurationDto.Builder().withLightType(this.aLightTypeDto())
                .withDaliConfiguration(this.aDaliConfigurationDto())
                .withRelayConfiguration(this.aRelayConfigurationDto()).withShortTermHistoryIntervalMinutes(131)
                .withLongTermHistoryInterval(132).withLongTermHysteryIntervalType(LongTermIntervalTypeDto.DAYS)
                .withPreferredLinkType(LinkTypeDto.CDMA).withMeterType(MeterTypeDto.AUX).build();
        source.setTimeSyncFrequency(133);
        source.setDeviceFixedIp(new DeviceFixedIpDto("ipAddress1", "netMask1", "gateWay1"));
        source.setDhcpEnabled(true);
        source.setTlsEnabled(true);
        source.setTlsPortNumber(134);
        source.setCommonNameString("commonNameString1");
        source.setCommunicationTimeout(135);
        source.setCommunicationNumberOfRetries(136);
        source.setCommunicationPauseTimeBetweenConnectionTrials(137);
        source.setOsgpIpAddress("osgpIpAddress1");
        source.setOsgpPortNumber(138);
        source.setNtpHost("ntpHost1");
        source.setNtpEnabled(true);
        source.setNtpSyncInterval(139);
        source.setTestButtonEnabled(true);
        source.setAutomaticSummerTimingEnabled(true);
        source.setAstroGateSunRiseOffset(140);
        source.setAstroGateSunSetOffset(141);
        source.setSwitchingDelays(asList(142, 143));
        source.setRelayLinking(asList(new RelayMatrixDto(144, true), new RelayMatrixDto(145, false)));
        source.setRelayRefreshing(true);
        source.setSummerTimeDetails(new DateTime(146L * 24 * 60 * 60 * 1000));
        source.setWinterTimeDetails(new DateTime(147L * 24 * 60 * 60 * 1000));
        return source;
    }

    private Configuration toConfiguration(final ConfigurationDto source) {
        return new Configuration.Builder().withLightType(LightType.DALI)
                .withDaliConfiguration(this.toDaliConfiguration(source.getDaliConfiguration()))
                .withRelayConfiguration(this.toRelayConfiguration(source.getRelayConfiguration()))
                .withShortTemHistoryIntervalMinutes(source.getShortTermHistoryIntervalMinutes())
                .withLongTermHistoryInterval(source.getLongTermHistoryInterval())
                .withLongTermHistoryIntervalType(this.toLongTermIntervalType(source.getLongTermHistoryIntervalType()))
                .withPreferredLinkType(this.toPreferredLinkType(source.getPreferredLinkType()))
                .withMeterType(this.toMeterType(source.getMeterType()))
                .withTimeSyncFrequency(source.getTimeSyncFrequency())
                .withDeviceFixedIp(this.toDeviceFixedIp(source.getDeviceFixedIp()))
                .withDhcpEnabled(source.isDhcpEnabled()).withTlsEnabled(source.isTlsEnabled())
                .withTlsPortNumber(source.getTlsPortNumber()).withCommonNameString(source.getCommonNameString())
                .withCommunicationTimeout(source.getCommunicationTimeout())
                .withCommunicationNumberOfRetries(source.getCommunicationNumberOfRetries())
                .withCommunicationPauseTimeBetweenConnectionTrials(
                        source.getCommunicationPauseTimeBetweenConnectionTrials())
                .withOsgpIpAddress(source.getOsgpIpAddres()).withOsgpPortNumber(source.getOsgpPortNumber())
                .withNtpHost(source.getNtpHost()).withNtpEnabled(source.getNtpEnabled())
                .withNtpSyncInterval(source.getNtpSyncInterval()).withTestButtonEnabled(source.isTestButtonEnabled())
                .withAutomaticSummerTimingEnabled(source.isAutomaticSummerTimingEnabled())
                .withAstroGateSunRiseOffset(source.getAstroGateSunRiseOffset())
                .withAstroGateSunSetOffset(source.getAstroGateSunSetOffset())
                .withSwitchingDelays(source.getSwitchingDelays())
                .withRelayLinking(this.toRelayLinking(source.getRelayLinking()))
                .withRelayRefreshing(source.isRelayRefreshing()).withSummerTimeDetails(source.getSummerTimeDetails())
                .withWinterTimeDetails(source.getWinterTimeDetails()).build();
    }

    private List<RelayMatrix> toRelayLinking(final List<RelayMatrixDto> relayLinking) {
        return relayLinking.stream().map(relayMatrixDto -> this.toRelayMatrix(relayMatrixDto)).collect(toList());
    }

    private RelayMatrix toRelayMatrix(final RelayMatrixDto dto) {
        return new RelayMatrix(dto.getMasterRelayIndex(), dto.isMasterRelayOn());
    }

    private DeviceFixedIp toDeviceFixedIp(final DeviceFixedIpDto dto) {
        return new DeviceFixedIp(dto.getIpAddress(), dto.getNetMask(), dto.getGateWay());
    }

    private MeterType toMeterType(final MeterTypeDto dto) {
        return MeterType.valueOf(dto.name());
    }

    private LinkType toPreferredLinkType(final LinkTypeDto dto) {
        return LinkType.valueOf(dto.name());
    }

    private LongTermIntervalType toLongTermIntervalType(final LongTermIntervalTypeDto dto) {
        return LongTermIntervalType.valueOf(dto.name());
    }

    private RelayConfiguration toRelayConfiguration(final RelayConfigurationDto dto) {
        final List<RelayMap> relayMaps = dto.getRelayMap().stream().map(relayMapDto -> this.toRelayMap(relayMapDto))
                .collect(toList());
        return new RelayConfiguration(relayMaps);
    }

    private RelayMap toRelayMap(final RelayMapDto dto) {
        return new RelayMap(dto.getIndex(), dto.getAddress(), this.toRelayType(dto.getRelayType()), dto.getAlias());
    }

    private RelayType toRelayType(final RelayTypeDto dto) {
        return RelayType.valueOf(dto.name());
    }

    private RelayConfigurationDto aRelayConfigurationDto() {
        return new RelayConfigurationDto(asList(new RelayMapDto(127, 128, RelayTypeDto.LIGHT, "alias1"),
                new RelayMapDto(129, 130, RelayTypeDto.TARIFF, "alias21")));
    }

    private LightTypeDto aLightTypeDto() {
        return LightTypeDto.DALI;
    }

    private DaliConfiguration toDaliConfiguration(final DaliConfigurationDto dto) {
        return new DaliConfiguration(dto.getNumberOfLights(), dto.getIndexAddressMap());
    }

    private DaliConfigurationDto aDaliConfigurationDto() {
        return new DaliConfigurationDto(123, this.anIndexAddressMap());
    }

    private HashMap<Integer, Integer> anIndexAddressMap() {
        final HashMap<Integer, Integer> map = new HashMap<>();
        map.put(124, 125);
        map.put(125, 126);
        return map;
    }
}
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.ws.core.application.mapping;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.ZonedDateTime;
import java.util.HashMap;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.opensmartgridplatform.adapter.ws.schema.core.configurationmanagement.Configuration;
import org.opensmartgridplatform.adapter.ws.schema.core.configurationmanagement.DaliConfiguration;
import org.opensmartgridplatform.adapter.ws.schema.core.configurationmanagement.DeviceFixedIp;
import org.opensmartgridplatform.adapter.ws.schema.core.configurationmanagement.IndexAddressMap;
import org.opensmartgridplatform.adapter.ws.schema.core.configurationmanagement.LightType;
import org.opensmartgridplatform.adapter.ws.schema.core.configurationmanagement.LinkType;
import org.opensmartgridplatform.adapter.ws.schema.core.configurationmanagement.RelayConfiguration;
import org.opensmartgridplatform.adapter.ws.schema.core.configurationmanagement.RelayMap;
import org.opensmartgridplatform.adapter.ws.schema.core.configurationmanagement.RelayMatrix;

class ConfigurationManagementMapperTest {
  private ConfigurationManagementMapper mapper;

  private DaliConfiguration aSourceDaliConfiguration(
      final int numberOfLights,
      final int index1,
      final int address1,
      final int index2,
      final int address2) {
    final DaliConfiguration daliConfiguration = new DaliConfiguration();
    daliConfiguration.setNumberOfLights(numberOfLights);
    daliConfiguration.getIndexAddressMap().add(this.aSourceIndexAddressMap(index1, address1));
    daliConfiguration.getIndexAddressMap().add(this.aSourceIndexAddressMap(index2, address2));
    return daliConfiguration;
  }

  private DeviceFixedIp aSourceDeviceFixedIp(
      final String ipAddress, final String netMask, final String gateWay) {
    final DeviceFixedIp deviceFixedIp = new DeviceFixedIp();
    deviceFixedIp.setGateWay(gateWay);
    deviceFixedIp.setIpAddress(ipAddress);
    deviceFixedIp.setNetMask(netMask);
    return deviceFixedIp;
  }

  private IndexAddressMap aSourceIndexAddressMap(final int index, final int address) {
    final IndexAddressMap indexAddressMap = new IndexAddressMap();
    indexAddressMap.setIndex(index);
    indexAddressMap.setAddress(address);
    return indexAddressMap;
  }

  private RelayConfiguration aSourceRelayConfiguration(
      final int index1, final int address1, final int index2, final int address2) {
    final RelayConfiguration relayConfiguration = new RelayConfiguration();
    relayConfiguration.getRelayMap().add(this.aSourceRelayMap(index1, address1));
    relayConfiguration.getRelayMap().add(this.aSourceRelayMap(index2, address2));
    return relayConfiguration;
  }

  private RelayMap aSourceRelayMap(final int index, final int address) {
    final RelayMap relayMap = new RelayMap();
    relayMap.setIndex(index);
    relayMap.setAddress(address);
    return relayMap;
  }

  private RelayMatrix aSourceRelayMatrix(final int masterRelayIndex, final boolean masterRelayOn) {
    final RelayMatrix relayMatrix = new RelayMatrix();
    relayMatrix.setMasterRelayIndex(masterRelayIndex);
    relayMatrix.setMasterRelayOn(masterRelayOn);
    return relayMatrix;
  }

  private org.opensmartgridplatform.domain.core.valueobjects.DaliConfiguration
      aTargetDaliConfiguration(
          final int numberOfLights,
          final int index1,
          final int address1,
          final int index2,
          final int address2) {
    final HashMap<Integer, Integer> indexAddressMap = new HashMap<>();
    indexAddressMap.put(index1, address1);
    indexAddressMap.put(index2, address2);
    return new org.opensmartgridplatform.domain.core.valueobjects.DaliConfiguration(
        numberOfLights, indexAddressMap);
  }

  private org.opensmartgridplatform.domain.core.valueobjects.RelayConfiguration
      aTargetRelayConfiguration(
          final int index1, final int address1, final int index2, final int address2) {
    return new org.opensmartgridplatform.domain.core.valueobjects.RelayConfiguration(
        asList(
            new org.opensmartgridplatform.domain.core.valueobjects.RelayMap(
                index1, address1, null, null),
            new org.opensmartgridplatform.domain.core.valueobjects.RelayMap(
                index2, address2, null, null)));
  }

  private org.opensmartgridplatform.domain.core.valueobjects.RelayMatrix aTargetRelayMatrix(
      final int masterRelayIndex, final boolean masterRelayOn) {
    final org.opensmartgridplatform.domain.core.valueobjects.RelayMatrix relayMatrix =
        new org.opensmartgridplatform.domain.core.valueobjects.RelayMatrix(
            masterRelayIndex, masterRelayOn);
    relayMatrix.setIndicesOfControlledRelaysOff(emptyList());
    relayMatrix.setIndicesOfControlledRelaysOn(emptyList());
    return relayMatrix;
  }

  @Test
  void mapsWsConfigurationToDomainConfiguration() throws DatatypeConfigurationException {
    final Configuration source = new Configuration();
    source.setLightType(LightType.DALI);
    source.setDaliConfiguration(this.aSourceDaliConfiguration(123, 124, 125, 126, 127));
    source.setRelayConfiguration(this.aSourceRelayConfiguration(128, 129, 130, 131));
    source.setPreferredLinkType(LinkType.CDMA);
    source.setTimeSyncFrequency(134);
    source.setDeviceFixedIp(this.aSourceDeviceFixedIp("ipAddress1", "netMask1", "gateWay1"));
    source.setDhcpEnabled(true);
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
    source.getSwitchingDelays().addAll(asList(142, 142));
    source
        .getRelayLinking()
        .addAll(asList(this.aSourceRelayMatrix(143, true), this.aSourceRelayMatrix(144, false)));
    final DatatypeFactory datatypeFactory = DatatypeFactory.newInstance();
    source.setSummerTimeDetails(
        datatypeFactory.newXMLGregorianCalendar("2010-06-30T01:20:30+02:00"));
    source.setWinterTimeDetails(
        datatypeFactory.newXMLGregorianCalendar("2011-06-30T01:20:30+02:00"));
    source.setRelayRefreshing(true);

    final org.opensmartgridplatform.domain.core.valueobjects.Configuration result =
        this.mapper.map(
            source, org.opensmartgridplatform.domain.core.valueobjects.Configuration.class);

    final org.opensmartgridplatform.domain.core.valueobjects.Configuration expected =
        new org.opensmartgridplatform.domain.core.valueobjects.Configuration.Builder()
            .withLightType(org.opensmartgridplatform.domain.core.valueobjects.LightType.DALI)
            .withDaliConfiguration(this.aTargetDaliConfiguration(123, 124, 125, 126, 127))
            .withRelayConfiguration(this.aTargetRelayConfiguration(128, 129, 130, 131))
            .withPreferredLinkType(org.opensmartgridplatform.domain.core.valueobjects.LinkType.CDMA)
            .withTimeSyncFrequency(134)
            .withDeviceFixedIp(
                new org.opensmartgridplatform.domain.core.valueobjects.DeviceFixedIp(
                    "ipAddress1", "netMask1", "gateWay1"))
            .withDhcpEnabled(true)
            .withCommunicationTimeout(135)
            .withCommunicationNumberOfRetries(136)
            .withCommunicationPauseTimeBetweenConnectionTrials(137)
            .withOsgpIpAddress("osgpIpAddress1")
            .withOsgpPortNumber(138)
            .withNtpHost("ntpHost1")
            .withNtpEnabled(true)
            .withNtpSyncInterval(139)
            .withTestButtonEnabled(true)
            .withAutomaticSummerTimingEnabled(true)
            .withAstroGateSunRiseOffset(140)
            .withAstroGateSunSetOffset(141)
            .withSwitchingDelays(asList(142, 142))
            .withRelayLinking(
                asList(this.aTargetRelayMatrix(143, true), this.aTargetRelayMatrix(144, false)))
            .withRelayRefreshing(true)
            .withSummerTimeDetails(ZonedDateTime.parse("2010-06-30T01:20:30+02:00"))
            .withWinterTimeDetails(ZonedDateTime.parse("2011-06-30T01:20:30+02:00"))
            .build();
    assertThat(result).usingRecursiveComparison().isEqualTo(expected);
  }

  @BeforeEach
  public void setUp() throws Exception {
    this.mapper = new ConfigurationManagementMapper();
    this.mapper.configure(new DefaultMapperFactory.Builder().build());
  }
}

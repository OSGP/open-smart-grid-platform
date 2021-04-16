/*
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ChannelElementValuesDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.MbusChannelElementsDto;

public class FindMatchingChannelHelperTest {

  final String deviceIdentification = "G00XX561204926013";
  final short channel = 1;
  final short primaryAddress = 1;
  final String identificationNumber = "12049260";
  final String manufacturerIdentification = "LGB";
  final short version = 66;
  final short deviceTypeIdentification = 3;

  final short noPrimaryAddress = 0;
  final String noIdentificationNumber = null;
  final String noManufacturerIdentification = null;
  final short noVersion = 0;
  final short noDeviceTypeIdentification = 0;

  final String otherDeviceIdentification = "G00XX561204926113";
  final short otherChannel = 2;
  final short otherPrimaryAddress = 2;
  final String otherIdentificationNumber = "12049261";
  final String otherManufacturerIdentification = "LGB";
  final short otherVersion = 66;
  final short otherDeviceTypeIdentification = 3;

  @Test
  public void testAllFilled() {

    /*
     * All attributes for requestData and channelValues are defined and
     * matching.
     */

    final MbusChannelElementsDto requestData =
        new MbusChannelElementsDto(
            this.primaryAddress,
            this.deviceIdentification,
            this.identificationNumber,
            this.manufacturerIdentification,
            this.version,
            this.deviceTypeIdentification);

    final ChannelElementValuesDto channelValues =
        new ChannelElementValuesDto(
            this.channel,
            this.primaryAddress,
            this.identificationNumber,
            this.manufacturerIdentification,
            this.version,
            this.deviceTypeIdentification);

    assertThat(FindMatchingChannelHelper.matches(requestData, channelValues))
        .withFailMessage(requestData + " should match " + channelValues)
        .isTrue();
  }

  @Test
  public void testAllAttributesInRequestNoneInRetrievedValues() {

    final MbusChannelElementsDto requestData =
        new MbusChannelElementsDto(
            this.primaryAddress,
            this.deviceIdentification,
            this.identificationNumber,
            this.manufacturerIdentification,
            this.version,
            this.deviceTypeIdentification);

    final ChannelElementValuesDto channelValues =
        new ChannelElementValuesDto(
            this.channel,
            this.noPrimaryAddress,
            this.noIdentificationNumber,
            this.noManufacturerIdentification,
            this.noVersion,
            this.noDeviceTypeIdentification);

    assertThat(FindMatchingChannelHelper.matches(requestData, channelValues))
        .withFailMessage(requestData + " should not match " + channelValues)
        .isFalse();

    assertThat(FindMatchingChannelHelper.matchesPartially(requestData, channelValues))
        .withFailMessage(requestData + " should not match partially " + channelValues)
        .isFalse();
  }

  @Test
  public void testAllAttributesInRequestOnlyPrimaryAddressInRetrievedValues() {

    final MbusChannelElementsDto requestData =
        new MbusChannelElementsDto(
            this.primaryAddress,
            this.deviceIdentification,
            this.identificationNumber,
            this.manufacturerIdentification,
            this.version,
            this.deviceTypeIdentification);

    final ChannelElementValuesDto channelValues =
        new ChannelElementValuesDto(
            this.channel,
            this.primaryAddress,
            this.noIdentificationNumber,
            this.noManufacturerIdentification,
            this.noVersion,
            this.noDeviceTypeIdentification);

    assertThat(FindMatchingChannelHelper.matches(requestData, channelValues))
        .withFailMessage(requestData + " should not match " + channelValues)
        .isFalse();

    assertThat(FindMatchingChannelHelper.matchesPartially(requestData, channelValues))
        .withFailMessage(requestData + " should match partially " + channelValues)
        .isTrue();

    final List<ChannelElementValuesDto> channelValuesList = Arrays.asList(channelValues);

    assertThat(FindMatchingChannelHelper.bestMatch(requestData, channelValuesList))
        .withFailMessage(requestData + " should have a best match from " + channelValuesList)
        .isNotNull();
  }

  @Test
  public void testAllAttributesInRequestOnlySomeInRetrievedValues() {

    final MbusChannelElementsDto requestData =
        new MbusChannelElementsDto(
            this.primaryAddress,
            this.deviceIdentification,
            this.identificationNumber,
            this.manufacturerIdentification,
            this.version,
            this.deviceTypeIdentification);

    final ChannelElementValuesDto channelValues =
        new ChannelElementValuesDto(
            this.channel,
            this.primaryAddress,
            this.identificationNumber,
            this.noManufacturerIdentification,
            this.noVersion,
            this.noDeviceTypeIdentification);

    assertThat(FindMatchingChannelHelper.matches(requestData, channelValues))
        .withFailMessage(requestData + " should not match " + channelValues)
        .isFalse();

    assertThat(FindMatchingChannelHelper.matchesPartially(requestData, channelValues))
        .withFailMessage(requestData + " should match partially " + channelValues)
        .isTrue();

    final List<ChannelElementValuesDto> channelValuesList = Arrays.asList(channelValues);

    assertThat(FindMatchingChannelHelper.bestMatch(requestData, channelValuesList))
        .withFailMessage(requestData + " should have a best match from " + channelValuesList)
        .isNotNull();
  }

  @Test
  public void testNoAttributesInRequestAllInRetrievedValues() {

    final MbusChannelElementsDto requestData =
        new MbusChannelElementsDto(
            this.primaryAddress,
            this.deviceIdentification,
            this.noIdentificationNumber,
            this.noManufacturerIdentification,
            null,
            null);

    final ChannelElementValuesDto channelValues =
        new ChannelElementValuesDto(
            this.channel,
            this.primaryAddress,
            this.identificationNumber,
            this.manufacturerIdentification,
            this.version,
            this.deviceTypeIdentification);

    assertThat(FindMatchingChannelHelper.matches(requestData, channelValues))
        .withFailMessage(requestData + " should match " + channelValues)
        .isTrue();

    assertThat(FindMatchingChannelHelper.matchesPartially(requestData, channelValues))
        .withFailMessage(requestData + " should match partially " + channelValues)
        .isTrue();

    final List<ChannelElementValuesDto> channelValuesList = Arrays.asList(channelValues);

    assertThat(FindMatchingChannelHelper.bestMatch(requestData, channelValuesList))
        .withFailMessage(requestData + " should have a best match from " + channelValuesList)
        .isNotNull();
  }

  @Test
  public void testValuesBestMatchingToRequestShouldBePicked() {

    final MbusChannelElementsDto requestData =
        new MbusChannelElementsDto(
            this.primaryAddress,
            this.deviceIdentification,
            this.identificationNumber,
            this.manufacturerIdentification,
            this.version,
            this.deviceTypeIdentification);

    final ChannelElementValuesDto channelValues =
        new ChannelElementValuesDto(
            this.channel,
            this.primaryAddress,
            this.identificationNumber,
            this.manufacturerIdentification,
            this.version,
            this.deviceTypeIdentification);

    final ChannelElementValuesDto otherChannelValues =
        new ChannelElementValuesDto(
            this.otherChannel,
            this.otherPrimaryAddress,
            this.otherIdentificationNumber,
            this.otherManufacturerIdentification,
            this.otherVersion,
            this.otherDeviceTypeIdentification);

    final ChannelElementValuesDto partiallyMatchingChannelValues =
        new ChannelElementValuesDto(
            (short) 3,
            (short) 3,
            this.noIdentificationNumber,
            this.manufacturerIdentification,
            this.version,
            this.deviceTypeIdentification);

    assertThat(FindMatchingChannelHelper.matches(requestData, channelValues))
        .withFailMessage(requestData + " should match " + channelValues)
        .isTrue();

    assertThat(FindMatchingChannelHelper.matches(requestData, otherChannelValues))
        .withFailMessage(requestData + " should not match " + otherChannelValues)
        .isFalse();

    assertThat(FindMatchingChannelHelper.matches(requestData, partiallyMatchingChannelValues))
        .withFailMessage(requestData + " should not match " + partiallyMatchingChannelValues)
        .isFalse();

    assertThat(FindMatchingChannelHelper.matchesPartially(requestData, channelValues))
        .withFailMessage(requestData + " should match partially " + channelValues)
        .isTrue();

    assertThat(FindMatchingChannelHelper.matchesPartially(requestData, otherChannelValues))
        .withFailMessage(requestData + " should not match partially " + otherChannelValues)
        .isFalse();

    assertThat(
            FindMatchingChannelHelper.matchesPartially(requestData, partiallyMatchingChannelValues))
        .withFailMessage(requestData + " should match partially " + partiallyMatchingChannelValues)
        .isTrue();

    final List<ChannelElementValuesDto> channelValuesList =
        Arrays.asList(partiallyMatchingChannelValues, otherChannelValues, channelValues);

    final ChannelElementValuesDto bestMatch =
        FindMatchingChannelHelper.bestMatch(requestData, channelValuesList);

    assertThat(bestMatch)
        .withFailMessage(requestData + " should have a best match from " + channelValuesList)
        .isNotNull();

    assertThat(bestMatch.getChannel())
        .withFailMessage("Channel for best match")
        .isEqualTo(channelValues.getChannel());
  }
}

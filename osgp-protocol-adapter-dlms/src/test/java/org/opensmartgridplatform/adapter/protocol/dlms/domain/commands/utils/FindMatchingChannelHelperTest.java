package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils;

import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

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

        final MbusChannelElementsDto requestData = new MbusChannelElementsDto(this.primaryAddress,
                this.deviceIdentification, this.identificationNumber, this.manufacturerIdentification, this.version,
                this.deviceTypeIdentification);

        final ChannelElementValuesDto channelValues = new ChannelElementValuesDto(this.channel, this.primaryAddress,
                this.identificationNumber, this.manufacturerIdentification, this.version,
                this.deviceTypeIdentification);

        Assert.assertTrue(requestData + " should match " + channelValues,
                FindMatchingChannelHelper.matches(requestData, channelValues));
    }

    @Test
    public void testAllAttributesInRequestNoneInRetrievedValues() {

        final MbusChannelElementsDto requestData = new MbusChannelElementsDto(this.primaryAddress,
                this.deviceIdentification, this.identificationNumber, this.manufacturerIdentification, this.version,
                this.deviceTypeIdentification);

        final ChannelElementValuesDto channelValues = new ChannelElementValuesDto(this.channel, this.noPrimaryAddress,
                this.noIdentificationNumber, this.noManufacturerIdentification, this.noVersion,
                this.noDeviceTypeIdentification);

        Assert.assertFalse(requestData + " should not match " + channelValues,
                FindMatchingChannelHelper.matches(requestData, channelValues));

        Assert.assertFalse(requestData + " should not match partially " + channelValues,
                FindMatchingChannelHelper.matchesPartially(requestData, channelValues));
    }

    @Test
    public void testAllAttributesInRequestOnlyPrimaryAddressInRetrievedValues() {

        final MbusChannelElementsDto requestData = new MbusChannelElementsDto(this.primaryAddress,
                this.deviceIdentification, this.identificationNumber, this.manufacturerIdentification, this.version,
                this.deviceTypeIdentification);

        final ChannelElementValuesDto channelValues = new ChannelElementValuesDto(this.channel, this.primaryAddress,
                this.noIdentificationNumber, this.noManufacturerIdentification, this.noVersion,
                this.noDeviceTypeIdentification);

        Assert.assertFalse(requestData + " should not match " + channelValues,
                FindMatchingChannelHelper.matches(requestData, channelValues));

        Assert.assertTrue(requestData + " should match partially " + channelValues,
                FindMatchingChannelHelper.matchesPartially(requestData, channelValues));

        final List<ChannelElementValuesDto> channelValuesList = Arrays.asList(channelValues);

        Assert.assertNotNull(requestData + " should have a best match from " + channelValuesList,
                FindMatchingChannelHelper.bestMatch(requestData, channelValuesList));
    }

    @Test
    public void testAllAttributesInRequestOnlySomeInRetrievedValues() {

        final MbusChannelElementsDto requestData = new MbusChannelElementsDto(this.primaryAddress,
                this.deviceIdentification, this.identificationNumber, this.manufacturerIdentification, this.version,
                this.deviceTypeIdentification);

        final ChannelElementValuesDto channelValues = new ChannelElementValuesDto(this.channel, this.primaryAddress,
                this.identificationNumber, this.noManufacturerIdentification, this.noVersion,
                this.noDeviceTypeIdentification);

        Assert.assertFalse(requestData + " should not match " + channelValues,
                FindMatchingChannelHelper.matches(requestData, channelValues));

        Assert.assertTrue(requestData + " should match partially " + channelValues,
                FindMatchingChannelHelper.matchesPartially(requestData, channelValues));

        final List<ChannelElementValuesDto> channelValuesList = Arrays.asList(channelValues);

        Assert.assertNotNull(requestData + " should have a best match from " + channelValuesList,
                FindMatchingChannelHelper.bestMatch(requestData, channelValuesList));
    }

    @Test
    public void testNoAttributesInRequestAllInRetrievedValues() {

        final MbusChannelElementsDto requestData = new MbusChannelElementsDto(this.primaryAddress,
                this.deviceIdentification, this.noIdentificationNumber, this.noManufacturerIdentification, null, null);

        final ChannelElementValuesDto channelValues = new ChannelElementValuesDto(this.channel, this.primaryAddress,
                this.identificationNumber, this.manufacturerIdentification, this.version,
                this.deviceTypeIdentification);

        Assert.assertTrue(requestData + " should match " + channelValues,
                FindMatchingChannelHelper.matches(requestData, channelValues));

        Assert.assertTrue(requestData + " should match partially " + channelValues,
                FindMatchingChannelHelper.matchesPartially(requestData, channelValues));

        final List<ChannelElementValuesDto> channelValuesList = Arrays.asList(channelValues);

        Assert.assertNotNull(requestData + " should have a best match from " + channelValuesList,
                FindMatchingChannelHelper.bestMatch(requestData, channelValuesList));
    }

    @Test
    public void testValuesBestMatchingToRequestShouldBePicked() {

        final MbusChannelElementsDto requestData = new MbusChannelElementsDto(this.primaryAddress,
                this.deviceIdentification, this.identificationNumber, this.manufacturerIdentification, this.version,
                this.deviceTypeIdentification);

        final ChannelElementValuesDto channelValues = new ChannelElementValuesDto(this.channel, this.primaryAddress,
                this.identificationNumber, this.manufacturerIdentification, this.version,
                this.deviceTypeIdentification);

        final ChannelElementValuesDto otherChannelValues = new ChannelElementValuesDto(this.otherChannel,
                this.otherPrimaryAddress, this.otherIdentificationNumber, this.otherManufacturerIdentification,
                this.otherVersion, this.otherDeviceTypeIdentification);

        final ChannelElementValuesDto partiallyMatchingChannelValues = new ChannelElementValuesDto((short) 3, (short) 3,
                this.noIdentificationNumber, this.manufacturerIdentification, this.version,
                this.deviceTypeIdentification);

        Assert.assertTrue(requestData + " should match " + channelValues,
                FindMatchingChannelHelper.matches(requestData, channelValues));

        Assert.assertFalse(requestData + " should not match " + otherChannelValues,
                FindMatchingChannelHelper.matches(requestData, otherChannelValues));

        Assert.assertFalse(requestData + " should not match " + partiallyMatchingChannelValues,
                FindMatchingChannelHelper.matches(requestData, partiallyMatchingChannelValues));

        Assert.assertTrue(requestData + " should match partially " + channelValues,
                FindMatchingChannelHelper.matchesPartially(requestData, channelValues));

        Assert.assertFalse(requestData + " should not match partially " + otherChannelValues,
                FindMatchingChannelHelper.matchesPartially(requestData, otherChannelValues));

        Assert.assertTrue(requestData + " should match partially " + partiallyMatchingChannelValues,
                FindMatchingChannelHelper.matchesPartially(requestData, partiallyMatchingChannelValues));

        final List<ChannelElementValuesDto> channelValuesList = Arrays.asList(partiallyMatchingChannelValues,
                otherChannelValues, channelValues);

        final ChannelElementValuesDto bestMatch = FindMatchingChannelHelper.bestMatch(requestData, channelValuesList);

        Assert.assertNotNull(requestData + " should have a best match from " + channelValuesList, bestMatch);

        Assert.assertEquals("Channel for best match", channelValues.getChannel(), bestMatch.getChannel());
    }
}

package org.osgp.adapter.protocol.dlms.domain.commands.utils;

import org.junit.Assert;
import org.junit.Test;

import com.alliander.osgp.dto.valueobjects.smartmetering.ChannelElementValuesDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.MbusChannelElementsDto;

public class FindMatchingChannelHelperTest {

    /**
     * Happy flow where all database fields are filled and e-meter returns
     * values for all attribute-ids.
     */
    @Test
    public void testAllFilled() {
        final Short channel = 1;
        final short primaryAddress = 1;
        final int identificationNumber = 302343985;
        final int manufacturerIdentification = 12514;
        final short version = 66;
        final short deviceTypeIdentification = 3;

        final ChannelElementValuesDto channelValues = new ChannelElementValuesDto(channel, primaryAddress,
                identificationNumber, manufacturerIdentification, version, deviceTypeIdentification);
        final MbusChannelElementsDto requestData = new MbusChannelElementsDto("TST", 12056731L, "LGB", (short) 66,
                (short) 3);

        final int score = FindMatchingChannelHelper.getMbusDeviceMatchesScore(channelValues, requestData);
        Assert.assertEquals("mbus channel should match with score 9", 9, score);
    }

    /**
     * All database fields are filled and e-meter returns all 0 values.
     */
    @Test
    public void testDbsFilledAllAttrIdsEmpty() {
        final Short channel = 0;
        final short primaryAddress = 0;
        final int identificationNumber = 0;
        final int manufacturerIdentification = 0;
        final short version = 0;
        final short deviceTypeIdentification = 0;

        final ChannelElementValuesDto channelValues = new ChannelElementValuesDto(channel, primaryAddress,
                identificationNumber, manufacturerIdentification, version, deviceTypeIdentification);
        final MbusChannelElementsDto requestData = new MbusChannelElementsDto("TST", 12056731L, "LGB", (short) 66,
                (short) 3);

        final int score = FindMatchingChannelHelper.getMbusDeviceMatchesScore(channelValues, requestData);
        Assert.assertEquals("mbus channel should match with score 0", 0, score);
    }

    /**
     * All database fields are filled and e-meter returns all 0 values, except
     * for primary-address.
     */
    @Test
    public void testDbsFilledOnlyAttrIdPrimaryAddress() {
        final Short channel = 0;
        final short primaryAddress = 1;
        final int identificationNumber = 0;
        final int manufacturerIdentification = 0;
        final short version = 0;
        final short deviceTypeIdentification = 0;

        final ChannelElementValuesDto channelValues = new ChannelElementValuesDto(channel, primaryAddress,
                identificationNumber, manufacturerIdentification, version, deviceTypeIdentification);
        final MbusChannelElementsDto requestData = new MbusChannelElementsDto("TST", 12056731L, "LGB", (short) 66,
                (short) 3);

        final int score = FindMatchingChannelHelper.getMbusDeviceMatchesScore(channelValues, requestData);
        Assert.assertEquals("mbus channel should match with score 1", 1, score);
    }

    /**
     * Database fields are filled and e-meter returns all 0 values, except for
     * primary-address and identificationNumber.
     */
    @Test
    public void testDbsFilledSomeAttrId() {
        final Short channel = 0;
        final short primaryAddress = 1;
        final int identificationNumber = 302343985;
        final int manufacturerIdentification = 0;
        final short version = 0;
        final short deviceTypeIdentification = 0;

        final ChannelElementValuesDto channelValues = new ChannelElementValuesDto(channel, primaryAddress,
                identificationNumber, manufacturerIdentification, version, deviceTypeIdentification);
        final MbusChannelElementsDto requestData = new MbusChannelElementsDto("TST", 12056731L, "LGB", (short) 66,
                (short) 3);

        final int score = FindMatchingChannelHelper.getMbusDeviceMatchesScore(channelValues, requestData);
        Assert.assertEquals("mbus channel should match with score 9", 6, score);
    }

    /**
     * Database fields are empty and e-meter returns values all valid values.
     */
    @Test
    public void testDbsNullAllAttrFilled() {
        final Short channel = 1;
        final short primaryAddress = 1;
        final int identificationNumber = 302343985;
        final int manufacturerIdentification = 12514;
        final short version = 66;
        final short deviceTypeIdentification = 3;

        final ChannelElementValuesDto channelValues = new ChannelElementValuesDto(channel, primaryAddress,
                identificationNumber, manufacturerIdentification, version, deviceTypeIdentification);
        final MbusChannelElementsDto requestData = new MbusChannelElementsDto(null, null, null, null, null);

        final int score = FindMatchingChannelHelper.getMbusDeviceMatchesScore(channelValues, requestData);
        Assert.assertEquals("mbus channel should match with score 1 ", 1, score);
    }
}

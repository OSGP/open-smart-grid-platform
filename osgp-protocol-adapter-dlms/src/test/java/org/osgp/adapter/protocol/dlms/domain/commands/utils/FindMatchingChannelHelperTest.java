package org.osgp.adapter.protocol.dlms.domain.commands.utils;

import org.junit.Assert;
import org.junit.Test;

import com.alliander.osgp.dto.valueobjects.smartmetering.ChannelElementValues;
import com.alliander.osgp.dto.valueobjects.smartmetering.MbusChannelElementsDto;

public class FindMatchingChannelHelperTest {

    /**
     * happy flow all dbs fields are filled and e-meter returns values for all
     * attr-ids
     */
    @Test
    public void testAllFilled() {
        final Integer channel = 1;
        final short primaryAddress = 1;
        final int identificationNumber = 302343985;
        final int manufacturerIdentification = 12514;
        final short version = 66;
        final short deviceTypeIdentification = 3;

        final ChannelElementValues channelValues = new ChannelElementValues(channel, primaryAddress,
                identificationNumber, manufacturerIdentification, version, deviceTypeIdentification);
        final MbusChannelElementsDto requestData = new MbusChannelElementsDto("TST", "12056731", "LGB", "066", "3");

        final boolean matches = FindMatchingChannelHelper.mbusChannelMatches(channelValues, requestData);
        Assert.assertTrue("mbus channel should match", matches);
    }

    /**
     * dbs fields are filled and e-meter returns values all 0 values
     */
    @Test
    public void testDbsFilledAllAttrIdsEmpty() {
        final Integer channel = 0;
        final short primaryAddress = 0;
        final int identificationNumber = 0;
        final int manufacturerIdentification = 0;
        final short version = 0;
        final short deviceTypeIdentification = 0;

        final ChannelElementValues channelValues = new ChannelElementValues(channel, primaryAddress,
                identificationNumber, manufacturerIdentification, version, deviceTypeIdentification);
        final MbusChannelElementsDto requestData = new MbusChannelElementsDto("TST", "12056731", "LGB", "066", "3");

        final boolean matches = FindMatchingChannelHelper.mbusChannelMatches(channelValues, requestData);
        Assert.assertFalse("mbus channel does not match", matches);
    }

    /**
     * dbs fields are filled and e-meter returns values all 0 values, except for
     * primary-address
     */
    @Test
    public void testDbsFilledSomeAttrIdsEmpty() {
        final Integer channel = 0;
        final short primaryAddress = 1;
        final int identificationNumber = 0;
        final int manufacturerIdentification = 0;
        final short version = 0;
        final short deviceTypeIdentification = 0;

        final ChannelElementValues channelValues = new ChannelElementValues(channel, primaryAddress,
                identificationNumber, manufacturerIdentification, version, deviceTypeIdentification);
        final MbusChannelElementsDto requestData = new MbusChannelElementsDto("TST", "12056731", "LGB", "066", "3");

        final boolean matches = FindMatchingChannelHelper.mbusChannelMatches(channelValues, requestData);
        Assert.assertTrue("mbus channel should match", matches);
    }

    /**
     * dbs fields are filled and e-meter returns values all 0 values, except for
     * primary-address
     */
    @Test
    public void testDbsNullAllAttrFilled() {
        final Integer channel = 1;
        final short primaryAddress = 1;
        final int identificationNumber = 302343985;
        final int manufacturerIdentification = 12514;
        final short version = 66;
        final short deviceTypeIdentification = 3;

        final ChannelElementValues channelValues = new ChannelElementValues(channel, primaryAddress,
                identificationNumber, manufacturerIdentification, version, deviceTypeIdentification);
        final MbusChannelElementsDto requestData = new MbusChannelElementsDto(null, null, null, null, null);

        final boolean matches = FindMatchingChannelHelper.mbusChannelMatches(channelValues, requestData);
        Assert.assertFalse("mbus channel should not match", matches);
    }
}

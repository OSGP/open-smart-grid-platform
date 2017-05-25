package org.osgp.adapter.protocol.dlms.domain.commands.utils;

import org.junit.Assert;
import org.junit.Test;

import com.alliander.osgp.dto.valueobjects.smartmetering.ChannelElementValues;
import com.alliander.osgp.dto.valueobjects.smartmetering.MbusChannelElementsDto;

public class FindMatchingChannelHelperTest {

    /**
     * obviously, once the rules become clear how to match, this junit test
     * should enhanced accordingly
     */
    @Test
    public void test() {
        final int channel = 1;
        final ChannelElementValues channelValues = new ChannelElementValues(1, (short) 1, 1, 1, (short) 1, (short) 1);
        final MbusChannelElementsDto requestData = new MbusChannelElementsDto("", "", "", "", "");
        final boolean matches = FindMatchingChannelHelper.mbusChannelMatches(channel, channelValues, requestData);
        Assert.assertTrue("mbus channel should match", matches);
    }

}

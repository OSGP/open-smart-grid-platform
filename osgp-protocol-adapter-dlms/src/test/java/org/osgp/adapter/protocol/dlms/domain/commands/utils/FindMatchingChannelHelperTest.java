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
        final Integer channel = 1;
        final short primaryAddress = (short) 1;
        final int identificationNumber = 302343985;
        final ChannelElementValues channelValues = new ChannelElementValues(channel, primaryAddress,
                identificationNumber, 1, (short) 1, (short) 1);
        final MbusChannelElementsDto requestData = new MbusChannelElementsDto("TESTE1024", "12056731", "", "", "");
        final boolean matches = FindMatchingChannelHelper.mbusChannelMatches(channelValues, requestData);
        Assert.assertTrue("mbus channel should match", matches);
    }

}

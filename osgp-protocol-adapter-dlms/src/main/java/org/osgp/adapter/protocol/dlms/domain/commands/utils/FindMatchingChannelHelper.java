package org.osgp.adapter.protocol.dlms.domain.commands.utils;

import com.alliander.osgp.dto.valueobjects.smartmetering.ChannelElementValues;
import com.alliander.osgp.dto.valueobjects.smartmetering.MbusChannelElementsDto;

/**
 * because the logic how to match a channel to couple a gas-meter to an e-meter,
 * this dedicated class and a corresponding junit is created
 */
public class FindMatchingChannelHelper {

    private FindMatchingChannelHelper() {
        // empty ctor because it onlu contains static method
    }

    /**
     * because it is not clear yet, how to match the 4 properties with 4
     * corresponding attributes from the e-meter currently the match is only on
     * the e-meter attribute 5 (which does not have a corresponding dbs
     * property)
     * 
     * @param channel
     * @param channelValues
     * @param requestData
     * @return
     */
    public static boolean mbusChannelMatches(final int channel, final ChannelElementValues channelValues,
            final MbusChannelElementsDto requestData) {

        return (channelValues.getPrimaryAddress() > 0);
    }

}

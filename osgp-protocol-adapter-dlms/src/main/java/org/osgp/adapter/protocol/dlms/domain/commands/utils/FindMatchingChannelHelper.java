/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
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
    public static boolean mbusChannelMatches(final ChannelElementValues channelValues,
            final MbusChannelElementsDto requestData) {

        final long hexValue = Long.parseLong(requestData.getMbusIdentificationNumber(), 16);
        final int mbusDeviceValue = channelValues.getIdentificationNumber();

        return channelValues.getPrimaryAddress() > 0 && mbusDeviceValue == hexValue;
    }

}

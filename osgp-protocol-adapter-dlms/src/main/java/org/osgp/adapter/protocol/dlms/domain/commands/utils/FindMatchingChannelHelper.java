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
     * Here we check if the values from the database that came from the shipment
     * file, match with the corresponding return values from the e-meter. The
     * primary-address is not stored in the database, and the rule is, that
     * value (that corresponds with attr-id:5) is > 0. For the other fields, the
     * rule is that return values from the e-meter may be empty. In that case
     * they match, otherwise this value should be compared with the dbs value,
     * where some specific converting rules may apply.
     *
     * @param channel
     * @param channelValues
     * @param requestData
     * @return
     */
    public static boolean mbusChannelMatches(final ChannelElementValues channelValues,
            final MbusChannelElementsDto requestData) {

        if (channelValues.getPrimaryAddress() == 0) {
            return false;
        } else if (!matchIdentificationNumber(channelValues, requestData)) {
            return false;
        } else if (!matchManufacturerId(channelValues, requestData)) {
            return false;
        } else if (!matchDeviceType(channelValues, requestData)) {
            return false;
        } else if (!matchVersion(channelValues, requestData)) {
            return false;
        } else {
            return true;
        }
    }

    private static boolean matchIdentificationNumber(final ChannelElementValues channelValues,
            final MbusChannelElementsDto requestData) {
        if (channelValues.getIdentificationNumber() > 0) {
            final String mbusId = requestData.getMbusIdentificationNumber();
            final long hexValue = mbusId == null ? -1 : Long.parseLong(mbusId, 16);
            if (channelValues.getIdentificationNumber() != hexValue) {
                return false;
            }
        }
        return true;
    }

    private static boolean matchManufacturerId(final ChannelElementValues channelValues,
            final MbusChannelElementsDto requestData) {
        if (channelValues.getManufacturerIdentification() > 0) {
            final int manufacturerId = calcManufacturerId(requestData.getMbusManufacturerIdentification());
            if (channelValues.getManufacturerIdentification() != manufacturerId) {
                return false;
            }
        }

        return true;
    }

    private static boolean matchDeviceType(final ChannelElementValues channelValues,
            final MbusChannelElementsDto requestData) {
        if (channelValues.getDeviceTypeIdentification() > 0) {
            final String dbsDeviceType = requestData.getMbusDeviceTypeIdentification();
            final int deviceType = (dbsDeviceType == null) ? -1 : Integer.parseInt(dbsDeviceType);
            if (channelValues.getDeviceTypeIdentification() != deviceType) {
                return false;
            }
        }

        return true;
    }

    private static boolean matchVersion(final ChannelElementValues channelValues,
            final MbusChannelElementsDto requestData) {
        if (channelValues.getVersion() > 0) {
            final String dbsVersion = requestData.getMbusVersion();
            final int version = (dbsVersion == null) ? -1 : Integer.parseInt(dbsVersion);
            if (channelValues.getVersion() != version) {
                return false;
            }
        }

        return true;
    }

    /**
     * This return the integer value from the given
     * mbusManufacturerIdentification String value, that is stored in the dbs as
     * part of the shipment value according to EN 62056-21. with the following
     * formula: Man. ID = [ASCII(1st letter)– 64] * 32 * 32 +[ASCII(2nd letter)
     * – 64] * 32 +[ASCII(3rd letter) – 64] We expect a string of three chars,
     * if not 0 will be returned.
     *
     * @param mbusManufacturerIdentification
     * @return
     */
    private static int calcManufacturerId(final String mbusManufacturerIdentification) {
        int result = 0;
        if (mbusManufacturerIdentification.length() == 3) {
            final char[] chars = mbusManufacturerIdentification.toCharArray();
            result += (chars[0] - 64) * 32 * 32;
            result += (chars[1] - 64) * 32;
            result += (chars[2] - 64);
        }
        return result;
    }
}

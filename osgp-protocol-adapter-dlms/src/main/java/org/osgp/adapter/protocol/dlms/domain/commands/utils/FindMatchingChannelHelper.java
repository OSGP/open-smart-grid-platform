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

    private static final int DWORD = 16;

    private FindMatchingChannelHelper() {
        // empty ctor because it only contains static methods
    }

    /**
     * Here we check if the values from the database that came from the shipment
     * file, match with the corresponding return values from the e-meter. The
     * primary-address is not stored in the database, and the rule for this
     * value (that corresponds with attr-id:5) matches if it is > 0. For the
     * other fields, the rule is that return values from the e-meter may be
     * empty, which result in 0 values. In that case they match, otherwise this
     * value should be compared with the dbs value, where some specific
     * converting rules may apply.
     *
     * @param channelValues
     * @param requestData
     * @return
     */
    public static boolean mbusChannelMatches(final ChannelElementValues channelValues,
            final MbusChannelElementsDto requestData) {

        return (channelValues.getPrimaryAddress() > 0) && matchIdentificationNumber(channelValues, requestData)
                && matchManufacturerId(channelValues, requestData) && matchDeviceType(channelValues, requestData)
                && matchVersion(channelValues, requestData);
    }

    private static boolean matchIdentificationNumber(final ChannelElementValues channelValues,
            final MbusChannelElementsDto requestData) {

        // the ResultData object from the meter may empty, which result in a 0
        // value, if that cases it matches! (the same applies for the methods
        // below)
        if (channelValues.getIdentificationNumber() > 0) {
            final String mbusId = requestData.getMbusIdentificationNumber();
            final long hexValue = mbusId == null ? -1 : Long.parseLong(mbusId, DWORD);
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

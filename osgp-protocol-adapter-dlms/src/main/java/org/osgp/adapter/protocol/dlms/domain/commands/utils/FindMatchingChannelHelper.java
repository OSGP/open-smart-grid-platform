/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.osgp.adapter.protocol.dlms.domain.commands.utils;

import com.alliander.osgp.dto.valueobjects.smartmetering.ChannelElementValuesDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.MbusChannelElementsDto;

public class FindMatchingChannelHelper {

    private static final int INT16 = 16;
    private static final int INT32 = 32;
    private static final int INT64 = 64;

    private FindMatchingChannelHelper() {
        // empty constructor because it only contains static methods
    }

    /**
     * Here we calculate a score that indicates how the values from the given
     * ChannelElementValuesDto matches the corresponding values from the given
     * the MbusChannelElementsDto. The rule is that the primaryAddress must
     * greater than 0, otherwise a score of 0 is returned. A matching
     * identificationNumber will result in a higher score than a matching
     * version.
     *
     * @param channelElementValuesDto
     * @param mbusChannelElementsDto
     * @return
     */
    public static short getMbusDeviceMatchesScore(final ChannelElementValuesDto channelElementValuesDto,
            final MbusChannelElementsDto mbusChannelElementsDto) {

        short score = 0;

        if (isMbusSlaveDeviceConfigured(channelElementValuesDto)) {
            score += 1;
        } else {
            return 0;
        }

        score += matchIdentificationNumber(channelElementValuesDto, mbusChannelElementsDto);
        score += matchManufacturerId(channelElementValuesDto, mbusChannelElementsDto);
        score += matchDeviceType(channelElementValuesDto, mbusChannelElementsDto);
        score += matchVersion(channelElementValuesDto, mbusChannelElementsDto);

        return score;

    }

    /**
     * The given MbusChannelElementsDto does not contain a corresponding
     * primaryAddress, hence here only check the given primaryAddress
     *
     * @param channelValues
     * @return
     */
    private static boolean isMbusSlaveDeviceConfigured(final ChannelElementValuesDto channelValues) {
        return channelValues.getPrimaryAddress() > 0;
    }

    private static short matchIdentificationNumber(final ChannelElementValuesDto channelValues,
            final MbusChannelElementsDto requestData) {

        if (channelValues.hasIdentificationNumber()) {
            final Long mbusId = requestData.getMbusIdentificationNumber();
            final long hexValue = mbusId == null ? -1 : Long.parseLong(mbusId.toString(), INT16);
            if (channelValues.getIdentificationNumber() == hexValue) {
                return 5;
            }
        }
        return 0;
    }

    private static short matchManufacturerId(final ChannelElementValuesDto channelValues,
            final MbusChannelElementsDto requestData) {
        if (channelValues.hasManufacturerIdentification()) {
            final int manufacturerId = calcManufacturerId(requestData.getMbusManufacturerIdentification());
            if (channelValues.getManufacturerIdentification() == manufacturerId) {
                return 1;
            }
        }
        return 0;
    }

    private static short matchDeviceType(final ChannelElementValuesDto channelValues,
            final MbusChannelElementsDto requestData) {
        if (channelValues.hasDeviceTypeIdentification()) {
            final Short dbsDeviceType = requestData.getMbusDeviceTypeIdentification();
            if (dbsDeviceType != null && channelValues.getDeviceTypeIdentification() == dbsDeviceType) {
                return 1;
            }
        }
        return 0;
    }

    private static short matchVersion(final ChannelElementValuesDto channelValues,
            final MbusChannelElementsDto requestData) {
        if (channelValues.hasVersion()) {
            final Short dbsVersion = requestData.getMbusVersion();
            if (dbsVersion != null && channelValues.getVersion() == dbsVersion) {
                return 1;
            }
        }
        return 0;
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
        if (mbusManufacturerIdentification != null && mbusManufacturerIdentification.length() == 3) {
            final char[] chars = mbusManufacturerIdentification.toCharArray();
            result += (chars[0] - INT64) * INT32 * INT32;
            result += (chars[1] - INT64) * INT32;
            result += (chars[2] - INT64);
        }
        return result;
    }
}

/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.osgp.adapter.protocol.dlms.domain.commands.utils;

import java.util.List;

import com.alliander.osgp.dto.valueobjects.smartmetering.ChannelElementValuesDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.MbusChannelElementsDto;

public class FindMatchingChannelHelper {

    private FindMatchingChannelHelper() {
        // empty constructor because it only contains static methods
    }

    /**
     * Returns whether the {@code channelElementValues} is for a configured
     * M-Bus slave device and all non-null attributes of the
     * {@code mbusChannelElements} have an equal value in the
     * {@code channelElementValues}.
     *
     * @param mbusChannelElements
     * @param channelElementValues
     * @return {@code true} if the corresponding attributes of the
     *         {@code channelElementValues} are a match with the non-null
     *         attributes of {@code mbusChannelElements}; otherwise
     *         {@code false}}
     */
    public static boolean matches(final MbusChannelElementsDto mbusChannelElements,
            final ChannelElementValuesDto channelElementValues) {
        if (!channelElementValues.isMbusSlaveDeviceConfigured()) {
            return false;
        }
        if (mbusChannelElements.hasMbusIdentificationNumber() && !mbusChannelElements.getMbusIdentificationNumber()
                .equals(channelElementValues.getIdentificationNumber())) {
            return false;
        }
        if (mbusChannelElements.hasMbusManufacturerIdentification() && !mbusChannelElements
                .getMbusManufacturerIdentification().equals(channelElementValues.getManufacturerIdentification())) {
            return false;
        }
        if (mbusChannelElements.hasMbusVersion()
                && !mbusChannelElements.getMbusVersion().equals(channelElementValues.getVersion())) {
            return false;
        }
        if (mbusChannelElements.hasMbusDeviceTypeIdentification() && !mbusChannelElements
                .getMbusDeviceTypeIdentification().equals(channelElementValues.getDeviceTypeIdentification())) {
            return false;
        }
        return true;
    }

    /**
     * Returns whether the {@code channelElementValues} is for a configured
     * M-Bus slave device and all attributes that are non-null in both the
     * {@code mbusChannelElements} and the {@code channelElementValues} are
     * equal.<br>
     * The difference with
     * {@link #matches(MbusChannelElementsDto, ChannelElementValuesDto)} is that
     * an attribute may be null in one of the parameters and non-null in the
     * other.
     *
     * @param mbusChannelElements
     * @param channelElementValues
     * @return {@code true} if the non-null attributes of the
     *         {@code channelElementValues) are a match with the non-null attributes of the
     *         {@code mbusChannelElements}; otherwise {@code false}}
     */
    public static boolean matchesPartially(final MbusChannelElementsDto mbusChannelElements,
            final ChannelElementValuesDto channelElementValues) {
        if (!channelElementValues.isMbusSlaveDeviceConfigured()) {
            return false;
        }
        if (mbusChannelElements.hasMbusIdentificationNumber() && channelElementValues.hasIdentificationNumber()
                && !mbusChannelElements.getMbusIdentificationNumber()
                        .equals(channelElementValues.getIdentificationNumber())) {
            return false;
        }
        if (mbusChannelElements.hasMbusManufacturerIdentification()
                && channelElementValues.hasManufacturerIdentification()
                && !mbusChannelElements.getMbusManufacturerIdentification()
                        .equals(channelElementValues.getManufacturerIdentification())) {
            return false;
        }
        if (mbusChannelElements.hasMbusVersion() && channelElementValues.hasVersion()
                && !mbusChannelElements.getMbusVersion().equals(channelElementValues.getVersion())) {
            return false;
        }
        if (mbusChannelElements.hasMbusDeviceTypeIdentification() && channelElementValues.hasDeviceTypeIdentification()
                && !mbusChannelElements.getMbusDeviceTypeIdentification()
                        .equals(channelElementValues.getDeviceTypeIdentification())) {
            return false;
        }
        return true;
    }

    public static ChannelElementValuesDto bestMatch(final MbusChannelElementsDto mbusChannelElements,
            final List<ChannelElementValuesDto> channelElementValuesList) {
        if (channelElementValuesList == null || channelElementValuesList.isEmpty()) {
            return null;
        }
        ChannelElementValuesDto bestMatch = null;
        int bestScore = -1;
        for (final ChannelElementValuesDto channelElementValues : channelElementValuesList) {
            if (matches(mbusChannelElements, channelElementValues)) {
                return channelElementValues;
            }
            if (!matchesPartially(mbusChannelElements, channelElementValues)) {
                continue;
            }
            final int score = score(mbusChannelElements, channelElementValues);
            if (score > bestScore) {
                bestMatch = channelElementValues;
                bestScore = score;
            }
        }
        return bestMatch;
    }

    private static int score(final MbusChannelElementsDto mbusChannelElements,
            final ChannelElementValuesDto channelElementValues) {
        if (!matchesPartially(mbusChannelElements, channelElementValues)) {
            return -1;
        }
        int score = 0;
        /*
         * Because matchesPartially(mbusChannelElements, channelElementValues)
         * is true at this point, any attributes that have a value in
         * mbusChannelElements as well as in channelElementValues should be
         * equal, so there is no need to repeat the checks for equality when
         * calculating the score.
         */
        if (mbusChannelElements.hasMbusIdentificationNumber() && channelElementValues.hasIdentificationNumber()) {
            /*
             * Give a higher value to a matching identification number, since it
             * is the least likely of all attributes to give a false positive
             * match for different devices.
             */
            score += 5;
        }
        if (mbusChannelElements.hasMbusManufacturerIdentification()
                && channelElementValues.hasManufacturerIdentification()) {
            score += 1;
        }
        if (mbusChannelElements.hasMbusVersion() && channelElementValues.hasVersion()) {
            score += 1;
        }
        if (mbusChannelElements.hasMbusDeviceTypeIdentification()
                && channelElementValues.hasDeviceTypeIdentification()) {
            score += 1;
        }
        return score;
    }

}

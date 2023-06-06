// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils;

import java.util.List;
import java.util.Objects;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ChannelElementValuesDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.MbusChannelElementsDto;

public class FindMatchingChannelHelper {

  private FindMatchingChannelHelper() {
    // empty constructor because it only contains static methods
  }

  /**
   * Returns whether the M-Bus Short ID attributes (identification number, manufacturer
   * identification, version and device type) match between the {@code channelElementValues} and the
   * {@code mbusChannelElements}.
   *
   * @return {@code true} if the corresponding attributes of the {@code channelElementValues} from
   *     the M-Bus Short ID are a match with the attributes of {@code mbusChannelElements};
   *     otherwise {@code false}}
   */
  public static boolean matches(
      final MbusChannelElementsDto mbusChannelElements,
      final ChannelElementValuesDto channelElementValues) {

    if (mbusChannelElements.hasPrimaryAddress() && mbusChannelElements.getPrimaryAddress() > 0) {
      return wiredMbusMatches(mbusChannelElements, channelElementValues);
    }

    /*
     * If the M-Bus ShortID matches, assume the M-Bus device is configured on the
     * channel. For wireless devices this means a full match, since the primary
     * address would always be 0.
     */
    return mbusShortIdMatches(mbusChannelElements, channelElementValues);
  }

  private static boolean mbusShortIdMatches(
      final MbusChannelElementsDto mbusChannelElements,
      final ChannelElementValuesDto channelElementValues) {

    return hasMbusDeviceInfo(mbusChannelElements)
        && hasMbusChannelElementMatch(mbusChannelElements, channelElementValues);
  }

  /**
   * @return true is all the channel elements are equal.
   */
  private static boolean hasMbusChannelElementMatch(
      final MbusChannelElementsDto mbusChannelElements,
      final ChannelElementValuesDto channelElementValues) {
    return Objects.equals(
            mbusChannelElements.getMbusIdentificationNumber(),
            channelElementValues.getIdentificationNumber())
        && Objects.equals(
            mbusChannelElements.getMbusManufacturerIdentification(),
            channelElementValues.getManufacturerIdentification())
        && Objects.equals(mbusChannelElements.getMbusVersion(), channelElementValues.getVersion())
        && Objects.equals(
            mbusChannelElements.getMbusDeviceTypeIdentification(),
            channelElementValues.getDeviceTypeIdentification());
  }

  /**
   * @return true is all M-Bus parameters are present
   */
  private static boolean hasMbusDeviceInfo(final MbusChannelElementsDto mbusChannelElements) {
    return mbusChannelElements.hasMbusIdentificationNumber()
        && mbusChannelElements.hasMbusManufacturerIdentification()
        && mbusChannelElements.hasMbusVersion()
        && mbusChannelElements.hasMbusDeviceTypeIdentification();
  }

  private static boolean wiredMbusMatches(
      final MbusChannelElementsDto mbusChannelElements,
      final ChannelElementValuesDto channelElementValues) {

    return hasValidMbusAttributes(mbusChannelElements, channelElementValues)
        && validateMbusDeviceInfoMatch(mbusChannelElements, channelElementValues);
  }

  /**
   * @return true when no single M-Bus parameter check has failed
   */
  private static boolean validateMbusDeviceInfoMatch(
      final MbusChannelElementsDto mbusChannelElements,
      final ChannelElementValuesDto channelElementValues) {
    return !(failMatchOnMbusIdentificationNumber(mbusChannelElements, channelElementValues)
        || failMatchOnMbusManufacturerIdentification(mbusChannelElements, channelElementValues)
        || failMatchOnMbusVersion(mbusChannelElements, channelElementValues)
        || failMatchOnMbusDeviceTypeIdentification(mbusChannelElements, channelElementValues));
  }

  /**
   * @return return true if M-Bus slave device is configured and primary address matches
   */
  private static boolean hasValidMbusAttributes(
      final MbusChannelElementsDto mbusChannelElements,
      final ChannelElementValuesDto channelElementValues) {
    return channelElementValues.isMbusSlaveDeviceConfigured()
        && mbusChannelElements.getPrimaryAddress().equals(channelElementValues.getPrimaryAddress());
  }

  private static boolean failMatchOnMbusIdentificationNumber(
      final MbusChannelElementsDto mbusChannelElements,
      final ChannelElementValuesDto channelElementValues) {
    return mbusChannelElements.hasMbusIdentificationNumber()
        && !mbusChannelElements
            .getMbusIdentificationNumber()
            .equals(channelElementValues.getIdentificationNumber());
  }

  private static boolean failMatchOnMbusManufacturerIdentification(
      final MbusChannelElementsDto mbusChannelElements,
      final ChannelElementValuesDto channelElementValues) {
    return mbusChannelElements.hasMbusManufacturerIdentification()
        && !mbusChannelElements
            .getMbusManufacturerIdentification()
            .equals(channelElementValues.getManufacturerIdentification());
  }

  private static boolean failMatchOnMbusVersion(
      final MbusChannelElementsDto mbusChannelElements,
      final ChannelElementValuesDto channelElementValues) {
    return mbusChannelElements.hasMbusVersion()
        && !mbusChannelElements.getMbusVersion().equals(channelElementValues.getVersion());
  }

  private static boolean failMatchOnMbusDeviceTypeIdentification(
      final MbusChannelElementsDto mbusChannelElements,
      final ChannelElementValuesDto channelElementValues) {
    return mbusChannelElements.hasMbusDeviceTypeIdentification()
        && !mbusChannelElements
            .getMbusDeviceTypeIdentification()
            .equals(channelElementValues.getDeviceTypeIdentification());
  }

  /**
   * Returns whether the {@code channelElementValues} is for a configured M-Bus slave device and all
   * attributes that are non-null in both the {@code mbusChannelElements} and the {@code
   * channelElementValues} are equal.<br>
   * The difference with {@link #matches(MbusChannelElementsDto, ChannelElementValuesDto)} is that
   * an attribute may be null in one of the parameters and non-null in the other.
   *
   * @return {@code true} if the non-null attributes of the {@code channelElementValues) are a match
   *     with the non-null attributes of the {@code mbusChannelElements}; otherwise {@code false}}
   */
  public static boolean matchesPartially(
      final MbusChannelElementsDto mbusChannelElements,
      final ChannelElementValuesDto channelElementValues) {

    return channelElementValues.isMbusSlaveDeviceConfigured()
        && validateMbusDeviceInfoPartialMatch(mbusChannelElements, channelElementValues);
  }

  /**
   * @return true if there no single partial check has failed
   */
  private static boolean validateMbusDeviceInfoPartialMatch(
      final MbusChannelElementsDto mbusChannelElements,
      final ChannelElementValuesDto channelElementValues) {
    return !(failPartialMatchOnMbusIdentificationNumber(mbusChannelElements, channelElementValues)
        || failPartialMatchOnMbusManufacturerIdentification(
            mbusChannelElements, channelElementValues)
        || failPartialMatchOnMbusVersion(mbusChannelElements, channelElementValues)
        || failPartialMatchOnMbusDeviceTypeIdentification(
            mbusChannelElements, channelElementValues));
  }

  private static boolean failPartialMatchOnMbusIdentificationNumber(
      final MbusChannelElementsDto mbusChannelElements,
      final ChannelElementValuesDto channelElementValues) {
    return mbusChannelElements.hasMbusIdentificationNumber()
        && channelElementValues.hasIdentificationNumber()
        && !mbusChannelElements
            .getMbusIdentificationNumber()
            .equals(channelElementValues.getIdentificationNumber());
  }

  private static boolean failPartialMatchOnMbusManufacturerIdentification(
      final MbusChannelElementsDto mbusChannelElements,
      final ChannelElementValuesDto channelElementValues) {
    return mbusChannelElements.hasMbusManufacturerIdentification()
        && channelElementValues.hasManufacturerIdentification()
        && !mbusChannelElements
            .getMbusManufacturerIdentification()
            .equals(channelElementValues.getManufacturerIdentification());
  }

  private static boolean failPartialMatchOnMbusVersion(
      final MbusChannelElementsDto mbusChannelElements,
      final ChannelElementValuesDto channelElementValues) {
    return mbusChannelElements.hasMbusVersion()
        && channelElementValues.hasVersion()
        && !mbusChannelElements.getMbusVersion().equals(channelElementValues.getVersion());
  }

  private static boolean failPartialMatchOnMbusDeviceTypeIdentification(
      final MbusChannelElementsDto mbusChannelElements,
      final ChannelElementValuesDto channelElementValues) {
    return mbusChannelElements.hasMbusDeviceTypeIdentification()
        && channelElementValues.hasDeviceTypeIdentification()
        && !mbusChannelElements
            .getMbusDeviceTypeIdentification()
            .equals(channelElementValues.getDeviceTypeIdentification());
  }

  public static ChannelElementValuesDto bestMatch(
      final MbusChannelElementsDto mbusChannelElements,
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

  private static int score(
      final MbusChannelElementsDto mbusChannelElements,
      final ChannelElementValuesDto channelElementValues) {

    if (!matchesPartially(mbusChannelElements, channelElementValues)) {
      return -1;
    }

    int score = 0;

    /*
     * Give a higher value for the matching of an identification number than in the
     * other attribute scores, since it is the least likely of all attributes to
     * give a false positive match for different devices.
     */
    score += scoreForMbusIdentificationNumber(mbusChannelElements, channelElementValues, 5);
    score += scoreForMbusManufacturerIdentification(mbusChannelElements, channelElementValues, 1);
    score += scoreForMbusVersion(mbusChannelElements, channelElementValues, 1);
    score += scoreForMbusDeviceTypeIdentification(mbusChannelElements, channelElementValues, 1);

    return score;
  }

  private static int scoreForMbusIdentificationNumber(
      final MbusChannelElementsDto mbusChannelElements,
      final ChannelElementValuesDto channelElementValues,
      final int attributeScore) {
    int score = 0;
    if (mbusChannelElements.hasMbusIdentificationNumber()
        && channelElementValues.hasIdentificationNumber()) {
      if (mbusChannelElements
          .getMbusIdentificationNumber()
          .equals(channelElementValues.getIdentificationNumber())) {
        score = attributeScore;
      } else {
        score = -attributeScore;
      }
    }
    return score;
  }

  private static int scoreForMbusManufacturerIdentification(
      final MbusChannelElementsDto mbusChannelElements,
      final ChannelElementValuesDto channelElementValues,
      final int attributeScore) {
    int score = 0;
    if (mbusChannelElements.hasMbusManufacturerIdentification()
        && channelElementValues.hasManufacturerIdentification()) {
      if (mbusChannelElements
          .getMbusManufacturerIdentification()
          .equals(channelElementValues.getManufacturerIdentification())) {
        score = attributeScore;
      } else {
        score = -attributeScore;
      }
    }
    return score;
  }

  private static int scoreForMbusDeviceTypeIdentification(
      final MbusChannelElementsDto mbusChannelElements,
      final ChannelElementValuesDto channelElementValues,
      final int attributeScore) {
    int score = 0;
    if (mbusChannelElements.hasMbusDeviceTypeIdentification()
        && channelElementValues.hasDeviceTypeIdentification()) {
      if (mbusChannelElements
          .getMbusDeviceTypeIdentification()
          .equals(channelElementValues.getDeviceTypeIdentification())) {
        score = attributeScore;
      } else {
        score = -attributeScore;
      }
    }
    return score;
  }

  private static int scoreForMbusVersion(
      final MbusChannelElementsDto mbusChannelElements,
      final ChannelElementValuesDto channelElementValues,
      final int attributeScore) {
    int score = 0;
    if (mbusChannelElements.hasMbusVersion() && channelElementValues.hasVersion()) {
      if (mbusChannelElements.getMbusVersion().equals(channelElementValues.getVersion())) {
        score = attributeScore;
      } else {
        score = -attributeScore;
      }
    }
    return score;
  }
}

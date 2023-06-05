// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.cucumber.platform.publiclighting.glue.steps.mocks;

import java.util.ArrayList;
import java.util.List;
import org.opensmartgridplatform.domain.core.valueobjects.RelayMap;
import org.opensmartgridplatform.domain.core.valueobjects.RelayType;
import org.opensmartgridplatform.oslp.Oslp.IndexAddressMap;
import org.opensmartgridplatform.oslp.OslpUtils;

public class RelayMapConverter {

  /**
   * Converts a list of IndexAddressMap items to a list of RelayMap items.
   *
   * @param indexAddressMapList the list to convert.
   * @return the created RelayMap list.
   */
  public static List<RelayMap> convertIndexAddressMapListToRelayMapList(
      final List<IndexAddressMap> indexAddressMapList) {
    if (indexAddressMapList == null) {
      return null;
    }

    final List<RelayMap> relayMapList = new ArrayList<>();
    for (final IndexAddressMap iam : indexAddressMapList) {
      relayMapList.add(RelayMapConverter.convertIndexAddressMapToRelayMap(iam));
    }

    return relayMapList;
  }

  /**
   * Converts a list of IndexAddressMap items to a list of RelayMap items.
   *
   * @param relayMapStringList the list to convert.
   * @return the created RelayMap list.
   */
  public static List<RelayMap> convertStringsListToRelayMapList(final String[] relayMapStringList) {
    if (relayMapStringList == null) {
      return null;
    }

    final List<RelayMap> relayMapList = new ArrayList<>();
    for (final String rms : relayMapStringList) {
      relayMapList.add(RelayMapConverter.convertStringsToRelayMap(rms));
    }

    return relayMapList;
  }

  /**
   * Converts an IndexAddressMap received from an OSLP device to a RelayMap
   *
   * @param indexAddressMap the IndexAddressMap to convert.
   * @return the created RelayMap.
   */
  private static RelayMap convertIndexAddressMapToRelayMap(final IndexAddressMap indexAddressMap) {
    final RelayType relayType = RelayType.valueOf(indexAddressMap.getRelayType().toString());
    final Integer relayIndex = OslpUtils.byteStringToInteger(indexAddressMap.getIndex());
    final Integer relayAddress = OslpUtils.byteStringToInteger(indexAddressMap.getAddress());

    return new RelayMap(relayIndex, relayAddress, relayType, null);
  }

  /**
   * Converts two Strings to a relay map. The String containing the index and address should conform
   * to the following format: "index,address,relayType" (without the quotes).
   *
   * @param relayMapString the index, address and relay type to convert.
   * @return the created RelayMap.
   */
  private static RelayMap convertStringsToRelayMap(final String relayMapString) {
    final String[] fields = relayMapString.split(",");

    return new RelayMap(
        Integer.parseInt(fields[0]),
        Integer.parseInt(fields[1]),
        RelayType.valueOf(fields[2]),
        null);
  }
}

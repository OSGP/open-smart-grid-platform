// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.domain.shared;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.opensmartgridplatform.domain.core.entities.DeviceOutputSetting;
import org.opensmartgridplatform.domain.core.valueobjects.DomainType;
import org.opensmartgridplatform.domain.core.valueobjects.LightValue;
import org.opensmartgridplatform.domain.core.valueobjects.RelayType;
import org.opensmartgridplatform.domain.core.valueobjects.TariffValue;

public class FilterLightAndTariffValuesHelper {

  private FilterLightAndTariffValuesHelper() {
    // Private constructor to prevent instantiating this class.
  }

  /**
   * Filter light values based on PublicLighting domain. Only matching values will be returned.
   *
   * @param source list to filter
   * @param dosMap mapping of output settings
   * @param allowedDomainType type of domain allowed
   * @return list with filtered values or empty list when domain is not allowed.
   */
  public static List<LightValue> filterLightValues(
      final List<LightValue> source,
      final Map<Integer, DeviceOutputSetting> dosMap,
      final DomainType allowedDomainType) {

    final List<LightValue> filteredValues = new ArrayList<>();
    if (allowedDomainType != DomainType.PUBLIC_LIGHTING) {
      // Return empty list
      return filteredValues;
    }

    for (final LightValue lv : source) {
      if (dosMap.containsKey(lv.getIndex())
          && dosMap.get(lv.getIndex()).getOutputType().domainType().equals(allowedDomainType)) {
        filteredValues.add(lv);
      }
    }

    return filteredValues;
  }

  /**
   * Filter light values based on TariffSwitching domain. Only matching values will be returned.
   *
   * @param source list to filter
   * @param dosMap mapping of output settings
   * @param allowedDomainType type of domain allowed
   * @return list with filtered values or empty list when domain is not allowed.
   */
  public static List<TariffValue> filterTariffValues(
      final List<LightValue> source,
      final Map<Integer, DeviceOutputSetting> dosMap,
      final DomainType allowedDomainType) {

    final List<TariffValue> filteredValues = new ArrayList<>();
    if (allowedDomainType != DomainType.TARIFF_SWITCHING) {
      // Return empty list
      return filteredValues;
    }

    for (final LightValue lv : source) {
      if (dosMap.containsKey(lv.getIndex())
          && dosMap.get(lv.getIndex()).getOutputType().domainType().equals(allowedDomainType)) {
        // Map light value to tariff value
        final TariffValue tf = new TariffValue();
        tf.setIndex(lv.getIndex());
        if (dosMap.get(lv.getIndex()).getOutputType().equals(RelayType.TARIFF_REVERSED)) {
          // Reversed means copy the 'isOn' value to the 'isHigh'
          // value without inverting the boolean value
          tf.setHigh(lv.isOn());
        } else {
          // Not reversed means copy the 'isOn' value to the 'isHigh'
          // value inverting the boolean value
          tf.setHigh(!lv.isOn());
        }

        filteredValues.add(tf);
      }
    }

    return filteredValues;
  }
}

// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.domain.core.valueobjects;

import java.util.List;

/** Mapped version of general DeviceStatus which maps to PL and TS domains. */
public class DeviceStatusMapped extends DeviceStatus {

  /** Serial Version UID. */
  private static final long serialVersionUID = 4843866024199626891L;

  private List<TariffValue> tariffValues;

  /**
   * Construct a device status mapped to domains (PL and TS).
   *
   * @param tariffValues tariff values to use
   * @param lightValues light values to use
   * @param preferredLinkType preferred linktype to use
   * @param actualLinkType actuallink type to use
   * @param lightType light type to use
   * @param eventNotificationsMask eventmask to use
   */
  public DeviceStatusMapped(
      final List<TariffValue> tariffValues,
      final List<LightValue> lightValues,
      final LinkType preferredLinkType,
      final LinkType actualLinkType,
      final LightType lightType,
      final int eventNotificationsMask) {
    super(lightValues, preferredLinkType, actualLinkType, lightType, eventNotificationsMask);
    this.tariffValues = tariffValues;
  }

  /**
   * @return the tariffValues
   */
  public List<TariffValue> getTariffValues() {
    return this.tariffValues;
  }

  /**
   * Update the current tariff values with new tariff values.
   *
   * @param tariffValues tariff values to update
   */
  public void updateTariffValues(final List<TariffValue> tariffValues) {
    this.tariffValues = tariffValues;
  }
}

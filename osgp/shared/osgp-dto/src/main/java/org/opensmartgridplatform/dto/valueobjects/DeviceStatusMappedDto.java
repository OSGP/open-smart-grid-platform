// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.dto.valueobjects;

import java.util.List;

/** Mapped version of general DeviceStatus which maps to PL and TS domains. */
public class DeviceStatusMappedDto extends DeviceStatusDto {

  /** Serial Version UID. */
  private static final long serialVersionUID = -8102526561021174241L;

  private List<TariffValueDto> tariffValues;

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
  public DeviceStatusMappedDto(
      final List<TariffValueDto> tariffValues,
      final List<LightValueDto> lightValues,
      final LinkTypeDto preferredLinkType,
      final LinkTypeDto actualLinkType,
      final LightTypeDto lightType,
      final int eventNotificationsMask) {
    super(lightValues, preferredLinkType, actualLinkType, lightType, eventNotificationsMask);
    this.tariffValues = tariffValues;
  }

  /**
   * @return the tariffValues
   */
  public List<TariffValueDto> getTariffValues() {
    return this.tariffValues;
  }

  /**
   * Update the current tariff values with new tariff values.
   *
   * @param tariffValues tariff values to update
   */
  public void updateTariffValues(final List<TariffValueDto> tariffValues) {
    this.tariffValues = tariffValues;
  }
}

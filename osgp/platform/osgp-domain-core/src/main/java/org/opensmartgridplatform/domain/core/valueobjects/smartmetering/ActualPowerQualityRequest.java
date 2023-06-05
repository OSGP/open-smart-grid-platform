// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.domain.core.valueobjects.smartmetering;

import lombok.EqualsAndHashCode;
import org.opensmartgridplatform.domain.core.valueobjects.DeviceFunction;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;

@EqualsAndHashCode
public class ActualPowerQualityRequest implements ActionRequest {

  private static final long serialVersionUID = 7924053476264448032L;

  private final String profileType;

  public ActualPowerQualityRequest(final String profileType) {
    this.profileType = profileType;
  }

  public String getProfileType() {
    return this.profileType;
  }

  @Override
  public void validate() throws FunctionalException {
    // not needed here
  }

  @Override
  public DeviceFunction getDeviceFunction() {
    return DeviceFunction.GET_ACTUAL_POWER_QUALITY;
  }

  @Override
  public String toString() {
    return String.format(
        "%s[profileType=%s]", ActualPowerQualityRequest.class.getSimpleName(), this.profileType);
  }
}

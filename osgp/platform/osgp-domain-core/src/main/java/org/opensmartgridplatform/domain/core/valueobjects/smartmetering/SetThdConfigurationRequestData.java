// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.domain.core.valueobjects.smartmetering;

import java.io.Serializable;
import lombok.Getter;
import org.opensmartgridplatform.domain.core.valueobjects.DeviceFunction;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;

@Getter
public class SetThdConfigurationRequestData implements Serializable, ActionRequest {

  private static final long serialVersionUID = -6433648884507669671L;

  private final long minDurationNormalToOver;
  private final long minDurationOverToNormal;
  private final long timeThreshold;
  private final int valueHysteresis;
  private final int valueThreshold;

  public SetThdConfigurationRequestData(
      final long minDurationNormalToOver,
      final long minDurationOverToNormal,
      final long timeThreshold,
      final int valueHysteresis,
      final int valueThreshold) {
    this.minDurationNormalToOver = minDurationNormalToOver;
    this.minDurationOverToNormal = minDurationOverToNormal;
    this.timeThreshold = timeThreshold;
    this.valueHysteresis = valueHysteresis;
    this.valueThreshold = valueThreshold;
  }

  @Override
  public void validate() throws FunctionalException {
    // No validation needed
  }

  @Override
  public DeviceFunction getDeviceFunction() {
    return DeviceFunction.SET_THD_CONFIGURATION;
  }
}

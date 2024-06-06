// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.domain.core.valueobjects.smartmetering;

import java.io.Serializable;
import org.opensmartgridplatform.domain.core.valueobjects.DeviceFunction;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;

public class SetClockConfigurationRequestData implements Serializable, ActionRequest {

  private static final long serialVersionUID = -5007970690190618239L;

  protected final short timeZoneOffset;

  protected final CosemDateTime daylightSavingsBegin;

  protected final CosemDateTime daylightSavingsEnd;

  protected final boolean daylightSavingsEnabled;

  public SetClockConfigurationRequestData(
      final short timeZoneOffset,
      final CosemDateTime daylightSavingsBegin,
      final CosemDateTime daylightSavingsEnd,
      final boolean daylightSavingsEnabled) {
    this.timeZoneOffset = timeZoneOffset;
    this.daylightSavingsBegin = daylightSavingsBegin;
    this.daylightSavingsEnd = daylightSavingsEnd;
    this.daylightSavingsEnabled = daylightSavingsEnabled;
  }

  public short getTimeZoneOffset() {
    return this.timeZoneOffset;
  }

  public CosemDateTime getDaylightSavingsBegin() {
    return this.daylightSavingsBegin;
  }

  public CosemDateTime getDaylightSavingsEnd() {
    return this.daylightSavingsEnd;
  }

  public boolean isDaylightSavingsEnabled() {
    return this.daylightSavingsEnabled;
  }

  @Override
  public void validate() throws FunctionalException {
    // No validation needed
  }

  @Override
  public DeviceFunction getDeviceFunction() {
    return DeviceFunction.SET_CLOCK_CONFIGURATION;
  }
}

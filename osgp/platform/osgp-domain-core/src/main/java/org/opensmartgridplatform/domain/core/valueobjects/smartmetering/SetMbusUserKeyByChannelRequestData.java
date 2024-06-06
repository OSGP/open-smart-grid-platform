// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.domain.core.valueobjects.smartmetering;

import java.io.Serializable;
import org.opensmartgridplatform.domain.core.valueobjects.DeviceFunction;
import org.opensmartgridplatform.shared.exceptionhandling.ComponentType;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalExceptionType;

public class SetMbusUserKeyByChannelRequestData implements Serializable, ActionRequest {

  private static final long serialVersionUID = 3484509683303452336L;

  private static final short MIN_CHANNEL = 1;
  private static final short MAX_CHANNEL = 4;

  private final short channel;

  public SetMbusUserKeyByChannelRequestData(final short channel) {
    this.channel = channel;
  }

  public short getChannel() {
    return this.channel;
  }

  @Override
  public void validate() throws FunctionalException {
    if (this.channel < MIN_CHANNEL || this.channel > MAX_CHANNEL) {
      throw new FunctionalException(
          FunctionalExceptionType.VALIDATION_ERROR,
          ComponentType.DOMAIN_SMART_METERING,
          new IllegalArgumentException(
              "Channel not in range [" + MIN_CHANNEL + ".." + MAX_CHANNEL + "]: " + this.channel));
    }
  }

  @Override
  public DeviceFunction getDeviceFunction() {
    return DeviceFunction.SET_MBUS_USER_KEY_BY_CHANNEL;
  }
}

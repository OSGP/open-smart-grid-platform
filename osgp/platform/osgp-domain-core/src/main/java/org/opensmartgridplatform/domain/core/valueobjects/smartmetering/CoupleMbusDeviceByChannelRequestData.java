// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.domain.core.valueobjects.smartmetering;

import org.opensmartgridplatform.domain.core.valueobjects.DeviceFunction;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;

public class CoupleMbusDeviceByChannelRequestData implements ActionRequest {

  private static final long serialVersionUID = 1522902244442651253L;
  private short channel;

  public CoupleMbusDeviceByChannelRequestData(final short channel) {
    this.channel = channel;
  }

  public short getChannel() {
    return this.channel;
  }

  @Override
  public void validate() throws FunctionalException {
    // nothing to validate
  }

  @Override
  public DeviceFunction getDeviceFunction() {
    return DeviceFunction.COUPLE_MBUS_DEVICE_BY_CHANNEL;
  }
}

// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.domain.core.valueobjects.smartmetering;

import org.opensmartgridplatform.domain.core.valueobjects.DeviceFunction;
import org.opensmartgridplatform.domain.core.valueobjects.DeviceLifecycleStatus;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;

public class SetDeviceLifecycleStatusByChannelRequestData implements ActionRequest {

  private static final long serialVersionUID = 3636769765482239443L;

  private final DeviceLifecycleStatus deviceLifecycleStatus;
  private final short channel;

  public SetDeviceLifecycleStatusByChannelRequestData(
      final short channel, final DeviceLifecycleStatus deviceLifecycleStatus) {
    this.deviceLifecycleStatus = deviceLifecycleStatus;
    this.channel = channel;
  }

  public short getChannel() {
    return this.channel;
  }

  public DeviceLifecycleStatus getDeviceLifecycleStatus() {
    return this.deviceLifecycleStatus;
  }

  @Override
  public void validate() throws FunctionalException {
    // nothing to validate
  }

  @Override
  public DeviceFunction getDeviceFunction() {
    return DeviceFunction.SET_DEVICE_LIFECYCLE_STATUS_BY_CHANNEL;
  }
}

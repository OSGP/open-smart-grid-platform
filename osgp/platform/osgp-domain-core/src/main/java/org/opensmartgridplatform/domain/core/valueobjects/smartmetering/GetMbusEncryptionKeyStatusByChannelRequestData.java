// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.domain.core.valueobjects.smartmetering;

import org.opensmartgridplatform.domain.core.valueobjects.DeviceFunction;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;

public class GetMbusEncryptionKeyStatusByChannelRequestData implements ActionRequest {

  private static final long serialVersionUID = 1714006845592365110L;
  private final short channel;

  public GetMbusEncryptionKeyStatusByChannelRequestData(final short channel) {
    this.channel = channel;
  }

  @Override
  public void validate() throws FunctionalException {
    // No validation needed
  }

  @Override
  public DeviceFunction getDeviceFunction() {
    return DeviceFunction.GET_MBUS_ENCRYPTION_KEY_STATUS_BY_CHANNEL;
  }

  public short getChannel() {
    return this.channel;
  }
}

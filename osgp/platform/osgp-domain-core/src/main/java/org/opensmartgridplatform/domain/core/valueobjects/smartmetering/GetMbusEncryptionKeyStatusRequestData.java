// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.domain.core.valueobjects.smartmetering;

import org.opensmartgridplatform.domain.core.valueobjects.DeviceFunction;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;

public class GetMbusEncryptionKeyStatusRequestData extends MbusActionRequest {

  private static final long serialVersionUID = 3636769765482239443L;

  public GetMbusEncryptionKeyStatusRequestData(final String mbusDeviceIdentification) {
    super(mbusDeviceIdentification);
  }

  @Override
  public void validate() throws FunctionalException {
    // No validation needed

  }

  @Override
  public DeviceFunction getDeviceFunction() {
    return DeviceFunction.GET_MBUS_ENCRYPTION_KEY_STATUS;
  }
}

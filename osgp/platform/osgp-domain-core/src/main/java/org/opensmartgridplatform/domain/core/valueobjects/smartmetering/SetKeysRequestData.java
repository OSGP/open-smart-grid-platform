// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.domain.core.valueobjects.smartmetering;

import java.io.Serializable;
import org.opensmartgridplatform.domain.core.valueobjects.DeviceFunction;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;

public class SetKeysRequestData implements Serializable, ActionRequest {

  private static final long serialVersionUID = 1573954141584647005L;

  private final byte[] authenticationKey;

  private final byte[] encryptionKey;

  public SetKeysRequestData(final byte[] authenticationKey, final byte[] encryptionKey) {
    this.authenticationKey = authenticationKey;
    this.encryptionKey = encryptionKey;
  }

  public byte[] getAuthenticationKey() {
    return this.authenticationKey;
  }

  public byte[] getEncryptionKey() {
    return this.encryptionKey;
  }

  @Override
  public void validate() throws FunctionalException {
    // No validation needed
  }

  @Override
  public DeviceFunction getDeviceFunction() {
    return DeviceFunction.REPLACE_KEYS;
  }
}

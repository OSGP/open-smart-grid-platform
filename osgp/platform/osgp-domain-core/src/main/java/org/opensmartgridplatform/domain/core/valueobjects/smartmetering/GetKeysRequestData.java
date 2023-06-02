//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.domain.core.valueobjects.smartmetering;

import java.util.List;
import org.opensmartgridplatform.domain.core.valueobjects.DeviceFunction;
import org.opensmartgridplatform.shared.exceptionhandling.ComponentType;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalExceptionType;

public class GetKeysRequestData implements ActionRequest {

  private static final long serialVersionUID = -1601425472295052943L;
  private final List<SecretType> secretTypes;

  public GetKeysRequestData(final List<SecretType> secretTypes) {
    this.secretTypes = secretTypes;
  }

  @Override
  public void validate() throws FunctionalException {
    if (this.secretTypes.isEmpty()) {
      throw new FunctionalException(
          FunctionalExceptionType.VALIDATION_ERROR,
          ComponentType.WS_SMART_METERING,
          new Exception("GetKeysRequest has an empty secret types list"));
    }
  }

  @Override
  public DeviceFunction getDeviceFunction() {
    return DeviceFunction.GET_KEYS;
  }

  public List<SecretType> getSecretTypes() {
    return this.secretTypes;
  }
}

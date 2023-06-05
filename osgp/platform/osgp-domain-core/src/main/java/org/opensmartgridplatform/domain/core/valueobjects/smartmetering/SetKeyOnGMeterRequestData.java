// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.domain.core.valueobjects.smartmetering;

import java.io.Serializable;
import org.opensmartgridplatform.domain.core.valueobjects.DeviceFunction;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;

public class SetKeyOnGMeterRequestData implements Serializable, ActionRequest {

  private static final long serialVersionUID = 3965412208032103531L;

  private final String mbusDeviceIdentification;

  private final SecretType secretType;

  private final Boolean closeOpticalPort;

  public SetKeyOnGMeterRequestData(
      final String mbusDeviceIdentification,
      final SecretType secretType,
      final Boolean closeOpticalPort) {
    super();
    this.mbusDeviceIdentification = mbusDeviceIdentification;
    this.secretType = secretType;
    this.closeOpticalPort = closeOpticalPort;
  }

  public String getMbusDeviceIdentification() {
    return this.mbusDeviceIdentification;
  }

  public SecretType getSecretType() {
    return this.secretType;
  }

  public Boolean getCloseOpticalPort() {
    return this.closeOpticalPort;
  }

  @Override
  public void validate() throws FunctionalException {
    // No validation needed
  }

  @Override
  public DeviceFunction getDeviceFunction() {
    return DeviceFunction.SET_KEY_ON_G_METER;
  }
}

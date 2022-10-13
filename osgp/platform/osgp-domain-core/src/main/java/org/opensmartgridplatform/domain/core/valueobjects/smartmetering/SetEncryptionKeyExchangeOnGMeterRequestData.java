/*
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.domain.core.valueobjects.smartmetering;

import java.io.Serializable;
import org.opensmartgridplatform.domain.core.valueobjects.DeviceFunction;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;

public class SetEncryptionKeyExchangeOnGMeterRequestData implements Serializable, ActionRequest {

  private static final long serialVersionUID = 3965412208032103531L;

  private final String mbusDeviceIdentification;

  private final SecretType secretType;

  private final Boolean closeOpticalPort;

  public SetEncryptionKeyExchangeOnGMeterRequestData(
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
    return DeviceFunction.SET_ENCRYPTION_KEY_EXCHANGE_ON_G_METER;
  }
}

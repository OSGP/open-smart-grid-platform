/*
 * Copyright 2022 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.dto.valueobjects.smartmetering;

public class SetEncryptionKeyExchangeOnGMeterRequestDto implements ActionRequestDto {

  private static final long serialVersionUID = -5864741384305038393L;

  private final String mbusDeviceIdentification;
  private final int channel;
  private final SecretTypeDto secretType;
  private final boolean closeOpticalPort;

  public SetEncryptionKeyExchangeOnGMeterRequestDto(
      final String mbusDeviceIdentification,
      final int channel,
      final SecretTypeDto secretType,
      final boolean closeOpticalPort) {
    this.mbusDeviceIdentification = mbusDeviceIdentification;
    this.channel = channel;
    this.secretType = secretType;
    this.closeOpticalPort = closeOpticalPort;
  }

  public String getMbusDeviceIdentification() {
    return this.mbusDeviceIdentification;
  }

  public int getChannel() {
    return this.channel;
  }

  public SecretTypeDto getSecretType() {
    return this.secretType;
  }

  public boolean getCloseOpticalPort() {
    return this.closeOpticalPort;
  }
}

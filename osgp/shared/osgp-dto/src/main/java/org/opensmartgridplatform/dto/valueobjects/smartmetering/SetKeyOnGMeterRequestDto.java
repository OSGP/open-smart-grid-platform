//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.dto.valueobjects.smartmetering;

public class SetKeyOnGMeterRequestDto implements ActionRequestDto {

  private static final long serialVersionUID = -5864741384305038393L;

  private final String mbusDeviceIdentification;
  private final int channel;
  private final SecretTypeDto secretType;
  private final boolean closeOpticalPort;

  public SetKeyOnGMeterRequestDto(
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

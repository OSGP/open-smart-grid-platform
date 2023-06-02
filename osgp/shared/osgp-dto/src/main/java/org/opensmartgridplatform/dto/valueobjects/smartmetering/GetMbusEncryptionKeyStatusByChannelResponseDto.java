//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.dto.valueobjects.smartmetering;

public class GetMbusEncryptionKeyStatusByChannelResponseDto extends ActionResponseDto {

  private static final long serialVersionUID = 2065630176825953917L;

  private final EncryptionKeyStatusTypeDto encryptionKeyStatus;
  private final short channel;
  private final String gatewayDeviceIdentification;

  public GetMbusEncryptionKeyStatusByChannelResponseDto(
      final String gatewayDeviceIdentification,
      final EncryptionKeyStatusTypeDto encryptionKeyStatus,
      final short channel) {
    this.gatewayDeviceIdentification = gatewayDeviceIdentification;
    this.encryptionKeyStatus = encryptionKeyStatus;
    this.channel = channel;
  }

  public EncryptionKeyStatusTypeDto getEncryptionKeyStatus() {
    return this.encryptionKeyStatus;
  }

  public short getChannel() {
    return this.channel;
  }

  public String getGatewayDeviceIdentification() {
    return this.gatewayDeviceIdentification;
  }
}

// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.dto.valueobjects.smartmetering;

public class GetMbusEncryptionKeyStatusResponseDto extends ActionResponseDto {

  private static final long serialVersionUID = -8661462528133418593L;

  private String mbusDeviceIdentification;
  private EncryptionKeyStatusTypeDto encryptionKeyStatus;

  public GetMbusEncryptionKeyStatusResponseDto(
      final String mbusDeviceIdentification, final EncryptionKeyStatusTypeDto encryptionKeyStatus) {
    this.mbusDeviceIdentification = mbusDeviceIdentification;
    this.encryptionKeyStatus = encryptionKeyStatus;
  }

  public String getMbusDeviceIdentification() {
    return this.mbusDeviceIdentification;
  }

  public EncryptionKeyStatusTypeDto getEncryptionKeyStatus() {
    return this.encryptionKeyStatus;
  }
}

// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.dto.valueobjects.smartmetering;

public class GetMbusEncryptionKeyStatusRequestDto implements ActionRequestDto {

  private static final long serialVersionUID = 3432576706197401825L;

  private String mbusDeviceIdentification;
  private Short channel;

  public GetMbusEncryptionKeyStatusRequestDto(
      final String mbusDeviceIdentification, final Short channel) {
    this.mbusDeviceIdentification = mbusDeviceIdentification;
    this.channel = channel;
  }

  public String getMbusDeviceIdentification() {
    return this.mbusDeviceIdentification;
  }

  public Short getChannel() {
    return this.channel;
  }
}

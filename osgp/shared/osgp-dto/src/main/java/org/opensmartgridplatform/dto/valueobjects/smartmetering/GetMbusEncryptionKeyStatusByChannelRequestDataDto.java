// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.dto.valueobjects.smartmetering;

public class GetMbusEncryptionKeyStatusByChannelRequestDataDto implements ActionRequestDto {

  private static final long serialVersionUID = 5456202868652046554L;

  private final short channel;

  public GetMbusEncryptionKeyStatusByChannelRequestDataDto(final short channel) {
    this.channel = channel;
  }

  public short getChannel() {
    return this.channel;
  }
}

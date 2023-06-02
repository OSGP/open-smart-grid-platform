//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.dto.valueobjects.smartmetering;

import java.io.Serializable;

public class SetMbusUserKeyByChannelRequestDataDto implements Serializable, ActionRequestDto {

  private static final long serialVersionUID = -5793708223405661077L;

  private final short channel;

  public SetMbusUserKeyByChannelRequestDataDto(final short channel) {
    this.channel = channel;
  }

  public short getChannel() {
    return this.channel;
  }
}

// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.domain.core.valueobjects.smartmetering;

import java.io.Serializable;

public class MbusChannelShortEquipmentIdentifier implements Serializable {

  private static final long serialVersionUID = 8725926156491614715L;

  private final short channel;
  private final MbusShortEquipmentIdentifier shortId;

  public MbusChannelShortEquipmentIdentifier(
      final short channel, final MbusShortEquipmentIdentifier shortId) {
    this.channel = channel;
    this.shortId = shortId;
  }

  @Override
  public String toString() {
    return String.format(
        "MbusChannelShortEquipmentIdentifier[channel=%d, shortId=%s]", this.channel, this.shortId);
  }

  public short getChannel() {
    return this.channel;
  }

  public MbusShortEquipmentIdentifier getShortId() {
    return this.shortId;
  }
}

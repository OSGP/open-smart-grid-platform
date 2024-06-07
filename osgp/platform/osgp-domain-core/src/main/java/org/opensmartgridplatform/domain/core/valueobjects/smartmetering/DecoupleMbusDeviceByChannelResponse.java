// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.domain.core.valueobjects.smartmetering;

import java.io.Serializable;
import lombok.Getter;

@Getter
public class DecoupleMbusDeviceByChannelResponse extends ActionResponse implements Serializable {

  private static final long serialVersionUID = -7800915379658671321L;

  private final ChannelElementValues channelElementValues;
  private final String mbusDeviceIdentification;

  public DecoupleMbusDeviceByChannelResponse(
      final String mbusDeviceIdentification,
      final ChannelElementValues channelElementValues,
      final String resultString) {
    super(OsgpResultType.OK, null, resultString);
    this.mbusDeviceIdentification = mbusDeviceIdentification;
    this.channelElementValues = channelElementValues;
  }

  @Override
  public String toString() {
    return String.format(
        "DecoupleMbusDeviceByChannelResponse [ mbusDeviceIdentification=%s, channelElements=%s ]",
        this.mbusDeviceIdentification, this.channelElementValues);
  }
}

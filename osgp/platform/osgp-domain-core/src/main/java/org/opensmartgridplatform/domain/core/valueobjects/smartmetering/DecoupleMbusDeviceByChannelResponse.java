// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.domain.core.valueobjects.smartmetering;

import java.io.Serializable;
import lombok.Getter;

@Getter
public class DecoupleMbusDeviceByChannelResponse extends ActionResponse implements Serializable {

  private static final long serialVersionUID = -7800915379658671321L;

  private final Short channel;
  private final String mbusDeviceIdentification;

  public DecoupleMbusDeviceByChannelResponse(
      final String mbusDeviceIdentification, final Short channel) {
    super(OsgpResultType.OK, null, "Decouple Mbus Device By Channel was successful");
    this.mbusDeviceIdentification = mbusDeviceIdentification;
    this.channel = channel;
  }

  @Override
  public String toString() {
    return "DecoupleMbusDeviceByChannelResponse [channel="
        + this.channel
        + ", mbusDeviceIdentification="
        + this.mbusDeviceIdentification
        + "]";
  }
}

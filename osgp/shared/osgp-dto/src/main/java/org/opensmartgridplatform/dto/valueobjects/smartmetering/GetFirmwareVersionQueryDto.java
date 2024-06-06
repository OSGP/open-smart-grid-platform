// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.dto.valueobjects.smartmetering;

import java.io.Serializable;
import lombok.Getter;

@Getter
public class GetFirmwareVersionQueryDto implements Serializable {

  private static final long serialVersionUID = 3789304613810680161L;

  private final ChannelDto channel;

  private final String mbusDeviceIdentification;

  public GetFirmwareVersionQueryDto() {
    this(null, null);
  }

  public GetFirmwareVersionQueryDto(
      final ChannelDto channel, final String mbusDeviceIdentification) {
    this.channel = channel;
    this.mbusDeviceIdentification = mbusDeviceIdentification;
  }

  public boolean isMbusQuery() {
    return this.channel != null;
  }
}

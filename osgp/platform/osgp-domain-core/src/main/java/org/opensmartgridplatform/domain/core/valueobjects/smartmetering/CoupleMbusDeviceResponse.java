// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.domain.core.valueobjects.smartmetering;

import java.io.Serializable;

public class CoupleMbusDeviceResponse extends ActionResponse implements Serializable {

  private static final long serialVersionUID = -7800915379658671321L;

  private final String mbusDeviceIdentification;
  private final ChannelElementValues channelElementValues;

  public CoupleMbusDeviceResponse(
      final String mbusDeviceIdentification, final ChannelElementValues channelElementValues) {
    super();
    this.mbusDeviceIdentification = mbusDeviceIdentification;
    this.channelElementValues = channelElementValues;
  }

  public String getMbusDeviceIdentification() {
    return this.mbusDeviceIdentification;
  }

  public ChannelElementValues getChannelElementValues() {
    return this.channelElementValues;
  }
}

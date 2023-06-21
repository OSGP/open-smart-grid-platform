// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.domain.core.valueobjects.smartmetering;

import java.io.Serializable;

public class CoupleMbusDeviceByChannelResponse extends ActionResponse implements Serializable {

  private static final long serialVersionUID = -7800915379658671321L;

  private ChannelElementValues channelElementValues;

  public CoupleMbusDeviceByChannelResponse(final ChannelElementValues channelElementValues) {
    super();
    this.channelElementValues = channelElementValues;
  }

  public ChannelElementValues getChannelElementValues() {
    return this.channelElementValues;
  }
}

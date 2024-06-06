// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.domain.core.valueobjects;

import jakarta.validation.constraints.NotNull;
import java.io.Serializable;

/** Value object, representing the fixed device ip address, netmask and gateway. */
public class DeviceFixedIp implements Serializable {

  private static final long serialVersionUID = 8329456164046440627L;

  @NotNull private final String ipAddress;

  @NotNull private final String netMask;

  @NotNull private final String gateWay;

  public DeviceFixedIp(final String ipAddress, final String netMask, final String gateWay) {
    this.ipAddress = ipAddress;
    this.netMask = netMask;
    this.gateWay = gateWay;
  }

  public String getIpAddress() {
    return this.ipAddress;
  }

  public String getNetMask() {
    return this.netMask;
  }

  public String getGateWay() {
    return this.gateWay;
  }
}

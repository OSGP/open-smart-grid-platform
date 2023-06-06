// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.dto.valueobjects;

import java.io.Serializable;

/** Value object, representing the fixed device ip address, netmask and gateway. */
public class DeviceFixedIpDto implements Serializable {

  private static final long serialVersionUID = 8329456121046440627L;

  private final String ipAddress;
  private final String netMask;
  private final String gateWay;

  public DeviceFixedIpDto(final String ipAddress, final String netMask, final String gateWay) {
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

/*
 * Copyright 2014-2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.domain.core.valueobjects;

import java.io.Serializable;
import javax.validation.constraints.NotNull;

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

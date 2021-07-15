/*
 * Copyright 2014-2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
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

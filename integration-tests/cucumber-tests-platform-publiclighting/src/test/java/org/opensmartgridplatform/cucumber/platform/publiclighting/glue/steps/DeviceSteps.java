/*
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.platform.publiclighting.glue.steps;

import io.cucumber.java.ParameterType;
import io.cucumber.java.en.Given;
import java.util.Map;
import org.opensmartgridplatform.cucumber.platform.helpers.DeviceType;
import org.opensmartgridplatform.cucumber.platform.helpers.Protocol;
import org.opensmartgridplatform.cucumber.platform.helpers.Protocol.ProtocolType;
import org.opensmartgridplatform.cucumber.platform.helpers.ProtocolHelper;
import org.opensmartgridplatform.cucumber.platform.publiclighting.domain.DeviceFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

public class DeviceSteps {

  @Autowired private DeviceFactory deviceFactory;

  @ParameterType("light measurement device|light measurement RTU")
  public DeviceType deviceType(final String deviceType) {
    return DeviceType.valueOf(StringUtils.replace(deviceType, " ", "_").toUpperCase());
  }

  @ParameterType(".*")
  public Protocol protocol(final String protocolName) {
    return ProtocolHelper.getProtocol(ProtocolType.valueOf(protocolName));
  }

  @Given("a(n) {deviceType}( device) using {protocol} protocol")
  public void givenADeviceUsingProtocol(
      final DeviceType deviceType, final Protocol protocol, final Map<String, String> parameters) {
    this.deviceFactory.createDevice(deviceType, protocol, parameters);
  }
}

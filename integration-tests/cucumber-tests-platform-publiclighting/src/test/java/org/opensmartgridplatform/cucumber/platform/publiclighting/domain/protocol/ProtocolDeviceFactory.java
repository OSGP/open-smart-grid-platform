/*
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.platform.publiclighting.domain.protocol;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.entities.Iec60870Device;
import org.opensmartgridplatform.cucumber.platform.helpers.DeviceType;
import org.opensmartgridplatform.cucumber.platform.helpers.Protocol;
import org.opensmartgridplatform.cucumber.platform.helpers.Protocol.ProtocolType;
import org.opensmartgridplatform.cucumber.protocol.iec60870.database.Iec60870Database;
import org.opensmartgridplatform.shared.domain.entities.AbstractEntity;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ProtocolDeviceFactory implements InitializingBean {

  @Autowired private Iec60870Database iec60870Database;

  private final Map<ProtocolType, BiFunction<DeviceType, Map<String, String>, AbstractEntity>>
      factoryMap = new HashMap<>();

  @Override
  public void afterPropertiesSet() throws Exception {
    this.factoryMap.put(ProtocolType.IEC60870, this::createIec60870Device);
  }

  public AbstractEntity createProtocolDevice(
      final DeviceType deviceType, final Protocol protocol, final Map<String, String> settings) {

    final ProtocolType protocolType = protocol.getType();

    if (this.factoryMap.containsKey(protocolType)) {
      return this.factoryMap.get(protocolType).apply(deviceType, settings);
    } else {
      throw new UnsupportedOperationException("Unsupported protocol: " + protocol);
    }
  }

  private Iec60870Device createIec60870Device(
      final DeviceType deviceType, final Map<String, String> settings) {
    return this.iec60870Database.addIec60870Device(deviceType, settings);
  }
}

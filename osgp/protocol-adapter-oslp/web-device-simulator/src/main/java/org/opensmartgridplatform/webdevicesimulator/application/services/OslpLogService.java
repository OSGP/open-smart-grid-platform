/*
 * Copyright 2020 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.webdevicesimulator.application.services;

import org.opensmartgridplatform.oslp.OslpEnvelope;
import org.opensmartgridplatform.webdevicesimulator.domain.entities.Device;
import org.opensmartgridplatform.webdevicesimulator.domain.entities.OslpLogItem;
import org.opensmartgridplatform.webdevicesimulator.domain.repositories.OslpLogItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OslpLogService {

  @Autowired private OslpLogItemRepository oslpLogItemRepository;

  @Transactional
  public OslpLogItem writeOslpLogItem(
      final OslpEnvelope oslpEnvelope, final Device device, final boolean incoming) {
    final OslpLogItem logItem =
        new OslpLogItem(
            oslpEnvelope.getDeviceId(),
            device.getDeviceIdentification(),
            incoming,
            oslpEnvelope.getPayloadMessage());
    return this.oslpLogItemRepository.save(logItem);
  }
}

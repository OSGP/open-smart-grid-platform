// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

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

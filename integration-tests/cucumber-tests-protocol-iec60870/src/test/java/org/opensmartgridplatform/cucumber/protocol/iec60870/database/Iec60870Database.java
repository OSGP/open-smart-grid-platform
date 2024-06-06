// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.cucumber.protocol.iec60870.database;

import java.util.Map;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.entities.Iec60870Device;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.repositories.Iec60870DeviceRepository;
import org.opensmartgridplatform.cucumber.platform.helpers.DeviceType;
import org.opensmartgridplatform.cucumber.protocol.iec60870.domain.Iec60870DeviceFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class Iec60870Database {

  @Autowired private Iec60870DeviceFactory factory;

  @Autowired private Iec60870DeviceRepository repository;

  @Transactional("txMgrIec60870")
  public void prepareForScenario() {
    this.repository.deleteAllInBatch();
  }

  @Transactional("txMgrIec60870")
  public Iec60870Device addIec60870Device(
      final DeviceType deviceType, final Map<String, String> settings) {
    final Iec60870Device device = this.factory.create(deviceType, settings);
    return this.repository.save(device);
  }
}

/*
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.platform.publiclighting.database;

import org.opensmartgridplatform.adapter.protocol.oslp.elster.domain.repositories.OslpDeviceRepository;
import org.opensmartgridplatform.adapter.protocol.oslp.elster.domain.repositories.PendingSetScheduleRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class OslpDatabase {

  @Autowired private OslpDeviceRepository oslpDeviceRepository;
  @Autowired private PendingSetScheduleRequestRepository pendingSetScheduleRequestRepository;

  @Transactional("txMgrOslp")
  public void prepareDatabaseForScenario() {
    // First remove stuff from osgp_adapter_protocol_oslp.
    this.oslpDeviceRepository.deleteAllInBatch();
    this.pendingSetScheduleRequestRepository.deleteAllInBatch();
  }

  public boolean isOslpDeviceTableEmpty() {
    return this.oslpDeviceRepository.count() == 0;
  }

  public boolean isPendingSetScheduleRequestEmpty() {
    return this.pendingSetScheduleRequestRepository.count() == 0;
  }
}

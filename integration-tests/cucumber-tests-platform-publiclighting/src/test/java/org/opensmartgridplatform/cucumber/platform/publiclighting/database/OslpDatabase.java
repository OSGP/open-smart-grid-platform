//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
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

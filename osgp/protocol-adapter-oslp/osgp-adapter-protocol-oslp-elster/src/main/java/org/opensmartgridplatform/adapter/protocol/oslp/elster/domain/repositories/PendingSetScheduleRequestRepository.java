/*
 * Copyright 2020 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.oslp.elster.domain.repositories;

import java.util.Date;
import java.util.List;
import org.opensmartgridplatform.adapter.protocol.oslp.elster.domain.entities.PendingSetScheduleRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PendingSetScheduleRequestRepository
    extends JpaRepository<PendingSetScheduleRequest, Long> {

  List<PendingSetScheduleRequest> findAllByDeviceIdentificationAndExpiredAtIsAfter(
      String deviceIdentification, Date expiredAt);

  List<PendingSetScheduleRequest> findAllByDeviceUidAndExpiredAtIsAfter(
      String deviceUid, Date expiredAt);

  void deleteAllByDeviceIdentificationAndExpiredAtIsBefore(
      String deviceIdentification, Date expireDateTime);
}

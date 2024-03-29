// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.oslp.elster.domain.repositories;

import java.time.Instant;
import java.util.List;
import org.opensmartgridplatform.adapter.protocol.oslp.elster.domain.entities.PendingSetScheduleRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PendingSetScheduleRequestRepository
    extends JpaRepository<PendingSetScheduleRequest, Long> {

  List<PendingSetScheduleRequest> findAllByDeviceIdentificationAndExpiredAtIsAfter(
      String deviceIdentification, Instant expiredAt);

  List<PendingSetScheduleRequest> findAllByDeviceUidAndExpiredAtIsAfter(
      String deviceUid, Instant expiredAt);

  void deleteAllByDeviceIdentificationAndExpiredAtIsBefore(
      String deviceIdentification, Instant expireDateTime);
}

// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

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

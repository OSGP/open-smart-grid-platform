// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.domain.core.repositories;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.opensmartgridplatform.domain.core.entities.DomainInfo;
import org.opensmartgridplatform.domain.core.entities.RtuDevice;
import org.opensmartgridplatform.domain.core.valueobjects.DeviceLifecycleStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface RtuDeviceRepository
    extends JpaRepository<RtuDevice, Long>, JpaSpecificationExecutor<RtuDevice> {
  RtuDevice findById(long id);

  Optional<RtuDevice> findByDeviceIdentification(String deviceIdentification);

  List<RtuDevice> findByDeviceLifecycleStatusAndLastCommunicationTimeBefore(
      DeviceLifecycleStatus deviceLifecycleStatus, Instant lastCommunicationTime);

  List<RtuDevice> findByDeviceLifecycleStatusAndLastCommunicationTimeBeforeAndDomainInfo(
      DeviceLifecycleStatus deviceLifecycleStatus,
      Instant lastCommunicationTime,
      DomainInfo domainInfo);
}

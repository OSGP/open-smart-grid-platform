/**
 * Copyright 2014-2016 Smart Society Services B.V.
 *
 * <p>Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * <p>http://www.apache.org/licenses/LICENSE-2.0
 */
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

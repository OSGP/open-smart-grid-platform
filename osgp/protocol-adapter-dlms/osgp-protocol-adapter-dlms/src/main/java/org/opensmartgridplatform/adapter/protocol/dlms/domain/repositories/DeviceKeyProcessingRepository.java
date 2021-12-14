/*
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.domain.repositories;

import java.time.Instant;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DeviceKeyProcessing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DeviceKeyProcessingRepository extends JpaRepository<DeviceKeyProcessing, String> {

  @Modifying
  @Query(
      "UPDATE DeviceKeyProcessing d SET d.startTime = CURRENT_TIMESTAMP"
          + " WHERE d.deviceIdentification = :deviceIdentification"
          + "   AND d.startTime < :delayTime")
  int updateStartTime(
      @Param("deviceIdentification") String deviceIdentification,
      @Param("delayTime") Instant delayTime);
}

// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.domain.repositories;

import java.time.Instant;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface DlmsDeviceRepository extends JpaRepository<DlmsDevice, Long> {

  DlmsDevice findByDeviceIdentification(String deviceIdentification);

  DlmsDevice findByMbusIdentificationNumberAndMbusManufacturerIdentification(
      String mbusIdentificationNumber, String mbusManufacturerIdentification);

  @Modifying
  @Query(
      value =
          "UPDATE DlmsDevice"
              + "   SET keyProcessingStartTime = CURRENT_TIMESTAMP"
              + " WHERE deviceIdentification = :deviceIdentification"
              + "   AND (keyProcessingStartTime IS NULL OR"
              + "        keyProcessingStartTime < :oldestStartTimeNotConsiderTimedOut)")
  int setProcessingStartTime(
      @Param("deviceIdentification") String deviceIdentification,
      @Param("oldestStartTimeNotConsiderTimedOut") Instant oldestStartTimeNotConsiderTimedOut);

  @Transactional
  @Modifying
  @Query(
      value =
          "UPDATE DlmsDevice"
              + "   SET invocationCounter = :invocationCounter"
              + " WHERE deviceIdentification = :deviceIdentification")
  int updateInvocationCounter(
      @Param("deviceIdentification") String deviceIdentification,
      @Param("invocationCounter") Long invocationCounter);

  @Transactional
  @Modifying
  @Query(
      value =
          "UPDATE DlmsDevice"
              + "   SET firmwareHash = :firmwareHash"
              + " WHERE deviceIdentification = :deviceIdentification")
  int storeFirmwareHash(
      @Param("deviceIdentification") String deviceIdentification,
      @Param("firmwareHash") String firmwareHash);
}

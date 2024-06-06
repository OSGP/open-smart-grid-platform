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
              + "   SET invocationCounter = invocationCounter + :incrementValue"
              + " WHERE deviceIdentification = :deviceIdentification")
  int incrementInvocationCounter(
      @Param("deviceIdentification") String deviceIdentification,
      @Param("incrementValue") Long incrementValue);

  @Transactional
  @Modifying
  @Query(
      value =
          """
              UPDATE dlms_device
                 SET hls3Active = coalesce(:hls3active, hls3Active)
                    ,hls4Active = coalesce(:hls4active, hls4Active)
                    ,hls5Active = coalesce(:hls5active, hls5Active)
               WHERE device_identification = :deviceIdentification""",
      nativeQuery = true)
  int updateHlsActive(
      @Param("deviceIdentification") String deviceIdentification,
      @Param("hls3active") Boolean hls3active,
      @Param("hls4active") Boolean hls4active,
      @Param("hls5active") Boolean hls5active);

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

  //  @Transactional
  //  @Modifying
  //  @Query(
  //      value =
  //          "UPDATE DlmsDevice"
  //              + " SET clientId = :clientId,"
  //              + " logicalId = :logicalId,"
  //              + " inDebugMode = :inDebugMode,"
  //              + " useHdlc = :useHdlc,"
  //              + " useSn = :useSn,"
  //              + " polyphase = :polyphase,"
  //              + " keyProcessingStartTime = :keyProcessingStartTime,"
  //              + " mbusIdentificationNumber = :mbusIdentificationNumber,"
  //              + " mbusManufacturerIdentification = :mbusManufacturerIdentification,"
  //              + " protocolName = :protocolName,"
  //              + " protocolVersion = :protocolVersion,"
  //              + " ipAddress = :ipAddress,"
  //              + " invocationCounter = :invocationCounter,"
  //              + " hls3Active = :hls3Active,"
  //              + " hls4Active = :hls4Active,"
  //              + " hls5Active = :hls5Active,"
  //              + " timezone = :timezone,"
  //              + " firmwareHash = :firmwareHash"
  //              + " WHERE deviceIdentification = :deviceIdentification")
  //  int updateDlmsDevice(
  //      @Param("deviceIdentification") String deviceIdentification,
  //      @Param("clientId") Long clientId,
  //      @Param("logicalId") Long logicalId,
  //      @Param("inDebugMode") Boolean inDebugMode,
  //      @Param("useHdlc") Boolean useHdlc,
  //      @Param("useSn") Boolean useSn,
  //      @Param("polyphase") Boolean polyphase,
  //      @Param("keyProcessingStartTime") Instant keyProcessingStartTime,
  //      @Param("mbusIdentificationNumber") String mbusIdentificationNumber,
  //      @Param("mbusManufacturerIdentification") String mbusManufacturerIdentification,
  //      @Param("protocolName") String protocolName,
  //      @Param("protocolVersion") String protocolVersion,
  //      @Param("ipAddress") String ipAddress,
  //      @Param("invocationCounter") Long invocationCounter,
  //      @Param("hls3Active") Boolean hls3Active,
  //      @Param("hls4Active") Boolean hls4Active,
  //      @Param("hls5Active") Boolean hls5Active,
  //      @Param("timezone") String timezone,
  //      @Param("firmwareHash") String firmwareHash);
}

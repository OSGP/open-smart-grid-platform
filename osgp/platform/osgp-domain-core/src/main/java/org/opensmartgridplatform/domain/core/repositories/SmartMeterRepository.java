// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.domain.core.repositories;

import java.util.List;
import org.opensmartgridplatform.domain.core.entities.Device;
import org.opensmartgridplatform.domain.core.entities.SmartMeter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface SmartMeterRepository extends JpaRepository<SmartMeter, Long> {
  SmartMeter findByDeviceIdentification(String deviceIdentification);

  /**
   * @param deviceId the id (primary key) of the gateway device
   * @return the list of mbus devices coupled to the gateway device identified by deviceId
   */
  @Query("select s from SmartMeter s where s.gatewayDevice.id= ?1")
  List<SmartMeter> getMbusDevicesForGateway(Long deviceId);

  /**
   * @param mbusIdentificationNumber the identification number of the mbus device
   * @param mbusManufacturerIdentification the identification for the manufacturer of the mbus
   *     device
   * @return a single mbus device with the given identificationNumber and
   *     manufacturerIdentification.
   */
  @Query(
      "select s from SmartMeter s where s.mbusIdentificationNumber = ?1 and s.mbusManufacturerIdentification = ?2")
  SmartMeter findByMBusIdentificationNumber(
      String mbusIdentificationNumber, String mbusManufacturerIdentification);

  @Query("select s from SmartMeter s where s.gatewayDevice = ?1 and s.channel = ?2")
  SmartMeter findByGatewayDeviceAndChannel(Device gatewayDevice, Short channel);

  List<SmartMeter> findByGatewayDevice(Device gatewayDevice);

  @Transactional
  @Modifying
  @Query(
      "UPDATE SmartMeter s SET s.supplier = :supplier, s.channel = :channel, s.mbusIdentificationNumber = :mbusIdentificationNumber, s.mbusManufacturerIdentification = :mbusManufacturerIdentification, s.mbusVersion = :mbusVersion, s.mbusDeviceTypeIdentification = :mbusDeviceTypeIdentification, s.mbusPrimaryAddress = :mbusPrimaryAddress WHERE s.id = :id")
  void updateSmartMeter(
      @Param("id") Long id,
      @Param("supplier") String supplier,
      @Param("channel") Short channel,
      @Param("mbusIdentificationNumber") String mbusIdentificationNumber,
      @Param("mbusManufacturerIdentification") String mbusManufacturerIdentification,
      @Param("mbusVersion") Short mbusVersion,
      @Param("mbusDeviceTypeIdentification") Short mbusDeviceTypeIdentification,
      @Param("mbusPrimaryAddress") Short mbusPrimaryAddress);
}

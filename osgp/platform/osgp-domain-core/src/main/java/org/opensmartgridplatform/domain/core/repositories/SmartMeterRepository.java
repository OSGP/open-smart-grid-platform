/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.domain.core.repositories;

import java.util.List;

import org.opensmartgridplatform.domain.core.entities.SmartMeter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface SmartMeterRepository extends JpaRepository<SmartMeter, Long> {
    SmartMeter findByDeviceIdentification(String deviceIdentification);

    /**
     * @param deviceId
     *            the id (primary key) of the gateway device
     * @return the list of mbus devices coupled to the gateway device identified
     *         by deviceId
     */
    @Query("select s from SmartMeter s where s.gatewayDevice.id= ?1")
    List<SmartMeter> getMbusDevicesForGateway(Long deviceId);

    /**
     * @param gatewayDeviceIdentification
     *            the identification of the gateway device
     * @param mbusIdentificationNumber
     *            the identification number of the mbus device
     * @param mbusManufacturerIdentification
     *            the identification for the manufacturer of the mbus device
     * @param mbusVersion
     *            the version of the mbus device
     * @param mbusDeviceTypeIdentification
     *            the identification for the devicetype of the mbus device
     * @param mbusPrimaryAddress
     *            the primary address of the mbus device
     * @return the list of mbus devices with the given device attributes
     */
    @Query("select s from SmartMeter s where s.gatewayDevice.deviceIdentification = ?1 "
            + " and s.mbusIdentificationNumber = ?2  and s.mbusManufacturerIdentification = ?3 "
            + " and s.mbusVersion = ?4 and s.mbusDeviceTypeIdentification = ?5 and s.mbusPrimaryAddress = ?6")
    List<SmartMeter> getMbusDevicesForGateway(final String gatewayDeviceIdentification, Long mbusIdentificationNumber,
            String musbManufacturerIdentification, Short mbusVersion, Short mbusDeviceTypeIdentification,
            Short mbusPrimaryAddress);

    /**
     * @param mbusIdentificationNumber
     *            the identification number of the mbus device
     * @param mbusManufacturerIdentification
     *            the identification for the manufacturer of the mbus device
     * @return a single mbus device with the given identificationNumber and
     *         manufacturerIdentification.
     */
    @Query("select s from SmartMeter s where s.mbusIdentificationNumber = ?1 and s.mbusManufacturerIdentification = ?2")
    SmartMeter findByMBusIdentificationNumber(Long mbusIdentificationNumber, String mbusManufacturerIdentification);

}

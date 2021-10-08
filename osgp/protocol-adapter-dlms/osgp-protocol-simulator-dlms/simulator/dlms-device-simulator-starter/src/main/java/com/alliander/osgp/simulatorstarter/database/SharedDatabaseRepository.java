/*
 * Copyright 2018 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.simulatorstarter.database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class SharedDatabaseRepository {
  private static final Logger LOGGER = LoggerFactory.getLogger(SharedDatabaseRepository.class);

  private final JdbcTemplate jdbc;

  @Autowired
  public SharedDatabaseRepository(@Qualifier("sharedDb") final JdbcTemplate jdbc) {
    this.jdbc = jdbc;
  }

  public void insertDevice(final String deviceIdentification, final int port) {
    if (this.alreadyInserted(deviceIdentification)) {
      return;
    }
    this.jdbc.update(
        "INSERT INTO device (version, device_identification, is_activated, protocol_info_id, "
            + "network_address, in_maintenance, gateway_device_id, device_type, icc_id, "
            + "communication_provider, communication_method, hls3active, hls4active, hls5active, "
            + "with_list_supported, selective_access_supported, ip_address_is_static, port, "
            + "logical_id, creatorid, lastmodifiedid, in_debug_mode, device_lifecycle_status) "
            + "VALUES (0, ?, TRUE, (SELECT id FROM protocol_info WHERE protocol=\'protocol\'), "
            + "\'127.0.0.1\', FALSE, NULL, \'SMART_METER_E\', \'icc id\', \'KPN\', \'GPRS\', "
            + "FALSE, FALSE, TRUE, TRUE, TRUE, TRUE, ?, 1, -2, -2, FALSE, \'IN_USE\')",
        deviceIdentification,
        port);
    LOGGER.debug("Device inserted into shared database: {}", deviceIdentification);
  }

  private boolean alreadyInserted(final String deviceIdentification) {
    return this.jdbc.queryForObject(
            "SELECT COUNT(*) FROM device WHERE device_identification = ?",
            Integer.class,
            deviceIdentification)
        != 0;
  }

  public void deleteDevice(final String deviceIdentification) {
    this.jdbc.update("DELETE FROM device WHERE device_identification = ?", deviceIdentification);
    LOGGER.debug("Device deleted from core database: {}", deviceIdentification);
  }
}

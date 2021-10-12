/*
 * Copyright 2018 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.simulator.protocol.dlms.database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class CoreDatabaseRepository {
  private static final Logger LOGGER = LoggerFactory.getLogger(CoreDatabaseRepository.class);

  private final JdbcTemplate jdbc;

  @Autowired
  public CoreDatabaseRepository(@Qualifier("coreDb") final JdbcTemplate jdbc) {
    this.jdbc = jdbc;
  }

  public int insertDevice(final String deviceIdentification) {
    if (this.alreadyInserted(deviceIdentification)) {
      return 0;
    }
    this.jdbc.update(
        "INSERT INTO device "
            + "(creation_time, modification_time, version, device_identification, device_type, "
            + "is_activated, protocol_info_id, network_address, in_maintenance, "
            + "technical_installation_date, device_lifecycle_status) "
            + "VALUES (now(), now(), 0, ?, \'SMART_METER_E\', TRUE, "
            + "(SELECT id FROM protocol_info WHERE protocol=\'DSMR\'), \'127.0.0.1\', FALSE, now(), "
            + "\'IN_USE\')",
        deviceIdentification);
    this.jdbc.update(
        "INSERT INTO smart_meter (id, supplier) "
            + "VALUES ((SELECT id FROM device WHERE device_identification = ?), \'KAIFA\')",
        deviceIdentification);
    this.jdbc.update(
        "INSERT INTO device_authorization "
            + "(creation_time, modification_time, version, function_group, device, organisation) "
            + "VALUES (now(), now(), 0, 0,  (SELECT id FROM device WHERE device_identification = ?), "
            + "(SELECT id FROM organisation WHERE organisation_identification = \'test-org\'))",
        deviceIdentification);
    LOGGER.debug("Device inserted into core database: {}", deviceIdentification);
    return 1;
  }

  private boolean alreadyInserted(final String deviceIdentification) {
    return this.jdbc.queryForObject(
            "SELECT COUNT(*) FROM device WHERE device_identification = ?",
            Integer.class,
            deviceIdentification)
        != 0;
  }

  public int deleteDevice(final String deviceIdentification) {
    final int nrDeleted =
        this.jdbc.update(
            "DELETE FROM device_authorization "
                + "WHERE device IN (SELECT id FROM device WHERE device_identification = ?)",
            deviceIdentification);
    this.jdbc.update(
        "DELETE from smart_meter "
            + "WHERE id IN (SELECT id FROM device WHERE device_identification = ?)",
        deviceIdentification);
    this.jdbc.update("DELETE from device WHERE device_identification = ?", deviceIdentification);
    LOGGER.debug("Device deleted from core database: {}", deviceIdentification);
    return nrDeleted;
  }
}

/*
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.simulatorstarter.database;

import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class ProtocolAdapterDlmsDatabaseRepository {
  private static final Logger LOGGER =
      LoggerFactory.getLogger(ProtocolAdapterDlmsDatabaseRepository.class);

  private final JdbcTemplate jdbc;

  @Autowired
  public ProtocolAdapterDlmsDatabaseRepository(
      @Qualifier("protocolAdapterDlmsDb") final JdbcTemplate jdbc) {
    this.jdbc = jdbc;
  }

  public void insertDevice(
      final String deviceIdentification,
      final int logicalDeviceId,
      final int port,
      final boolean forSmr5) {
    final Date validFrom = new Date(System.currentTimeMillis() - 86400000L);
    if (this.alreadyInserted(deviceIdentification)) {
      return;
    }

    final String protocol;
    final String protocolInfo;
    if (forSmr5) {
      protocol = "SMR";
      protocolInfo = "5.1";
    } else {
      protocol = "DSMR";
      protocolInfo = "4.2.2";
    }
    this.jdbc.update(
        "INSERT INTO dlms_device "
            + "(creation_time, modification_time, device_identification, version, icc_id, communication_provider, "
            + "communication_method, hls3active, hls4active, hls5active, challenge_length, with_list_supported, "
            + "selective_access_supported, ip_address_is_static, port, logical_id, in_debug_mode, use_sn, "
            + "use_hdlc, lls1active, mbus_identification_number, mbus_manufacturer_identification, protocol, "
            + "protocol_version) "
            + "VALUES (now(), now(), ?, 0, \'iccid\', \'KPN\', \'GPRS\', FALSE, FALSE, TRUE, NULL, FALSE, FALSE, "
            + "TRUE, ?, ?, FALSE, FALSE, FALSE, FALSE, NULL, NULL, ?, ?)",
        deviceIdentification,
        port,
        logicalDeviceId,
        protocol,
        protocolInfo);
    this.insertKey(
        "E_METER_MASTER",
        "bc082efed278e1bbebddc0431877d4fa16374b00e96dd102beab666dcb72efbd1f0b868412497f6d3d0c62caa4700585",
        deviceIdentification,
        validFrom);
    this.insertKey(
        "E_METER_ENCRYPTION",
        "bc082efed278e1bbebddc0431877d4fa2df7728229f3e03c57b2549142b40d047b35011dbf9f77ad91db5fe6f19a7b9c",
        deviceIdentification,
        validFrom);
    this.insertKey(
        "E_METER_AUTHENTICATION",
        "bc082efed278e1bbebddc0431877d4fae80fa4e72925b6ad0bc67c84b8721598eda8458bcc1b2827fe6e5e7918ce22fd",
        deviceIdentification,
        validFrom);
    LOGGER.debug("Device inserted into protocol adapter DLMS database: {}", deviceIdentification);
  }

  private void insertKey(
      final String keyType,
      final String key,
      final String deviceIdentification,
      final Date validFrom) {
    this.jdbc.update(
        "INSERT INTO security_key "
            + "(creation_time, modification_time, version, dlms_device_id, security_key_type, valid_from, "
            + "security_key) "
            + "VALUES (now(), now(), 0, (SELECT id FROM dlms_device WHERE device_identification = ?), "
            + "\'"
            + keyType
            + "\', ?, "
            + "\'"
            + key
            + "\')",
        deviceIdentification,
        validFrom);
  }

  private boolean alreadyInserted(final String deviceIdentification) {
    return this.jdbc.queryForObject(
            "SELECT COUNT(*) FROM dlms_device WHERE device_identification = ?",
            Integer.class,
            deviceIdentification)
        != 0;
  }

  public void deleteDevice(final String deviceIdentification) {
    this.jdbc.update(
        "DELETE FROM security_key "
            + "WHERE dlms_device_id IN (SELECT id FROM dlms_device WHERE device_identification = ?)",
        deviceIdentification);
    this.jdbc.update(
        "DELETE FROM dlms_device WHERE device_identification = ?", deviceIdentification);
    LOGGER.debug("Device deleted from protocol adapter DLMS database: {}", deviceIdentification);
  }
}

/*
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.simulatorstarter.database;

import com.alliander.osgp.simulatorstarter.SimulatorConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DatabaseHelper {
  private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseHelper.class);

  private final CoreDatabaseRepository coreDatabaseRepository;
  private final ProtocolAdapterDlmsDatabaseRepository protocolAdapterDlmsDatabaseRepository;
  private final SharedDatabaseRepository sharedDatabaseRepository;

  @Autowired
  public DatabaseHelper(
      final CoreDatabaseRepository coreDatabaseRepository,
      final ProtocolAdapterDlmsDatabaseRepository protocolAdapterDlmsDatabaseRepository,
      final SharedDatabaseRepository sharedDatabaseRepository) {
    this.coreDatabaseRepository = coreDatabaseRepository;
    this.protocolAdapterDlmsDatabaseRepository = protocolAdapterDlmsDatabaseRepository;
    this.sharedDatabaseRepository = sharedDatabaseRepository;
  }

  public void insertDevices(final SimulatorConfiguration simulatorConfiguration) {
    final int[] logicalDeviceIds = simulatorConfiguration.getLogicalDeviceIds();
    final int port = simulatorConfiguration.getPort();
    final boolean forSmr5 = simulatorConfiguration.isForSmr5();
    int nrInserted = 0;
    for (final int logicalDeviceId : logicalDeviceIds) {
      final String deviceIdentification =
          simulatorConfiguration.deviceIdentificationForIndex(logicalDeviceId);
      nrInserted += this.coreDatabaseRepository.insertDevice(deviceIdentification);
      this.protocolAdapterDlmsDatabaseRepository.insertDevice(
          deviceIdentification, logicalDeviceId, port, forSmr5);
      this.sharedDatabaseRepository.insertDevice(deviceIdentification, port);
    }
    LOGGER.info("{} Device(s) inserted.", nrInserted);
  }

  public void deleteDevices(final SimulatorConfiguration simulatorConfiguration) {
    final int[] logicalDeviceIds = simulatorConfiguration.getLogicalDeviceIds();
    int nrDeleted = 0;
    for (final int logicalDeviceId : logicalDeviceIds) {
      final String deviceIdentification =
          simulatorConfiguration.deviceIdentificationForIndex(logicalDeviceId);
      nrDeleted += this.coreDatabaseRepository.deleteDevice(deviceIdentification);
      this.protocolAdapterDlmsDatabaseRepository.deleteDevice(deviceIdentification);
      this.sharedDatabaseRepository.deleteDevice(deviceIdentification);
    }
    LOGGER.info("{} Device(s) deleted.", nrDeleted);
  }
}

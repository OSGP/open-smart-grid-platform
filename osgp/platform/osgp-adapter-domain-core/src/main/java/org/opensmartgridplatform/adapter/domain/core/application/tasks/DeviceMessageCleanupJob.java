/*
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.domain.core.application.tasks;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.joda.time.DateTime;
import org.opensmartgridplatform.adapter.domain.core.application.services.TransactionalDeviceLogItemService;
import org.opensmartgridplatform.logging.domain.entities.DeviceLogItem;
import org.opensmartgridplatform.shared.utils.FileZipper;
import org.opensmartgridplatform.shared.utils.csv.CsvWriter;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.Assert;

/**
 * Scheduled job which will find {@link DeviceLogItem} records older than {@link
 * DeviceMessageCleanupJob#deviceMessageRetentionPeriodInMonths}}. The old device messages are
 * converted to CSV format and stored as a file on the file system. Then the old device messages are
 * deleted from the database.
 */
@DisallowConcurrentExecution
public class DeviceMessageCleanupJob implements Job {

  private static final Logger LOGGER = LoggerFactory.getLogger(DeviceMessageCleanupJob.class);

  @Value("${osgp.scheduling.job.database.cleanup.device.message.enabled}")
  private boolean deviceMessageCleanupEnabled;

  @Value("${osgp.scheduling.job.database.cleanup.device.message.retention}")
  private int deviceMessageRetentionPeriodInMonths;

  @Value("${osgp.scheduling.job.database.cleanup.device.message.csv.file.location}")
  private String csvFileLocation;

  @Value("${osgp.scheduling.job.database.cleanup.device.message.csv.file.prefix}")
  private String csvFilePrefix;

  @Value("${osgp.scheduling.job.database.cleanup.device.message.csv.file.compression.enabled}")
  private boolean csvFileCompressionEnabled;

  @Value("${osgp.scheduling.job.database.cleanup.device.message.page.size}")
  private int deviceMessagePageSize;

  @Autowired private TransactionalDeviceLogItemService transactionalDeviceLogItemService;

  @Override
  public void execute(final JobExecutionContext context) {
    if (!this.deviceMessageCleanupEnabled) {
      LOGGER.debug("Device message records cleanup disabled.");
      return;
    }

    LOGGER.info("Quartz triggered cleanup of database - device message records.");
    final DateTime start = DateTime.now();

    try {
      final Date retention = this.calculateRetentionDate();
      final List<DeviceLogItem> oldDeviceMessages =
          this.transactionalDeviceLogItemService.findDeviceLogItemsBeforeDate(
              retention, this.deviceMessagePageSize);
      if (!oldDeviceMessages.isEmpty()) {
        this.saveDeviceMessagesToCsvFile(oldDeviceMessages);

        LOGGER.info("Deleting device messages...");
        this.transactionalDeviceLogItemService.deleteDeviceLogItems(oldDeviceMessages);
      }
    } catch (final Exception e) {
      LOGGER.error(
          "Exception during CSV file creation, compression or device message deletion.", e);
    }

    final DateTime end = DateTime.now();
    LOGGER.info(
        "Start: {}, end: {}, duration: {} milliseconds.",
        start,
        end,
        end.getMillis() - start.getMillis());
  }

  private Date calculateRetentionDate() {
    final Date date =
        DateTime.now().minusMonths(this.deviceMessageRetentionPeriodInMonths).toDate();
    LOGGER.info(
        "Determined date: {} based on device message retention period in months: {}.",
        date,
        this.deviceMessageRetentionPeriodInMonths);

    return date;
  }

  private void saveDeviceMessagesToCsvFile(final List<DeviceLogItem> deviceMessages)
      throws IOException {
    LOGGER.info("Converting device messages ...");
    final String[] headerLine = DeviceMessageToStringArrayConverter.getDeviceMessageFieldNames();
    final List<String[]> lines =
        DeviceMessageToStringArrayConverter.convertDeviceMessages(deviceMessages);
    LOGGER.info("Device messages converted.");

    final String csvFilePath =
        CsvWriter.writeCsvFile(this.csvFileLocation, this.csvFilePrefix, headerLine, lines);

    if (this.csvFileCompressionEnabled) {
      LOGGER.info("Compressing CSV file...");
      FileZipper.compressFileUsingDefaultSettings(csvFilePath);
    }
  }

  private static final class DeviceMessageToStringArrayConverter {

    private static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    private static final int ID = 0;
    private static final int CREATION_TIME = 1;
    private static final int MODIFICATION_TIME = 2;
    private static final int VERSION = 3;
    private static final int INCOMING = 4;
    private static final int DEVICE_UID = 5;
    private static final int ENCODED_MESSAGE = 6;
    private static final int DECODED_MESSAGE = 7;
    private static final int DEVICE_IDENTIFICATION = 8;
    private static final int ORGANISATION_IDENTIFICATION = 9;
    private static final int VALID = 10;
    private static final int DATA_SIZE = 11;

    private DeviceMessageToStringArrayConverter() {
      // Private constructor to prevent instantiation.
    }

    public static String[] getDeviceMessageFieldNames() {
      final String[] array = new String[12];
      array[ID] = "ID";
      array[CREATION_TIME] = "CREATION_TIME_UTC";
      array[MODIFICATION_TIME] = "MODIFICATION_TIME_UTC";
      array[VERSION] = "VERSION";
      array[INCOMING] = "INCOMING";
      array[DEVICE_UID] = "DEVICE_UID";
      array[ENCODED_MESSAGE] = "ENCODED_MESSAGE";
      array[DECODED_MESSAGE] = "DECODED_MESSAGE";
      array[DEVICE_IDENTIFICATION] = "DEVICE_IDENTIFICATION";
      array[ORGANISATION_IDENTIFICATION] = "ORGANISATION_IDENTIFICATION";
      array[VALID] = "VALID";
      array[DATA_SIZE] = "DATA_SIZE";

      return array;
    }

    public static List<String[]> convertDeviceMessages(final List<DeviceLogItem> deviceMessages) {
      final List<String[]> list = new ArrayList<>();

      for (final DeviceLogItem deviceMessage : deviceMessages) {
        final String[] representation = deviceMessageToStringArray(deviceMessage);
        list.add(representation);
      }

      return list;
    }

    private static String[] deviceMessageToStringArray(final DeviceLogItem deviceMessage) {
      Assert.notNull(deviceMessage, "Device message instance is null!");

      final String[] array = new String[12];
      array[ID] = String.valueOf(deviceMessage.getId());
      array[CREATION_TIME] = formatDate(deviceMessage.getCreationTime());
      array[MODIFICATION_TIME] = formatDate(deviceMessage.getModificationTime());
      array[VERSION] = String.valueOf(deviceMessage.getVersion());
      array[INCOMING] = String.valueOf(deviceMessage.isIncoming());
      array[DEVICE_UID] = deviceMessage.getDeviceUid();
      array[ENCODED_MESSAGE] = deviceMessage.getEncodedMessage();
      array[DECODED_MESSAGE] = deviceMessage.getDecodedMessage();
      array[DEVICE_IDENTIFICATION] = deviceMessage.getDeviceIdentification();
      array[ORGANISATION_IDENTIFICATION] = deviceMessage.getOrganisationIdentification();
      array[VALID] = String.valueOf(deviceMessage.isValid());
      array[DATA_SIZE] = String.valueOf(deviceMessage.getPayloadMessageSerializedSize());

      return array;
    }

    private static String formatDate(final Date date) {
      return new DateTime(date).toString(DATE_TIME_FORMAT);
    }
  }
}

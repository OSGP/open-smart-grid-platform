/**
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.domain.core.application.tasks;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.joda.time.DateTime;
import org.opensmartgridplatform.adapter.domain.core.application.services.TransactionalDeviceLogItemService;
import org.opensmartgridplatform.logging.domain.entities.DeviceLogItem;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * Scheduled job which will find {@link DeviceLogItem} records older than
 * {@link DeviceMessageCleanupJob#deviceMessageRetentionPeriodInMonths}}. The
 * old device messages are converted to CSV file and stored to the file system.
 * Then the old device messages are deleted from the database.
 */
@DisallowConcurrentExecution
public class DeviceMessageCleanupJob extends CsvWriterJob implements Job {

    private static final Logger LOGGER = LoggerFactory.getLogger(DeviceMessageCleanupJob.class);

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

    @Autowired
    private TransactionalDeviceLogItemService transactionalDeviceLogItemService;

    @Override
    public void execute(final JobExecutionContext context) throws JobExecutionException {
        LOGGER.info("Quartz triggered cleanup of database - device message records.");
        final DateTime start = DateTime.now();

        try {
            final Date retention = this.calculateDate();
            final List<DeviceLogItem> oldDeviceMessages = this.transactionalDeviceLogItemService
                    .findDeviceLogItemsBeforeDate(retention, this.deviceMessagePageSize);
            if (!oldDeviceMessages.isEmpty()) {
                this.saveDeviceMessagesToCsvFile(oldDeviceMessages);

                LOGGER.info("Deleting device messages...");
                this.transactionalDeviceLogItemService.deleteDeviceLogItems(oldDeviceMessages);
            }
        } catch (final Exception e) {
            LOGGER.error("Exception during CSV file creation, compression or device message deletion.", e);
        }

        final DateTime end = DateTime.now();
        LOGGER.info("Start: {}, end: {}, duration: {} milliseconds.", start, end, end.getMillis() - start.getMillis());
    }

    private Date calculateDate() {
        final Date date = DateTime.now().minusMonths(this.deviceMessageRetentionPeriodInMonths).toDate();
        LOGGER.info("Determined date: {} based on device message retention period in months: {}.", date,
                this.deviceMessageRetentionPeriodInMonths);

        return date;
    }

    private void saveDeviceMessagesToCsvFile(final List<DeviceLogItem> deviceMessages) throws IOException {
        LOGGER.info("Converting device messages ...");
        final String[] header = DeviceMessageToStringArrayConverter.getDeviceMessageFieldNames();
        final List<String[]> lines = DeviceMessageToStringArrayConverter.convertDeviceMessages(deviceMessages);
        LOGGER.info("Device messages converted.");

        final String csvFilePath = this.writeCsvFile(this.csvFileLocation, this.csvFilePrefix, header, lines);

        if (this.csvFileCompressionEnabled) {
            this.compressCsvFile(csvFilePath);
        }
    }

    private static final class DeviceMessageToStringArrayConverter {

        private static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

        private DeviceMessageToStringArrayConverter() {
            // Private constructor to prevent instantiation.
        }

        public static String[] getDeviceMessageFieldNames() {
            final String[] array = new String[12];
            array[0] = "ID";
            array[1] = "CREATION_TIME_UTC";
            array[2] = "MODIFICATION_TIME_UTC";
            array[3] = "VERSION";
            array[4] = "INCOMING";
            array[5] = "DEVICE_UID";
            array[6] = "ENCODED_MESSAGE";
            array[7] = "DECODED_MESSAGE";
            array[8] = "DEVICE_IDENTIFICATON";
            array[9] = "ORGANISATION_IDENTIFICATON";
            array[10] = "VALID";
            array[11] = "DATA_SIZE";

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
            array[0] = String.valueOf(deviceMessage.getId());
            array[1] = formatDate(deviceMessage.getCreationTime());
            array[2] = formatDate(deviceMessage.getModificationTime());
            array[3] = String.valueOf(deviceMessage.getVersion());
            array[4] = String.valueOf(deviceMessage.isIncoming());
            array[5] = deviceMessage.getDeviceUid();
            if (StringUtils.isEmpty(deviceMessage.getEncodedMessage())) {
                array[6] = "";
            } else {
                array[6] = deviceMessage.getEncodedMessage().replace(",", "");
            }
            if (StringUtils.isEmpty(deviceMessage.getDecodedMessage())) {
                array[7] = "";
            } else {
                array[7] = deviceMessage.getDecodedMessage().replace("\n", "");
            }
            array[8] = deviceMessage.getDeviceIdentification();
            array[9] = deviceMessage.getOrganisationIdentification();
            array[10] = String.valueOf(deviceMessage.isValid());
            array[11] = String.valueOf(deviceMessage.getPayloadMessageSerializedSize());

            return array;
        }

        private static String formatDate(final Date date) {
            return new DateTime(date).toString(DATE_TIME_FORMAT);
        }
    }

}

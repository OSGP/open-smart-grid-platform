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
import org.opensmartgridplatform.adapter.domain.core.application.services.TransactionalEventService;
import org.opensmartgridplatform.domain.core.entities.Event;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.Assert;

/**
 * Scheduled job which will find {@link Event} records older than
 * {@link EventCleanupJob#eventRetentionPeriodInMonths}}. The old events are
 * converted to CSV file and stored to the file system. Then the old events are
 * deleted from the database.
 */
@DisallowConcurrentExecution
public class EventCleanupJob extends CsvWriterJob implements Job {

    private static final Logger LOGGER = LoggerFactory.getLogger(EventCleanupJob.class);

    @Value("${osgp.scheduling.job.database.cleanup.event.retention}")
    private int eventRetentionPeriodInMonths;

    @Value("${osgp.scheduling.job.database.cleanup.event.csv.file.location}")
    private String csvFileLocation;

    @Value("${osgp.scheduling.job.database.cleanup.event.csv.file.prefix}")
    private String csvFilePrefix;

    @Value("${osgp.scheduling.job.database.cleanup.event.csv.file.compression.enabled}")
    private boolean csvFileCompressionEnabled;

    @Value("${osgp.scheduling.job.database.cleanup.event.page.size}")
    private int eventPageSize;

    @Autowired
    private TransactionalEventService transactionalEventService;

    @Override
    public void execute(final JobExecutionContext context) throws JobExecutionException {
        LOGGER.info("Quartz triggered cleanup of database - event records.");
        final DateTime start = DateTime.now();

        try {
            final Date retention = this.calculateDate();
            final List<Event> oldEvents = this.transactionalEventService.getEventsBeforeDate(retention,
                    this.eventPageSize);
            if (!oldEvents.isEmpty()) {
                this.saveEventsToCsvFile(oldEvents);

                LOGGER.info("Deleting events...");
                this.transactionalEventService.deleteEvents(oldEvents);
            }
        } catch (final Exception e) {
            LOGGER.error("Exception during CSV file creation, compression or event deletion.", e);
        }

        final DateTime end = DateTime.now();
        LOGGER.info("Start: {}, end: {}, duration: {} milliseconds.", start, end, end.getMillis() - start.getMillis());
    }

    private Date calculateDate() {
        final Date date = DateTime.now().minusMonths(this.eventRetentionPeriodInMonths).toDate();
        LOGGER.info("Determined date: {} based on event retention period in months: {}.", date,
                this.eventRetentionPeriodInMonths);

        return date;
    }

    private void saveEventsToCsvFile(final List<Event> events) throws IOException {
        LOGGER.info("Converting events...");
        final String[] header = EventToStringArrayConverter.getEventFieldNames();
        final List<String[]> lines = EventToStringArrayConverter.convertEvents(events);
        LOGGER.info("Events converted.");

        final String csvFilePath = this.writeCsvFile(this.csvFileLocation, this.csvFilePrefix, header, lines);

        if (this.csvFileCompressionEnabled) {
            this.compressCsvFile(csvFilePath);
        }
    }

    private static final class EventToStringArrayConverter {

        private static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

        private EventToStringArrayConverter() {
            // Private constructor to prevent instantiation.
        }

        public static String[] getEventFieldNames() {
            final String[] array = new String[9];
            array[0] = "ID";
            array[1] = "CREATION_TIME_UTC";
            array[2] = "MODIFICATION_TIME_UTC";
            array[3] = "VERSION";
            array[4] = "DEVICE_IDENTIFICATION";
            array[5] = "DATE_TIME_UTC";
            array[6] = "EVENT_TYPE";
            array[7] = "DESCRIPTION";
            array[8] = "RELAY_INDEX";

            return array;
        }

        public static List<String[]> convertEvents(final List<Event> events) {
            final List<String[]> list = new ArrayList<>();

            for (final Event event : events) {
                final String[] representation = eventToStringArray(event);
                list.add(representation);
            }

            return list;
        }

        private static String[] eventToStringArray(final Event event) {
            Assert.notNull(event, "Event instance is null!");

            final String[] array = new String[9];
            array[0] = String.valueOf(event.getId());
            array[1] = formatDate(event.getCreationTime());
            array[2] = formatDate(event.getModificationTime());
            array[3] = String.valueOf(event.getVersion());
            array[4] = event.getDevice().getDeviceIdentification();
            array[5] = formatDate(event.getDateTime());
            array[6] = event.getEventType().name();
            array[7] = event.getDescription();
            array[8] = String.valueOf(event.getIndex());

            return array;
        }

        private static String formatDate(final Date date) {
            return new DateTime(date).toString(DATE_TIME_FORMAT);
        }
    }

}

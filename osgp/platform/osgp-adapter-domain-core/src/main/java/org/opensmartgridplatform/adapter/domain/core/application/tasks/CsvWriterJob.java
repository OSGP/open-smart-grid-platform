/**
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.domain.core.application.tasks;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.opensmartgridplatform.shared.utils.FileZipper;
import org.opensmartgridplatform.shared.utils.csv.CsvWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CsvWriterJob {

    private static final Logger LOGGER = LoggerFactory.getLogger(CsvWriterJob.class);

    protected String writeCsvFile(final String csvFileLocation, final String csvFilePrefix, final String[] header,
            final List<String[]> lines) throws IOException {
        LOGGER.info("Writing data to CSV file...");
        final CsvWriter csvWriter = new CsvWriter();
        csvWriter.checkCsvFileStorageLocation(csvFileLocation);
        final String csvFilePath = csvWriter.getCsvFilePath(csvFileLocation, csvFilePrefix);
        csvWriter.writeCsvFile(csvFilePath, header, lines);
        return csvFilePath;
    }

    protected void compressCsvFile(final String csvFilePath) throws IOException {
        LOGGER.info("Compressing CSV file...");
        final FileZipper fileZipper = new FileZipper();
        fileZipper.compressFile(csvFilePath);

        LOGGER.info("Deleting CSV file...");
        final File csvFile = new File(csvFilePath);
        if (csvFile.delete()) {
            LOGGER.info("CSV file deleted.");
        } else {
            LOGGER.warn("CSV file not deleted!");
        }
    }

}

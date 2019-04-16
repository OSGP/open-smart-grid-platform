/**
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.shared.utils.csv;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

public class CsvWriter {

    private static final Logger LOGGER = LoggerFactory.getLogger(CsvWriter.class);

    private static final String COMMA = ",";
    private static final String CSV = ".csv";
    private static final String DATE_TIME_FORMAT = "yyyyMMdd-HHmmss";

    /**
     * Create or validate the folder where a CSV file is going to be stored.
     *
     * @param csvFileStorageLocation
     *            The path to the folder.
     */
    public void checkCsvFileStorageLocation(final String csvFileStorageLocation) {
        new File(csvFileStorageLocation).mkdirs();
    }

    /**
     * Get the full path to the CSV file to be stored. The file name will be
     * composed of the prefix, time stamp and file extension.
     *
     * @param csvFileStorageLocation
     *            The path to the folder.
     * @param csvFilePrefix
     *            The file name prefix.
     *
     * @return The full path, e.g. /tmp/csv-files/[prefix]-[time stamp].csv
     */
    public String getCsvFilePath(final String csvFileStorageLocation, final String csvFilePrefix) {
        String slash = "";
        if (!csvFileStorageLocation.endsWith(File.separator)) {
            slash = File.separator;
        }
        final String timestamp = DateTime.now().toString(DATE_TIME_FORMAT);

        return csvFileStorageLocation + slash + csvFilePrefix + timestamp + CSV;
    }

    /**
     * Write the header line and the content lines to a CSV file.
     *
     * @param csvFilePath
     *            The full path of the CSV file to be stored. Create it using
     *            {@link CsvWriter#getCsvFilePath(String, String)} for example.
     * @param header
     *            The header to be used.
     * @param lines
     *            The content of the CSV file.
     *
     * @throws IOException
     *             In case the file can not be written.
     */
    public void writeCsvFile(final String csvFilePath, final String[] header, final List<String[]> lines)
            throws IOException {
        final File csvFile = new File(csvFilePath);
        try (PrintWriter printWriter = new PrintWriter(csvFile)) {
            printWriter.println(this.convertToCsv(header));
            lines.stream().map(this::convertToCsv).forEach(printWriter::println);
        }
        final String message = String.format("CSV file [%s] not created!", csvFilePath);
        Assert.isTrue(csvFile.exists(), message);
        LOGGER.info("CSV file [{}] written.", csvFilePath);
    }

    private String convertToCsv(final String[] line) {
        return Stream.of(line).collect(Collectors.joining(COMMA));
    }
}

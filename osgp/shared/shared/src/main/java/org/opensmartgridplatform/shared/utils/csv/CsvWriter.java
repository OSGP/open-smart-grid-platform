// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.shared.utils.csv;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.text.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

public class CsvWriter {

  private static final Logger LOGGER = LoggerFactory.getLogger(CsvWriter.class);

  private static final String COMMA = ",";
  private static final String CSV = ".csv";
  private static final String DATE_TIME_FORMAT = "yyyyMMdd-HHmmss";

  public static String writeCsvFile(
      final String csvFileLocation,
      final String csvFilePrefix,
      final String[] headerLine,
      final List<String[]> lines)
      throws IOException {
    LOGGER.info("Writing data to CSV file...");
    final CsvWriter csvWriter = new CsvWriter();
    final boolean storageLocationOk = csvWriter.checkCsvFileStorageLocation(csvFileLocation);
    if (!storageLocationOk) {
      throw new IOException("Unable to find or create folder with path: " + csvFileLocation);
    }

    final String csvFilePath = csvWriter.getCsvFilePath(csvFileLocation, csvFilePrefix);
    csvWriter.writeCsvFile(csvFilePath, headerLine, lines);

    return csvFilePath;
  }

  /**
   * Create or validate the folder where a CSV file is going to be stored.
   *
   * @param csvFileStorageLocation The path to the folder.
   * @return True if the folder and it's parent folders have been created, false otherwise.
   */
  public boolean checkCsvFileStorageLocation(final String csvFileStorageLocation) {
    final File folder = new File(csvFileStorageLocation);
    if (folder.exists()) {
      return true;
    } else {
      return folder.mkdirs();
    }
  }

  /**
   * Get the full path to the CSV file to be stored. The file name will be composed of the prefix,
   * time stamp and file extension.
   *
   * @param csvFileStorageLocation The path to the folder.
   * @param csvFilePrefix The file name prefix.
   * @return The full path, e.g. /tmp/csv-files/[prefix]-[time stamp].csv
   */
  public String getCsvFilePath(final String csvFileStorageLocation, final String csvFilePrefix) {
    String slash = File.separator;
    if (csvFileStorageLocation.endsWith(File.separator)) {
      slash = "";
    }
    final String timestamp =
        ZonedDateTime.now().format(DateTimeFormatter.ofPattern(DATE_TIME_FORMAT));

    return csvFileStorageLocation + slash + csvFilePrefix + timestamp + CSV;
  }

  /**
   * Write the header line and the content lines to a CSV file.
   *
   * @param csvFilePath The full path of the CSV file to be stored. Create it using {@link
   *     CsvWriter#getCsvFilePath(String, String)} for example.
   * @param headerLine The header line to be used.
   * @param lines The content of the CSV file.
   * @throws IOException In case the file can not be written.
   */
  public void writeCsvFile(
      final String csvFilePath, final String[] headerLine, final List<String[]> lines)
      throws IOException {
    final File csvFile = new File(csvFilePath);
    try (final PrintWriter printWriter = new PrintWriter(csvFile)) {
      printWriter.println(this.convertToCsv(headerLine));
      lines.stream().map(this::convertToCsv).forEach(printWriter::println);
    }
    final String message = String.format("CSV file [%s] not created!", csvFilePath);
    Assert.isTrue(csvFile.exists(), message);
    LOGGER.info("CSV file [{}] written.", csvFilePath);
  }

  private String convertToCsv(final String[] line) {
    return Stream.of(line).map(this::escapeText).collect(Collectors.joining(COMMA));
  }

  private String escapeText(final String text) {
    if (StringUtils.isEmpty(text)) {
      return "";
    }

    return StringEscapeUtils.escapeCsv(text);
  }
}

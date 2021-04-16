/*
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.shared.utils;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.opensmartgridplatform.shared.utils.csv.CsvWriter;

public class CsvWriterTest {

  private static final String COMMA = ",";

  @TempDir Path folder;

  private final CsvWriter csvWriter = new CsvWriter();

  private final String fileName = "file.csv";
  private final String[] header = new String[] {"HEADER_1", "HEADER_2"};
  private List<String[]> lines;

  private String filePath;

  @BeforeEach
  public void setup() throws IOException {
    this.lines = new ArrayList<>();
    this.lines.add(new String[] {"a1", "a2"});
    this.lines.add(new String[] {"b1", null});

    this.filePath = this.folder.resolve(this.fileName).toString();
  }

  @Test
  public void createAndValidateCsvFile() throws IOException {
    this.csvWriter.writeCsvFile(this.filePath, this.header, this.lines);

    final File csvFile = new File(this.filePath);
    assertThat(csvFile).isNotNull();
    assertThat(csvFile.exists()).isTrue();

    this.validateCsvFileContent(csvFile);
  }

  private void validateCsvFileContent(final File csvFile) throws IOException {
    final StringBuilder stringBuilder = new StringBuilder();

    try (InputStream inputStream = new FileInputStream(csvFile)) {
      final byte[] buffer = new byte[1024];
      while (inputStream.read(buffer) > 0) {
        stringBuilder.append(new String(buffer, StandardCharsets.UTF_8).trim());
      }
    }

    final String content = stringBuilder.toString();
    assertThat(content).isEqualTo(this.getExpectedCsvFileContent());
  }

  private Object getExpectedCsvFileContent() {
    final StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append(this.header[0] + COMMA + this.header[1] + System.lineSeparator());
    stringBuilder.append(
        this.lines.get(0)[0] + COMMA + this.lines.get(0)[1] + System.lineSeparator());
    stringBuilder.append(this.lines.get(1)[0] + COMMA + "");

    return stringBuilder.toString();
  }
}

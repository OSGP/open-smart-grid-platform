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
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class FileZipperTest {

  @TempDir Path folder;

  final FileZipper fileZipper = new FileZipper();

  private final String fileName = "file.txt";
  private final List<String> lines = Arrays.asList("text text text");

  private String filePath;

  @BeforeEach
  public void setup() throws IOException {
    final Path path = this.folder.resolve(this.fileName);
    this.filePath = path.toString();
    Files.write(
        path,
        this.lines,
        StandardCharsets.UTF_8,
        StandardOpenOption.CREATE,
        StandardOpenOption.APPEND);
  }

  @Test
  public void createAndValidateZipFile() throws IOException {
    final String zipFilePath = this.fileZipper.compressFile(this.filePath);
    assertThat(zipFilePath).isEqualTo(this.filePath + ".zip");

    final File zipFile = new File(zipFilePath);
    assertThat(zipFile).isNotNull();
    assertThat(zipFile.exists()).isTrue();

    this.validateZipFileContent(zipFile);
  }

  private void validateZipFileContent(final File zipFile) throws IOException {
    final StringBuilder stringBuilder = new StringBuilder();

    try (ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(zipFile))) {
      ZipEntry zipEntry = zipInputStream.getNextEntry();
      final byte[] buffer = new byte[1024];

      while (zipEntry != null) {
        while (zipInputStream.read(buffer) > 0) {
          stringBuilder.append(new String(buffer, StandardCharsets.UTF_8).trim());
        }
        zipEntry = zipInputStream.getNextEntry();
      }
      zipInputStream.closeEntry();
    }

    final String content = stringBuilder.toString();
    assertThat(content).isEqualTo(this.lines.get(0));
  }
}

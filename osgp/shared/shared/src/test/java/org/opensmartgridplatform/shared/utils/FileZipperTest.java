// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.shared.utils;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayOutputStream;
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
    final ByteArrayOutputStream baos = new ByteArrayOutputStream();

    try (final ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(zipFile))) {
      ZipEntry zipEntry = zipInputStream.getNextEntry();

      while (zipEntry != null) {
        final byte[] buf = new byte[1024];
        int length;
        while ((length = zipInputStream.read(buf, 0, buf.length)) >= 0) {
          baos.write(buf, 0, length);
        }
        zipEntry = zipInputStream.getNextEntry();
      }
      zipInputStream.closeEntry();
    }

    final String content = baos.toString();
    assertThat(content)
        .isEqualTo(String.join(System.lineSeparator(), this.lines) + System.lineSeparator());
  }
}

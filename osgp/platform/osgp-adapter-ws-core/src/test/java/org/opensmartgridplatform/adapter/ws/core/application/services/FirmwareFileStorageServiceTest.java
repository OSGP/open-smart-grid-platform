// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.ws.core.application.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.opensmartgridplatform.shared.exceptionhandling.TechnicalException;

class FirmwareFileStorageServiceTest {

  private static final Path FIRMWARE_DIRECTORY = Paths.get("target/firmwarefiles");
  private static final byte[] FIRMWARE_FILE = "Firmware file content".getBytes();
  private static final byte[] IMAGE_IDENTIFIER = "some-identifier".getBytes();
  private static final String FILE_SHA256_DIGEST =
      "3292c982337e6ba20dc7435d0545b57a534971d1a88eb2d942d53d6685005f03";

  private static final String IMAGE_ID_EXTENSION = "imgid";

  private FirmwareFileStorageService service;

  @BeforeEach
  void setup() throws IOException, TechnicalException {
    Files.createDirectories(FIRMWARE_DIRECTORY);

    this.service =
        new FirmwareFileStorageService(FIRMWARE_DIRECTORY.toString(), IMAGE_ID_EXTENSION);
  }

  @Test
  void storeFirmwareFile() throws TechnicalException, IOException {
    final String identification = "myFirmware";

    this.service.storeFirmwareFile(FIRMWARE_FILE, identification);

    final byte[] writtenFile = Files.readAllBytes(FIRMWARE_DIRECTORY.resolve(identification));
    assertThat(writtenFile).isEqualTo(FIRMWARE_FILE);
  }

  @Test
  void storeFirmwareFileWhenNull() throws TechnicalException, IOException {
    final String identification = "myFirmware";

    this.service.storeFirmwareFile(null, identification);

    assertThat(Files.exists(FIRMWARE_DIRECTORY.resolve(identification))).isFalse();
  }

  @Test
  void storeFirmwareFileWhenEmpty() throws TechnicalException, IOException {
    final String identification = "myFirmware";

    this.service.storeFirmwareFile(new byte[0], identification);

    assertThat(Files.exists(FIRMWARE_DIRECTORY.resolve(identification))).isFalse();
  }

  @Test
  void storeFirmwareFileFails() {
    final String identification = "subdirectory/myFirmware";

    assertThatThrownBy(() -> this.service.storeFirmwareFile(FIRMWARE_FILE, identification))
        .isInstanceOf(TechnicalException.class)
        .hasMessage("Error storing firmware file with identifier 'subdirectory/myFirmware'");
  }

  @Test
  void storeFirmwareFileExisting() throws TechnicalException, IOException {
    final String identification = "myFirmware";

    this.service.storeFirmwareFile(FIRMWARE_FILE, identification);
    this.service.storeFirmwareFile("some other file".getBytes(), identification);

    final byte[] writtenFile = Files.readAllBytes(FIRMWARE_DIRECTORY.resolve(identification));
    assertThat(writtenFile).isEqualTo(FIRMWARE_FILE);
  }

  @Test
  void storeImageIdentifier() throws TechnicalException, IOException {
    final String identification = "myFirmware";

    this.service.storeImageIdentifier(IMAGE_IDENTIFIER, identification);

    final byte[] writtenFile =
        Files.readAllBytes(
            FIRMWARE_DIRECTORY.resolve(String.format("%s.%s", identification, IMAGE_ID_EXTENSION)));
    assertThat(writtenFile).isEqualTo(IMAGE_IDENTIFIER);
  }

  @Test
  void storeImageIdentifierFails() {
    final String identification = "subdirectory/myFirmware";

    assertThatThrownBy(() -> this.service.storeImageIdentifier(IMAGE_IDENTIFIER, identification))
        .isInstanceOf(TechnicalException.class)
        .hasMessage("Error storing image identifier with identifier 'subdirectory/myFirmware'");
  }

  @Test
  void createDigest() throws TechnicalException {
    final String identification = "myFirmware";
    this.service.storeFirmwareFile(FIRMWARE_FILE, identification);

    final String digest = this.service.createDigest("SHA-256", identification);

    assertThat(digest).isEqualTo(FILE_SHA256_DIGEST);
  }

  @Test
  void createDigestFailsOnAlgorithm() throws TechnicalException {
    final String identification = "myFirmware";
    this.service.storeFirmwareFile(FIRMWARE_FILE, identification);

    assertThatThrownBy(() -> this.service.createDigest("FOO-64", identification))
        .isInstanceOf(TechnicalException.class)
        .hasMessage("Cannot create digest with unknown algorithm 'FOO-64'");
  }

  @Test
  void createDigestReturnsNullWhenFirmwareFileDoesNotExist() throws TechnicalException {
    final String digest = this.service.createDigest("SHA-256", "non-existing-firmware");

    assertThat(digest).isNull();
  }

  @AfterEach
  void tearDown() throws IOException {
    if (Files.exists(FIRMWARE_DIRECTORY)) {
      Files.walk(FIRMWARE_DIRECTORY)
          .sorted(Comparator.reverseOrder())
          .map(Path::toFile)
          .forEach(File::delete);
    }
  }
}

// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.ws.core.application.services;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import lombok.extern.slf4j.Slf4j;
import org.opensmartgridplatform.shared.exceptionhandling.ComponentType;
import org.opensmartgridplatform.shared.exceptionhandling.TechnicalException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class FirmwareFileStorageService {

  private final Path firmwareDirectory;
  private final String firmwareImageIdExtension;

  public FirmwareFileStorageService(
      final String firmwareFileStorageDirectory, final String firmwareImageIdExtension)
      throws TechnicalException {
    this.firmwareDirectory = Paths.get(firmwareFileStorageDirectory);
    this.firmwareImageIdExtension = firmwareImageIdExtension;
  }

  /**
   * Stores the given file on disk with the given identifier. If the file already exists, it will
   * not be overwritten. If the file is null or empty, it will not be stored.
   *
   * @param firmwareFile
   * @param firmwareIdentification
   * @return <code>true</code> if the file was stored, <code>false</code> if the file already on
   *     disk or when the file is null or empty
   * @throws TechnicalException
   */
  public void storeFirmwareFile(final byte[] firmwareFile, final String firmwareIdentification)
      throws TechnicalException {

    this.checkStorageDirectory();
    if (firmwareFile == null || firmwareFile.length == 0) {
      log.info(
          "Firmware file with identifier '{}' is null or empty. File is not stored",
          firmwareIdentification);
      return;
    }

    if (this.isExistingFirmwareFile(firmwareIdentification)) {
      log.info(
          "Firmware file with identifier '{}' already exists in storage. File will not be overwritten",
          firmwareIdentification);
      return;
    }

    try {
      Files.write(this.firmwareDirectory.resolve(firmwareIdentification), firmwareFile);

    } catch (final IOException e) {
      throw new TechnicalException(
          ComponentType.WS_CORE,
          String.format("Error storing firmware file with identifier '%s'", firmwareIdentification),
          e);
    }
  }

  public void storeImageIdentifier(
      final byte[] imageIdentifier, final String firmwareIdentification) throws TechnicalException {

    this.checkStorageDirectory();
    if (imageIdentifier == null || imageIdentifier.length == 0) {
      log.info(
          "Image identifier with identifier '{}' is null or empty. Image identifier is not stored",
          firmwareIdentification);
      return;
    }

    try {
      Files.write(
          this.firmwareDirectory.resolve(
              String.format("%s.%s", firmwareIdentification, this.firmwareImageIdExtension)),
          imageIdentifier);
    } catch (final IOException e) {
      throw new TechnicalException(
          ComponentType.WS_CORE,
          String.format(
              "Error storing image identifier with identifier '%s'", firmwareIdentification),
          e);
    }
  }

  /**
   * Creates a digest of the firmware file with the given identifier. The precondition is that the
   * file is already stored on disk. If the file does not exist, null is returned instead.
   *
   * @param hashType
   * @param firmwareIdentification
   * @return the generated digest or null if the file does not exist
   * @throws TechnicalException
   */
  public String createDigest(final String hashType, final String firmwareIdentification)
      throws TechnicalException {

    String digest = null;

    try {
      if (this.isExistingFirmwareFile(firmwareIdentification)) {
        final MessageDigest messageDigest = MessageDigest.getInstance(hashType);
        final byte[] firmwareFile = this.readFirmwareFile(firmwareIdentification);

        digest = new BigInteger(1, messageDigest.digest(firmwareFile)).toString(16);
      }
    } catch (final NoSuchAlgorithmException e) {
      throw new TechnicalException(
          String.format("Cannot create digest with unknown algorithm '%s'", hashType), e);
    }

    return digest;
  }

  private void checkStorageDirectory() throws TechnicalException {
    if (!Files.exists(this.firmwareDirectory)) {
      throw new TechnicalException(
          ComponentType.WS_CORE,
          String.format(
              "%s cannot be used. Configured firmware.filestorage.directory '%s' does not exist.",
              this.getClass().getSimpleName(), this.firmwareDirectory));
    }
  }

  private boolean isExistingFirmwareFile(final String firmwareIdentification) {
    return Files.exists(this.firmwareDirectory.resolve(firmwareIdentification));
  }

  private byte[] readFirmwareFile(final String firmwareIdentification) throws TechnicalException {
    try {
      return Files.readAllBytes(this.firmwareDirectory.resolve(firmwareIdentification));
    } catch (final IOException e) {
      throw new TechnicalException(
          String.format(
              "Firmware file with identifier '%s' cannot be read from disk",
              firmwareIdentification),
          e);
    }
  }
}

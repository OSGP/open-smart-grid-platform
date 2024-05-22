package org.opensmartgridplatform.adapter.ws.core.application.services;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import lombok.extern.slf4j.Slf4j;
import org.opensmartgridplatform.shared.exceptionhandling.TechnicalException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class FirmwareFileStorageService {

  private final Path firmwareDirectory;
  private final String firmwareImageIdExtension;

  public FirmwareFileStorageService(
      final String firmwareDirectory, final String firmwareImageIdExtension) throws IOException {
    this.firmwareDirectory = Paths.get(firmwareDirectory);
    this.firmwareImageIdExtension = firmwareImageIdExtension;
    Files.createDirectories(this.firmwareDirectory);
  }

  public void storeFirmwareFile(final byte[] firmwareFile, final String firmwareIdentification)
      throws IOException {
    Files.write(this.firmwareDirectory.resolve(firmwareIdentification), firmwareFile);
  }

  public void storeImageIdentifier(
      final byte[] imageIdentifier, final String firmwareIdentification) throws IOException {
    Files.write(
        this.firmwareDirectory.resolve(
            String.format("%s.%s", firmwareIdentification, this.firmwareImageIdExtension)),
        imageIdentifier);
  }

  public String calculateHash(final String hashType, final byte[] firmwareFile)
      throws TechnicalException {
    try {
      final MessageDigest messageDigest = MessageDigest.getInstance(hashType);
      final byte[] digest = messageDigest.digest(firmwareFile);
      return new BigInteger(1, digest).toString(16);
    } catch (final NoSuchAlgorithmException e) {
      log.error("Error calculating digest", e);
      throw new TechnicalException("Error calculating digest", e);
    }
  }
}

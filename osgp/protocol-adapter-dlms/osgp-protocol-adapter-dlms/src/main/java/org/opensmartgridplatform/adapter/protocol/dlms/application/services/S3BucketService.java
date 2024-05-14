// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.application.services;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.opensmartgridplatform.adapter.protocol.dlms.application.config.S3BucketConfig;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.springframework.stereotype.Service;

@Service(value = "dlmsS3BucketService")
public class S3BucketService {

  final S3BucketConfig s3BucketConfig;

  public S3BucketService(final S3BucketConfig s3BucketConfig) {
    this.s3BucketConfig = s3BucketConfig;
  }

  public byte[] readFirmwareFile(final String firmwareFileIdentification)
      throws ProtocolAdapterException {
    final Path fwFile =
        Paths.get(this.s3BucketConfig.getFirmwarePath(), firmwareFileIdentification);
    try {
      return Files.readAllBytes(fwFile);
    } catch (final IOException e) {
      throw new ProtocolAdapterException(
          String.format("Error reading firmware file (%s) from s3 bucket", fwFile.toString()), e);
    }
  }

  public byte[] readImageIdentifier(final String firmwareFileIdentification)
      throws ProtocolAdapterException {
    final Path imageIdentifierFile =
        Paths.get(
            this.s3BucketConfig.getFirmwarePath(),
            firmwareFileIdentification,
            ".",
            this.s3BucketConfig.getFirmwareImageIdentifierExtension());
    try {
      return Files.readAllBytes(imageIdentifierFile);
    } catch (final IOException e) {
      throw new ProtocolAdapterException(
          String.format(
              "Error reading image identifier file (%3) from s3 bucket",
              imageIdentifierFile.toString()),
          e);
    }
  }
}

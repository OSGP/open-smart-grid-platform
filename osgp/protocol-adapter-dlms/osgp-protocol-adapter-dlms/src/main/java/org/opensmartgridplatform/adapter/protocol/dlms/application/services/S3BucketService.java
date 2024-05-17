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
    return getBytesFromFile(fwFile, "firmware");
  }

  public byte[] readImageIdentifier(final String firmwareFileIdentification)
      throws ProtocolAdapterException {
    final Path imageIdentifierFile =
        Paths.get(
            this.s3BucketConfig.getFirmwarePath(),
            String.format(
                "%s.%s",
                firmwareFileIdentification,
                this.s3BucketConfig.getFirmwareImageIdentifierExtension()));
    return getBytesFromFile(imageIdentifierFile, "image identifier");
  }

  private static byte[] getBytesFromFile(final Path path, final String filetype)
      throws ProtocolAdapterException {
    try {
      return Files.readAllBytes(path);
    } catch (final IOException e) {
      throw new ProtocolAdapterException(
          String.format("Error reading %s file (%s) from s3 bucket", filetype, path.toString()), e);
    }
  }
}

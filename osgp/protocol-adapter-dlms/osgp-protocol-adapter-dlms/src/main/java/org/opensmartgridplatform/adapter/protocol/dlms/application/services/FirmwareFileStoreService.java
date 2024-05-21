// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.application.services;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.opensmartgridplatform.adapter.protocol.dlms.application.config.FirmwareFileStoreConfig;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.springframework.stereotype.Service;

@Service(value = "dlmsFirmwareFileStoreService")
public class FirmwareFileStoreService {

  final FirmwareFileStoreConfig firmwareFileStoreConfig;

  public FirmwareFileStoreService(final FirmwareFileStoreConfig firmwareFileStoreConfig) {
    this.firmwareFileStoreConfig = firmwareFileStoreConfig;
  }

  public byte[] readFirmwareFile(final String firmwareFileIdentification)
      throws ProtocolAdapterException {
    final Path fwFile =
        Paths.get(this.firmwareFileStoreConfig.getFirmwarePath(), firmwareFileIdentification);
    return getBytesFromFile(fwFile, "firmware");
  }

  public byte[] readImageIdentifier(final String firmwareFileIdentification)
      throws ProtocolAdapterException {
    final Path imageIdentifierFile =
        Paths.get(
            this.firmwareFileStoreConfig.getFirmwarePath(),
            String.format(
                "%s.%s",
                firmwareFileIdentification,
                this.firmwareFileStoreConfig.getFirmwareImageIdentifierExtension()));
    return getBytesFromFile(imageIdentifierFile, "image identifier");
  }

  private static byte[] getBytesFromFile(final Path path, final String filetype)
      throws ProtocolAdapterException {
    try {
      return Files.readAllBytes(path);
    } catch (final IOException e) {
      throw new ProtocolAdapterException(
          String.format(
              "Error reading %s file (%s) from firmware file store", filetype, path.toString()),
          e);
    }
  }
}

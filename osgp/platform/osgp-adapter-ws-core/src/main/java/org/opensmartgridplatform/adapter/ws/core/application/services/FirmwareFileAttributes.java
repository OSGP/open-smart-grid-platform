// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.ws.core.application.services;

import lombok.Getter;

@Getter
public class FirmwareFileAttributes {
  private final String identification;
  private final String description;
  private final String fileName;
  private final boolean pushToNewDevices;
  private final boolean active;
  private final byte[] imageIdentifier;
  private final String hash;
  private final String hashType;

  public FirmwareFileAttributes(
      final String identification,
      final String description,
      final String fileName,
      final boolean pushToNewDevices,
      final boolean active,
      final byte[] imageIdentifier,
      final String hash,
      final String hashType) {
    this.identification = identification;
    this.description = description;
    this.fileName = fileName;
    this.pushToNewDevices = pushToNewDevices;
    this.active = active;
    this.imageIdentifier = imageIdentifier;
    this.hash = hash;
    this.hashType = hashType;
  }
}

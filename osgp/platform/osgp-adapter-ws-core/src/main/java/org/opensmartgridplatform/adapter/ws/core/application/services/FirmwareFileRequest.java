// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.ws.core.application.services;

import lombok.Getter;

@Getter
public class FirmwareFileRequest {
  private final String identification;
  private final String description;
  private final String fileName;
  private final boolean pushToNewDevices;
  private final boolean active;
  private final byte[] imageIdentifier;

  public FirmwareFileRequest(
      final String identification,
      final String description,
      final String fileName,
      final boolean pushToNewDevices,
      final boolean active,
      final byte[] imageIdentifier) {
    this.identification = identification;
    this.description = description;
    this.fileName = fileName;
    this.pushToNewDevices = pushToNewDevices;
    this.active = active;
    this.imageIdentifier = imageIdentifier;
  }
}

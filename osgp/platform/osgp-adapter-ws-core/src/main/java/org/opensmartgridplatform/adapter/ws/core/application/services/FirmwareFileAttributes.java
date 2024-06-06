// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.ws.core.application.services;

import lombok.Builder;
import lombok.Getter;

@Builder
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
}

// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.throttling.model;

import java.util.UUID;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@EqualsAndHashCode
@Getter
public class PermitRequest {
  final int clientId;
  final int requestId;
  final UUID uuid;

  public PermitRequest(final int clientId, final int requestId) {
    this.clientId = clientId;
    this.requestId = requestId;
    this.uuid = UUID.randomUUID();
  }
}

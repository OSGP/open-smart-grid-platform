// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.shared.domain.services;

import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class CorrelationIdProviderUUIDService implements CorrelationIdProviderService {

  private static final String SEPARATOR = "|||";

  @Override
  public String getCorrelationId(
      final String organisationIdentification, final String deviceIdentification) {
    return organisationIdentification
        + SEPARATOR
        + deviceIdentification
        + SEPARATOR
        + UUID.randomUUID();
  }
}

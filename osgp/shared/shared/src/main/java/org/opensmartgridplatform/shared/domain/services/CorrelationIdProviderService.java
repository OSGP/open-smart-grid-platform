//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.shared.domain.services;

import org.springframework.stereotype.Service;

@Service
@FunctionalInterface
public interface CorrelationIdProviderService {
  String getCorrelationId(
      final String organisationIdentification, final String deviceIdentification);
}

// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.throttling.services;

import org.opensmartgridplatform.throttling.model.ThrottlingSettings;

public interface RateLimitService {

  boolean isNewConnectionRequestAllowed(
      final int baseTransceiverStationId,
      final int cellId,
      final ThrottlingSettings throttlingSettings);
}

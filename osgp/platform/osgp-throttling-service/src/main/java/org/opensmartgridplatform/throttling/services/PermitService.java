// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.throttling.services;

import java.util.Optional;
import org.opensmartgridplatform.throttling.model.NetworkSegment;
import org.opensmartgridplatform.throttling.model.Permit;
import org.opensmartgridplatform.throttling.model.PermitRequest;

public interface PermitService {

  boolean createPermit(
      NetworkSegment networkSegment,
      PermitRequest permitRequest,
      int maxConcurrentRequests,
      boolean highPrioRequest);

  boolean removePermit(NetworkSegment networkSegment, PermitRequest permitRequest);

  Optional<Permit> findByClientIdAndRequestId(int clientId, int requestId);

  long countByClientId(int clientId);
}

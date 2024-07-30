// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.throttling.services;

import java.util.Optional;
import org.opensmartgridplatform.throttling.model.NetworkSegment;
import org.opensmartgridplatform.throttling.model.Permit;

public interface PermitService {

  boolean createPermit(
      final NetworkSegment networkSegment,
      final int clientId,
      final int requestId,
      final int maxConcurrentRequests);

  boolean createPermitWithHighPriority(
      final NetworkSegment networkSegment,
      final int clientId,
      final int requestId,
      final int maxConcurrentRequests);

  boolean removePermit(
      final NetworkSegment networkSegment, final int clientId, final int requestId);

  Optional<Permit> findByClientIdAndRequestId(int clientId, int requestId);

  long countByClientId(int clientId);
}

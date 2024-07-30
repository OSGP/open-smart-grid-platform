// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.throttling;

import lombok.extern.slf4j.Slf4j;
import org.opensmartgridplatform.shared.wsheaderattribute.priority.MessagePriorityEnum;
import org.opensmartgridplatform.throttling.model.NetworkSegment;
import org.opensmartgridplatform.throttling.model.ThrottlingSettings;
import org.opensmartgridplatform.throttling.services.PermitService;
import org.opensmartgridplatform.throttling.services.RateLimitService;

@Slf4j
public class PermitsPerNetworkSegment {
  private final PermitService permitService;
  private final RateLimitService rateLimitService;
  private final boolean highPrioPoolEnabled;
  private final int maxWaitForHighPrioInMs;

  public PermitsPerNetworkSegment(
      final PermitService permitService,
      final RateLimitService rateLimitService,
      final boolean highPrioPoolEnabled,
      final int maxWaitForHighPrioInMs) {
    this.permitService = permitService;
    this.rateLimitService = rateLimitService;
    this.highPrioPoolEnabled = highPrioPoolEnabled;
    this.maxWaitForHighPrioInMs = maxWaitForHighPrioInMs;
  }

  public boolean requestPermit(
      final NetworkSegment networkSegment,
      final int clientId,
      final int requestId,
      final int priority,
      final ThrottlingSettings throttlingSettings) {

    final boolean newConnectionRequestAllowed =
        this.rateLimitService.isNewConnectionRequestAllowed(
            networkSegment.baseTransceiverStationId(), networkSegment.cellId(), throttlingSettings);

    if (newConnectionRequestAllowed) {
      return this.tryAcquiringPermit(
          networkSegment, clientId, requestId, priority, throttlingSettings.getMaxConcurrency());
    }

    return false;
  }

  public boolean releasePermit(
      final NetworkSegment networkSegment, final int clientId, final int requestId) {
    return this.permitService.removePermit(networkSegment, clientId, requestId);
  }

  private boolean tryAcquiringPermit(
      final NetworkSegment networkSegment,
      final int clientId,
      final int requestId,
      final int priority,
      final int maxConcurrency) {

    if (maxConcurrency == 0) {
      return false;
    }

    final boolean granted =
        this.permitService.createPermit(networkSegment, clientId, requestId, maxConcurrency);

    if (granted) {
      return true;

    } else {

      if (this.highPrioPoolEnabled && priority > MessagePriorityEnum.DEFAULT.getPriority()) {
        log.info("***** High priority request detected. Waiting for permit release.");

        return this.waitUntilPermitIsAvailable(
            networkSegment, clientId, requestId, maxConcurrency, this.maxWaitForHighPrioInMs);
      }
    }

    return false;
  }

  private boolean waitUntilPermitIsAvailable(
      final NetworkSegment networkSegment,
      final int clientId,
      final int requestId,
      final int maxConcurrency,
      final int maxWaitForHighPrioInMs) {

    final long startTime = System.currentTimeMillis();

    while (System.currentTimeMillis() - startTime < maxWaitForHighPrioInMs) {

      final boolean granted =
          this.permitService.createPermitWithHighPriority(
              networkSegment, clientId, requestId, maxConcurrency);

      if (!granted) {
        log.info("***** Permit not available yet. Try again ...");
        continue;
      }

      return true;
    }

    return false;
  }
}

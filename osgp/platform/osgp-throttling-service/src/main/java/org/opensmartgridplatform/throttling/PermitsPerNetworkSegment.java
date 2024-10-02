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

    log.debug(
        "Request [{}] for permit is {} by rate-limiter",
        requestId,
        newConnectionRequestAllowed ? "allowed" : "NOT allowed");

    if (newConnectionRequestAllowed) {
      log.debug("Request [{}] for permit is allowed by rate-limiter", requestId);
      return this.tryAcquiringPermit(
          networkSegment, clientId, requestId, priority, throttlingSettings.getMaxConcurrency());
    }

    log.debug("Request [{}] for permit is NOT allowed by rate-limiter", requestId);
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
      log.warn("ThrottlingSettings.getMaxConcurrency is set to 0.");
      return false;
    }

    final boolean granted =
        this.permitService.createPermit(networkSegment, clientId, requestId, maxConcurrency);

    if (granted) {
      log.debug("Request [{}] is granted a permit.", requestId);
      return true;

    } else {
      log.debug("Request [{}], is NOT granted a permit.", requestId);

      if (this.highPrioPoolEnabled && priority > MessagePriorityEnum.DEFAULT.getPriority()) {
        log.debug(
            "Request [{}] is a high priority request and high priority pool is enabled -> we will wait for a permit release...",
            requestId);

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

    log.debug("High priority request [{}] is waiting until permit is available.", requestId);

    while (System.currentTimeMillis() - startTime < maxWaitForHighPrioInMs) {

      final boolean granted =
          this.permitService.createPermitWithHighPriority(
              networkSegment, clientId, requestId, maxConcurrency);

      if (!granted) {
        continue;
      }

      log.debug("High priority request [{}] is granted a permit.", requestId);
      return true;
    }

    return false;
  }
}

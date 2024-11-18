// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.throttling;

import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.opensmartgridplatform.shared.wsheaderattribute.priority.MessagePriorityEnum;
import org.opensmartgridplatform.throttling.model.NetworkSegment;
import org.opensmartgridplatform.throttling.model.PermitRequest;
import org.opensmartgridplatform.throttling.model.ThrottlingSettings;
import org.opensmartgridplatform.throttling.services.PermitService;
import org.opensmartgridplatform.throttling.services.RateLimitService;

@Slf4j
public class PermitsPerNetworkSegment {

  private final PermitService permitService;
  private final RateLimitService rateLimitService;
  private final boolean highPrioPoolEnabled;
  private final int maxWaitForHighPrioInMs;
  private final int pauseWaitForHighPrioInMs;

  public PermitsPerNetworkSegment(
      final PermitService permitService,
      final RateLimitService rateLimitService,
      final boolean highPrioPoolEnabled,
      final int maxWaitForHighPrioInMs,
      final int pauseWaitForHighPrioInMs) {
    this.permitService = permitService;
    this.rateLimitService = rateLimitService;
    this.highPrioPoolEnabled = highPrioPoolEnabled;
    this.maxWaitForHighPrioInMs = maxWaitForHighPrioInMs;
    this.pauseWaitForHighPrioInMs = pauseWaitForHighPrioInMs;
  }

  public boolean requestPermit(
      final NetworkSegment networkSegment,
      final PermitRequest permitRequest,
      final int priority,
      final ThrottlingSettings throttlingSettings) {

    final boolean newConnectionRequestAllowed =
        this.rateLimitService.isNewConnectionRequestAllowed(
            networkSegment.baseTransceiverStationId(), networkSegment.cellId(), throttlingSettings);

    if (newConnectionRequestAllowed) {
      log.debug("Request [{}] for permit is allowed by rate-limiter", permitRequest.getRequestId());
      return this.tryAcquiringPermit(
          networkSegment, permitRequest, priority, throttlingSettings.getMaxConcurrency());
    }

    log.debug(
        "Request [{}] for permit is NOT allowed by rate-limiter", permitRequest.getRequestId());
    return false;
  }

  public boolean releasePermit(
      final NetworkSegment networkSegment, final PermitRequest permitRequest) {
    return this.permitService.removePermit(networkSegment, permitRequest);
  }

  private boolean tryAcquiringPermit(
      final NetworkSegment networkSegment,
      final PermitRequest permitRequest,
      final int priority,
      final int maxConcurrency) {

    if (maxConcurrency == 0) {
      log.warn("ThrottlingSettings.getMaxConcurrency is set to 0.");
      return false;
    }

    final boolean highPrio =
        this.highPrioPoolEnabled && priority > MessagePriorityEnum.DEFAULT.getPriority();

    final boolean granted =
        this.permitService.createPermit(networkSegment, permitRequest, maxConcurrency, highPrio);

    if (granted) {
      log.debug("Request [{}] is granted a permit.", permitRequest.getRequestId());
      return true;

    } else {
      log.debug("Request [{}], is NOT granted a permit.", permitRequest.getRequestId());

      if (highPrio) {
        log.debug(
            "Request [{}] is a high priority request and high priority pool is enabled -> we will wait for a permit release...",
            permitRequest.getRequestId());

        return this.waitUntilPermitIsAvailable(
            networkSegment, permitRequest, maxConcurrency, this.maxWaitForHighPrioInMs);
      }
    }

    return false;
  }

  private boolean waitUntilPermitIsAvailable(
      final NetworkSegment networkSegment,
      final PermitRequest permitRequest,
      final int maxConcurrency,
      final int maxWaitForHighPrioInMs) {

    final long startTime = System.currentTimeMillis();

    log.debug(
        "High priority request [{}] is waiting until permit is available.",
        permitRequest.getRequestId());

    while (System.currentTimeMillis() - startTime < maxWaitForHighPrioInMs) {

      final boolean granted =
          this.permitService.createPermit(networkSegment, permitRequest, maxConcurrency, true);

      if (!granted) {

        if (this.pauseWaitForHighPrioInMs > -1) {
          try {
            TimeUnit.MILLISECONDS.sleep(this.pauseWaitForHighPrioInMs);
          } catch (final InterruptedException e) {
            Thread.currentThread().interrupt();
          }
        }

        continue;
      }

      log.debug("High priority request [{}] is granted a permit.", permitRequest.getRequestId());
      return true;
    }

    return false;
  }
}

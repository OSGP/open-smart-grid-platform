// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.throttling;

import jakarta.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.opensmartgridplatform.throttling.entities.ThrottlingConfig;
import org.opensmartgridplatform.throttling.model.NetworkSegment;
import org.opensmartgridplatform.throttling.model.ThrottlingSettings;
import org.opensmartgridplatform.throttling.repositories.ThrottlingConfigRepository;
import org.opensmartgridplatform.throttling.services.PermitService;
import org.opensmartgridplatform.throttling.services.RateLimitService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

@Component
public class PermitsByThrottlingConfig {
  private static final Logger LOGGER = LoggerFactory.getLogger(PermitsByThrottlingConfig.class);

  private final ConcurrentMap<Short, PermitsPerNetworkSegment> permitsPerSegmentByConfig =
      new ConcurrentHashMap<>();

  private final ThrottlingConfigRepository throttlingConfigRepository;
  private final PermitService permitRepository;
  private final RateLimitService rateLimitService;
  private final boolean highPrioPoolEnabled;
  private final int maxWaitForHighPrioInMs;

  public PermitsByThrottlingConfig(
      final ThrottlingConfigRepository throttlingConfigRepository,
      final PermitService permitRepository,
      final RateLimitService rateLimitService,
      @Value("${wait.for.high.prio.enabled:true}") final boolean highPrioPoolEnabled,
      @Value("${wait.for.high.prio.max.in.ms:10000}") final int maxWaitForHighPrioInMs) {

    this.throttlingConfigRepository = throttlingConfigRepository;
    this.permitRepository = permitRepository;
    this.rateLimitService = rateLimitService;
    this.highPrioPoolEnabled = highPrioPoolEnabled;
    this.maxWaitForHighPrioInMs = maxWaitForHighPrioInMs;
  }

  /** Clears all cached permit counts and initializes the cached information from the database. */
  @PostConstruct
  public void initialize() {
    final StopWatch stopWatch = new StopWatch(this.getClass().getSimpleName());
    stopWatch.start();

    final List<Short> throttlingConfigIdsInDb =
        this.throttlingConfigRepository.findAll().stream().map(ThrottlingConfig::getId).toList();

    /* Create new config */
    throttlingConfigIdsInDb.forEach(
        throttlingConfigId ->
            this.permitsPerSegmentByConfig.putIfAbsent(
                throttlingConfigId,
                new PermitsPerNetworkSegment(
                    this.permitRepository,
                    this.rateLimitService,
                    this.highPrioPoolEnabled,
                    this.maxWaitForHighPrioInMs)));

    /* Remove config not in database */
    final List<Short> throttlingConfigIdsToBeRemoved =
        this.permitsPerSegmentByConfig.keySet().stream()
            .filter(configId -> !throttlingConfigIdsInDb.contains(configId))
            .toList();
    throttlingConfigIdsToBeRemoved.forEach(this.permitsPerSegmentByConfig::remove);

    stopWatch.stop();
    LOGGER.info("Initialize of all configs took {}ms", stopWatch.getLastTaskTimeMillis());
  }

  public Map<Short, PermitsPerNetworkSegment> permitsPerNetworkSegmentByConfig() {
    return new TreeMap<>(this.permitsPerSegmentByConfig);
  }

  public boolean requestPermit(
      final NetworkSegment networkSegment,
      final int clientId,
      final int requestId,
      final int priority,
      final ThrottlingSettings throttlingSettings) {

    final PermitsPerNetworkSegment permitsPerNetworkSegment =
        this.permitsPerSegmentByConfig.computeIfAbsent(
            networkSegment.throttlingConfigId(), this::createAndInitialize);

    return permitsPerNetworkSegment.requestPermit(
        networkSegment, clientId, requestId, priority, throttlingSettings);
  }

  private PermitsPerNetworkSegment createAndInitialize(final short throttlingConfigId) {
    return new PermitsPerNetworkSegment(
        this.permitRepository,
        this.rateLimitService,
        this.highPrioPoolEnabled,
        this.maxWaitForHighPrioInMs);
  }

  public void newThrottlingConfigCreated(final short throttlingConfigId) {
    /*
     * No need to initialize the new PermitsPerNetworkSegment with existing permits from the
     * database as this method should be called before any permits are granted for the new
     * throttling configuration.
     */
    this.permitsPerSegmentByConfig.putIfAbsent(
        throttlingConfigId,
        new PermitsPerNetworkSegment(
            this.permitRepository,
            this.rateLimitService,
            this.highPrioPoolEnabled,
            this.maxWaitForHighPrioInMs));
  }

  public boolean releasePermit(
      final NetworkSegment networkSegment, final int clientId, final int requestId) {

    final PermitsPerNetworkSegment permitsPerNetworkSegment =
        this.permitsPerSegmentByConfig.get(networkSegment.throttlingConfigId());
    return permitsPerNetworkSegment != null
        && permitsPerNetworkSegment.releasePermit(networkSegment, clientId, requestId);
  }

  public boolean discardPermit(final int clientId, final int requestId) {
    return this.permitRepository
        .findByClientIdAndRequestId(clientId, requestId)
        .map(
            permit ->
                this.releasePermit(permit.networkSegment(), permit.clientId(), permit.requestId()))
        .orElse(false);
  }
}

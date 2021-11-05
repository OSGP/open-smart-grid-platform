/*
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */

package org.opensmartgridplatform.throttling.cleanup;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import org.opensmartgridplatform.throttling.SegmentedNetworkThrottler;
import org.opensmartgridplatform.throttling.entities.Permit;
import org.opensmartgridplatform.throttling.repositories.PermitRepository;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

@Component
public class PermitCleanUpJob implements Job {

  private static final Logger LOGGER = LoggerFactory.getLogger(PermitCleanUpJob.class);

  @Autowired private PermitRepository permitRepository;
  @Autowired private SegmentedNetworkThrottler segmentedNetworkThrottler;

  @Value("#{T(java.time.Duration).parse('${cleanup.permits.time-to-live:PT1H}')}")
  private Duration timeToLive;

  @Value("${cleanup.permits.batch.size:50}")
  private int batchSize;

  @Override
  public void execute(final JobExecutionContext jobExecutionContext) {
    if (this.batchSize < 1) {
      LOGGER.warn(
          "Not cleaning any permits, as batch size is configured at a value less than one: cleanup.permits.batch.size={}",
          this.batchSize);
      return;
    }
    final Instant createdAtBefore = Instant.now().minus(this.timeToLive);
    final PageRequest pageRequest = PageRequest.of(0, this.batchSize);
    boolean done = false;
    while (!done) {
      final List<Permit> expiredPermits =
          this.permitRepository.findByCreatedAtBefore(createdAtBefore, pageRequest);
      final int numberOfExpiredPermits = expiredPermits.size();
      LOGGER.debug("Found {} permits to be cleaned", numberOfExpiredPermits);
      this.cleanUpPermits(expiredPermits);
      done = numberOfExpiredPermits < this.batchSize;
    }
  }

  private void cleanUpPermits(final List<Permit> expiredPermits) {
    /*
     * Collect a list of permits that have not been released via the SegmentedNetworkThrottler, so
     * they can be deleted from the database by a direct call to the repository.
     */
    final List<Permit> releaseWithSegmentedNetworkThrottlerFailed = new ArrayList<>();
    for (final Permit permit : expiredPermits) {
      LOGGER.warn(
          "Cleaning up permit (bts: {}/cell: {}), created on {} for client {}",
          permit.getBaseTransceiverStationId(),
          permit.getCellId(),
          permit.getCreatedAt(),
          permit.getClientId());
      if (!this.segmentedNetworkThrottler.releasePermit(
          permit.getThrottlingConfigId(),
          permit.getClientId(),
          permit.getBaseTransceiverStationId(),
          permit.getCellId(),
          permit.getRequestId())) {
        /*
         * The throttler returns true if the permit has been deleted from the database. If the
         * throttler ever returns false, it has not deleted the permit, possibly because for
         * some reason it was not present in memory. In any such case, make sure the permit that
         * exceeded its time to live is deleted, so it wont be loaded if the throttling service
         * is restarted.
         */
        LOGGER.warn("Releasing permit with SegmentedNetworkThrottler failed");
        releaseWithSegmentedNetworkThrottlerFailed.add(permit);
      }
    }
    if (!releaseWithSegmentedNetworkThrottlerFailed.isEmpty()) {
      this.permitRepository.deleteAll(releaseWithSegmentedNetworkThrottlerFailed);
    }
  }
}

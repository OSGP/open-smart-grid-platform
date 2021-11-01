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
import org.springframework.stereotype.Component;

@Component
public class PermitCleanUpJob implements Job {

  private static final Logger LOGGER = LoggerFactory.getLogger(PermitCleanUpJob.class);

  @Autowired private PermitRepository permitRepository;
  @Autowired private SegmentedNetworkThrottler segmentedNetworkThrottler;

  @Value("#{T(java.time.Duration).parse('${cleanup.permits.time-to-live:PT1H}')}")
  private Duration timeToLive;

  @Override
  public void execute(final JobExecutionContext jobExecutionContext) {
    final List<Permit> expiredPermits =
        this.permitRepository.findByCreatedAtBefore(Instant.now().minus(this.timeToLive));
    LOGGER.debug("Found {} permits to be cleaned", expiredPermits.size());
    expiredPermits.forEach(
        permit -> {
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
            this.permitRepository.delete(permit);
          }
        });
  }
}

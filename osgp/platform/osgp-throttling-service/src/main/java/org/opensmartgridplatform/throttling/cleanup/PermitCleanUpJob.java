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
import org.opensmartgridplatform.throttling.entities.Client;
import org.opensmartgridplatform.throttling.entities.Permit;
import org.opensmartgridplatform.throttling.repositories.ClientRepository;
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
  @Autowired private ClientRepository clientRepository;

  @Value("${cleanup.permits.threshold-seconds:86400}")
  private int thresholdSeconds;

  @Override
  public void execute(final JobExecutionContext jobExecutionContext) {
    final List<Permit> expiredPermits =
        this.permitRepository.findByCreatedAtBefore(
            Instant.now().minus(Duration.ofSeconds(this.thresholdSeconds)));
    LOGGER.debug("Found {} permits to be cleaned", expiredPermits.size());
    expiredPermits.forEach(
        permit ->
            LOGGER.warn(
                "Cleaning up permit (bts: {}/cell: {}), created on {} for client '{}'",
                permit.getBaseTransceiverStationId(),
                permit.getCellId(),
                permit.getCreatedAt(),
                this.clientRepository
                    .findById(permit.getClientId())
                    .map(Client::getName)
                    .orElse("<unknown>")));
    this.permitRepository.deleteAll(expiredPermits);
  }
}

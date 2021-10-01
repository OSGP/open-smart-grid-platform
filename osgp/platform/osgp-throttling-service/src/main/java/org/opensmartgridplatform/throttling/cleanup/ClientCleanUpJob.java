/*
 * Copyright 2021 Alliander N.V.
 */

package org.opensmartgridplatform.throttling.cleanup;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import org.opensmartgridplatform.throttling.entities.Client;
import org.opensmartgridplatform.throttling.repositories.ClientRepository;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ClientCleanUpJob implements Job {
  private static final Logger LOGGER = LoggerFactory.getLogger(ClientCleanUpJob.class);
  @Autowired private ClientRepository clientRepository;

  @Value("${cleanup.clients.threshold-seconds:86400}")
  private int thresholdSeconds;

  @Override
  public void execute(final JobExecutionContext jobExecutionContext) {
    final List<Client> expiredClients =
        this.clientRepository.findByLastSeenAtBefore(
            Instant.now().minus(Duration.ofSeconds(this.thresholdSeconds)));
    LOGGER.debug("Found {} clients to be cleaned", expiredClients.size());
    expiredClients.forEach(
        client ->
            LOGGER.warn(
                "Cleaning up client '{}', last seen on {}",
                client.getName(),
                client.getLastSeenAt()));
    this.clientRepository.deleteAll(expiredClients);
  }
}

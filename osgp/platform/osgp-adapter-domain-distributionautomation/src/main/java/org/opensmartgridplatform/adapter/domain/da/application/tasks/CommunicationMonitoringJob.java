/*
 * Copyright 2020 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.domain.da.application.tasks;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import org.opensmartgridplatform.adapter.domain.da.application.services.CommunicationRecoveryService;
import org.opensmartgridplatform.domain.core.entities.DomainInfo;
import org.opensmartgridplatform.domain.core.entities.RtuDevice;
import org.opensmartgridplatform.domain.core.repositories.DomainInfoRepository;
import org.opensmartgridplatform.domain.core.repositories.RtuDeviceRepository;
import org.opensmartgridplatform.domain.core.valueobjects.DeviceLifecycleStatus;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@DisallowConcurrentExecution
public class CommunicationMonitoringJob implements Job {

  private static final Logger LOGGER = LoggerFactory.getLogger(CommunicationMonitoringJob.class);

  private static final String DOMAIN = "DISTRIBUTION_AUTOMATION";
  private static final String DOMAIN_VERSION = "1.0";

  @Autowired private CommunicationRecoveryService communicationRecoveryService;

  @Autowired private RtuDeviceRepository rtuDeviceRepository;

  @Autowired private DomainInfoRepository domainInfoRepository;

  @Value(
      "#{T(java.time.Duration).parse('${communication.monitoring.maximum.duration.without.communication:PT5M}')}")
  private Duration maximumDurationWithoutCommunication;

  @Override
  public void execute(final JobExecutionContext context) throws JobExecutionException {

    LOGGER.info("Executing communication monitoring job.");

    final Instant startTime = Instant.now().minus(this.maximumDurationWithoutCommunication);

    final List<RtuDevice> rtuDevices = this.getDevicesWithLastCommunicationBefore(startTime);
    LOGGER.info(
        "Found {} device(s) for which communication should be restored.", rtuDevices.size());

    for (final RtuDevice rtu : rtuDevices) {
      LOGGER.debug("Restoring communication for device {}.", rtu.getDeviceIdentification());

      this.communicationRecoveryService.signalConnectionLost(rtu);
      this.communicationRecoveryService.restoreCommunication(rtu);
    }

    LOGGER.info("Finished executing communication monitoring job.");
  }

  private List<RtuDevice> getDevicesWithLastCommunicationBefore(final Instant time) {
    final DomainInfo domainInfo =
        this.domainInfoRepository.findByDomainAndDomainVersion(DOMAIN, DOMAIN_VERSION);
    return this.rtuDeviceRepository
        .findByDeviceLifecycleStatusAndLastCommunicationTimeBeforeAndDomainInfo(
            DeviceLifecycleStatus.IN_USE, time, domainInfo);
  }
}

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

import org.opensmartgridplatform.throttling.repositories.PermitRepository;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class PermitCleanUpJob implements Job {
  private final PermitRepository permitRepository;

  @Value("${cleanup.permits.threshold-seconds:86400}")
  private int thresholdSeconds;

  public PermitCleanUpJob(final PermitRepository permitRepository) {
    this.permitRepository = permitRepository;
  }

  @Override
  public void execute(final JobExecutionContext jobExecutionContext) {}
}

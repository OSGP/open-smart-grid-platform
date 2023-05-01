/*
 * Copyright 2023 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */

package org.opensmartgridplatform.throttling.cleanup;

import org.opensmartgridplatform.throttling.PermitsByThrottlingConfig;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@DisallowConcurrentExecution
public class ReinitializeStateJob implements Job {

  private static final Logger LOGGER = LoggerFactory.getLogger(ReinitializeStateJob.class);

  @Autowired private PermitsByThrottlingConfig permitsByThrottlingConfig;

  @Override
  public void execute(final JobExecutionContext jobExecutionContext) {
    LOGGER.info("Start executing ReinitializeStateJob");
    this.permitsByThrottlingConfig.initialize();
    LOGGER.info("Finished executing ReinitializeStateJob");
  }
}

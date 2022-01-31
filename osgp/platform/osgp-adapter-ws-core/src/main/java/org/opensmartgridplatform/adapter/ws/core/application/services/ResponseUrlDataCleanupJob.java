/*
 * Copyright 2022 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.ws.core.application.services;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

@DisallowConcurrentExecution
public class ResponseUrlDataCleanupJob implements Job {

  private static final Logger LOGGER = LoggerFactory.getLogger(ResponseUrlDataCleanupJob.class);

  @Autowired private ResponseUrlDataCleanupService responseUrlDataCleanupService;

  @Override
  public void execute(final JobExecutionContext context) throws JobExecutionException {

    LOGGER.info("Quartz triggered cleanup of response url data.");
    this.responseUrlDataCleanupService.execute();
  }
}

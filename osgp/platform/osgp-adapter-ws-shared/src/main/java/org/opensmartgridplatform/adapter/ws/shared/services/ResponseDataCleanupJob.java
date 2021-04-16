/*
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.ws.shared.services;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

@DisallowConcurrentExecution
public class ResponseDataCleanupJob implements Job {

  private static final Logger LOGGER = LoggerFactory.getLogger(ResponseDataCleanupJob.class);

  @Autowired private ResponseDataCleanupService responseDataCleanupService;

  @Override
  public void execute(final JobExecutionContext context) throws JobExecutionException {

    LOGGER.info("Quartz triggered cleanup of response data.");
    this.responseDataCleanupService.execute();
  }
}

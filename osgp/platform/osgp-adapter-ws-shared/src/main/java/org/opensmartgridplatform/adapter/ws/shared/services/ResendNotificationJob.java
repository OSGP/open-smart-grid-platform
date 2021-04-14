/**
 * Copyright 2018 Smart Society Services B.V.
 *
 * <p>Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * <p>http://www.apache.org/licenses/LICENSE-2.0
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
public class ResendNotificationJob implements Job {

  private static final Logger LOGGER = LoggerFactory.getLogger(ResendNotificationJob.class);

  @Autowired private AbstractResendNotificationService<?> resendNotificationService;

  @Override
  public void execute(final JobExecutionContext context) throws JobExecutionException {

    LOGGER.info("Quartz triggered resend notification.");
    this.resendNotificationService.execute();
  }
}

// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.ws.shared.services;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@DisallowConcurrentExecution
public class ResendNotificationJob implements Job {

  private static final Logger LOGGER = LoggerFactory.getLogger(ResendNotificationJob.class);

  @SuppressWarnings("java:S6813")
  @Autowired // Constructor injection doesn't work with quartz jobs
  private AbstractResendNotificationService<?> resendNotificationService;

  @Override
  public void execute(final JobExecutionContext context) throws JobExecutionException {

    LOGGER.info("Quartz triggered resend notification.");
    this.resendNotificationService.execute();
  }
}

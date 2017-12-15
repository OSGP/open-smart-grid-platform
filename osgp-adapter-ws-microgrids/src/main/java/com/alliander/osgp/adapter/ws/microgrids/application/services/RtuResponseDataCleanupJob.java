/**
 * Copyright 2017 Smart Society Services B.V.
 */
package com.alliander.osgp.adapter.ws.microgrids.application.services;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

@DisallowConcurrentExecution
public class RtuResponseDataCleanupJob implements Job {

    private static final Logger LOGGER = LoggerFactory.getLogger(RtuResponseDataCleanupJob.class);

    @Autowired
    private RtuResponseDataCleanupService rtuResponseDataCleanupService;

    @Override
    public void execute(final JobExecutionContext context) throws JobExecutionException {

        LOGGER.info("Quartz triggered cleanup of rtu response data.");
        this.rtuResponseDataCleanupService.execute();
    }
}

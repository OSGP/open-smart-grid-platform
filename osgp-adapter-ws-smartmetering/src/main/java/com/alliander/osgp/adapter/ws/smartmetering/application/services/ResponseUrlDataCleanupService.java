/**
 * Copyright 2018 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.ws.smartmetering.application.services;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alliander.osgp.adapter.ws.smartmetering.domain.repositories.ResponseUrlDataRepository;

@Service
@Transactional(value = "transactionManager")
public class ResponseUrlDataCleanupService {

    @Autowired
    private ResponseUrlDataRepository responseUrlDataRepository;

    @Autowired
    private int cleanupJobRetentionTimeInDays;

    public void execute() {

        final DateTime removeBeforeDateTime = DateTime.now(DateTimeZone.UTC)
                .minusDays(this.cleanupJobRetentionTimeInDays);
        this.responseUrlDataRepository.removeByCreationTimeBefore(removeBeforeDateTime.toDate());
    }
}

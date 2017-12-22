/**
 * Copyright 2017 Smart Society Services B.V.
 */
package com.alliander.osgp.adapter.ws.shared.services;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alliander.osgp.adapter.ws.domain.repositories.ResponseDataRepository;

@Service
@Transactional(value = "transactionManager")
public class ResponseDataCleanupService {

    @Autowired
    private ResponseDataRepository responseDataRepository;

    @Autowired
    private int retentionTimeInDays;

    public void execute() {

        final DateTime removeBeforeDateTime = DateTime.now(DateTimeZone.UTC).minusDays(this.retentionTimeInDays);
        this.responseDataRepository.removeByCreationTimeBefore(removeBeforeDateTime.toDate());
    }
}

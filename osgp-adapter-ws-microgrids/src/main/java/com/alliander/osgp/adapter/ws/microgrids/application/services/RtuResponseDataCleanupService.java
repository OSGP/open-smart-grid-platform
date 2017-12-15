/**
 * Copyright 2017 Smart Society Services B.V.
 */
package com.alliander.osgp.adapter.ws.microgrids.application.services;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alliander.osgp.adapter.ws.microgrids.domain.repositories.RtuResponseDataRepository;

@Service
@Transactional(transactionManager = "wsTransactionManager")
public class RtuResponseDataCleanupService {

    @Autowired
    private RtuResponseDataRepository rtuResponseDataRepository;

    @Autowired
    private int retentionTimeInDays;

    public void execute() {

        final DateTime removeBeforeDateTime = DateTime.now(DateTimeZone.UTC).minusDays(this.retentionTimeInDays);
        this.rtuResponseDataRepository.removeByCreationTimeBefore(removeBeforeDateTime.toDate());
    }
}

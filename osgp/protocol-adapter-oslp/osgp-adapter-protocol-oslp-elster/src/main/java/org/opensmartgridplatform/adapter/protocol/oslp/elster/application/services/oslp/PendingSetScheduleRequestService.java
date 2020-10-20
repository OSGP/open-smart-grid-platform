/**
 * Copyright 2020 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.oslp.elster.application.services.oslp;

import java.util.Date;
import java.util.List;

import org.opensmartgridplatform.adapter.protocol.oslp.elster.domain.entities.PendingSetScheduleRequest;
import org.opensmartgridplatform.adapter.protocol.oslp.elster.domain.repositories.PendingSetScheduleRequestRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(value = "transactionManager")
public class PendingSetScheduleRequestService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PendingSetScheduleRequestService.class);

    @Autowired
    private PendingSetScheduleRequestRepository pendingSetScheduleRequestRepository;

    /**
     * Constructor
     */
    public PendingSetScheduleRequestService() {
        // Parameterless constructor required for transactions...
    }

    public PendingSetScheduleRequest add(final PendingSetScheduleRequest pendingSetScheduleRequest) {
        LOGGER.info("add PendingSetScheduleRequest for device : {}", pendingSetScheduleRequest.getDeviceIdentification());

        return this.pendingSetScheduleRequestRepository.save(pendingSetScheduleRequest);
    }

    public void remove(final PendingSetScheduleRequest pendingSetScheduleRequest) {
        LOGGER.info("remove PendingSetScheduleRequest for device : {}", pendingSetScheduleRequest.getDeviceIdentification());

        this.pendingSetScheduleRequestRepository.delete(pendingSetScheduleRequest);
    }

    public List<PendingSetScheduleRequest> getAllByDeviceIdentificationNotExpired(final String deviceUid) {
        final Date currentDate = new Date();
        LOGGER.info("get device by deviceUid {} and current time: {}", deviceUid, currentDate);

        return this.pendingSetScheduleRequestRepository.findAllByDeviceIdentificationAndExpiredAtIsAfter(deviceUid, currentDate);
    }


    public List<PendingSetScheduleRequest> getAll() {
        LOGGER.info("get all PendingSetScheduleRequests");

        return this.pendingSetScheduleRequestRepository.findAll();
    }
}

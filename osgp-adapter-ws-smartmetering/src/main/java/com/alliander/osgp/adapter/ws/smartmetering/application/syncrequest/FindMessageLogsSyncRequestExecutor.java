/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.ws.smartmetering.application.syncrequest;

import java.io.Serializable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import com.alliander.osgp.domain.core.valueobjects.DeviceFunction;
import com.alliander.osgp.logging.domain.entities.DeviceLogItem;
import com.alliander.osgp.logging.domain.repositories.DeviceLogItemRepository;
import com.alliander.osgp.shared.application.config.PagingSettings;

@Component
public class FindMessageLogsSyncRequestExecutor extends SyncRequestExecutor {

    @Autowired
    private DeviceLogItemRepository logItemRepository;

    @Autowired
    private PagingSettings pagingSettings;

    public FindMessageLogsSyncRequestExecutor() {
        super(DeviceFunction.GET_MESSAGES);
    }

    public void execute(final String organisationIdentification, final String deviceIdentification,
            final String correlationUid, final int pageNumber) {

        try {
            final PageRequest request = new PageRequest(pageNumber, this.pagingSettings.getMaximumPageSize(),
                    Sort.Direction.DESC, "modificationTime");

            Page<DeviceLogItem> pages = null;
            if (deviceIdentification != null && !deviceIdentification.isEmpty()) {
                pages = this.logItemRepository.findByDeviceIdentification(deviceIdentification, request);
            } else {
                pages = this.logItemRepository.findAll(request);
            }

            this.postExecute(organisationIdentification, deviceIdentification, correlationUid, (Serializable) pages);
        } catch (final RuntimeException e) {
            this.handleException(organisationIdentification, deviceIdentification, correlationUid, e);
        }
    }
}

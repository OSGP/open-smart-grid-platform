/**
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.domain.core.application.services;

import java.util.Date;
import java.util.List;

import org.opensmartgridplatform.adapter.domain.core.application.config.PersistenceDomainLoggingConfig;
import org.opensmartgridplatform.logging.domain.entities.DeviceLogItem;
import org.opensmartgridplatform.logging.domain.repositories.DeviceLogItemSlicingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * This service uses {@link PersistenceDomainLoggingConfig} application
 * configuration class.
 */
@Service
@Transactional(transactionManager = "domainLoggingTransactionManager")
public class TransactionalDeviceLogItemService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TransactionalDeviceLogItemService.class);

    @Autowired
    private DeviceLogItemSlicingRepository deviceLogItemSlicingRepository;

    public List<DeviceLogItem> findDeviceLogItemsBeforeDate(final Date date, final int pageSize) {
        final PageRequest pageRequest = new PageRequest(0, pageSize, Sort.Direction.DESC, "id");
        final Slice<DeviceLogItem> slice = this.deviceLogItemSlicingRepository.findByCreationTimeBefore(date,
                pageRequest);
        final List<DeviceLogItem> deviceLogItems = slice.getContent();
        LOGGER.info("Found {} device log items with date time before {}.", deviceLogItems.size(), date);

        return deviceLogItems;
    }

    public void deleteDeviceLogItems(final List<DeviceLogItem> deviceLogItems) {
        final int size = deviceLogItems.size();
        this.deviceLogItemSlicingRepository.delete(deviceLogItems);
        LOGGER.info("{} device log items deleted.", size);
    }
}

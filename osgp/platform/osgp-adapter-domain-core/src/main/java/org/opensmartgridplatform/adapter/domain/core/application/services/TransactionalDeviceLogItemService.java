// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.domain.core.application.services;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;
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

/** This service uses {@link PersistenceDomainLoggingConfig} application configuration class. */
@Service
@Transactional(transactionManager = "domainLoggingTransactionManager")
public class TransactionalDeviceLogItemService {

  private static final int PAGE_SIZE = 500;

  private static final Logger LOGGER =
      LoggerFactory.getLogger(TransactionalDeviceLogItemService.class);

  @Autowired private DeviceLogItemSlicingRepository deviceLogItemSlicingRepository;

  public List<DeviceLogItem> findDeviceLogItemsBeforeDate(final Instant date, final int pageSize) {
    final PageRequest pageRequest = PageRequest.of(0, pageSize, Sort.Direction.ASC, "id");
    final Slice<DeviceLogItem> slice =
        this.deviceLogItemSlicingRepository.findByModificationTimeBefore(date, pageRequest);
    final List<DeviceLogItem> deviceLogItems = slice.getContent();
    LOGGER.info("Found {} device log items with date time before {}.", deviceLogItems.size(), date);

    return deviceLogItems;
  }

  public void deleteDeviceLogItems(final List<DeviceLogItem> deviceLogItems) {
    final int size = deviceLogItems.size();
    final List<Long> ids =
        deviceLogItems.stream().map(DeviceLogItem::getId).collect(Collectors.toList());

    final int listSize = ids.size();

    for (int pageNumber = 0; pageNumber < (listSize + PAGE_SIZE - 1) / PAGE_SIZE; pageNumber++) {
      final int fromIndex = pageNumber * PAGE_SIZE;
      final int toIndex = Math.min(listSize, pageNumber * PAGE_SIZE + PAGE_SIZE);

      final List<Long> sublistToDelete = ids.subList(fromIndex, toIndex);
      this.deviceLogItemSlicingRepository.deleteBatchById(sublistToDelete);
    }

    LOGGER.info("{} device log items deleted.", size);
  }
}

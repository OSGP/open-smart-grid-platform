//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.adapter.ws.smartmetering.application.syncrequest;

import java.io.Serializable;
import org.opensmartgridplatform.domain.core.valueobjects.DeviceFunction;
import org.opensmartgridplatform.logging.domain.entities.DeviceLogItem;
import org.opensmartgridplatform.logging.domain.repositories.DeviceLogItemPagingRepository;
import org.opensmartgridplatform.shared.application.config.PagingSettings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

@Component
public class FindMessageLogsSyncRequestExecutor extends SyncRequestExecutor {

  @Autowired private DeviceLogItemPagingRepository logItemRepository;

  @Autowired private PagingSettings pagingSettings;

  public FindMessageLogsSyncRequestExecutor() {
    super(DeviceFunction.GET_MESSAGES);
  }

  public void execute(
      final String organisationIdentification,
      final String deviceIdentification,
      final String correlationUid,
      final int pageNumber) {

    try {
      final PageRequest request =
          PageRequest.of(
              pageNumber,
              this.pagingSettings.getMaximumPageSize(),
              Sort.Direction.DESC,
              "modificationTime");

      Page<DeviceLogItem> pages = null;
      if (deviceIdentification != null && !deviceIdentification.isEmpty()) {
        pages = this.logItemRepository.findByDeviceIdentification(deviceIdentification, request);
      } else {
        pages = this.logItemRepository.findAll(request);
      }

      this.postExecute(
          organisationIdentification, deviceIdentification, correlationUid, (Serializable) pages);
    } catch (final RuntimeException e) {
      this.handleException(organisationIdentification, deviceIdentification, correlationUid, e);
    }
  }
}

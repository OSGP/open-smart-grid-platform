//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.adapter.ws.admin.application.specifications;

import java.time.ZonedDateTime;
import org.opensmartgridplatform.logging.domain.entities.DeviceLogItem;
import org.springframework.data.jpa.domain.Specification;

public interface DeviceLogItemSpecifications {

  Specification<DeviceLogItem> hasDeviceIdentification(String deviceIdentification);

  Specification<DeviceLogItem> hasOrganisationIdentification(String organisationIdentification);

  Specification<DeviceLogItem> hasStartDate(ZonedDateTime startDate);

  Specification<DeviceLogItem> hasEndDate(ZonedDateTime endDate);
}

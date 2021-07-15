/*
 * Copyright 2020 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
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

/*
 * Copyright 2018 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.dto.valueobjects.smartmetering;

public enum DeviceLifecycleStatusDto {
  NEW_IN_INVENTORY,
  READY_FOR_USE,
  REGISTERED,
  REGISTERED_BUILD_IN_FAILED,
  REGISTERED_INSTALL_FAILED,
  REGISTERED_UPDATE_FAILED,
  IN_USE,
  RETURNED_TO_INVENTORY,
  UNDER_TEST,
  DESTROYED
}

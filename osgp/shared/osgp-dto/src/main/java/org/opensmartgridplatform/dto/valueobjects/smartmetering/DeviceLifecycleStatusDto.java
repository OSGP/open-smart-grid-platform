// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

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

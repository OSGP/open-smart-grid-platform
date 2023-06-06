// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.dto.valueobjects.smartmetering;

public enum AttributeAccessModeTypeDto {
  NO_ACCESS,
  READ_ONLY,
  WRITE_ONLY,
  READ_AND_WRITE,
  AUTHENTICATED_READ_ONLY,
  AUTHENTICATED_WRITE_ONLY,
  AUTHENTICATED_READ_AND_WRITE;
}

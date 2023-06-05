// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.dto.valueobjects.smartmetering;

import java.io.Serializable;

public class AssociationLnListElementDto implements Serializable {

  private static final long serialVersionUID = 2432320129309477392L;

  private final long classId;

  private final int version;

  private final CosemObisCodeDto logicalName;

  private final AccessRightDto accessRights;

  public AssociationLnListElementDto(
      final long classId,
      final int version,
      final CosemObisCodeDto logicalName,
      final AccessRightDto accessRights) {
    this.classId = classId;
    this.version = version;
    this.logicalName = logicalName;
    this.accessRights = accessRights;
  }

  public long getClassId() {
    return this.classId;
  }

  public int getVersion() {
    return this.version;
  }

  public CosemObisCodeDto getLogicalName() {
    return this.logicalName;
  }

  public AccessRightDto getAccessRights() {
    return this.accessRights;
  }
}

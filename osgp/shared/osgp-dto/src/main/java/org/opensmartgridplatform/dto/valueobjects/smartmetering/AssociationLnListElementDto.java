/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * <p>Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * <p>http://www.apache.org/licenses/LICENSE-2.0
 */
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

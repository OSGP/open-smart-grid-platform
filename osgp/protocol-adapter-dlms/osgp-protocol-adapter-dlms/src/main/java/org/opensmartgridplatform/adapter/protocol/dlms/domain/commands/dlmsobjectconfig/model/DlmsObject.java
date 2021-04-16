/*
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.model;

import org.openmuc.jdlms.ObisCode;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.DlmsObjectType;

public class DlmsObject {
  private static final int DEFAULT_ATTRIBUTE_ID = 2;

  private final DlmsObjectType type;
  private final int classId;
  private final String obisCode;

  public DlmsObject(final DlmsObjectType type, final int classId, final String obisCode) {
    this.type = type;
    this.classId = classId;
    this.obisCode = obisCode;
  }

  public DlmsObjectType getType() {
    return this.type;
  }

  public int getClassId() {
    return this.classId;
  }

  public String getObisCodeAsString() {
    return this.obisCode;
  }

  public ObisCode getObisCode() {
    return new ObisCode(this.obisCode);
  }

  public int getDefaultAttributeId() {
    return DEFAULT_ATTRIBUTE_ID;
  }

  /** @param medium Specifies the medium to match on in overriding classes */
  public boolean mediumMatches(final Medium medium) {
    return true;
  }
}

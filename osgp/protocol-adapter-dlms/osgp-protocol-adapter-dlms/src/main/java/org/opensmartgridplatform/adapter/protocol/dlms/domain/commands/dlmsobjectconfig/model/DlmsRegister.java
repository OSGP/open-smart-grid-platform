/*
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.model;

import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.DlmsObjectType;

public class DlmsRegister extends DlmsObject {

  private static final int CLASS_ID_REGISTER = 3;
  private static final int SCALER_UNIT_ATTRIBUTE_ID = 3;

  private final int scaler;
  private final RegisterUnit unit;
  private final Medium medium;

  public DlmsRegister(
      final DlmsObjectType type,
      final String obisCode,
      final int scaler,
      final RegisterUnit unit,
      final Medium medium) {
    super(type, CLASS_ID_REGISTER, obisCode);
    this.scaler = scaler;
    this.unit = unit;
    this.medium = medium;
  }

  DlmsRegister(
      final DlmsObjectType type,
      final int classId,
      final String obisCode,
      final int scaler,
      final RegisterUnit unit,
      final Medium medium) {
    super(type, classId, obisCode);
    this.scaler = scaler;
    this.unit = unit;
    this.medium = medium;
  }

  public int getScaler() {
    return this.scaler;
  }

  public RegisterUnit getUnit() {
    return this.unit;
  }

  public Medium getMedium() {
    return this.medium;
  }

  public int getScalerUnitAttributeId() {
    return SCALER_UNIT_ATTRIBUTE_ID;
  }

  @Override
  public boolean mediumMatches(final Medium medium) {
    return medium == null || this.medium == medium;
  }
}

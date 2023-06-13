// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

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

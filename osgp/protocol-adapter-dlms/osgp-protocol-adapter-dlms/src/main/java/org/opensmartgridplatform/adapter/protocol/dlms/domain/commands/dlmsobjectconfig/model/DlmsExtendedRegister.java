//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.model;

import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.DlmsObjectType;

public class DlmsExtendedRegister extends DlmsRegister {
  private static final int CLASS_ID_EXTENDED_REGISTER = 4;

  public DlmsExtendedRegister(
      final DlmsObjectType type,
      final String obisCode,
      final int scaler,
      final RegisterUnit unit,
      final Medium medium) {
    super(type, CLASS_ID_EXTENDED_REGISTER, obisCode, scaler, unit, medium);
  }
}

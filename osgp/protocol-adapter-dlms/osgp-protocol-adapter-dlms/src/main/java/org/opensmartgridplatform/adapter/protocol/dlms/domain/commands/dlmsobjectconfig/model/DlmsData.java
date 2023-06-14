// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.model;

import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.DlmsObjectType;

public class DlmsData extends DlmsObject {
  private static final int CLASS_ID_DATA = 1;

  public DlmsData(final DlmsObjectType type, final String obisCode) {
    super(type, CLASS_ID_DATA, obisCode);
  }
}

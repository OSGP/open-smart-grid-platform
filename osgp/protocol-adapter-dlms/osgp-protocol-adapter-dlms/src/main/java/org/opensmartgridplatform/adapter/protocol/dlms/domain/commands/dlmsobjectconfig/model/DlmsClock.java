// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.model;

import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.DlmsObjectType;

public class DlmsClock extends DlmsObject {
  private static final int CLASS_ID_CLOCK = 8;

  public DlmsClock(final String obisCode) {
    super(DlmsObjectType.CLOCK, CLASS_ID_CLOCK, obisCode);
  }
}

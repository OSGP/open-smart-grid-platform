// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.model;

import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.DlmsObjectType;

public class DlmsAutoAnswer extends DlmsObject {
  private static final int CLASS_ID_AUTO_ANSWER = 28;

  public DlmsAutoAnswer(final DlmsObjectType type, final String obisCode) {
    super(type, CLASS_ID_AUTO_ANSWER, obisCode);
  }
}

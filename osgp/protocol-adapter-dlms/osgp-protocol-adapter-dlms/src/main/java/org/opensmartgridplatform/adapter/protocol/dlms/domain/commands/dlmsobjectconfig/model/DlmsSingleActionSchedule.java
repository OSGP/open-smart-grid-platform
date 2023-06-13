// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.model;

import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.DlmsObjectType;

public class DlmsSingleActionSchedule extends DlmsObject {
  private static final int CLASS_ID_SINGLE_ACTION_SCHEDULE = 22;

  public DlmsSingleActionSchedule(final DlmsObjectType type, final String obisCode) {
    super(type, CLASS_ID_SINGLE_ACTION_SCHEDULE, obisCode);
  }
}

// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.model;

import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.DlmsObjectType;

public class DlmsPushSetup extends DlmsObject {
  private static final int CLASS_ID_PUSH_SETUP = 40;

  public DlmsPushSetup(final DlmsObjectType type, final String obisCode) {
    super(type, CLASS_ID_PUSH_SETUP, obisCode);
  }
}

// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.model;

import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.DlmsClassVersion;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.DlmsObjectType;
import org.opensmartgridplatform.dlms.interfaceclass.InterfaceClass;

public class DlmsMbusClientSetup extends DlmsObject {

  public DlmsMbusClientSetup(
      final DlmsObjectType type, final String obisCode, final DlmsClassVersion version) {
    super(type, InterfaceClass.MBUS_CLIENT.id(), obisCode, version);
  }
}

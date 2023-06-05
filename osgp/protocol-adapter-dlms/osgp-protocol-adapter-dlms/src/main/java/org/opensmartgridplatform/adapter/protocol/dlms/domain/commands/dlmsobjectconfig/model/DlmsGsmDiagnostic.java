// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.model;

import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.DlmsObjectType;
import org.opensmartgridplatform.dlms.interfaceclass.InterfaceClass;

public class DlmsGsmDiagnostic extends DlmsObject
    implements DlmsObjectDependingOnCommunicationMethod {

  private final CommunicationMethod communicationMethod;

  public DlmsGsmDiagnostic(
      final DlmsObjectType type,
      final String obisCode,
      final CommunicationMethod communicationMethod) {
    super(type, InterfaceClass.GSM_DIAGNOSTIC.id(), obisCode);

    this.communicationMethod = communicationMethod;
  }

  @Override
  public CommunicationMethod getCommunicationMethod() {
    return this.communicationMethod;
  }
}

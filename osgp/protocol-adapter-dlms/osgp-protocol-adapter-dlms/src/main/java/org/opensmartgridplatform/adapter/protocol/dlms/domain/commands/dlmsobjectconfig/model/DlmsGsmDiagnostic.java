/**
 * Copyright 2020 Alliander N.V.
 *
 * <p>Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * <p>http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.model;

import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.DlmsObjectType;
import org.opensmartgridplatform.dlms.interfaceclass.InterfaceClass;

public class DlmsGsmDiagnostic extends DlmsObject {

  private final CommunicationMethod communicationMethod;

  public DlmsGsmDiagnostic(
      final DlmsObjectType type,
      final String obisCode,
      final CommunicationMethod communicationMethod) {
    super(type, InterfaceClass.GSM_DIAGNOSTIC.id(), obisCode);

    this.communicationMethod = communicationMethod;
  }

  public CommunicationMethod getCommunicationMethod() {
    return this.communicationMethod;
  }
}

/*
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig;

import static org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.DlmsObjectType.ALARM_REGISTER_2;

import java.util.Collections;
import java.util.List;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.model.DlmsData;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.model.DlmsObject;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.Protocol;

public class DlmsObjectConfigSmr52 extends DlmsObjectConfigSmr51 {

  @Override
  List<Protocol> initProtocols() {
    return Collections.singletonList(Protocol.SMR_5_2);
  }

  @Override
  List<DlmsObject> initObjects() {
    final List<DlmsObject> objectList = super.initObjects();

    // Abstract objects
    final DlmsObject alarmRegisters2 = new DlmsData(ALARM_REGISTER_2, "0.0.97.98.1.255");

    objectList.addAll(Collections.singletonList(alarmRegisters2));

    return objectList;
  }
}

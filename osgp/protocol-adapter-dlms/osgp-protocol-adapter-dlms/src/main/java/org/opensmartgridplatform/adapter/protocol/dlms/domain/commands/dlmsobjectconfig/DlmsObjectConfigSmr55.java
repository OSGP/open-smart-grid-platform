/*
 * Copyright 2022 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig;

import static org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.DlmsObjectType.ALARM_FILTER_3;
import static org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.DlmsObjectType.ALARM_REGISTER_3;
import static org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.DlmsObjectType.LAST_GASP_TEST;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.model.DlmsData;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.model.DlmsObject;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.model.DlmsSingleActionSchedule;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.Protocol;

public class DlmsObjectConfigSmr55 extends DlmsObjectConfigSmr52 {

  @Override
  List<Protocol> initProtocols() {
    return Collections.singletonList(Protocol.SMR_5_5);
  }

  @Override
  List<DlmsObject> initObjects() {
    final List<DlmsObject> objectList = super.initObjects();

    final DlmsObject alarmFilter3 = new DlmsData(ALARM_FILTER_3, "0.0.97.98.12.255");
    final DlmsObject alarmRegister3 = new DlmsData(ALARM_REGISTER_3, "0.0.97.98.2.255");

    objectList.addAll(Arrays.asList(alarmFilter3, alarmRegister3));

    final DlmsObject lastGaspTestAlarm =
        new DlmsSingleActionSchedule(LAST_GASP_TEST, "0.0.15.2.4.255");

    objectList.add(lastGaspTestAlarm);

    return objectList;
  }
}

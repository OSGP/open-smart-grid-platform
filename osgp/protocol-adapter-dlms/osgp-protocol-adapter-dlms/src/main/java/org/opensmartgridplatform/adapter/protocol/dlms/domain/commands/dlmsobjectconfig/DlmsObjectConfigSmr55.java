// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig;

import static org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.DlmsObjectType.ALARM_FILTER_3;
import static org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.DlmsObjectType.ALARM_REGISTER_3;
import static org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.DlmsObjectType.LAST_GASP;
import static org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.DlmsObjectType.LAST_GASP_TEST;
import static org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.DlmsObjectType.PUSH_SETUP_UDP;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.model.DlmsData;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.model.DlmsObject;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.model.DlmsPushSetup;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.model.DlmsRegisterMonitor;
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

    final DlmsObject pushSetupUdp = new DlmsPushSetup(PUSH_SETUP_UDP, "0.3.25.9.0.255");
    final DlmsObject alarmFilter3 = new DlmsData(ALARM_FILTER_3, "0.0.97.98.12.255");
    final DlmsObject alarmRegister3 = new DlmsData(ALARM_REGISTER_3, "0.0.97.98.2.255");

    objectList.addAll(Arrays.asList(pushSetupUdp, alarmFilter3, alarmRegister3));

    final DlmsObject lastGaspAlarm = new DlmsRegisterMonitor(LAST_GASP, "0.0.16.1.2.255");
    final DlmsObject lastGaspTestAlarm =
        new DlmsSingleActionSchedule(LAST_GASP_TEST, "0.0.15.2.4.255");

    objectList.addAll(Arrays.asList(lastGaspAlarm, lastGaspTestAlarm));

    return objectList;
  }
}

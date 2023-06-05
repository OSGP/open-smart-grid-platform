// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig;

import static org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.DlmsObjectType.ALARM_FILTER_2;
import static org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.DlmsObjectType.ALARM_REGISTER_2;
import static org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.DlmsObjectType.POWER_QUALITY_EXTENDED_EVENT_CODE;
import static org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.DlmsObjectType.POWER_QUALITY_EXTENDED_EVENT_CODE_DURATION;
import static org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.DlmsObjectType.POWER_QUALITY_EXTENDED_EVENT_CODE_MAGNITUDE;
import static org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.DlmsObjectType.POWER_QUALITY_EXTENDED_EVENT_LOG;
import static org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.model.Medium.ABSTRACT;
import static org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.model.ProfileCaptureTime.ASYNCHRONOUSLY;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.model.DlmsData;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.model.DlmsObject;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.model.DlmsProfile;
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
    final DlmsObject alarmFilter2 = new DlmsData(ALARM_FILTER_2, "0.0.97.98.11.255");
    final DlmsObject alarmRegister2 = new DlmsData(ALARM_REGISTER_2, "0.0.97.98.1.255");

    objectList.addAll(Arrays.asList(alarmFilter2, alarmRegister2));

    // Additionally extended log events for SMR5.2, ISKRA only
    final DlmsObject extendedEventCode =
        new DlmsData(POWER_QUALITY_EXTENDED_EVENT_CODE, "0.0.96.11.7.255");
    final DlmsObject extendedEventCodeMagnitude =
        new DlmsData(POWER_QUALITY_EXTENDED_EVENT_CODE_MAGNITUDE, "0.0.96.11.20.255");
    final DlmsObject extendedEventCodeDuration =
        new DlmsData(POWER_QUALITY_EXTENDED_EVENT_CODE_DURATION, "0.0.96.11.21.255");

    final List<DlmsCaptureObject> captureObjectsPowerQualityExtendedEventLogEvents =
        Arrays.asList(
            DlmsCaptureObject.create(this.getClock(objectList)),
            DlmsCaptureObject.create(extendedEventCode),
            DlmsCaptureObject.create(extendedEventCodeMagnitude),
            DlmsCaptureObject.create(extendedEventCodeDuration));

    objectList.add(
        new DlmsProfile(
            POWER_QUALITY_EXTENDED_EVENT_LOG,
            "0.0.99.98.7.255",
            captureObjectsPowerQualityExtendedEventLogEvents,
            ASYNCHRONOUSLY,
            ABSTRACT));

    return objectList;
  }
}

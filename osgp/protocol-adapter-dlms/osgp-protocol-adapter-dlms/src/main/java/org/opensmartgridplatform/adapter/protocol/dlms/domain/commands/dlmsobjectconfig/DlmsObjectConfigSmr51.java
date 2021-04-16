/**
 * Copyright 2020 Alliander N.V.
 *
 * <p>Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * <p>http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig;

import static org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.DlmsObjectType.MODEM_INFO;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.model.CommunicationMethod;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.model.DlmsModemInfo;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.model.DlmsObject;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.Protocol;

public class DlmsObjectConfigSmr51 extends DlmsObjectConfigSmr50 {

  @Override
  List<Protocol> initProtocols() {
    return Collections.singletonList(Protocol.SMR_5_1);
  }

  @Override
  List<DlmsObject> initObjects() {
    final List<DlmsObject> objectList = super.initObjects();

    final DlmsObject modemInfoGprs =
        new DlmsModemInfo(MODEM_INFO, "0.0.25.6.0.255", CommunicationMethod.GPRS);
    final DlmsObject modemInfoCdma =
        new DlmsModemInfo(MODEM_INFO, "0.1.25.6.0.255", CommunicationMethod.CDMA);
    final DlmsObject modemInfoLteM =
        new DlmsModemInfo(MODEM_INFO, "0.2.25.6.0.255", CommunicationMethod.LTE_M);

    objectList.addAll(Arrays.asList(modemInfoGprs, modemInfoCdma, modemInfoLteM));

    return objectList;
  }
}

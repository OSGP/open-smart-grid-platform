/*
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.stub;

import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ActionRequestDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ActionResponseDto;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;

public class SetPushSetupAlarmBundleCommandExecutorStub extends AbstractCommandExecutorStub {

  @Override
  public ActionResponseDto executeBundleAction(
      final DlmsConnectionManager conn,
      final DlmsDevice device,
      final ActionRequestDto object,
      final MessageMetadata messageMetadata)
      throws ProtocolAdapterException {
    return this.doExecute(conn, device, object);
  }
}

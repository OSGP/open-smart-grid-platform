/*
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.datetime;

import java.io.IOException;
import org.openmuc.jdlms.MethodParameter;
import org.openmuc.jdlms.MethodResult;
import org.openmuc.jdlms.MethodResultCode;
import org.openmuc.jdlms.ObisCode;
import org.openmuc.jdlms.datatypes.DataObject;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.AbstractCommandExecutor;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.JdlmsObjectToStringUtil;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ConnectionException;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component()
public class SetActivityCalendarCommandActivationExecutor
    extends AbstractCommandExecutor<Void, MethodResultCode> {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(SetActivityCalendarCommandActivationExecutor.class);

  private static final int CLASS_ID = 20;
  private static final ObisCode OBIS_CODE = new ObisCode("0.0.13.0.0.255");
  private static final int METHOD_ID_ACTIVATE_PASSIVE_CALENDAR = 1;

  @Override
  public MethodResultCode execute(
      final DlmsConnectionManager conn,
      final DlmsDevice device,
      final Void v,
      final MessageMetadata messageMetadata)
      throws ProtocolAdapterException {

    LOGGER.debug("ACTIVATING PASSIVE CALENDAR");
    final MethodParameter method =
        new MethodParameter(
            CLASS_ID,
            OBIS_CODE,
            METHOD_ID_ACTIVATE_PASSIVE_CALENDAR,
            DataObject.newInteger32Data(0));

    conn.getDlmsMessageListener()
        .setDescription(
            "SetActivityCalendarActivation, call method: "
                + JdlmsObjectToStringUtil.describeMethod(method));

    final MethodResult methodResultCode;
    try {
      methodResultCode = conn.getConnection().action(method);
    } catch (final IOException e) {
      throw new ConnectionException(e);
    }
    if (!MethodResultCode.SUCCESS.equals(methodResultCode.getResultCode())) {
      throw new ProtocolAdapterException(
          "Activating the activity calendar failed. MethodResult is: "
              + methodResultCode.getResultCode()
              + " ClassId: "
              + CLASS_ID
              + " obisCode: "
              + OBIS_CODE
              + " method id: "
              + METHOD_ID_ACTIVATE_PASSIVE_CALENDAR);
    }
    return MethodResultCode.SUCCESS;
  }
}

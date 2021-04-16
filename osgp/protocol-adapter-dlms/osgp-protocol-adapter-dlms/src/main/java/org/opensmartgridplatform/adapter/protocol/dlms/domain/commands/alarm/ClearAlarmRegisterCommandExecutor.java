/*
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.alarm;

import java.io.IOException;
import org.openmuc.jdlms.AccessResultCode;
import org.openmuc.jdlms.AttributeAddress;
import org.openmuc.jdlms.ObisCode;
import org.openmuc.jdlms.SetParameter;
import org.openmuc.jdlms.datatypes.DataObject;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.AbstractCommandExecutor;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.JdlmsObjectToStringUtil;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ConnectionException;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ActionRequestDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ActionResponseDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ClearAlarmRegisterRequestDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ClearAlarmRegisterCommandExecutor
    extends AbstractCommandExecutor<ClearAlarmRegisterRequestDto, AccessResultCode> {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(ClearAlarmRegisterCommandExecutor.class);

  private static final int CLASS_ID = 1;
  private static final ObisCode OBIS_CODE = new ObisCode("0.0.97.98.0.255");
  private static final int ATTRIBUTE_ID = 2;

  private static final int ALARM_CODE = 0;

  public ClearAlarmRegisterCommandExecutor() {
    super(ClearAlarmRegisterRequestDto.class);
  }

  @Override
  public ActionResponseDto asBundleResponse(final AccessResultCode executionResult)
      throws ProtocolAdapterException {

    this.checkAccessResultCode(executionResult);

    return new ActionResponseDto("Clear alarm register was successful");
  }

  @Override
  public ClearAlarmRegisterRequestDto fromBundleRequestInput(final ActionRequestDto bundleInput)
      throws ProtocolAdapterException {

    this.checkActionRequestType(bundleInput);

    return (ClearAlarmRegisterRequestDto) bundleInput;
  }

  @Override
  public AccessResultCode execute(
      final DlmsConnectionManager conn,
      final DlmsDevice device,
      final ClearAlarmRegisterRequestDto clearAlarmRegisterRequestDto)
      throws ProtocolAdapterException {

    LOGGER.info(
        "Clear alarm register by request for class id: {}, obis code: {}, attribute id: {}",
        CLASS_ID,
        OBIS_CODE,
        ATTRIBUTE_ID);

    final SetParameter setParameter = this.getSetParameter();

    conn.getDlmsMessageListener()
        .setDescription(
            "ClearAlarmRegister, with alarm code = "
                + ALARM_CODE
                + "and set attribute: "
                + JdlmsObjectToStringUtil.describeAttributes(
                    new AttributeAddress(CLASS_ID, OBIS_CODE, ATTRIBUTE_ID)));

    final AccessResultCode resultCode;
    try {
      resultCode = conn.getConnection().set(setParameter);
    } catch (final IOException e) {
      throw new ConnectionException(e);
    }
    if (resultCode != null) {
      return resultCode;
    } else {
      throw new ProtocolAdapterException("Error occurred for clear alarm register.");
    }
  }

  private SetParameter getSetParameter() {
    final AttributeAddress alarmRegisterValue =
        new AttributeAddress(CLASS_ID, OBIS_CODE, ATTRIBUTE_ID);
    final DataObject data = DataObject.newUInteger32Data(ALARM_CODE);

    return new SetParameter(alarmRegisterValue, data);
  }
}

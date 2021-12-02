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
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.openmuc.jdlms.AccessResultCode;
import org.openmuc.jdlms.AttributeAddress;
import org.openmuc.jdlms.SetParameter;
import org.openmuc.jdlms.datatypes.DataObject;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.AbstractCommandExecutor;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.DlmsObjectConfigService;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.DlmsObjectType;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.JdlmsObjectToStringUtil;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ConnectionException;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ActionRequestDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ActionResponseDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ClearAlarmRegisterRequestDto;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ClearAlarmRegisterCommandExecutor
    extends AbstractCommandExecutor<ClearAlarmRegisterRequestDto, AccessResultCode> {

  final DlmsObjectConfigService dlmsObjectConfigService;

  private static final int ALARM_CODE = 0;

  public ClearAlarmRegisterCommandExecutor(final DlmsObjectConfigService dlmsObjectConfigService) {
    super(ClearAlarmRegisterRequestDto.class);
    this.dlmsObjectConfigService = dlmsObjectConfigService;
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
      final ClearAlarmRegisterRequestDto clearAlarmRegisterRequestDto,
      final MessageMetadata messageMetadata)
      throws ProtocolAdapterException {

    final AttributeAddress alarmRegister1AttributeAddress =
        this.dlmsObjectConfigService.getAttributeAddress(
            device, DlmsObjectType.ALARM_REGISTER_1, null);

    final AccessResultCode resultCodeAlarmRegister1 =
        this.executeForAlarmRegister(conn, alarmRegister1AttributeAddress);

    if (resultCodeAlarmRegister1 == null) {
      throw new ProtocolAdapterException("Error occurred for clear alarm register 1.");
    }
    if (resultCodeAlarmRegister1 != AccessResultCode.SUCCESS) {
      return resultCodeAlarmRegister1;
    }

    final Optional<AttributeAddress> optAlarmRegister2AttributeAddress =
        this.dlmsObjectConfigService.findAttributeAddress(
            device, DlmsObjectType.ALARM_REGISTER_2, null);

    if (!optAlarmRegister2AttributeAddress.isPresent()) {
      return resultCodeAlarmRegister1;
    } else {
      final AccessResultCode resultCodeAlarmRegister2 =
          this.executeForAlarmRegister(conn, optAlarmRegister2AttributeAddress.get());

      if (resultCodeAlarmRegister2 != null) {
        return resultCodeAlarmRegister2;
      } else {
        throw new ProtocolAdapterException("Error occurred for clear alarm register 2.");
      }
    }
  }

  public AccessResultCode executeForAlarmRegister(
      final DlmsConnectionManager conn, final AttributeAddress alarmRegisterAttributeAddress) {

    log.info(
        "Clear alarm register {}",
        JdlmsObjectToStringUtil.describeAttributes(alarmRegisterAttributeAddress));

    final DataObject data = DataObject.newUInteger32Data(ALARM_CODE);
    final SetParameter setParameter = new SetParameter(alarmRegisterAttributeAddress, data);

    conn.getDlmsMessageListener()
        .setDescription(
            "ClearAlarmRegister, with alarm code = "
                + ALARM_CODE
                + JdlmsObjectToStringUtil.describeAttributes(alarmRegisterAttributeAddress));

    try {
      return conn.getConnection().set(setParameter);
    } catch (final IOException e) {
      throw new ConnectionException(e);
    }
  }
}

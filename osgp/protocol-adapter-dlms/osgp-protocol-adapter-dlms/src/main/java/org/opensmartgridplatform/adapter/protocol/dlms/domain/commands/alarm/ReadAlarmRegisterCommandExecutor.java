/*
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.alarm;

import java.io.IOException;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import org.openmuc.jdlms.AttributeAddress;
import org.openmuc.jdlms.GetResult;
import org.openmuc.jdlms.ObisCode;
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
import org.opensmartgridplatform.dto.valueobjects.smartmetering.AlarmRegisterResponseDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.AlarmTypeDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ReadAlarmRegisterDataDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ReadAlarmRegisterRequestDto;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ReadAlarmRegisterCommandExecutor
    extends AbstractCommandExecutor<ReadAlarmRegisterRequestDto, AlarmRegisterResponseDto> {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(ReadAlarmRegisterCommandExecutor.class);

  final DlmsObjectConfigService dlmsObjectConfigService;

  private static final int CLASS_ID = 1;
  private static final ObisCode OBIS_CODE = new ObisCode("0.0.97.98.0.255");
  private static final int ATTRIBUTE_ID = 2;

  @Autowired private AlarmHelperService alarmHelperService;

  public ReadAlarmRegisterCommandExecutor(final DlmsObjectConfigService dlmsObjectConfigService) {
    super(ReadAlarmRegisterDataDto.class);
    this.dlmsObjectConfigService = dlmsObjectConfigService;
  }

  @Override
  public ReadAlarmRegisterRequestDto fromBundleRequestInput(final ActionRequestDto bundleInput)
      throws ProtocolAdapterException {

    this.checkActionRequestType(bundleInput);

    /*
     * ReadAlarmRegisterDataDto does not have a deviceIdentification. Since
     * the device identification is not used by the executor anyway, it is
     * given the value "not relevant", as long as the XSD for the WS input
     * still specifies a device identification for the non-bundled call.
     */
    return new ReadAlarmRegisterRequestDto("not relevant");
  }

  @Override
  public AlarmRegisterResponseDto execute(
      final DlmsConnectionManager conn,
      final DlmsDevice device,
      final ReadAlarmRegisterRequestDto object,
      final MessageMetadata messageMetadata)
      throws ProtocolAdapterException {

    final AttributeAddress alarmRegister1AttributeAddress =
        this.dlmsObjectConfigService.getAttributeAddress(
            device, DlmsObjectType.ALARM_REGISTER_1, null);

    final GetResult resultAlarmRegister1 =
        this.executeForAlarmRegister(conn, alarmRegister1AttributeAddress);

    if (resultAlarmRegister1 == null) {
      throw new ProtocolAdapterException(
          "No GetResult received while retrieving alarm register 1.");
    }

    final Optional<AttributeAddress> optAlarmRegister2AttributeAddress =
        this.dlmsObjectConfigService.findAttributeAddress(
            device, DlmsObjectType.ALARM_REGISTER_2, null);

    final Set<AlarmTypeDto> alarmList = new HashSet<>();
    if (!optAlarmRegister2AttributeAddress.isPresent()) {
      alarmList.addAll(
          this.convertToAlarmTypes(DlmsObjectType.ALARM_REGISTER_1, resultAlarmRegister1));
    } else {
      final GetResult resultAlarmRegister2 =
          this.executeForAlarmRegister(conn, optAlarmRegister2AttributeAddress.get());

      if (resultAlarmRegister2 != null) {
        alarmList.addAll(
            this.convertToAlarmTypes(DlmsObjectType.ALARM_REGISTER_2, resultAlarmRegister2));
      } else {
        throw new ProtocolAdapterException(
            "No GetResult received while retrieving alarm register 2.");
      }
    }

    return new AlarmRegisterResponseDto(alarmList);
  }

  private Set<AlarmTypeDto> convertToAlarmTypes(
      final DlmsObjectType dlmsObjectType, final GetResult getResult)
      throws ProtocolAdapterException {
    final DataObject resultData = getResult.getResultData();
    if (resultData != null && resultData.isNumber()) {
      return this.alarmHelperService.toAlarmTypes(
          dlmsObjectType, getResult.getResultData().getValue());
    } else {
      LOGGER.error("Result: {} --> {}", getResult.getResultCode(), getResult.getResultData());
      throw new ProtocolAdapterException("Invalid register value received from the meter.");
    }
  }

  private GetResult executeForAlarmRegister(
      final DlmsConnectionManager conn, final AttributeAddress alarmRegisterAttributeAddress) {

    conn.getDlmsMessageListener()
        .setDescription(
            "ReadAlarmRegister, retrieve attribute: "
                + JdlmsObjectToStringUtil.describeAttributes(alarmRegisterAttributeAddress));

    try {
      return conn.getConnection().get(alarmRegisterAttributeAddress);
    } catch (final IOException e) {
      throw new ConnectionException(e);
    }
  }
}

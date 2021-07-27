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
import java.util.Set;
import org.openmuc.jdlms.AttributeAddress;
import org.openmuc.jdlms.GetResult;
import org.openmuc.jdlms.ObisCode;
import org.openmuc.jdlms.datatypes.DataObject;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.AbstractCommandExecutor;
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

  private static final int CLASS_ID = 1;
  private static final ObisCode OBIS_CODE = new ObisCode("0.0.97.98.0.255");
  private static final int ATTRIBUTE_ID = 2;

  @Autowired private AlarmHelperService alarmHelperService;

  public ReadAlarmRegisterCommandExecutor() {
    super(ReadAlarmRegisterDataDto.class);
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
    return new AlarmRegisterResponseDto(this.retrieveAlarmRegister(conn));
  }

  private Set<AlarmTypeDto> retrieveAlarmRegister(final DlmsConnectionManager conn)
      throws ProtocolAdapterException {

    final AttributeAddress alarmRegisterValue =
        new AttributeAddress(CLASS_ID, OBIS_CODE, ATTRIBUTE_ID);

    conn.getDlmsMessageListener()
        .setDescription(
            "ReadAlarmRegister, retrieve attribute: "
                + JdlmsObjectToStringUtil.describeAttributes(alarmRegisterValue));

    final GetResult getResult;
    try {
      getResult = conn.getConnection().get(alarmRegisterValue);
    } catch (final IOException e) {
      throw new ConnectionException(e);
    }

    if (getResult == null) {
      throw new ProtocolAdapterException("No GetResult received while retrieving alarm register.");
    }

    final DataObject resultData = getResult.getResultData();
    if (resultData != null && resultData.isNumber()) {
      return this.alarmHelperService.toAlarmTypes(getResult.getResultData().getValue());
    } else {
      LOGGER.error(
          "Result: {} --> {}", getResult.getResultCode().name(), getResult.getResultData());
      throw new ProtocolAdapterException("Invalid register value received from the meter.");
    }
  }
}

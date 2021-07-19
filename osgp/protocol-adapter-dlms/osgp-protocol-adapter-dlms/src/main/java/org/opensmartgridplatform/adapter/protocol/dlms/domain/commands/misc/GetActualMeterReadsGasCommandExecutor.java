/*
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.misc;

import java.util.Date;
import java.util.List;
import org.openmuc.jdlms.AttributeAddress;
import org.openmuc.jdlms.GetResult;
import org.openmuc.jdlms.ObisCode;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.AbstractCommandExecutor;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.DlmsHelper;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.JdlmsObjectToStringUtil;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ActionRequestDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ActualMeterReadsDataGasDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ActualMeterReadsQueryDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ChannelDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.CosemDateTimeDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.DlmsMeterValueDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.MeterReadsGasResponseDto;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component()
public class GetActualMeterReadsGasCommandExecutor
    extends AbstractCommandExecutor<ActualMeterReadsQueryDto, MeterReadsGasResponseDto> {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(GetActualMeterReadsGasCommandExecutor.class);

  private static final int CLASS_ID_MBUS = 4;
  private static final byte ATTRIBUTE_ID_VALUE = 2;
  private static final byte ATTRIBUTE_ID_SCALER_UNIT = 3;
  private static final byte ATTRIBUTE_ID_TIME = 5;
  private static final ObisCode OBIS_CODE_MBUS_MASTER_VALUE_1 = new ObisCode("0.1.24.2.1.255");
  private static final ObisCode OBIS_CODE_MBUS_MASTER_VALUE_2 = new ObisCode("0.2.24.2.1.255");
  private static final ObisCode OBIS_CODE_MBUS_MASTER_VALUE_3 = new ObisCode("0.3.24.2.1.255");
  private static final ObisCode OBIS_CODE_MBUS_MASTER_VALUE_4 = new ObisCode("0.4.24.2.1.255");

  @Autowired private DlmsHelper dlmsHelper;

  public GetActualMeterReadsGasCommandExecutor() {
    super(ActualMeterReadsDataGasDto.class);
  }

  @Override
  public ActualMeterReadsQueryDto fromBundleRequestInput(final ActionRequestDto bundleInput)
      throws ProtocolAdapterException {

    this.checkActionRequestType(bundleInput);
    final ActualMeterReadsDataGasDto actualMeterReadsDataGasDto =
        (ActualMeterReadsDataGasDto) bundleInput;

    return new ActualMeterReadsQueryDto(actualMeterReadsDataGasDto.getChannel());
  }

  @Override
  public MeterReadsGasResponseDto execute(
      final DlmsConnectionManager conn,
      final DlmsDevice device,
      final ActualMeterReadsQueryDto actualMeterReadsRequest,
      final MessageMetadata messageMetadata)
      throws ProtocolAdapterException {

    final ObisCode obisCodeMbusMasterValue =
        this.masterValueForChannel(actualMeterReadsRequest.getChannel());

    LOGGER.debug("Retrieving current MBUS master value for ObisCode: {}", obisCodeMbusMasterValue);

    final AttributeAddress mbusValue =
        new AttributeAddress(
            CLASS_ID_MBUS,
            this.masterValueForChannel(actualMeterReadsRequest.getChannel()),
            ATTRIBUTE_ID_VALUE);

    LOGGER.debug(
        "Retrieving current MBUS master capture time for ObisCode: {}", obisCodeMbusMasterValue);

    final AttributeAddress mbusTime =
        new AttributeAddress(CLASS_ID_MBUS, obisCodeMbusMasterValue, ATTRIBUTE_ID_TIME);

    final AttributeAddress scalerUnit =
        new AttributeAddress(
            CLASS_ID_MBUS,
            this.masterValueForChannel(actualMeterReadsRequest.getChannel()),
            ATTRIBUTE_ID_SCALER_UNIT);

    conn.getDlmsMessageListener()
        .setDescription(
            "GetActualMeterReadsGas for channel "
                + actualMeterReadsRequest.getChannel()
                + ", retrieve attributes: "
                + JdlmsObjectToStringUtil.describeAttributes(mbusValue, mbusTime, scalerUnit));

    final List<GetResult> getResultList =
        this.dlmsHelper.getAndCheck(
            conn,
            device,
            "retrieve actual meter reads for mbus " + actualMeterReadsRequest.getChannel(),
            mbusValue,
            mbusTime,
            scalerUnit);

    final DlmsMeterValueDto consumption =
        this.dlmsHelper.getScaledMeterValue(
            getResultList.get(0),
            getResultList.get(2),
            "retrieve scaled value for mbus " + actualMeterReadsRequest.getChannel());
    final CosemDateTimeDto cosemDateTime =
        this.dlmsHelper.readDateTime(getResultList.get(1), "captureTime gas");
    final Date captureTime;
    if (cosemDateTime.isDateTimeSpecified()) {
      captureTime = cosemDateTime.asDateTime().toDate();
    } else {
      throw new ProtocolAdapterException(
          "Unexpected null/unspecified value for M-Bus Capture Time");
    }

    return new MeterReadsGasResponseDto(new Date(), consumption, captureTime);
  }

  private ObisCode masterValueForChannel(final ChannelDto channel) throws ProtocolAdapterException {
    switch (channel) {
      case ONE:
        return OBIS_CODE_MBUS_MASTER_VALUE_1;
      case TWO:
        return OBIS_CODE_MBUS_MASTER_VALUE_2;
      case THREE:
        return OBIS_CODE_MBUS_MASTER_VALUE_3;
      case FOUR:
        return OBIS_CODE_MBUS_MASTER_VALUE_4;
      default:
        throw new ProtocolAdapterException(String.format("channel %s not supported", channel));
    }
  }
}

/*
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.misc;

import java.util.List;
import org.joda.time.DateTime;
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
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ActiveEnergyValuesDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ActualMeterReadsDataDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ActualMeterReadsQueryDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.CosemDateTimeDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.DlmsMeterValueDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.MeterReadsResponseDto;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component()
public class GetActualMeterReadsCommandExecutor
    extends AbstractCommandExecutor<ActualMeterReadsQueryDto, MeterReadsResponseDto> {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(GetActualMeterReadsCommandExecutor.class);

  private static final int CLASS_ID_REGISTER = 3;
  private static final ObisCode OBIS_CODE_ACTIVE_ENERGY_IMPORT = new ObisCode("1.0.1.8.0.255");
  private static final ObisCode OBIS_CODE_ACTIVE_ENERGY_EXPORT = new ObisCode("1.0.2.8.0.255");
  private static final ObisCode OBIS_CODE_ACTIVE_ENERGY_IMPORT_RATE_1 =
      new ObisCode("1.0.1.8.1.255");
  private static final ObisCode OBIS_CODE_ACTIVE_ENERGY_IMPORT_RATE_2 =
      new ObisCode("1.0.1.8.2.255");
  private static final ObisCode OBIS_CODE_ACTIVE_ENERGY_EXPORT_RATE_1 =
      new ObisCode("1.0.2.8.1.255");
  private static final ObisCode OBIS_CODE_ACTIVE_ENERGY_EXPORT_RATE_2 =
      new ObisCode("1.0.2.8.2.255");
  private static final byte ATTRIBUTE_ID_VALUE = 2;
  private static final byte ATTRIBUTE_ID_SCALER_UNIT = 3;

  private static final int CLASS_ID_CLOCK = 8;
  private static final ObisCode OBIS_CODE_CLOCK = new ObisCode("0.0.1.0.0.255");
  private static final byte ATTRIBUTE_ID_TIME = 2;

  // scaler unit attribute address is filled dynamically
  private static final AttributeAddress[] ATTRIBUTE_ADDRESSES = {
    new AttributeAddress(CLASS_ID_CLOCK, OBIS_CODE_CLOCK, ATTRIBUTE_ID_TIME),
    new AttributeAddress(CLASS_ID_REGISTER, OBIS_CODE_ACTIVE_ENERGY_IMPORT, ATTRIBUTE_ID_VALUE),
    new AttributeAddress(
        CLASS_ID_REGISTER, OBIS_CODE_ACTIVE_ENERGY_IMPORT_RATE_1, ATTRIBUTE_ID_VALUE),
    new AttributeAddress(
        CLASS_ID_REGISTER, OBIS_CODE_ACTIVE_ENERGY_IMPORT_RATE_2, ATTRIBUTE_ID_VALUE),
    new AttributeAddress(CLASS_ID_REGISTER, OBIS_CODE_ACTIVE_ENERGY_EXPORT, ATTRIBUTE_ID_VALUE),
    new AttributeAddress(
        CLASS_ID_REGISTER, OBIS_CODE_ACTIVE_ENERGY_EXPORT_RATE_1, ATTRIBUTE_ID_VALUE),
    new AttributeAddress(
        CLASS_ID_REGISTER, OBIS_CODE_ACTIVE_ENERGY_EXPORT_RATE_2, ATTRIBUTE_ID_VALUE),
    new AttributeAddress(
        CLASS_ID_REGISTER, OBIS_CODE_ACTIVE_ENERGY_IMPORT, ATTRIBUTE_ID_SCALER_UNIT),
    new AttributeAddress(
        CLASS_ID_REGISTER, OBIS_CODE_ACTIVE_ENERGY_IMPORT_RATE_1, ATTRIBUTE_ID_SCALER_UNIT),
    new AttributeAddress(
        CLASS_ID_REGISTER, OBIS_CODE_ACTIVE_ENERGY_IMPORT_RATE_2, ATTRIBUTE_ID_SCALER_UNIT),
    new AttributeAddress(
        CLASS_ID_REGISTER, OBIS_CODE_ACTIVE_ENERGY_EXPORT, ATTRIBUTE_ID_SCALER_UNIT),
    new AttributeAddress(
        CLASS_ID_REGISTER, OBIS_CODE_ACTIVE_ENERGY_EXPORT_RATE_1, ATTRIBUTE_ID_SCALER_UNIT),
    new AttributeAddress(
        CLASS_ID_REGISTER, OBIS_CODE_ACTIVE_ENERGY_EXPORT_RATE_2, ATTRIBUTE_ID_SCALER_UNIT)
  };

  private static final int INDEX_TIME = 0;
  private static final int INDEX_ACTIVE_ENERGY_IMPORT = 1;
  private static final int INDEX_ACTIVE_ENERGY_IMPORT_RATE_1 = 2;
  private static final int INDEX_ACTIVE_ENERGY_IMPORT_RATE_2 = 3;
  private static final int INDEX_ACTIVE_ENERGY_EXPORT = 4;
  private static final int INDEX_ACTIVE_ENERGY_EXPORT_RATE_1 = 5;
  private static final int INDEX_ACTIVE_ENERGY_EXPORT_RATE_2 = 6;
  private static final int INDEX_ACTIVE_ENERGY_IMPORT_SCALER_UNIT = 7;
  private static final int INDEX_ACTIVE_ENERGY_IMPORT_RATE_1_SCALER_UNIT = 8;
  private static final int INDEX_ACTIVE_ENERGY_IMPORT_RATE_2_SCALER_UNIT = 9;
  private static final int INDEX_ACTIVE_ENERGY_EXPORT_SCALER_UNIT = 10;
  private static final int INDEX_ACTIVE_ENERGY_EXPORT_RATE_1_SCALER_UNIT = 11;
  private static final int INDEX_ACTIVE_ENERGY_EXPORT_RATE_2_SCALER_UNIT = 12;

  @Autowired private DlmsHelper dlmsHelper;

  public GetActualMeterReadsCommandExecutor() {
    super(ActualMeterReadsDataDto.class);
  }

  @Override
  public ActualMeterReadsQueryDto fromBundleRequestInput(final ActionRequestDto bundleInput)
      throws ProtocolAdapterException {

    this.checkActionRequestType(bundleInput);

    /*
     * The ActionRequestDto, which is an ActualMeterReadsDataDto does not
     * contain any data, so no further configuration of the
     * ActualMeterReadsQueryDto is necessary.
     */
    return new ActualMeterReadsQueryDto();
  }

  @Override
  public MeterReadsResponseDto execute(
      final DlmsConnectionManager conn,
      final DlmsDevice device,
      final ActualMeterReadsQueryDto actualMeterReadsQuery,
      final MessageMetadata messageMetadata)
      throws ProtocolAdapterException {

    if (actualMeterReadsQuery != null && actualMeterReadsQuery.isMbusQuery()) {
      throw new IllegalArgumentException(
          "ActualMeterReadsQuery object for energy reads should not be about gas.");
    }

    conn.getDlmsMessageListener()
        .setDescription(
            "GetActualMeterReads retrieve attributes: "
                + JdlmsObjectToStringUtil.describeAttributes(ATTRIBUTE_ADDRESSES));

    LOGGER.debug("Retrieving actual energy reads");
    final List<GetResult> getResultList =
        this.dlmsHelper.getAndCheck(
            conn, device, "retrieve actual meter reads", ATTRIBUTE_ADDRESSES);

    final CosemDateTimeDto cosemDateTime =
        this.dlmsHelper.readDateTime(getResultList.get(INDEX_TIME), "Actual Energy Reads Time");
    final DateTime time = cosemDateTime.asDateTime();
    if (time == null) {
      throw new ProtocolAdapterException(
          "Unexpected null/unspecified value for Actual Energy Reads Time");
    }
    final DlmsMeterValueDto activeEnergyImport =
        this.dlmsHelper.getScaledMeterValue(
            getResultList.get(INDEX_ACTIVE_ENERGY_IMPORT),
            getResultList.get(INDEX_ACTIVE_ENERGY_IMPORT_SCALER_UNIT),
            "Actual Energy Reads +A");
    final DlmsMeterValueDto activeEnergyExport =
        this.dlmsHelper.getScaledMeterValue(
            getResultList.get(INDEX_ACTIVE_ENERGY_EXPORT),
            getResultList.get(INDEX_ACTIVE_ENERGY_EXPORT_SCALER_UNIT),
            "Actual Energy Reads -A");
    final DlmsMeterValueDto activeEnergyImportRate1 =
        this.dlmsHelper.getScaledMeterValue(
            getResultList.get(INDEX_ACTIVE_ENERGY_IMPORT_RATE_1),
            getResultList.get(INDEX_ACTIVE_ENERGY_IMPORT_RATE_1_SCALER_UNIT),
            "Actual Energy Reads +A rate 1");
    final DlmsMeterValueDto activeEnergyImportRate2 =
        this.dlmsHelper.getScaledMeterValue(
            getResultList.get(INDEX_ACTIVE_ENERGY_IMPORT_RATE_2),
            getResultList.get(INDEX_ACTIVE_ENERGY_IMPORT_RATE_2_SCALER_UNIT),
            "Actual Energy Reads +A rate 2");
    final DlmsMeterValueDto activeEnergyExportRate1 =
        this.dlmsHelper.getScaledMeterValue(
            getResultList.get(INDEX_ACTIVE_ENERGY_EXPORT_RATE_1),
            getResultList.get(INDEX_ACTIVE_ENERGY_EXPORT_RATE_1_SCALER_UNIT),
            "Actual Energy Reads -A rate 1");
    final DlmsMeterValueDto activeEnergyExportRate2 =
        this.dlmsHelper.getScaledMeterValue(
            getResultList.get(INDEX_ACTIVE_ENERGY_EXPORT_RATE_2),
            getResultList.get(INDEX_ACTIVE_ENERGY_EXPORT_RATE_2_SCALER_UNIT),
            "Actual Energy Reads -A rate 2");

    return new MeterReadsResponseDto(
        time.toDate(),
        new ActiveEnergyValuesDto(
            activeEnergyImport,
            activeEnergyExport,
            activeEnergyImportRate1,
            activeEnergyImportRate2,
            activeEnergyExportRate1,
            activeEnergyExportRate2));
  }
}

/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
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
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ActualPowerQualityPrivateResponseDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ActualPowerQualityRequestDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.CosemDateTimeDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.DlmsMeterValueDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component()
public class GetActualPowerQualityPrivateCommandExecutor
        extends AbstractCommandExecutor<ActualPowerQualityRequestDto, ActualPowerQualityPrivateResponseDto> {

    private static final Logger LOGGER = LoggerFactory.getLogger(GetActualPowerQualityPrivateCommandExecutor.class);

    private static final int CLASS_ID_REGISTER = 3;
    private static final ObisCode OBIS_CODE_INSTANTANEOUS_CURRENT_L1 = new ObisCode("1.0.31.7.0.255");
    private static final ObisCode OBIS_CODE_INSTANTANEOUS_CURRENT_L2 = new ObisCode("1.0.51.7.0.255");
    private static final ObisCode OBIS_CODE_INSTANTANEOUS_CURRENT_L3 = new ObisCode("1.0.71.7.0.255");
    private static final ObisCode OBIS_CODE_INSTANTANEOUS_ACTIVE_POWER_IMPORT = new ObisCode("1.0.1.7.0.255");
    private static final ObisCode OBIS_CODE_INSTANTANEOUS_ACTIVE_POWER_EXPORT = new ObisCode("1.0.2.7.0.255");
    private static final ObisCode OBIS_CODE_INSTANTANEOUS_ACTIVE_POWER_IMPORT_L1 = new ObisCode("1.0.21.7.0.255");
    private static final ObisCode OBIS_CODE_INSTANTANEOUS_ACTIVE_POWER_IMPORT_L2 = new ObisCode("1.0.41.7.0.255");
    private static final ObisCode OBIS_CODE_INSTANTANEOUS_ACTIVE_POWER_IMPORT_L3 = new ObisCode("1.0.61.7.0.255");
    private static final ObisCode OBIS_CODE_INSTANTANEOUS_ACTIVE_POWER_EXPORT_L1 = new ObisCode("1.0.22.7.0.255");
    private static final ObisCode OBIS_CODE_INSTANTANEOUS_ACTIVE_POWER_EXPORT_L2 = new ObisCode("1.0.42.7.0.255");
    private static final ObisCode OBIS_CODE_INSTANTANEOUS_ACTIVE_POWER_EXPORT_L3 = new ObisCode("1.0.62.7.0.255");
    private static final ObisCode OBIS_CODE_AVERAGE_CURRENT_L1 = new ObisCode("1.0.31.24.0.255");
    private static final ObisCode OBIS_CODE_AVERAGE_CURRENT_L2 = new ObisCode("1.0.51.24.0.255");
    private static final ObisCode OBIS_CODE_AVERAGE_CURRENT_L3 = new ObisCode("1.0.71.24.0.255");
    private static final ObisCode OBIS_CODE_AVERAGE_ACTIVE_POWER_IMPORT_L1 = new ObisCode("1.0.21.4.0.255");
    private static final ObisCode OBIS_CODE_AVERAGE_ACTIVE_POWER_IMPORT_L2 = new ObisCode("1.0.41.4.0.255");
    private static final ObisCode OBIS_CODE_AVERAGE_ACTIVE_POWER_IMPORT_L3 = new ObisCode("1.0.61.4.0.255");
    private static final ObisCode OBIS_CODE_AVERAGE_ACTIVE_POWER_EXPORT_L1 = new ObisCode("1.0.22.4.0.255");
    private static final ObisCode OBIS_CODE_AVERAGE_ACTIVE_POWER_EXPORT_L2 = new ObisCode("1.0.42.4.0.255");
    private static final ObisCode OBIS_CODE_AVERAGE_ACTIVE_POWER_EXPORT_L3 = new ObisCode("1.0.62.4.0.255");
    private static final ObisCode OBIS_CODE_AVERAGE_REACTIVE_POWER_IMPORT_L1 = new ObisCode("1.0.23.4.0.255");
    private static final ObisCode OBIS_CODE_AVERAGE_REACTIVE_POWER_IMPORT_L2 = new ObisCode("1.0.43.4.0.255");
    private static final ObisCode OBIS_CODE_AVERAGE_REACTIVE_POWER_IMPORT_L3 = new ObisCode("1.0.63.4.0.255");
    private static final ObisCode OBIS_CODE_AVERAGE_REACTIVE_POWER_EXPORT_L1 = new ObisCode("1.0.24.4.0.255");
    private static final ObisCode OBIS_CODE_AVERAGE_REACTIVE_POWER_EXPORT_L2 = new ObisCode("1.0.44.4.0.255");
    private static final ObisCode OBIS_CODE_AVERAGE_REACTIVE_POWER_EXPORT_L3 = new ObisCode("1.0.64.4.0.255");
    private static final ObisCode OBIS_CODE_INSTANTANEOUS_ACTIVE_CURRENT_TOTAL_OVER_ALL_PHASES = new ObisCode("1.0.90.7.0.255");

    private static final byte ATTRIBUTE_ID_VALUE = 2;
    private static final byte ATTRIBUTE_ID_SCALER_UNIT = 3;

    private static final int CLASS_ID_CLOCK = 8;
    private static final ObisCode OBIS_CODE_CLOCK = new ObisCode("0.0.1.0.0.255");
    private static final byte ATTRIBUTE_ID_TIME = 2;

    // scaler unit attribute address is filled dynamically
    private static final AttributeAddress[] ATTRIBUTE_ADDRESSES = {
        new AttributeAddress(CLASS_ID_CLOCK, OBIS_CODE_CLOCK, ATTRIBUTE_ID_TIME),
        new AttributeAddress(CLASS_ID_REGISTER, OBIS_CODE_INSTANTANEOUS_CURRENT_L1, ATTRIBUTE_ID_VALUE),
        new AttributeAddress(CLASS_ID_REGISTER, OBIS_CODE_INSTANTANEOUS_CURRENT_L2, ATTRIBUTE_ID_VALUE),
        new AttributeAddress(CLASS_ID_REGISTER, OBIS_CODE_INSTANTANEOUS_CURRENT_L3, ATTRIBUTE_ID_VALUE),
        new AttributeAddress(CLASS_ID_REGISTER, OBIS_CODE_INSTANTANEOUS_ACTIVE_POWER_IMPORT, ATTRIBUTE_ID_VALUE),
        new AttributeAddress(CLASS_ID_REGISTER, OBIS_CODE_INSTANTANEOUS_ACTIVE_POWER_EXPORT, ATTRIBUTE_ID_VALUE),
        new AttributeAddress(CLASS_ID_REGISTER, OBIS_CODE_INSTANTANEOUS_ACTIVE_POWER_IMPORT_L1, ATTRIBUTE_ID_VALUE),
        new AttributeAddress(CLASS_ID_REGISTER, OBIS_CODE_INSTANTANEOUS_ACTIVE_POWER_IMPORT_L2, ATTRIBUTE_ID_VALUE),
        new AttributeAddress(CLASS_ID_REGISTER, OBIS_CODE_INSTANTANEOUS_ACTIVE_POWER_IMPORT_L3, ATTRIBUTE_ID_VALUE),
        new AttributeAddress(CLASS_ID_REGISTER, OBIS_CODE_INSTANTANEOUS_ACTIVE_POWER_EXPORT_L1, ATTRIBUTE_ID_VALUE),
        new AttributeAddress(CLASS_ID_REGISTER, OBIS_CODE_INSTANTANEOUS_ACTIVE_POWER_EXPORT_L2, ATTRIBUTE_ID_VALUE),
        new AttributeAddress(CLASS_ID_REGISTER, OBIS_CODE_INSTANTANEOUS_ACTIVE_POWER_EXPORT_L3, ATTRIBUTE_ID_VALUE),
        new AttributeAddress(CLASS_ID_REGISTER, OBIS_CODE_AVERAGE_CURRENT_L1, ATTRIBUTE_ID_VALUE),
        new AttributeAddress(CLASS_ID_REGISTER, OBIS_CODE_AVERAGE_CURRENT_L2, ATTRIBUTE_ID_VALUE),
        new AttributeAddress(CLASS_ID_REGISTER, OBIS_CODE_AVERAGE_CURRENT_L3, ATTRIBUTE_ID_VALUE),
        new AttributeAddress(CLASS_ID_REGISTER, OBIS_CODE_AVERAGE_ACTIVE_POWER_IMPORT_L1, ATTRIBUTE_ID_VALUE),
        new AttributeAddress(CLASS_ID_REGISTER, OBIS_CODE_AVERAGE_ACTIVE_POWER_IMPORT_L2, ATTRIBUTE_ID_VALUE),
        new AttributeAddress(CLASS_ID_REGISTER, OBIS_CODE_AVERAGE_ACTIVE_POWER_IMPORT_L3, ATTRIBUTE_ID_VALUE),
        new AttributeAddress(CLASS_ID_REGISTER, OBIS_CODE_AVERAGE_ACTIVE_POWER_EXPORT_L1, ATTRIBUTE_ID_VALUE),
        new AttributeAddress(CLASS_ID_REGISTER, OBIS_CODE_AVERAGE_ACTIVE_POWER_EXPORT_L2, ATTRIBUTE_ID_VALUE),
        new AttributeAddress(CLASS_ID_REGISTER, OBIS_CODE_AVERAGE_ACTIVE_POWER_EXPORT_L3, ATTRIBUTE_ID_VALUE),
        new AttributeAddress(CLASS_ID_REGISTER, OBIS_CODE_AVERAGE_REACTIVE_POWER_IMPORT_L1, ATTRIBUTE_ID_VALUE),
        new AttributeAddress(CLASS_ID_REGISTER, OBIS_CODE_AVERAGE_REACTIVE_POWER_IMPORT_L2, ATTRIBUTE_ID_VALUE),
        new AttributeAddress(CLASS_ID_REGISTER, OBIS_CODE_AVERAGE_REACTIVE_POWER_IMPORT_L3, ATTRIBUTE_ID_VALUE),
        new AttributeAddress(CLASS_ID_REGISTER, OBIS_CODE_AVERAGE_REACTIVE_POWER_EXPORT_L1, ATTRIBUTE_ID_VALUE),
        new AttributeAddress(CLASS_ID_REGISTER, OBIS_CODE_AVERAGE_REACTIVE_POWER_EXPORT_L2, ATTRIBUTE_ID_VALUE),
        new AttributeAddress(CLASS_ID_REGISTER, OBIS_CODE_AVERAGE_REACTIVE_POWER_EXPORT_L3, ATTRIBUTE_ID_VALUE),
        new AttributeAddress(CLASS_ID_REGISTER, OBIS_CODE_INSTANTANEOUS_ACTIVE_CURRENT_TOTAL_OVER_ALL_PHASES, ATTRIBUTE_ID_VALUE),
        new AttributeAddress(CLASS_ID_REGISTER, OBIS_CODE_INSTANTANEOUS_CURRENT_L1, ATTRIBUTE_ID_SCALER_UNIT),
        new AttributeAddress(CLASS_ID_REGISTER, OBIS_CODE_INSTANTANEOUS_CURRENT_L2, ATTRIBUTE_ID_SCALER_UNIT),
        new AttributeAddress(CLASS_ID_REGISTER, OBIS_CODE_INSTANTANEOUS_CURRENT_L3, ATTRIBUTE_ID_SCALER_UNIT),
        new AttributeAddress(CLASS_ID_REGISTER, OBIS_CODE_INSTANTANEOUS_ACTIVE_POWER_IMPORT, ATTRIBUTE_ID_SCALER_UNIT),
        new AttributeAddress(CLASS_ID_REGISTER, OBIS_CODE_INSTANTANEOUS_ACTIVE_POWER_EXPORT, ATTRIBUTE_ID_SCALER_UNIT),
        new AttributeAddress(CLASS_ID_REGISTER, OBIS_CODE_INSTANTANEOUS_ACTIVE_POWER_IMPORT_L1, ATTRIBUTE_ID_SCALER_UNIT),
        new AttributeAddress(CLASS_ID_REGISTER, OBIS_CODE_INSTANTANEOUS_ACTIVE_POWER_IMPORT_L2, ATTRIBUTE_ID_SCALER_UNIT),
        new AttributeAddress(CLASS_ID_REGISTER, OBIS_CODE_INSTANTANEOUS_ACTIVE_POWER_IMPORT_L3, ATTRIBUTE_ID_SCALER_UNIT),
        new AttributeAddress(CLASS_ID_REGISTER, OBIS_CODE_INSTANTANEOUS_ACTIVE_POWER_EXPORT_L1, ATTRIBUTE_ID_SCALER_UNIT),
        new AttributeAddress(CLASS_ID_REGISTER, OBIS_CODE_INSTANTANEOUS_ACTIVE_POWER_EXPORT_L2, ATTRIBUTE_ID_SCALER_UNIT),
        new AttributeAddress(CLASS_ID_REGISTER, OBIS_CODE_INSTANTANEOUS_ACTIVE_POWER_EXPORT_L3, ATTRIBUTE_ID_SCALER_UNIT),
        new AttributeAddress(CLASS_ID_REGISTER, OBIS_CODE_AVERAGE_CURRENT_L1, ATTRIBUTE_ID_SCALER_UNIT),
        new AttributeAddress(CLASS_ID_REGISTER, OBIS_CODE_AVERAGE_CURRENT_L2, ATTRIBUTE_ID_SCALER_UNIT),
        new AttributeAddress(CLASS_ID_REGISTER, OBIS_CODE_AVERAGE_CURRENT_L3, ATTRIBUTE_ID_SCALER_UNIT),
        new AttributeAddress(CLASS_ID_REGISTER, OBIS_CODE_AVERAGE_ACTIVE_POWER_IMPORT_L1, ATTRIBUTE_ID_SCALER_UNIT),
        new AttributeAddress(CLASS_ID_REGISTER, OBIS_CODE_AVERAGE_ACTIVE_POWER_IMPORT_L2, ATTRIBUTE_ID_SCALER_UNIT),
        new AttributeAddress(CLASS_ID_REGISTER, OBIS_CODE_AVERAGE_ACTIVE_POWER_IMPORT_L3, ATTRIBUTE_ID_SCALER_UNIT),
        new AttributeAddress(CLASS_ID_REGISTER, OBIS_CODE_AVERAGE_ACTIVE_POWER_EXPORT_L1, ATTRIBUTE_ID_SCALER_UNIT),
        new AttributeAddress(CLASS_ID_REGISTER, OBIS_CODE_AVERAGE_ACTIVE_POWER_EXPORT_L2, ATTRIBUTE_ID_SCALER_UNIT),
        new AttributeAddress(CLASS_ID_REGISTER, OBIS_CODE_AVERAGE_ACTIVE_POWER_EXPORT_L3, ATTRIBUTE_ID_SCALER_UNIT),
        new AttributeAddress(CLASS_ID_REGISTER, OBIS_CODE_AVERAGE_REACTIVE_POWER_IMPORT_L1, ATTRIBUTE_ID_SCALER_UNIT),
        new AttributeAddress(CLASS_ID_REGISTER, OBIS_CODE_AVERAGE_REACTIVE_POWER_IMPORT_L2, ATTRIBUTE_ID_SCALER_UNIT),
        new AttributeAddress(CLASS_ID_REGISTER, OBIS_CODE_AVERAGE_REACTIVE_POWER_IMPORT_L3, ATTRIBUTE_ID_SCALER_UNIT),
        new AttributeAddress(CLASS_ID_REGISTER, OBIS_CODE_AVERAGE_REACTIVE_POWER_EXPORT_L1, ATTRIBUTE_ID_SCALER_UNIT),
        new AttributeAddress(CLASS_ID_REGISTER, OBIS_CODE_AVERAGE_REACTIVE_POWER_EXPORT_L2, ATTRIBUTE_ID_SCALER_UNIT),
        new AttributeAddress(CLASS_ID_REGISTER, OBIS_CODE_AVERAGE_REACTIVE_POWER_EXPORT_L3, ATTRIBUTE_ID_SCALER_UNIT),
        new AttributeAddress(CLASS_ID_REGISTER, OBIS_CODE_INSTANTANEOUS_ACTIVE_CURRENT_TOTAL_OVER_ALL_PHASES, ATTRIBUTE_ID_SCALER_UNIT)

    };

    private static final int INDEX_TIME = 0;
    private static final int INDEX_INSTANTANEOUS_CURRENT_L1 = 1;
    private static final int INDEX_INSTANTANEOUS_CURRENT_L2 = 2;
    private static final int INDEX_INSTANTANEOUS_CURRENT_L3 = 3;
    private static final int INDEX_INSTANTANEOUS_ACTIVE_POWER_IMPORT = 4;
    private static final int INDEX_INSTANTANEOUS_ACTIVE_POWER_EXPORT = 5;
    private static final int INDEX_INSTANTANEOUS_ACTIVE_POWER_IMPORT_L1 = 6;
    private static final int INDEX_INSTANTANEOUS_ACTIVE_POWER_IMPORT_L2 = 7;
    private static final int INDEX_INSTANTANEOUS_ACTIVE_POWER_IMPORT_L3 = 8;
    private static final int INDEX_INSTANTANEOUS_ACTIVE_POWER_EXPORT_L1 = 9;
    private static final int INDEX_INSTANTANEOUS_ACTIVE_POWER_EXPORT_L2 = 10;
    private static final int INDEX_INSTANTANEOUS_ACTIVE_POWER_EXPORT_L3 = 11;
    private static final int INDEX_AVERAGE_CURRENT_L1 = 12;
    private static final int INDEX_AVERAGE_CURRENT_L2 = 13;
    private static final int INDEX_AVERAGE_CURRENT_L3 = 14;
    private static final int INDEX_AVERAGE_ACTIVE_POWER_IMPORT_L1 = 15;
    private static final int INDEX_AVERAGE_ACTIVE_POWER_IMPORT_L2 = 16;
    private static final int INDEX_AVERAGE_ACTIVE_POWER_IMPORT_L3 = 17;
    private static final int INDEX_AVERAGE_ACTIVE_POWER_EXPORT_L1 = 18;
    private static final int INDEX_AVERAGE_ACTIVE_POWER_EXPORT_L2 = 19;
    private static final int INDEX_AVERAGE_ACTIVE_POWER_EXPORT_L3 = 20;
    private static final int INDEX_AVERAGE_REACTIVE_POWER_IMPORT_L1 = 21;
    private static final int INDEX_AVERAGE_REACTIVE_POWER_IMPORT_L2 = 22;
    private static final int INDEX_AVERAGE_REACTIVE_POWER_IMPORT_L3 = 23;
    private static final int INDEX_AVERAGE_REACTIVE_POWER_EXPORT_L1 = 24;
    private static final int INDEX_AVERAGE_REACTIVE_POWER_EXPORT_L2 = 25;
    private static final int INDEX_AVERAGE_REACTIVE_POWER_EXPORT_L3 = 26;
    private static final int INDEX_INSTANTANEOUS_ACTIVE_CURRENT_TOTAL_OVER_ALL_PHASES = 27;
    private static final int INDEX_INSTANTANEOUS_CURRENT_L1_SCALER_UNIT = 28;
    private static final int INDEX_INSTANTANEOUS_CURRENT_L2_SCALER_UNIT = 29;
    private static final int INDEX_INSTANTANEOUS_CURRENT_L3_SCALER_UNIT = 30;
    private static final int INDEX_INSTANTANEOUS_ACTIVE_POWER_IMPORT_SCALER_UNIT = 31;
    private static final int INDEX_INSTANTANEOUS_ACTIVE_POWER_EXPORT_SCALER_UNIT = 32;
    private static final int INDEX_INSTANTANEOUS_ACTIVE_POWER_IMPORT_L1_SCALER_UNIT = 33;
    private static final int INDEX_INSTANTANEOUS_ACTIVE_POWER_IMPORT_L2_SCALER_UNIT = 34;
    private static final int INDEX_INSTANTANEOUS_ACTIVE_POWER_IMPORT_L3_SCALER_UNIT = 35;
    private static final int INDEX_INSTANTANEOUS_ACTIVE_POWER_EXPORT_L1_SCALER_UNIT = 36;
    private static final int INDEX_INSTANTANEOUS_ACTIVE_POWER_EXPORT_L2_SCALER_UNIT = 37;
    private static final int INDEX_INSTANTANEOUS_ACTIVE_POWER_EXPORT_L3_SCALER_UNIT = 38;
    private static final int INDEX_AVERAGE_CURRENT_L1_SCALER_UNIT = 39;
    private static final int INDEX_AVERAGE_CURRENT_L2_SCALER_UNIT = 40;
    private static final int INDEX_AVERAGE_CURRENT_L3_SCALER_UNIT = 41;
    private static final int INDEX_AVERAGE_ACTIVE_POWER_IMPORT_L1_SCALER_UNIT = 42;
    private static final int INDEX_AVERAGE_ACTIVE_POWER_IMPORT_L2_SCALER_UNIT = 43;
    private static final int INDEX_AVERAGE_ACTIVE_POWER_IMPORT_L3_SCALER_UNIT = 44;
    private static final int INDEX_AVERAGE_ACTIVE_POWER_EXPORT_L1_SCALER_UNIT = 45;
    private static final int INDEX_AVERAGE_ACTIVE_POWER_EXPORT_L2_SCALER_UNIT = 46;
    private static final int INDEX_AVERAGE_ACTIVE_POWER_EXPORT_L3_SCALER_UNIT = 47;
    private static final int INDEX_AVERAGE_REACTIVE_POWER_IMPORT_L1_SCALER_UNIT = 48;
    private static final int INDEX_AVERAGE_REACTIVE_POWER_IMPORT_L2_SCALER_UNIT = 49;
    private static final int INDEX_AVERAGE_REACTIVE_POWER_IMPORT_L3_SCALER_UNIT = 50;
    private static final int INDEX_AVERAGE_REACTIVE_POWER_EXPORT_L1_SCALER_UNIT = 51;
    private static final int INDEX_AVERAGE_REACTIVE_POWER_EXPORT_L2_SCALER_UNIT = 52;
    private static final int INDEX_AVERAGE_REACTIVE_POWER_EXPORT_L3_SCALER_UNIT = 53;
    private static final int INDEX_INSTANTANEOUS_ACTIVE_CURRENT_TOTAL_OVER_ALL_PHASES_SCALER_UNIT = 54;



    @Autowired
    private DlmsHelper dlmsHelper;

    public GetActualPowerQualityPrivateCommandExecutor() {
        super(ActualPowerQualityRequestDto.class);
    }

    @Override
    public ActualPowerQualityRequestDto fromBundleRequestInput(final ActionRequestDto bundleInput)
            throws ProtocolAdapterException {

        this.checkActionRequestType(bundleInput);

        /*
         * The ActionRequestDto, which is an ActualMeterReadsDataDto does not
         * contain any data, so no further configuration of the
         * ActualMeterReadsQueryDto is necessary.
         */
        return new ActualPowerQualityRequestDto();
    }

    @Override
    public ActualPowerQualityPrivateResponseDto execute(final DlmsConnectionManager conn, final DlmsDevice device,
            final ActualPowerQualityRequestDto actualPowerQualityRequestDto) throws ProtocolAdapterException {

//        TODO Checks needed here?
//        if (actualPowerQualityRequestDto != null && actualPowerQualityRequestDto.isMbusQuery()) {
//            throw new IllegalArgumentException(
//                    "ActualMeterReadsQuery object for energy reads should not be about gas.");
//        }

        conn.getDlmsMessageListener().setDescription(
                "GetActualPowerQuality retrieve attributes: " + JdlmsObjectToStringUtil
                        .describeAttributes(ATTRIBUTE_ADDRESSES));

        LOGGER.info("Retrieving actual power quality");
        final List<GetResult> getResultList = this.dlmsHelper
                .getAndCheck(conn, device, "retrieve actual power quality", ATTRIBUTE_ADDRESSES);

        final CosemDateTimeDto cosemDateTime = this.dlmsHelper
                .readDateTime(getResultList.get(INDEX_TIME), "Actual Power Quality Time");
        final DateTime time = cosemDateTime.asDateTime();
        if (time == null) {
            throw new ProtocolAdapterException("Unexpected null/unspecified value for Actual Power Quality Time");
        }
        final DlmsMeterValueDto instantaneousCurrentL1 = this.dlmsHelper.getScaledMeterValue(
                getResultList.get(INDEX_INSTANTANEOUS_CURRENT_L1),
                getResultList.get(INDEX_INSTANTANEOUS_CURRENT_L1_SCALER_UNIT), "Instantaneous current L1");
        final DlmsMeterValueDto instantaneousCurrentL2 = this.dlmsHelper.getScaledMeterValue(
                getResultList.get(INDEX_INSTANTANEOUS_CURRENT_L2),
                getResultList.get(INDEX_INSTANTANEOUS_CURRENT_L2_SCALER_UNIT), "Instantaneous current L2");
        final DlmsMeterValueDto instantaneousCurrentL3 = this.dlmsHelper.getScaledMeterValue(
                getResultList.get(INDEX_INSTANTANEOUS_CURRENT_L3),
                getResultList.get(INDEX_INSTANTANEOUS_CURRENT_L3_SCALER_UNIT), "Instantaneous current L3");
        final DlmsMeterValueDto instantaneousActivePowerImport = this.dlmsHelper.getScaledMeterValue(
                getResultList.get(INDEX_INSTANTANEOUS_ACTIVE_POWER_IMPORT),
                getResultList.get(INDEX_INSTANTANEOUS_ACTIVE_POWER_IMPORT_SCALER_UNIT),
                "Instantaneous active power import");
        final DlmsMeterValueDto instantaneousActivePowerExport = this.dlmsHelper.getScaledMeterValue(
                getResultList.get(INDEX_INSTANTANEOUS_ACTIVE_POWER_EXPORT),
                getResultList.get(INDEX_INSTANTANEOUS_ACTIVE_POWER_EXPORT_SCALER_UNIT),
                "Instantaneous active power export");
        final DlmsMeterValueDto instantaneousActivePowerImportL1 = this.dlmsHelper.getScaledMeterValue(
                getResultList.get(INDEX_INSTANTANEOUS_ACTIVE_POWER_IMPORT_L1),
                getResultList.get(INDEX_INSTANTANEOUS_ACTIVE_POWER_IMPORT_L1_SCALER_UNIT),
                "Instantaneous active power import L1");
        final DlmsMeterValueDto instantaneousActivePowerImportL2 = this.dlmsHelper.getScaledMeterValue(
                getResultList.get(INDEX_INSTANTANEOUS_ACTIVE_POWER_IMPORT_L2),
                getResultList.get(INDEX_INSTANTANEOUS_ACTIVE_POWER_IMPORT_L2_SCALER_UNIT),
                "Instantaneous active power import L2");
        final DlmsMeterValueDto instantaneousActivePowerImportL3 = this.dlmsHelper.getScaledMeterValue(
                getResultList.get(INDEX_INSTANTANEOUS_ACTIVE_POWER_IMPORT_L3),
                getResultList.get(INDEX_INSTANTANEOUS_ACTIVE_POWER_IMPORT_L3_SCALER_UNIT),
                "Instantaneous active power import L3");
        final DlmsMeterValueDto instantaneousActivePowerExportL1 = this.dlmsHelper.getScaledMeterValue(
                getResultList.get(INDEX_INSTANTANEOUS_ACTIVE_POWER_EXPORT_L1),
                getResultList.get(INDEX_INSTANTANEOUS_ACTIVE_POWER_EXPORT_L1_SCALER_UNIT),
                "Instantaneous active power export L1");
        final DlmsMeterValueDto instantaneousActivePowerExportL2 = this.dlmsHelper.getScaledMeterValue(
                getResultList.get(INDEX_INSTANTANEOUS_ACTIVE_POWER_EXPORT_L2),
                getResultList.get(INDEX_INSTANTANEOUS_ACTIVE_POWER_EXPORT_L2_SCALER_UNIT),
                "Instantaneous active power export L2");
        final DlmsMeterValueDto instantaneousActivePowerExportL3 = this.dlmsHelper.getScaledMeterValue(
                getResultList.get(INDEX_INSTANTANEOUS_ACTIVE_POWER_EXPORT_L3),
                getResultList.get(INDEX_INSTANTANEOUS_ACTIVE_POWER_EXPORT_L3_SCALER_UNIT),
                "Instantaneous active power export L3");
        final DlmsMeterValueDto averageCurrentL1 = this.dlmsHelper.getScaledMeterValue(
                getResultList.get(INDEX_AVERAGE_CURRENT_L1), getResultList.get(INDEX_AVERAGE_CURRENT_L1_SCALER_UNIT),
                "Average current L1");
        final DlmsMeterValueDto averageCurrentL2 = this.dlmsHelper.getScaledMeterValue(
                getResultList.get(INDEX_AVERAGE_CURRENT_L2), getResultList.get(INDEX_AVERAGE_CURRENT_L2_SCALER_UNIT),
                "Average current L2");
        final DlmsMeterValueDto averageCurrentL3 = this.dlmsHelper.getScaledMeterValue(
                getResultList.get(INDEX_AVERAGE_CURRENT_L3), getResultList.get(INDEX_AVERAGE_CURRENT_L3_SCALER_UNIT),
                "Average current L3");
        final DlmsMeterValueDto averageActivePowerImportL1 = this.dlmsHelper.getScaledMeterValue(
                getResultList.get(INDEX_AVERAGE_ACTIVE_POWER_IMPORT_L1),
                getResultList.get(INDEX_AVERAGE_ACTIVE_POWER_IMPORT_L1_SCALER_UNIT), "Average active power import L1");
        final DlmsMeterValueDto averageActivePowerImportL2 = this.dlmsHelper.getScaledMeterValue(
                getResultList.get(INDEX_AVERAGE_ACTIVE_POWER_IMPORT_L2),
                getResultList.get(INDEX_AVERAGE_ACTIVE_POWER_IMPORT_L2_SCALER_UNIT), "Average active power import L2");
        final DlmsMeterValueDto averageActivePowerImportL3 = this.dlmsHelper.getScaledMeterValue(
                getResultList.get(INDEX_AVERAGE_ACTIVE_POWER_IMPORT_L3),
                getResultList.get(INDEX_AVERAGE_ACTIVE_POWER_IMPORT_L3_SCALER_UNIT), "Average active power import L3");
        final DlmsMeterValueDto averageActivePowerExportL1 = this.dlmsHelper.getScaledMeterValue(
                getResultList.get(INDEX_AVERAGE_ACTIVE_POWER_EXPORT_L1),
                getResultList.get(INDEX_AVERAGE_ACTIVE_POWER_EXPORT_L1_SCALER_UNIT), "Average active power export L1");
        final DlmsMeterValueDto averageActivePowerExportL2 = this.dlmsHelper.getScaledMeterValue(
                getResultList.get(INDEX_AVERAGE_ACTIVE_POWER_EXPORT_L2),
                getResultList.get(INDEX_AVERAGE_ACTIVE_POWER_EXPORT_L2_SCALER_UNIT), "Average active power export L2");
        final DlmsMeterValueDto averageActivePowerExportL3 = this.dlmsHelper.getScaledMeterValue(
                getResultList.get(INDEX_AVERAGE_ACTIVE_POWER_EXPORT_L3),
                getResultList.get(INDEX_AVERAGE_ACTIVE_POWER_EXPORT_L3_SCALER_UNIT), "Average active power export L3");
        final DlmsMeterValueDto averageReactivePowerImportL1 = this.dlmsHelper.getScaledMeterValue(
                getResultList.get(INDEX_AVERAGE_REACTIVE_POWER_IMPORT_L1),
                getResultList.get(INDEX_AVERAGE_REACTIVE_POWER_IMPORT_L1_SCALER_UNIT),
                "Average reactive power import L1");
        final DlmsMeterValueDto averageReactivePowerImportL2 = this.dlmsHelper.getScaledMeterValue(
                getResultList.get(INDEX_AVERAGE_REACTIVE_POWER_IMPORT_L2),
                getResultList.get(INDEX_AVERAGE_REACTIVE_POWER_IMPORT_L2_SCALER_UNIT),
                "Average reactive power import L2");
        final DlmsMeterValueDto averageReactivePowerImportL3 = this.dlmsHelper.getScaledMeterValue(
                getResultList.get(INDEX_AVERAGE_REACTIVE_POWER_IMPORT_L3),
                getResultList.get(INDEX_AVERAGE_REACTIVE_POWER_IMPORT_L3_SCALER_UNIT),
                "Average reactive power import L3");
        final DlmsMeterValueDto averageReactivePowerExportL1 = this.dlmsHelper.getScaledMeterValue(
                getResultList.get(INDEX_AVERAGE_REACTIVE_POWER_EXPORT_L1),
                getResultList.get(INDEX_AVERAGE_REACTIVE_POWER_EXPORT_L1_SCALER_UNIT),
                "Average reactive power export L1");
        final DlmsMeterValueDto averageReactivePowerExportL2 = this.dlmsHelper.getScaledMeterValue(
                getResultList.get(INDEX_AVERAGE_REACTIVE_POWER_EXPORT_L2),
                getResultList.get(INDEX_AVERAGE_REACTIVE_POWER_EXPORT_L2_SCALER_UNIT),
                "Average reactive power export L2");
        final DlmsMeterValueDto averageReactivePowerExportL3 = this.dlmsHelper.getScaledMeterValue(
                getResultList.get(INDEX_AVERAGE_REACTIVE_POWER_EXPORT_L3),
                getResultList.get(INDEX_AVERAGE_REACTIVE_POWER_EXPORT_L3_SCALER_UNIT),
                "Average reactive power export L3");
        final DlmsMeterValueDto instantaneousActiveCurrentTotalOverAllPhases = this.dlmsHelper.getScaledMeterValue(
                getResultList.get(INDEX_INSTANTANEOUS_ACTIVE_CURRENT_TOTAL_OVER_ALL_PHASES),
                getResultList.get(INDEX_INSTANTANEOUS_ACTIVE_CURRENT_TOTAL_OVER_ALL_PHASES_SCALER_UNIT),
                "Instantaneous active current total over all phases");

        return new ActualPowerQualityPrivateResponseDto(time.toDate(), instantaneousCurrentL1, instantaneousCurrentL2,
                instantaneousCurrentL3, instantaneousActivePowerImport, instantaneousActivePowerExport,
                instantaneousActivePowerImportL1, instantaneousActivePowerImportL2, instantaneousActivePowerImportL3,
                instantaneousActivePowerExportL1, instantaneousActivePowerExportL2, instantaneousActivePowerExportL3,
                averageCurrentL1, averageCurrentL2, averageCurrentL3, averageActivePowerImportL1,
                averageActivePowerImportL2, averageActivePowerImportL3, averageActivePowerExportL1,
                averageActivePowerExportL2, averageActivePowerExportL3, averageReactivePowerImportL1,
                averageReactivePowerImportL2, averageReactivePowerImportL3, averageReactivePowerExportL1,
                averageReactivePowerExportL2, averageReactivePowerExportL3,
                instantaneousActiveCurrentTotalOverAllPhases);
    }

}

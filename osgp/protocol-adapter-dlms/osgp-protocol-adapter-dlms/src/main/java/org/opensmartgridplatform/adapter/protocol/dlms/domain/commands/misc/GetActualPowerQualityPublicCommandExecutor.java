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
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ActualPowerQualityRequestDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ActualPowerQualityPublicResponseDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.CosemDateTimeDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.DlmsMeterValueDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component()
public class GetActualPowerQualityPublicCommandExecutor
        extends AbstractCommandExecutor<ActualPowerQualityRequestDto, ActualPowerQualityPublicResponseDto> {

    private static final Logger LOGGER = LoggerFactory.getLogger(GetActualPowerQualityPublicCommandExecutor.class);

    private static final int CLASS_ID_REGISTER = 3;
    private static final ObisCode OBIS_CODE_INSTANTANEOUS_VOLTAGE_L1 = new ObisCode("1.0.32.7.0.255");
    private static final ObisCode OBIS_CODE_INSTANTANEOUS_VOLTAGE_L2 = new ObisCode("1.0.52.7.0.255");
    private static final ObisCode OBIS_CODE_INSTANTANEOUS_VOLTAGE_L3 = new ObisCode("1.0.72.7.0.255");
    private static final ObisCode OBIS_CODE_AVERAGE_VOLTAGE_L1 = new ObisCode("1.0.32.24.0.255");
    private static final ObisCode OBIS_CODE_AVERAGE_VOLTAGE_L2 = new ObisCode("1.0.52.24.0.255");
    private static final ObisCode OBIS_CODE_AVERAGE_VOLTAGE_L3 = new ObisCode("1.0.72.24.0.255");
    private static final ObisCode OBIS_CODE_NUMBER_OF_LONG_POWER_FAILURES = new ObisCode("0.0.96.7.9.255");
    private static final ObisCode OBIS_CODE_NUMBER_OF_POWER_FAILURES = new ObisCode("0.0.96.7.21.255");
    private static final ObisCode OBIS_CODE_NUMBER_OF_VOLTAGE_SAGS_FOR_L1 = new ObisCode("1.0.32.32.0.255");
    private static final ObisCode OBIS_CODE_NUMBER_OF_VOLTAGE_SAGS_FOR_L2 = new ObisCode("1.0.52.32.0.255");
    private static final ObisCode OBIS_CODE_NUMBER_OF_VOLTAGE_SAGS_FOR_L3 = new ObisCode("1.0.72.32.0.255");
    private static final ObisCode OBIS_CODE_NUMBER_OF_VOLTAGE_SWELLS_FOR_L1 = new ObisCode("1.0.32.36.0.255");
    private static final ObisCode OBIS_CODE_NUMBER_OF_VOLTAGE_SWELLS_FOR_L2 = new ObisCode("1.0.52.36.0.255");
    private static final ObisCode OBIS_CODE_NUMBER_OF_VOLTAGE_SWELLS_FOR_L3 = new ObisCode("1.0.72.36.0.255");

    private static final byte ATTRIBUTE_ID_VALUE = 2;
    private static final byte ATTRIBUTE_ID_SCALER_UNIT = 3;

    private static final int CLASS_ID_CLOCK = 8;
    private static final ObisCode OBIS_CODE_CLOCK = new ObisCode("0.0.1.0.0.255");
    private static final byte ATTRIBUTE_ID_TIME = 2;

    // scaler unit attribute address is filled dynamically
    private static final AttributeAddress[] ATTRIBUTE_ADDRESSES = {
        new AttributeAddress(CLASS_ID_CLOCK, OBIS_CODE_CLOCK, ATTRIBUTE_ID_TIME),
        new AttributeAddress(CLASS_ID_REGISTER, OBIS_CODE_INSTANTANEOUS_VOLTAGE_L1, ATTRIBUTE_ID_VALUE),
        new AttributeAddress(CLASS_ID_REGISTER, OBIS_CODE_INSTANTANEOUS_VOLTAGE_L2, ATTRIBUTE_ID_VALUE),
        new AttributeAddress(CLASS_ID_REGISTER, OBIS_CODE_INSTANTANEOUS_VOLTAGE_L3, ATTRIBUTE_ID_VALUE),
        new AttributeAddress(CLASS_ID_REGISTER, OBIS_CODE_AVERAGE_VOLTAGE_L1, ATTRIBUTE_ID_VALUE),
        new AttributeAddress(CLASS_ID_REGISTER, OBIS_CODE_AVERAGE_VOLTAGE_L2, ATTRIBUTE_ID_VALUE),
        new AttributeAddress(CLASS_ID_REGISTER, OBIS_CODE_AVERAGE_VOLTAGE_L3, ATTRIBUTE_ID_VALUE),
        new AttributeAddress(CLASS_ID_REGISTER, OBIS_CODE_NUMBER_OF_LONG_POWER_FAILURES, ATTRIBUTE_ID_VALUE),
        new AttributeAddress(CLASS_ID_REGISTER, OBIS_CODE_NUMBER_OF_POWER_FAILURES, ATTRIBUTE_ID_VALUE),
        new AttributeAddress(CLASS_ID_REGISTER, OBIS_CODE_NUMBER_OF_VOLTAGE_SAGS_FOR_L1, ATTRIBUTE_ID_VALUE),
        new AttributeAddress(CLASS_ID_REGISTER, OBIS_CODE_NUMBER_OF_VOLTAGE_SAGS_FOR_L2, ATTRIBUTE_ID_VALUE),
        new AttributeAddress(CLASS_ID_REGISTER, OBIS_CODE_NUMBER_OF_VOLTAGE_SAGS_FOR_L3, ATTRIBUTE_ID_VALUE),
        new AttributeAddress(CLASS_ID_REGISTER, OBIS_CODE_NUMBER_OF_VOLTAGE_SWELLS_FOR_L1, ATTRIBUTE_ID_VALUE),
        new AttributeAddress(CLASS_ID_REGISTER, OBIS_CODE_NUMBER_OF_VOLTAGE_SWELLS_FOR_L2, ATTRIBUTE_ID_VALUE),
        new AttributeAddress(CLASS_ID_REGISTER, OBIS_CODE_NUMBER_OF_VOLTAGE_SWELLS_FOR_L3, ATTRIBUTE_ID_VALUE),
        new AttributeAddress(CLASS_ID_REGISTER, OBIS_CODE_INSTANTANEOUS_VOLTAGE_L1, ATTRIBUTE_ID_SCALER_UNIT),
        new AttributeAddress(CLASS_ID_REGISTER, OBIS_CODE_INSTANTANEOUS_VOLTAGE_L2, ATTRIBUTE_ID_SCALER_UNIT),
        new AttributeAddress(CLASS_ID_REGISTER, OBIS_CODE_INSTANTANEOUS_VOLTAGE_L3, ATTRIBUTE_ID_SCALER_UNIT),
        new AttributeAddress(CLASS_ID_REGISTER, OBIS_CODE_AVERAGE_VOLTAGE_L1, ATTRIBUTE_ID_SCALER_UNIT),
        new AttributeAddress(CLASS_ID_REGISTER, OBIS_CODE_AVERAGE_VOLTAGE_L2, ATTRIBUTE_ID_SCALER_UNIT),
        new AttributeAddress(CLASS_ID_REGISTER, OBIS_CODE_AVERAGE_VOLTAGE_L3, ATTRIBUTE_ID_SCALER_UNIT),
        new AttributeAddress(CLASS_ID_REGISTER, OBIS_CODE_NUMBER_OF_LONG_POWER_FAILURES, ATTRIBUTE_ID_SCALER_UNIT),
        new AttributeAddress(CLASS_ID_REGISTER, OBIS_CODE_NUMBER_OF_POWER_FAILURES, ATTRIBUTE_ID_SCALER_UNIT),
        new AttributeAddress(CLASS_ID_REGISTER, OBIS_CODE_NUMBER_OF_VOLTAGE_SAGS_FOR_L1, ATTRIBUTE_ID_SCALER_UNIT),
        new AttributeAddress(CLASS_ID_REGISTER, OBIS_CODE_NUMBER_OF_VOLTAGE_SAGS_FOR_L2, ATTRIBUTE_ID_SCALER_UNIT),
        new AttributeAddress(CLASS_ID_REGISTER, OBIS_CODE_NUMBER_OF_VOLTAGE_SAGS_FOR_L3, ATTRIBUTE_ID_SCALER_UNIT),
        new AttributeAddress(CLASS_ID_REGISTER, OBIS_CODE_NUMBER_OF_VOLTAGE_SWELLS_FOR_L1, ATTRIBUTE_ID_SCALER_UNIT),
        new AttributeAddress(CLASS_ID_REGISTER, OBIS_CODE_NUMBER_OF_VOLTAGE_SWELLS_FOR_L2, ATTRIBUTE_ID_SCALER_UNIT),
        new AttributeAddress(CLASS_ID_REGISTER, OBIS_CODE_NUMBER_OF_VOLTAGE_SWELLS_FOR_L3, ATTRIBUTE_ID_SCALER_UNIT)
    };

    private static final int INDEX_TIME = 0;
    private static final int INDEX_INSTANTANEOUS_VOLTAGE_L1 = 1;
    private static final int INDEX_INSTANTANEOUS_VOLTAGE_L2 = 2;
    private static final int INDEX_INSTANTANEOUS_VOLTAGE_L3 = 3;
    private static final int INDEX_AVERAGE_VOLTAGE_L1 = 4;
    private static final int INDEX_AVERAGE_VOLTAGE_L2 = 5;
    private static final int INDEX_AVERAGE_VOLTAGE_L3 = 6;
    private static final int INDEX_NUMBER_OF_LONG_POWER_FAILURES = 7;
    private static final int INDEX_NUMBER_OF_POWER_FAILURES = 8;
    private static final int INDEX_NUMBER_OF_VOLTAGE_SAGS_FOR_L1 = 9;
    private static final int INDEX_NUMBER_OF_VOLTAGE_SAGS_FOR_L2 = 10;
    private static final int INDEX_NUMBER_OF_VOLTAGE_SAGS_FOR_L3 = 11;
    private static final int INDEX_NUMBER_OF_VOLTAGE_SWELLS_FOR_L1 = 12;
    private static final int INDEX_NUMBER_OF_VOLTAGE_SWELLS_FOR_L2 = 13;
    private static final int INDEX_NUMBER_OF_VOLTAGE_SWELLS_FOR_L3 = 14;
    
    private static final int INDEX_INSTANTANEOUS_VOLTAGE_L1_SCALER_UNIT = 15;
    private static final int INDEX_INSTANTANEOUS_VOLTAGE_L2_SCALER_UNIT = 16;
    private static final int INDEX_INSTANTANEOUS_VOLTAGE_L3_SCALER_UNIT = 17;
    private static final int INDEX_AVERAGE_VOLTAGE_L1_SCALER_UNIT = 18;
    private static final int INDEX_AVERAGE_VOLTAGE_L2_SCALER_UNIT = 19;
    private static final int INDEX_AVERAGE_VOLTAGE_L3_SCALER_UNIT = 20;
    private static final int INDEX_NUMBER_OF_LONG_POWER_FAILURES_SCALER_UNIT = 21;
    private static final int INDEX_NUMBER_OF_POWER_FAILURES_SCALER_UNIT = 22;
    private static final int INDEX_NUMBER_OF_VOLTAGE_SAGS_FOR_L1_SCALER_UNIT = 23;
    private static final int INDEX_NUMBER_OF_VOLTAGE_SAGS_FOR_L2_SCALER_UNIT = 24;
    private static final int INDEX_NUMBER_OF_VOLTAGE_SAGS_FOR_L3_SCALER_UNIT = 25;
    private static final int INDEX_NUMBER_OF_VOLTAGE_SWELLS_FOR_L1_SCALER_UNIT = 26;
    private static final int INDEX_NUMBER_OF_VOLTAGE_SWELLS_FOR_L2_SCALER_UNIT = 27;
    private static final int INDEX_NUMBER_OF_VOLTAGE_SWELLS_FOR_L3_SCALER_UNIT = 28;

    @Autowired
    private DlmsHelper dlmsHelper;

    public GetActualPowerQualityPublicCommandExecutor() {
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
    public ActualPowerQualityPublicResponseDto execute(final DlmsConnectionManager conn, final DlmsDevice device,
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
        final DlmsMeterValueDto instantaneousVoltageL1 = this.dlmsHelper.getScaledMeterValue(
                getResultList.get(INDEX_INSTANTANEOUS_VOLTAGE_L1),
                getResultList.get(INDEX_INSTANTANEOUS_VOLTAGE_L1_SCALER_UNIT), "Instantaneous voltage L1");
        final DlmsMeterValueDto instantaneousVoltageL2 = this.dlmsHelper.getScaledMeterValue(
                getResultList.get(INDEX_INSTANTANEOUS_VOLTAGE_L2),
                getResultList.get(INDEX_INSTANTANEOUS_VOLTAGE_L2_SCALER_UNIT), "Instantaneous voltage L2");
        final DlmsMeterValueDto instantaneousVoltageL3 = this.dlmsHelper.getScaledMeterValue(
                getResultList.get(INDEX_INSTANTANEOUS_VOLTAGE_L3),
                getResultList.get(INDEX_INSTANTANEOUS_VOLTAGE_L3_SCALER_UNIT), "Instantaneous voltage L3");
        final DlmsMeterValueDto averageVoltageL1 = this.dlmsHelper.getScaledMeterValue(
                getResultList.get(INDEX_AVERAGE_VOLTAGE_L1), getResultList.get(INDEX_AVERAGE_VOLTAGE_L1_SCALER_UNIT),
                "Average voltage L1");
        final DlmsMeterValueDto averageVoltageL2 = this.dlmsHelper.getScaledMeterValue(
                getResultList.get(INDEX_AVERAGE_VOLTAGE_L2), getResultList.get(INDEX_AVERAGE_VOLTAGE_L2_SCALER_UNIT),
                "Average voltage L2");
        final DlmsMeterValueDto averageVoltageL3 = this.dlmsHelper.getScaledMeterValue(
                getResultList.get(INDEX_AVERAGE_VOLTAGE_L3), getResultList.get(INDEX_AVERAGE_VOLTAGE_L3_SCALER_UNIT),
                "Average voltage L3");
        final DlmsMeterValueDto numberOfLongPowerFailures = this.dlmsHelper.getScaledMeterValue(
                getResultList.get(INDEX_NUMBER_OF_LONG_POWER_FAILURES),
                getResultList.get(INDEX_NUMBER_OF_LONG_POWER_FAILURES_SCALER_UNIT), "Number of long power failures");
        final DlmsMeterValueDto numberOfPowerFailures = this.dlmsHelper.getScaledMeterValue(
                getResultList.get(INDEX_NUMBER_OF_POWER_FAILURES),
                getResultList.get(INDEX_NUMBER_OF_POWER_FAILURES_SCALER_UNIT), "Number of power failures");
        final DlmsMeterValueDto numberOfVoltageSagsForL1 = this.dlmsHelper.getScaledMeterValue(
                getResultList.get(INDEX_NUMBER_OF_VOLTAGE_SAGS_FOR_L1),
                getResultList.get(INDEX_NUMBER_OF_VOLTAGE_SAGS_FOR_L1_SCALER_UNIT), "Number of voltage sags for L1");
        final DlmsMeterValueDto numberOfVoltageSagsForL2 = this.dlmsHelper.getScaledMeterValue(
                getResultList.get(INDEX_NUMBER_OF_VOLTAGE_SAGS_FOR_L2),
                getResultList.get(INDEX_NUMBER_OF_VOLTAGE_SAGS_FOR_L2_SCALER_UNIT), "Number of voltage sags for L2");
        final DlmsMeterValueDto numberOfVoltageSagsForL3 = this.dlmsHelper.getScaledMeterValue(
                getResultList.get(INDEX_NUMBER_OF_VOLTAGE_SAGS_FOR_L3),
                getResultList.get(INDEX_NUMBER_OF_VOLTAGE_SAGS_FOR_L3_SCALER_UNIT), "Number of voltage sags for L3");
        final DlmsMeterValueDto numberOfVoltageSwellsForL1 = this.dlmsHelper.getScaledMeterValue(
                getResultList.get(INDEX_NUMBER_OF_VOLTAGE_SWELLS_FOR_L1),
                getResultList.get(INDEX_NUMBER_OF_VOLTAGE_SWELLS_FOR_L1_SCALER_UNIT),
                "Number of voltage swells for L1");
        final DlmsMeterValueDto numberOfVoltageSwellsForL2 = this.dlmsHelper.getScaledMeterValue(
                getResultList.get(INDEX_NUMBER_OF_VOLTAGE_SWELLS_FOR_L2),
                getResultList.get(INDEX_NUMBER_OF_VOLTAGE_SWELLS_FOR_L2_SCALER_UNIT),
                "Number of voltage swells for L2");
        final DlmsMeterValueDto numberOfVoltageSwellsForL3 = this.dlmsHelper.getScaledMeterValue(
                getResultList.get(INDEX_NUMBER_OF_VOLTAGE_SWELLS_FOR_L3),
                getResultList.get(INDEX_NUMBER_OF_VOLTAGE_SWELLS_FOR_L3_SCALER_UNIT),
                "Number of voltage swells for L3");

        return new ActualPowerQualityPublicResponseDto(time.toDate(), instantaneousVoltageL1, instantaneousVoltageL2, instantaneousVoltageL3,
                        averageVoltageL1, averageVoltageL2, averageVoltageL3, numberOfLongPowerFailures,
                        numberOfPowerFailures, numberOfVoltageSagsForL1, numberOfVoltageSagsForL2,
                        numberOfVoltageSagsForL3, numberOfVoltageSwellsForL1, numberOfVoltageSwellsForL2,
                        numberOfVoltageSwellsForL3);
    }

}

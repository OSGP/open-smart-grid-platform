/**
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.misc;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.openmuc.jdlms.AttributeAddress;
import org.openmuc.jdlms.GetResult;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.AbstractCommandExecutor;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.DlmsHelper;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.JdlmsObjectToStringUtil;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ActualPowerQualityDataDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ActualPowerQualityRequestDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ActualPowerQualityResponseDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ActualValueDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.CaptureObjectDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.CosemDateTimeDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.DlmsMeterValueDto;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class GetActualPowerQualityCommandExecutor
        extends AbstractCommandExecutor<ActualPowerQualityRequestDto, ActualPowerQualityResponseDto> {

    private static final int CLASS_ID_REGISTER = 3;
    private static final int CLASS_ID_CLOCK = 8;
    private static final byte ATTRIBUTE_ID_TIME = 2;
    private static final byte ATTRIBUTE_ID_VALUE = 2;
    private static final byte ATTRIBUTE_ID_SCALER_UNIT = 3;
    private static final String PUBLIC = "PUBLIC";
    private static final String PRIVATE = "PRIVATE";

    private final DlmsHelper dlmsHelper;

    public GetActualPowerQualityCommandExecutor(final DlmsHelper dlmsHelper) {
        super(ActualPowerQualityRequestDto.class);
        this.dlmsHelper = dlmsHelper;
    }

    @Override
    public ActualPowerQualityResponseDto execute(DlmsConnectionManager conn, DlmsDevice device,
            ActualPowerQualityRequestDto actualPowerQualityRequestDto) throws ProtocolAdapterException {

        final Profile profile = this.determineProfileForDevice(actualPowerQualityRequestDto.getProfileType());
        final AttributeAddress[] attributeAddresses = createAttributeAddresses(profile);

        conn.getDlmsMessageListener().setDescription(
                "GetActualPowerQuality retrieve attributes: " + JdlmsObjectToStringUtil
                        .describeAttributes(attributeAddresses));

        log.info("Retrieving actual power quality");
        final List<GetResult> resultList = this.dlmsHelper
                .getAndCheck(conn, device, "retrieve actual power quality", attributeAddresses);

        return makeActualPowerQualityResponseDto(resultList, profile.getLogicalNames());
    }

    private ActualPowerQualityResponseDto makeActualPowerQualityResponseDto(List<GetResult> resultList,
            List<ActualPowerQualityLogicalName> logicalNames) throws ProtocolAdapterException {
        final ActualPowerQualityResponseDto responseDto = new ActualPowerQualityResponseDto();
        final ActualPowerQualityDataDto actualPowerQualityDataDto = makeActualPowerQualityDataDto(resultList,
                logicalNames);
        responseDto.setActualPowerQualityDataDto(actualPowerQualityDataDto);
        return responseDto;
    }

    private ActualPowerQualityDataDto makeActualPowerQualityDataDto(List<GetResult> resultList, List<ActualPowerQualityLogicalName> logicalNames) throws ProtocolAdapterException {

        final List<CaptureObjectDto> captureObjects = new ArrayList<>();
        final List<ActualValueDto> actualValues = new ArrayList<>();

        for (int i=0;i<logicalNames.size();i++) {
            ActualPowerQualityLogicalName logicalName = logicalNames.get(i);
            CaptureObjectDto captureObject;
            ActualValueDto actualValue;
            if (logicalName.getClassId() == CLASS_ID_CLOCK) {
                final GetResult resultTime  = resultList.get(0);
                final CosemDateTimeDto cosemDateTime = this.dlmsHelper
                        .readDateTime(resultTime, "Actual Power Quality - Time");
                captureObject = new CaptureObjectDto(logicalName.getClassId(), logicalName.getObisCode(),
                        logicalName.getAttributeIdValue(),
                        0, null);
                actualValue = new ActualValueDto(cosemDateTime.asDateTime().toDate());
            } else {
                // Clock is te first value, without a scalar unit
                final GetResult resultValue  = resultList.get((i*2)-1);
                final GetResult resultScalar = resultList.get((i*2));

                final DlmsMeterValueDto meterValue = this.dlmsHelper
                        .getScaledMeterValue(resultValue,
                                resultScalar, "Actual Power Quality - " + logicalName.getObisCode());

                final BigDecimal value = meterValue != null?meterValue.getValue():null;
                final String unit = meterValue != null?meterValue.getDlmsUnit().getUnit():null;

                captureObject = new CaptureObjectDto(logicalName.getClassId(), logicalName.getObisCode(),
                        logicalName.getAttributeIdValue(),
                        0, unit);
                actualValue = new ActualValueDto(value);
            }
            captureObjects.add(captureObject);
            actualValues.add(actualValue);

        }

        return new ActualPowerQualityDataDto(captureObjects, actualValues);
    }

    private AttributeAddress[] createAttributeAddresses(Profile profile) {
        List<AttributeAddress> attributeAddresses = new ArrayList<>();
        for (ActualPowerQualityLogicalName logicalName : profile.getLogicalNames()) {
            attributeAddresses.add(new AttributeAddress(logicalName.getClassId(), logicalName.getObisCode(),
                    logicalName.getAttributeIdValue()));
            if (logicalName.getAttributeIdScalarUnit() != null) {
                attributeAddresses.add(new AttributeAddress(logicalName.getClassId(), logicalName.getObisCode(),
                        logicalName.getAttributeIdScalarUnit()));
            }
        }
        return attributeAddresses.toArray(new AttributeAddress[0]);
    }

    private Profile determineProfileForDevice(final String profileType) {

        switch (profileType) {
        case PUBLIC:
            return Profile.PROFILE_PUBLIC;
        case PRIVATE:
            return Profile.PROFILE_PRIVATE;
        default:
            throw new IllegalArgumentException(
                    "ActualPowerQuality: an unknown profileType was requested: " + profileType);
        }
    }

    protected static List<ActualPowerQualityLogicalName> getLogicalNamesPublic() {
        return Arrays.asList(
                ActualPowerQualityLogicalName.CLOCK,
                ActualPowerQualityLogicalName.PUBLIC_INSTANTANEOUS_VOLTAGE_L1,
                ActualPowerQualityLogicalName.PUBLIC_INSTANTANEOUS_VOLTAGE_L2,
                ActualPowerQualityLogicalName.PUBLIC_INSTANTANEOUS_VOLTAGE_L3,
                ActualPowerQualityLogicalName.PUBLIC_AVERAGE_VOLTAGE_L1,
                ActualPowerQualityLogicalName.PUBLIC_AVERAGE_VOLTAGE_L2,
                ActualPowerQualityLogicalName.PUBLIC_AVERAGE_VOLTAGE_L3,
                ActualPowerQualityLogicalName.PUBLIC_NUMBER_OF_LONG_POWER_FAILURES,
                ActualPowerQualityLogicalName.PUBLIC_NUMBER_OF_POWER_FAILURES,
                ActualPowerQualityLogicalName.PUBLIC_NUMBER_OF_VOLTAGE_SAGS_FOR_L1,
                ActualPowerQualityLogicalName.PUBLIC_NUMBER_OF_VOLTAGE_SAGS_FOR_L2,
                ActualPowerQualityLogicalName.PUBLIC_NUMBER_OF_VOLTAGE_SAGS_FOR_L3,
                ActualPowerQualityLogicalName.PUBLIC_NUMBER_OF_VOLTAGE_SWELLS_FOR_L1,
                ActualPowerQualityLogicalName.PUBLIC_NUMBER_OF_VOLTAGE_SWELLS_FOR_L2,
                ActualPowerQualityLogicalName.PUBLIC_NUMBER_OF_VOLTAGE_SWELLS_FOR_L3);
    }

    protected static List<ActualPowerQualityLogicalName> getLogicalNamesPrivate() {
        return Arrays.asList(
                ActualPowerQualityLogicalName.CLOCK,
                ActualPowerQualityLogicalName.PRIVATE_INSTANTANEOUS_CURRENT_L1,
                ActualPowerQualityLogicalName.PRIVATE_INSTANTANEOUS_CURRENT_L2,
                ActualPowerQualityLogicalName.PRIVATE_INSTANTANEOUS_CURRENT_L3,
                ActualPowerQualityLogicalName.PRIVATE_INSTANTANEOUS_ACTIVE_POWER_IMPORT,
                ActualPowerQualityLogicalName.PRIVATE_INSTANTANEOUS_ACTIVE_POWER_EXPORT,
                ActualPowerQualityLogicalName.PRIVATE_INSTANTANEOUS_ACTIVE_POWER_IMPORT_L1,
                ActualPowerQualityLogicalName.PRIVATE_INSTANTANEOUS_ACTIVE_POWER_IMPORT_L2,
                ActualPowerQualityLogicalName.PRIVATE_INSTANTANEOUS_ACTIVE_POWER_IMPORT_L3,
                ActualPowerQualityLogicalName.PRIVATE_INSTANTANEOUS_ACTIVE_POWER_EXPORT_L1,
                ActualPowerQualityLogicalName.PRIVATE_INSTANTANEOUS_ACTIVE_POWER_EXPORT_L2,
                ActualPowerQualityLogicalName.PRIVATE_INSTANTANEOUS_ACTIVE_POWER_EXPORT_L3,
                ActualPowerQualityLogicalName.PRIVATE_AVERAGE_CURRENT_L1,
                ActualPowerQualityLogicalName.PRIVATE_AVERAGE_CURRENT_L2,
                ActualPowerQualityLogicalName.PRIVATE_AVERAGE_CURRENT_L3,
                ActualPowerQualityLogicalName.PRIVATE_AVERAGE_ACTIVE_POWER_IMPORT_L1,
                ActualPowerQualityLogicalName.PRIVATE_AVERAGE_ACTIVE_POWER_IMPORT_L2,
                ActualPowerQualityLogicalName.PRIVATE_AVERAGE_ACTIVE_POWER_IMPORT_L3,
                ActualPowerQualityLogicalName.PRIVATE_AVERAGE_ACTIVE_POWER_EXPORT_L1,
                ActualPowerQualityLogicalName.PRIVATE_AVERAGE_ACTIVE_POWER_EXPORT_L2,
                ActualPowerQualityLogicalName.PRIVATE_AVERAGE_ACTIVE_POWER_EXPORT_L3,
                ActualPowerQualityLogicalName.PRIVATE_AVERAGE_REACTIVE_POWER_IMPORT_L1,
                ActualPowerQualityLogicalName.PRIVATE_AVERAGE_REACTIVE_POWER_IMPORT_L2,
                ActualPowerQualityLogicalName.PRIVATE_AVERAGE_REACTIVE_POWER_IMPORT_L3,
                ActualPowerQualityLogicalName.PRIVATE_AVERAGE_REACTIVE_POWER_EXPORT_L1,
                ActualPowerQualityLogicalName.PRIVATE_AVERAGE_REACTIVE_POWER_EXPORT_L2,
                ActualPowerQualityLogicalName.PRIVATE_AVERAGE_REACTIVE_POWER_EXPORT_L3,
                ActualPowerQualityLogicalName.PRIVATE_INSTANTANEOUS_ACTIVE_CURRENT_TOTAL_OVER_ALL_PHASES);
    }

    @Getter
    protected enum ActualPowerQualityLogicalName {
        CLOCK("0.0.1.0.0.255", CLASS_ID_CLOCK, ATTRIBUTE_ID_TIME, null),
        // PRIVATE
        PRIVATE_INSTANTANEOUS_CURRENT_L1("1.0.31.7.0.255", CLASS_ID_REGISTER, ATTRIBUTE_ID_VALUE, ATTRIBUTE_ID_SCALER_UNIT),
        PRIVATE_INSTANTANEOUS_CURRENT_L2("1.0.51.7.0.255", CLASS_ID_REGISTER, ATTRIBUTE_ID_VALUE, ATTRIBUTE_ID_SCALER_UNIT),
        PRIVATE_INSTANTANEOUS_CURRENT_L3("1.0.71.7.0.255", CLASS_ID_REGISTER, ATTRIBUTE_ID_VALUE, ATTRIBUTE_ID_SCALER_UNIT),
        PRIVATE_INSTANTANEOUS_ACTIVE_POWER_IMPORT("1.0.1.7.0.255", CLASS_ID_REGISTER, ATTRIBUTE_ID_VALUE, ATTRIBUTE_ID_SCALER_UNIT),
        PRIVATE_INSTANTANEOUS_ACTIVE_POWER_EXPORT("1.0.2.7.0.255", CLASS_ID_REGISTER, ATTRIBUTE_ID_VALUE, ATTRIBUTE_ID_SCALER_UNIT),
        PRIVATE_INSTANTANEOUS_ACTIVE_POWER_IMPORT_L1("1.0.21.7.0.255", CLASS_ID_REGISTER, ATTRIBUTE_ID_VALUE, ATTRIBUTE_ID_SCALER_UNIT),
        PRIVATE_INSTANTANEOUS_ACTIVE_POWER_IMPORT_L2("1.0.41.7.0.255", CLASS_ID_REGISTER, ATTRIBUTE_ID_VALUE, ATTRIBUTE_ID_SCALER_UNIT),
        PRIVATE_INSTANTANEOUS_ACTIVE_POWER_IMPORT_L3("1.0.61.7.0.255", CLASS_ID_REGISTER, ATTRIBUTE_ID_VALUE, ATTRIBUTE_ID_SCALER_UNIT),
        PRIVATE_INSTANTANEOUS_ACTIVE_POWER_EXPORT_L1("1.0.22.7.0.255", CLASS_ID_REGISTER, ATTRIBUTE_ID_VALUE, ATTRIBUTE_ID_SCALER_UNIT),
        PRIVATE_INSTANTANEOUS_ACTIVE_POWER_EXPORT_L2("1.0.42.7.0.255", CLASS_ID_REGISTER, ATTRIBUTE_ID_VALUE, ATTRIBUTE_ID_SCALER_UNIT),
        PRIVATE_INSTANTANEOUS_ACTIVE_POWER_EXPORT_L3("1.0.62.7.0.255", CLASS_ID_REGISTER, ATTRIBUTE_ID_VALUE, ATTRIBUTE_ID_SCALER_UNIT),
        PRIVATE_AVERAGE_CURRENT_L1("1.0.31.24.0.255", CLASS_ID_REGISTER, ATTRIBUTE_ID_VALUE, ATTRIBUTE_ID_SCALER_UNIT),
        PRIVATE_AVERAGE_CURRENT_L2("1.0.51.24.0.255", CLASS_ID_REGISTER, ATTRIBUTE_ID_VALUE, ATTRIBUTE_ID_SCALER_UNIT),
        PRIVATE_AVERAGE_CURRENT_L3("1.0.71.24.0.255", CLASS_ID_REGISTER, ATTRIBUTE_ID_VALUE, ATTRIBUTE_ID_SCALER_UNIT),
        PRIVATE_AVERAGE_ACTIVE_POWER_IMPORT_L1("1.0.21.4.0.255", CLASS_ID_REGISTER, ATTRIBUTE_ID_VALUE, ATTRIBUTE_ID_SCALER_UNIT),
        PRIVATE_AVERAGE_ACTIVE_POWER_IMPORT_L2("1.0.41.4.0.255", CLASS_ID_REGISTER, ATTRIBUTE_ID_VALUE, ATTRIBUTE_ID_SCALER_UNIT),
        PRIVATE_AVERAGE_ACTIVE_POWER_IMPORT_L3("1.0.61.4.0.255", CLASS_ID_REGISTER, ATTRIBUTE_ID_VALUE, ATTRIBUTE_ID_SCALER_UNIT),
        PRIVATE_AVERAGE_ACTIVE_POWER_EXPORT_L1("1.0.22.4.0.255", CLASS_ID_REGISTER, ATTRIBUTE_ID_VALUE, ATTRIBUTE_ID_SCALER_UNIT),
        PRIVATE_AVERAGE_ACTIVE_POWER_EXPORT_L2("1.0.42.4.0.255", CLASS_ID_REGISTER, ATTRIBUTE_ID_VALUE, ATTRIBUTE_ID_SCALER_UNIT),
        PRIVATE_AVERAGE_ACTIVE_POWER_EXPORT_L3("1.0.62.4.0.255", CLASS_ID_REGISTER, ATTRIBUTE_ID_VALUE, ATTRIBUTE_ID_SCALER_UNIT),
        PRIVATE_AVERAGE_REACTIVE_POWER_IMPORT_L1("1.0.23.4.0.255", CLASS_ID_REGISTER, ATTRIBUTE_ID_VALUE, ATTRIBUTE_ID_SCALER_UNIT),
        PRIVATE_AVERAGE_REACTIVE_POWER_IMPORT_L2("1.0.43.4.0.255", CLASS_ID_REGISTER, ATTRIBUTE_ID_VALUE, ATTRIBUTE_ID_SCALER_UNIT),
        PRIVATE_AVERAGE_REACTIVE_POWER_IMPORT_L3("1.0.63.4.0.255", CLASS_ID_REGISTER, ATTRIBUTE_ID_VALUE, ATTRIBUTE_ID_SCALER_UNIT),
        PRIVATE_AVERAGE_REACTIVE_POWER_EXPORT_L1("1.0.24.4.0.255", CLASS_ID_REGISTER, ATTRIBUTE_ID_VALUE, ATTRIBUTE_ID_SCALER_UNIT),
        PRIVATE_AVERAGE_REACTIVE_POWER_EXPORT_L2("1.0.44.4.0.255", CLASS_ID_REGISTER, ATTRIBUTE_ID_VALUE, ATTRIBUTE_ID_SCALER_UNIT),
        PRIVATE_AVERAGE_REACTIVE_POWER_EXPORT_L3("1.0.64.4.0.255", CLASS_ID_REGISTER, ATTRIBUTE_ID_VALUE, ATTRIBUTE_ID_SCALER_UNIT),
        PRIVATE_INSTANTANEOUS_ACTIVE_CURRENT_TOTAL_OVER_ALL_PHASES("1.0.90.7.0.255", CLASS_ID_REGISTER, ATTRIBUTE_ID_VALUE, ATTRIBUTE_ID_SCALER_UNIT),

        // PUBLIC
        PUBLIC_INSTANTANEOUS_VOLTAGE_L1("1.0.32.7.0.255", CLASS_ID_REGISTER, ATTRIBUTE_ID_VALUE, ATTRIBUTE_ID_SCALER_UNIT),
        PUBLIC_INSTANTANEOUS_VOLTAGE_L2("1.0.52.7.0.255", CLASS_ID_REGISTER, ATTRIBUTE_ID_VALUE, ATTRIBUTE_ID_SCALER_UNIT),
        PUBLIC_INSTANTANEOUS_VOLTAGE_L3("1.0.72.7.0.255", CLASS_ID_REGISTER, ATTRIBUTE_ID_VALUE, ATTRIBUTE_ID_SCALER_UNIT),
        PUBLIC_AVERAGE_VOLTAGE_L1("1.0.32.24.0.255", CLASS_ID_REGISTER, ATTRIBUTE_ID_VALUE, ATTRIBUTE_ID_SCALER_UNIT),
        PUBLIC_AVERAGE_VOLTAGE_L2("1.0.52.24.0.255", CLASS_ID_REGISTER, ATTRIBUTE_ID_VALUE, ATTRIBUTE_ID_SCALER_UNIT),
        PUBLIC_AVERAGE_VOLTAGE_L3("1.0.72.24.0.255", CLASS_ID_REGISTER, ATTRIBUTE_ID_VALUE, ATTRIBUTE_ID_SCALER_UNIT),
        PUBLIC_NUMBER_OF_LONG_POWER_FAILURES("0.0.96.7.9.255", CLASS_ID_REGISTER, ATTRIBUTE_ID_VALUE, ATTRIBUTE_ID_SCALER_UNIT),
        PUBLIC_NUMBER_OF_POWER_FAILURES("0.0.96.7.21.255", CLASS_ID_REGISTER, ATTRIBUTE_ID_VALUE, ATTRIBUTE_ID_SCALER_UNIT),
        PUBLIC_NUMBER_OF_VOLTAGE_SAGS_FOR_L1("1.0.32.32.0.255", CLASS_ID_REGISTER, ATTRIBUTE_ID_VALUE, ATTRIBUTE_ID_SCALER_UNIT),
        PUBLIC_NUMBER_OF_VOLTAGE_SAGS_FOR_L2("1.0.52.32.0.255", CLASS_ID_REGISTER, ATTRIBUTE_ID_VALUE, ATTRIBUTE_ID_SCALER_UNIT),
        PUBLIC_NUMBER_OF_VOLTAGE_SAGS_FOR_L3("1.0.72.32.0.255", CLASS_ID_REGISTER, ATTRIBUTE_ID_VALUE, ATTRIBUTE_ID_SCALER_UNIT),
        PUBLIC_NUMBER_OF_VOLTAGE_SWELLS_FOR_L1("1.0.32.36.0.255", CLASS_ID_REGISTER, ATTRIBUTE_ID_VALUE, ATTRIBUTE_ID_SCALER_UNIT),
        PUBLIC_NUMBER_OF_VOLTAGE_SWELLS_FOR_L2("1.0.52.36.0.255", CLASS_ID_REGISTER, ATTRIBUTE_ID_VALUE, ATTRIBUTE_ID_SCALER_UNIT),
        PUBLIC_NUMBER_OF_VOLTAGE_SWELLS_FOR_L3("1.0.72.36.0.255", CLASS_ID_REGISTER, ATTRIBUTE_ID_VALUE,
                ATTRIBUTE_ID_SCALER_UNIT);

        private final String obisCode;
        private final int classId;
        private final Byte attributeIdValue;
        private final Byte attributeIdScalarUnit;

        ActualPowerQualityLogicalName(final String obisCode, int classId, Byte attributeIdValue,
                Byte attributeIdScalarUnit) {
            this.obisCode = obisCode;
            this.classId = classId;
            this.attributeIdValue = attributeIdValue;
            this.attributeIdScalarUnit = attributeIdScalarUnit;
        }

    }

    private enum Profile {

        PROFILE_PUBLIC(getLogicalNamesPublic()),
        PROFILE_PRIVATE(getLogicalNamesPrivate());

        private final List<ActualPowerQualityLogicalName> logicalNames;

        Profile(final List<ActualPowerQualityLogicalName> logicalNames) {
            this.logicalNames = logicalNames;
        }

        public List<ActualPowerQualityLogicalName> getLogicalNames() {
            return this.logicalNames;
        }
    }
}

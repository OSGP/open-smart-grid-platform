/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.joda.time.DateTime;
import org.openmuc.jdlms.AttributeAddress;
import org.openmuc.jdlms.GetResult;
import org.openmuc.jdlms.ObisCode;
import org.openmuc.jdlms.SelectiveAccessDescription;
import org.openmuc.jdlms.datatypes.DataObject;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.BufferedDateTimeValidationException;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ActionRequestDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.AmrProfileStatusCodeDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.AmrProfileStatusCodeFlagDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.CosemDateTimeDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.DlmsMeterValueDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.PeriodTypeDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.PeriodicMeterReadsRequestDataDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.PeriodicMeterReadsRequestDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.PeriodicMeterReadsResponseDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.PeriodicMeterReadsResponseItemDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component()
public class GetPeriodicMeterReadsCommandExecutor extends
        AbstractPeriodicMeterReadsCommandExecutor<PeriodicMeterReadsRequestDto, PeriodicMeterReadsResponseDto> {

    private static final Logger LOGGER = LoggerFactory.getLogger(GetPeriodicMeterReadsCommandExecutor.class);

    private static final int CLASS_ID_PROFILE_GENERIC = 7;
    private static final ObisCode OBIS_CODE_INTERVAL_BILLING = new ObisCode("1.0.99.1.0.255");
    private static final ObisCode OBIS_CODE_DAILY_BILLING = new ObisCode("1.0.99.2.0.255");
    private static final ObisCode OBIS_CODE_MONTHLY_BILLING = new ObisCode("0.0.98.1.0.255");
    private static final byte ATTRIBUTE_ID_BUFFER = 2;
    private static final byte ATTRIBUTE_ID_SCALER_UNIT = 3;
    private static final ObisCode OBIS_CODE_INTERVAL_IMPORT_SCALER_UNIT = new ObisCode("1.0.1.8.0.255");
    private static final ObisCode OBIS_CODE_INTERVAL_EXPORT_SCALER_UNIT = new ObisCode("1.0.2.8.0.255");
    private static final ObisCode OBIS_CODE_MONTHLY_DAILY_IMPORT_RATE_1_SCALER_UNIT = new ObisCode("1.0.1.8.1.255");
    private static final ObisCode OBIS_CODE_MONTHLY_DAILY_IMPORT_RATE_2_SCALER_UNIT = new ObisCode("1.0.1.8.2.255");
    private static final ObisCode OBIS_CODE_MONTHLY_DAILY_EXPORT_RATE_1_SCALER_UNIT = new ObisCode("1.0.2.8.1.255");
    private static final ObisCode OBIS_CODE_MONTHLY_DAILY_EXPORT_RATE_2_SCALER_UNIT = new ObisCode("1.0.2.8.2.255");
    private static final int RESULT_INDEX_IMPORT = 1;
    private static final int RESULT_INDEX_IMPORT_2_OR_EXPORT = 2;
    private static final int RESULT_INDEX_EXPORT = 3;
    private static final int RESULT_INDEX_EXPORT_2 = 4;

    private static final int CLASS_ID_REGISTER = 3;

    private static final byte[] OBIS_BYTES_ACTIVE_ENERGY_IMPORT_RATE_1 = new byte[] { 1, 0, 1, 8, 1, (byte) 255 };
    private static final byte[] OBIS_BYTES_ACTIVE_ENERGY_IMPORT_RATE_2 = new byte[] { 1, 0, 1, 8, 2, (byte) 255 };
    private static final byte[] OBIS_BYTES_ACTIVE_ENERGY_EXPORT_RATE_1 = new byte[] { 1, 0, 2, 8, 1, (byte) 255 };
    private static final byte[] OBIS_BYTES_ACTIVE_ENERGY_EXPORT_RATE_2 = new byte[] { 1, 0, 2, 8, 2, (byte) 255 };
    private static final byte ATTRIBUTE_ID_VALUE = 2;

    private static final int ACCESS_SELECTOR_RANGE_DESCRIPTOR = 1;

    private static final int BUFFER_INDEX_CLOCK = 0;
    private static final int BUFFER_INDEX_AMR_STATUS = 1;
    private static final int BUFFER_INDEX_A_POS_RATE_1 = 2;
    private static final int BUFFER_INDEX_A_POS_RATE_2 = 3;
    private static final int BUFFER_INDEX_A_NEG_RATE_1 = 4;
    private static final int BUFFER_INDEX_A_NEG_RATE_2 = 5;
    private static final int BUFFER_INDEX_A_POS = 2;
    private static final int BUFFER_INDEX_A_NEG = 3;

    @Autowired
    private DlmsHelperService dlmsHelperService;

    @Autowired
    private AmrProfileStatusCodeHelperService amrProfileStatusCodeHelperService;

    public GetPeriodicMeterReadsCommandExecutor() {
        super(PeriodicMeterReadsRequestDataDto.class);
    }

    @Override
    public PeriodicMeterReadsRequestDto fromBundleRequestInput(final ActionRequestDto bundleInput)
            throws ProtocolAdapterException {

        this.checkActionRequestType(bundleInput);
        final PeriodicMeterReadsRequestDataDto periodicMeterReadsRequestDataDto = (PeriodicMeterReadsRequestDataDto) bundleInput;

        return new PeriodicMeterReadsRequestDto(periodicMeterReadsRequestDataDto.getPeriodType(),
                periodicMeterReadsRequestDataDto.getBeginDate(), periodicMeterReadsRequestDataDto.getEndDate());
    }

    @Override
    public PeriodicMeterReadsResponseDto execute(final DlmsConnectionManager conn, final DlmsDevice device,
            final PeriodicMeterReadsRequestDto periodicMeterReadsRequest) throws ProtocolAdapterException {

        final PeriodTypeDto periodType = periodicMeterReadsRequest.getPeriodType();
        final DateTime beginDateTime = new DateTime(periodicMeterReadsRequest.getBeginDate());
        final DateTime endDateTime = new DateTime(periodicMeterReadsRequest.getEndDate());

        final AttributeAddress[] profileBufferAndScalerUnit = this.getProfileBufferAndScalerUnit(periodType,
                beginDateTime, endDateTime, device.isSelectiveAccessSupported());

        LOGGER.debug("Retrieving current billing period and profiles for period type: {}, from: {}, to: {}",
                periodType, beginDateTime, endDateTime);

        /*
         * workaround for a problem when using with_list and retrieving a
         * profile buffer, this will be returned erroneously.
         */
        final List<GetResult> getResultList = new ArrayList<>(profileBufferAndScalerUnit.length);
        for (final AttributeAddress address : profileBufferAndScalerUnit) {

            conn.getDlmsMessageListener().setDescription(
                    "GetPeriodicMeterReads " + periodType + " from " + beginDateTime + " until " + endDateTime
                            + ", retrieve attribute: " + JdlmsObjectToStringUtil.describeAttributes(address));

            getResultList.addAll(this.dlmsHelperService.getAndCheck(conn, device, "retrieve periodic meter reads for "
                    + periodType, address));
        }

        final DataObject resultData = this.dlmsHelperService.readDataObject(getResultList.get(0),
                "Periodic E-Meter Reads");
        final List<DataObject> bufferedObjectsList = resultData.getValue();

        final List<PeriodicMeterReadsResponseItemDto> periodicMeterReads = new ArrayList<>();
        for (final DataObject bufferedObject : bufferedObjectsList) {
            final List<DataObject> bufferedObjects = bufferedObject.getValue();
            try {
                periodicMeterReads.add(this.processNextPeriodicMeterReads(periodType, beginDateTime, endDateTime,
                        bufferedObjects, getResultList));
            } catch (final BufferedDateTimeValidationException e) {
                LOGGER.warn(e.getMessage(), e);
            }
        }

        return new PeriodicMeterReadsResponseDto(periodType, periodicMeterReads);
    }

    private PeriodicMeterReadsResponseItemDto processNextPeriodicMeterReads(final PeriodTypeDto periodType,
            final DateTime beginDateTime, final DateTime endDateTime, final List<DataObject> bufferedObjects,
            final List<GetResult> results) throws ProtocolAdapterException, BufferedDateTimeValidationException {

        final CosemDateTimeDto cosemDateTime = this.dlmsHelperService.readDateTime(
                bufferedObjects.get(BUFFER_INDEX_CLOCK), "Clock from " + periodType + " buffer");
        final DateTime bufferedDateTime = cosemDateTime == null ? null : cosemDateTime.asDateTime();

        this.dlmsHelperService.validateBufferedDateTime(bufferedDateTime, cosemDateTime, beginDateTime, endDateTime);

        LOGGER.debug("Processing profile (" + periodType + ") objects captured at: {}", cosemDateTime);

        switch (periodType) {
        case INTERVAL:
            return this.getNextPeriodicMeterReadsForInterval(bufferedObjects, bufferedDateTime, results);
        case DAILY:
            return this.getNextPeriodicMeterReadsForDaily(bufferedObjects, bufferedDateTime, results);
        case MONTHLY:
            return this.getNextPeriodicMeterReadsForMonthly(bufferedObjects, bufferedDateTime, results);
        default:
            throw new AssertionError("Unknown PeriodType: " + periodType);
        }
    }

    private PeriodicMeterReadsResponseItemDto getNextPeriodicMeterReadsForInterval(
            final List<DataObject> bufferedObjects, final DateTime bufferedDateTime, final List<GetResult> results)
            throws ProtocolAdapterException {

        final AmrProfileStatusCodeDto amrProfileStatusCode = this.readAmrProfileStatusCode(bufferedObjects
                .get(BUFFER_INDEX_AMR_STATUS));

        final DlmsMeterValueDto positiveActiveEnergy = this.dlmsHelperService.getScaledMeterValue(
                bufferedObjects.get(BUFFER_INDEX_A_POS), results.get(RESULT_INDEX_IMPORT).getResultData(),
                "positiveActiveEnergy");
        final DlmsMeterValueDto negativeActiveEnergy = this.dlmsHelperService.getScaledMeterValue(
                bufferedObjects.get(BUFFER_INDEX_A_NEG), results.get(RESULT_INDEX_IMPORT_2_OR_EXPORT).getResultData(),
                "negativeActiveEnergy");

        return new PeriodicMeterReadsResponseItemDto(bufferedDateTime.toDate(), positiveActiveEnergy,
                negativeActiveEnergy, amrProfileStatusCode);
    }

    private PeriodicMeterReadsResponseItemDto getNextPeriodicMeterReadsForDaily(final List<DataObject> bufferedObjects,
            final DateTime bufferedDateTime, final List<GetResult> results) throws ProtocolAdapterException {

        final AmrProfileStatusCodeDto amrProfileStatusCode = this.readAmrProfileStatusCode(bufferedObjects
                .get(BUFFER_INDEX_AMR_STATUS));

        final DlmsMeterValueDto positiveActiveEnergyTariff1 = this.dlmsHelperService.getScaledMeterValue(
                bufferedObjects.get(BUFFER_INDEX_A_POS_RATE_1), results.get(RESULT_INDEX_IMPORT).getResultData(),
                "positiveActiveEnergyTariff1");
        final DlmsMeterValueDto positiveActiveEnergyTariff2 = this.dlmsHelperService.getScaledMeterValue(
                bufferedObjects.get(BUFFER_INDEX_A_POS_RATE_2), results.get(RESULT_INDEX_IMPORT_2_OR_EXPORT)
                        .getResultData(), "positiveActiveEnergyTariff2");
        final DlmsMeterValueDto negativeActiveEnergyTariff1 = this.dlmsHelperService.getScaledMeterValue(
                bufferedObjects.get(BUFFER_INDEX_A_NEG_RATE_1), results.get(RESULT_INDEX_EXPORT).getResultData(),
                "negativeActiveEnergyTariff1");
        final DlmsMeterValueDto negativeActiveEnergyTariff2 = this.dlmsHelperService.getScaledMeterValue(
                bufferedObjects.get(BUFFER_INDEX_A_NEG_RATE_2), results.get(RESULT_INDEX_EXPORT_2).getResultData(),
                "negativeActiveEnergyTariff2");

        return new PeriodicMeterReadsResponseItemDto(bufferedDateTime.toDate(), positiveActiveEnergyTariff1,
                positiveActiveEnergyTariff2, negativeActiveEnergyTariff1, negativeActiveEnergyTariff2,
                amrProfileStatusCode);
    }

    /**
     * Reads AmrProfileStatusCode from DataObject holding a bitvalue in a
     * numeric datatype.
     *
     * @param amrProfileStatusData
     *            AMR profile register value.
     * @return AmrProfileStatusCode object holding status enum values.
     * @throws ProtocolAdapterException
     *             on invalid register data.
     */
    private AmrProfileStatusCodeDto readAmrProfileStatusCode(final DataObject amrProfileStatusData)
            throws ProtocolAdapterException {

        if (!amrProfileStatusData.isNumber()) {
            throw new ProtocolAdapterException("Could not read AMR profile register data. Invalid data type.");
        }

        final Set<AmrProfileStatusCodeFlagDto> flags = this.amrProfileStatusCodeHelperService
                .toAmrProfileStatusCodeFlags(amrProfileStatusData.getValue());
        return new AmrProfileStatusCodeDto(flags);
    }

    private PeriodicMeterReadsResponseItemDto getNextPeriodicMeterReadsForMonthly(
            final List<DataObject> bufferedObjects, final DateTime bufferedDateTime, final List<GetResult> results)
            throws ProtocolAdapterException {

        /*
         * Buffer indexes minus one, since Monthly captured objects don't
         * include the AMR Profile status.
         */
        final DlmsMeterValueDto positiveActiveEnergyTariff1 = this.dlmsHelperService.getScaledMeterValue(
                bufferedObjects.get(BUFFER_INDEX_A_POS_RATE_1 - 1), results.get(RESULT_INDEX_IMPORT).getResultData(),
                "positiveActiveEnergyTariff1");
        final DlmsMeterValueDto positiveActiveEnergyTariff2 = this.dlmsHelperService.getScaledMeterValue(
                bufferedObjects.get(BUFFER_INDEX_A_POS_RATE_2 - 1), results.get(RESULT_INDEX_IMPORT_2_OR_EXPORT)
                        .getResultData(), "positiveActiveEnergyTariff2");
        final DlmsMeterValueDto negativeActiveEnergyTariff1 = this.dlmsHelperService.getScaledMeterValue(
                bufferedObjects.get(BUFFER_INDEX_A_NEG_RATE_1 - 1), results.get(RESULT_INDEX_EXPORT).getResultData(),
                "negativeActiveEnergyTariff1");
        final DlmsMeterValueDto negativeActiveEnergyTariff2 = this.dlmsHelperService.getScaledMeterValue(
                bufferedObjects.get(BUFFER_INDEX_A_NEG_RATE_2 - 1), results.get(RESULT_INDEX_EXPORT_2).getResultData(),
                "negativeActiveEnergyTariff2");

        return new PeriodicMeterReadsResponseItemDto(bufferedDateTime.toDate(), positiveActiveEnergyTariff1,
                positiveActiveEnergyTariff2, negativeActiveEnergyTariff1, negativeActiveEnergyTariff2);
    }

    private AttributeAddress[] getProfileBufferAndScalerUnit(final PeriodTypeDto periodType,
            final DateTime beginDateTime, final DateTime endDateTime, final boolean isSelectingValuesSupported)
            throws ProtocolAdapterException {

        final SelectiveAccessDescription access = this.getSelectiveAccessDescription(periodType, beginDateTime,
                endDateTime, isSelectingValuesSupported);

        final List<AttributeAddress> profileBuffer = new ArrayList<>();
        switch (periodType) {
        case INTERVAL:
            profileBuffer.add(new AttributeAddress(CLASS_ID_PROFILE_GENERIC, OBIS_CODE_INTERVAL_BILLING,
                    ATTRIBUTE_ID_BUFFER, access));
            break;
        case DAILY:
            profileBuffer.add(new AttributeAddress(CLASS_ID_PROFILE_GENERIC, OBIS_CODE_DAILY_BILLING,
                    ATTRIBUTE_ID_BUFFER, access));
            break;
        case MONTHLY:
            profileBuffer.add(new AttributeAddress(CLASS_ID_PROFILE_GENERIC, OBIS_CODE_MONTHLY_BILLING,
                    ATTRIBUTE_ID_BUFFER, access));
            break;
        default:
            throw new ProtocolAdapterException(String.format("periodtype %s not supported", periodType));
        }
        profileBuffer.addAll(this.getScalerUnit(periodType));
        return profileBuffer.toArray(new AttributeAddress[0]);
    }

    private List<AttributeAddress> getScalerUnit(final PeriodTypeDto periodType) throws ProtocolAdapterException {
        switch (periodType) {
        case INTERVAL:
            return this.createScalerUnitForInterval();
        case DAILY:
        case MONTHLY:
            return this.createScalerUnitForMonth();
        default:
            throw new ProtocolAdapterException(String.format("periodtype %s not supported", periodType));
        }
    }

    private List<AttributeAddress> createScalerUnitForInterval() {
        final List<AttributeAddress> scalerUnit = new ArrayList<>();
        scalerUnit.add(new AttributeAddress(CLASS_ID_REGISTER, OBIS_CODE_INTERVAL_IMPORT_SCALER_UNIT,
                ATTRIBUTE_ID_SCALER_UNIT));
        scalerUnit.add(new AttributeAddress(CLASS_ID_REGISTER, OBIS_CODE_INTERVAL_EXPORT_SCALER_UNIT,
                ATTRIBUTE_ID_SCALER_UNIT));
        return scalerUnit;
    }

    private List<AttributeAddress> createScalerUnitForMonth() {
        final List<AttributeAddress> scalerUnit = new ArrayList<>();
        scalerUnit.add(new AttributeAddress(CLASS_ID_REGISTER, OBIS_CODE_MONTHLY_DAILY_IMPORT_RATE_1_SCALER_UNIT,
                ATTRIBUTE_ID_SCALER_UNIT));
        scalerUnit.add(new AttributeAddress(CLASS_ID_REGISTER, OBIS_CODE_MONTHLY_DAILY_IMPORT_RATE_2_SCALER_UNIT,
                ATTRIBUTE_ID_SCALER_UNIT));
        scalerUnit.add(new AttributeAddress(CLASS_ID_REGISTER, OBIS_CODE_MONTHLY_DAILY_EXPORT_RATE_1_SCALER_UNIT,
                ATTRIBUTE_ID_SCALER_UNIT));
        scalerUnit.add(new AttributeAddress(CLASS_ID_REGISTER, OBIS_CODE_MONTHLY_DAILY_EXPORT_RATE_2_SCALER_UNIT,
                ATTRIBUTE_ID_SCALER_UNIT));
        return scalerUnit;
    }

    private SelectiveAccessDescription getSelectiveAccessDescription(final PeriodTypeDto periodType,
            final DateTime beginDateTime, final DateTime endDateTime, final boolean isSelectingValuesSupported) {

        /*
         * Define the clock object {8,0-0:1.0.0.255,2,0} to be used as
         * restricting object in a range descriptor with a from value and to
         * value to determine which elements from the buffered array should be
         * retrieved.
         */
        final DataObject clockDefinition = this.dlmsHelperService.getClockDefinition();

        final DataObject fromValue = this.dlmsHelperService.asDataObject(beginDateTime);
        final DataObject toValue = this.dlmsHelperService.asDataObject(endDateTime);

        /*
         * List of object definitions to determine which of the capture objects
         * to retrieve from the buffer.
         */
        final List<DataObject> objectDefinitions = new ArrayList<>();
        if (isSelectingValuesSupported) {
            this.addSelectedValues(periodType, objectDefinitions);
        }
        final DataObject selectedValues = DataObject.newArrayData(objectDefinitions);

        final DataObject accessParameter = DataObject.newStructureData(Arrays.asList(clockDefinition, fromValue,
                toValue, selectedValues));

        return new SelectiveAccessDescription(ACCESS_SELECTOR_RANGE_DESCRIPTOR, accessParameter);
    }

    private void addSelectedValues(final PeriodTypeDto periodType, final List<DataObject> objectDefinitions) {

        switch (periodType) {
        case INTERVAL:
            // empty objectDefinitions is ok, since all values are applicable,
            // hence selective access is not applicable
            break;
        case DAILY:
            this.addSelectedValuesForDaily(objectDefinitions);
            break;
        case MONTHLY:
            this.addSelectedValuesForMonthly(objectDefinitions);
            break;
        default:
            throw new AssertionError("Unknown PeriodType: " + periodType);
        }
    }

    private void addSelectedValuesForDaily(final List<DataObject> objectDefinitions) {
        /*-
         * Available objects in the profile buffer (1-0:99.2.0.255):
         * {8,0-0:1.0.0.255,2,0}    -  clock
         * {1,0-0:96.10.2.255,2,0}  -  AMR profile status
         * {3,1-0:1.8.1.255,2,0}    -  Active energy import (+A) rate 1
         * {3,1-0:1.8.2.255,2,0}    -  Active energy import (+A) rate 2
         * {3,1-0:2.8.1.255,2,0}    -  Active energy export (-A) rate 1
         * {3,1-0:2.8.2.255,2,0}    -  Active energy export (-A) rate 2
         *
         * Objects not retrieved with E meter readings:
         * {4,0-1.24.2.1.255,2,0}  -  M-Bus Master Value 1 Channel 1
         * {4,0-1.24.2.1.255,5,0}  -  M-Bus Master Value 1 Channel 1 Capture time
         * {4,0-2.24.2.1.255,2,0}  -  M-Bus Master Value 1 Channel 2
         * {4,0-2.24.2.1.255,5,0}  -  M-Bus Master Value 1 Channel 2 Capture time
         * {4,0-3.24.2.1.255,2,0}  -  M-Bus Master Value 1 Channel 3
         * {4,0-3.24.2.1.255,5,0}  -  M-Bus Master Value 1 Channel 3 Capture time
         * {4,0-4.24.2.1.255,2,0}  -  M-Bus Master Value 1 Channel 4
         * {4,0-4.24.2.1.255,5,0}  -  M-Bus Master Value 1 Channel 4 Capture time
         */

        objectDefinitions.add(this.dlmsHelperService.getClockDefinition());

        objectDefinitions.add(this.dlmsHelperService.getAMRProfileDefinition());

        this.addActiveEnergyImportRate1(objectDefinitions);
        this.addActiveEnergyImportRate2(objectDefinitions);

        this.addActiveEnergyExportRate1(objectDefinitions);
        this.addActiveEnergyExportRate2(objectDefinitions);
    }

    private void addSelectedValuesForMonthly(final List<DataObject> objectDefinitions) {
        /*-
         * Available objects in the profile buffer (0-0:98.1.0.255):
         * {8,0-0:1.0.0.255,2,0}    -  clock
         * {3,1-0:1.8.1.255,2,0}    -  Active energy import (+A) rate 1
         * {3,1-0:1.8.2.255,2,0}    -  Active energy import (+A) rate 2
         * {3,1-0:2.8.1.255,2,0}    -  Active energy export (-A) rate 1
         * {3,1-0:2.8.2.255,2,0}    -  Active energy export (-A) rate 2
         *
         * Objects not retrieved with E meter readings:
         * {4,0-1.24.2.1.255,2,0}  -  M-Bus Master Value 1 Channel 1
         * {4,0-1.24.2.1.255,5,0}  -  M-Bus Master Value 1 Channel 1 Capture time
         * {4,0-2.24.2.1.255,2,0}  -  M-Bus Master Value 1 Channel 2
         * {4,0-2.24.2.1.255,5,0}  -  M-Bus Master Value 1 Channel 2 Capture time
         * {4,0-3.24.2.1.255,2,0}  -  M-Bus Master Value 1 Channel 3
         * {4,0-3.24.2.1.255,5,0}  -  M-Bus Master Value 1 Channel 3 Capture time
         * {4,0-4.24.2.1.255,2,0}  -  M-Bus Master Value 1 Channel 4
         * {4,0-4.24.2.1.255,5,0}  -  M-Bus Master Value 1 Channel 4 Capture time
         */

        objectDefinitions.add(this.dlmsHelperService.getClockDefinition());

        this.addActiveEnergyImportRate1(objectDefinitions);
        this.addActiveEnergyImportRate2(objectDefinitions);

        this.addActiveEnergyExportRate1(objectDefinitions);
        this.addActiveEnergyExportRate2(objectDefinitions);
    }

    private void addActiveEnergyImportRate1(final List<DataObject> objectDefinitions) {
        // {3,1-0:1.8.1.255,2,0} - Active energy import (+A) rate 1
        objectDefinitions.add(DataObject.newStructureData(Arrays.asList(
                DataObject.newUInteger16Data(CLASS_ID_REGISTER),
                DataObject.newOctetStringData(OBIS_BYTES_ACTIVE_ENERGY_IMPORT_RATE_1),
                DataObject.newInteger8Data(ATTRIBUTE_ID_VALUE), DataObject.newUInteger16Data(0))));
    }

    private void addActiveEnergyImportRate2(final List<DataObject> objectDefinitions) {
        // {3,1-0:1.8.2.255,2,0} - Active energy import (+A) rate 2
        objectDefinitions.add(DataObject.newStructureData(Arrays.asList(
                DataObject.newUInteger16Data(CLASS_ID_REGISTER),
                DataObject.newOctetStringData(OBIS_BYTES_ACTIVE_ENERGY_IMPORT_RATE_2),
                DataObject.newInteger8Data(ATTRIBUTE_ID_VALUE), DataObject.newUInteger16Data(0))));
    }

    private void addActiveEnergyExportRate1(final List<DataObject> objectDefinitions) {
        // {3,1-0:2.8.1.255,2,0} - Active energy export (-A) rate 1
        objectDefinitions.add(DataObject.newStructureData(Arrays.asList(
                DataObject.newUInteger16Data(CLASS_ID_REGISTER),
                DataObject.newOctetStringData(OBIS_BYTES_ACTIVE_ENERGY_EXPORT_RATE_1),
                DataObject.newInteger8Data(ATTRIBUTE_ID_VALUE), DataObject.newUInteger16Data(0))));
    }

    private void addActiveEnergyExportRate2(final List<DataObject> objectDefinitions) {
        // {3,1-0:2.8.2.255,2,0} - Active energy export (-A) rate 2
        objectDefinitions.add(DataObject.newStructureData(Arrays.asList(
                DataObject.newUInteger16Data(CLASS_ID_REGISTER),
                DataObject.newOctetStringData(OBIS_BYTES_ACTIVE_ENERGY_EXPORT_RATE_2),
                DataObject.newInteger8Data(ATTRIBUTE_ID_VALUE), DataObject.newUInteger16Data(0))));
    }
}

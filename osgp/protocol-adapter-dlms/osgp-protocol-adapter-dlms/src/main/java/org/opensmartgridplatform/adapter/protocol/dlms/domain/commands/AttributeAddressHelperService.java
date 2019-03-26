/**
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.joda.time.DateTime;
import org.openmuc.jdlms.AttributeAddress;
import org.openmuc.jdlms.ObisCode;
import org.openmuc.jdlms.SelectiveAccessDescription;
import org.openmuc.jdlms.datatypes.DataObject;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.PeriodTypeDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service(value = "attributeAddressHelperService")
public class AttributeAddressHelperService {

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

    private static final int CLASS_ID_REGISTER = 3;

    private static final byte[] OBIS_BYTES_ACTIVE_ENERGY_IMPORT_RATE_1 = new byte[] { 1, 0, 1, 8, 1, (byte) 255 };
    private static final byte[] OBIS_BYTES_ACTIVE_ENERGY_IMPORT_RATE_2 = new byte[] { 1, 0, 1, 8, 2, (byte) 255 };
    private static final byte[] OBIS_BYTES_ACTIVE_ENERGY_EXPORT_RATE_1 = new byte[] { 1, 0, 2, 8, 1, (byte) 255 };
    private static final byte[] OBIS_BYTES_ACTIVE_ENERGY_EXPORT_RATE_2 = new byte[] { 1, 0, 2, 8, 2, (byte) 255 };
    private static final byte ATTRIBUTE_ID_VALUE = 2;

    private static final int ACCESS_SELECTOR_RANGE_DESCRIPTOR = 1;

    private final DlmsHelperService dlmsHelperService;

    @Autowired
    public AttributeAddressHelperService(final DlmsHelperService dlmsHelperService) {
        this.dlmsHelperService = dlmsHelperService;
    }

    public AttributeAddress[] getProfileBufferAndScalerUnitForPeriodicMeterReads(final PeriodTypeDto periodType,
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
        return profileBuffer.toArray(new AttributeAddress[profileBuffer.size()]);
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
         * List of object definitions to determine which of the capture objects
         * to retrieve from the buffer.
         */
        final List<DataObject> objectDefinitions = new ArrayList<>();
        if (isSelectingValuesSupported) {
            this.addSelectedValues(periodType, objectDefinitions);
        }
        final DataObject selectedValues = DataObject.newArrayData(objectDefinitions);

        final DataObject accessParameter = this.dlmsHelperService.getAccessSelectionTimeRangeParameter(beginDateTime,
                endDateTime, selectedValues);

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
         * DSMR4 Objects not retrieved with E meter readings:
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
         * DSMR4 Objects not retrieved with E meter readings:
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

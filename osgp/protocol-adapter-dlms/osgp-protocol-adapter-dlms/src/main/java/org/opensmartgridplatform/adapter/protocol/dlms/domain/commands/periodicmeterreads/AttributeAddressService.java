/**
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.periodicmeterreads;

import static org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.periodicmeterreads.PeriodicMeterReadsConstants.ACCESS_SELECTOR_RANGE_DESCRIPTOR;
import static org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.periodicmeterreads.PeriodicMeterReadsConstants.ATTRIBUTE_DAILY_OR_MONTHLY_EXPORT_RATE_1_SCALER_UNIT;
import static org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.periodicmeterreads.PeriodicMeterReadsConstants.ATTRIBUTE_DAILY_OR_MONTHLY_EXPORT_RATE_2_SCALER_UNIT;
import static org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.periodicmeterreads.PeriodicMeterReadsConstants.ATTRIBUTE_DAILY_OR_MONTHLY_IMPORT_RATE_1_SCALER_UNIT;
import static org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.periodicmeterreads.PeriodicMeterReadsConstants.ATTRIBUTE_DAILY_OR_MONTHLY_IMPORT_RATE_2_SCALER_UNIT;
import static org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.periodicmeterreads.PeriodicMeterReadsConstants.ATTRIBUTE_ID_BUFFER;
import static org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.periodicmeterreads.PeriodicMeterReadsConstants.ATTRIBUTE_ID_VALUE;
import static org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.periodicmeterreads.PeriodicMeterReadsConstants.ATTRIBUTE_INTERVAL_EXPORT_SCALER_UNIT;
import static org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.periodicmeterreads.PeriodicMeterReadsConstants.ATTRIBUTE_INTERVAL_IMPORT_SCALER_UNIT;
import static org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.periodicmeterreads.PeriodicMeterReadsConstants.CLASS_ID_PROFILE_GENERIC;
import static org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.periodicmeterreads.PeriodicMeterReadsConstants.CLASS_ID_REGISTER;
import static org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.periodicmeterreads.PeriodicMeterReadsConstants.OBIS_BYTES_ACTIVE_ENERGY_EXPORT_RATE_1;
import static org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.periodicmeterreads.PeriodicMeterReadsConstants.OBIS_BYTES_ACTIVE_ENERGY_EXPORT_RATE_2;
import static org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.periodicmeterreads.PeriodicMeterReadsConstants.OBIS_BYTES_ACTIVE_ENERGY_IMPORT_RATE_1;
import static org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.periodicmeterreads.PeriodicMeterReadsConstants.OBIS_BYTES_ACTIVE_ENERGY_IMPORT_RATE_2;
import static org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.periodicmeterreads.PeriodicMeterReadsConstants.OBIS_CODE_DAILY_BILLING;
import static org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.periodicmeterreads.PeriodicMeterReadsConstants.OBIS_CODE_INTERVAL_BILLING;
import static org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.periodicmeterreads.PeriodicMeterReadsConstants.OBIS_CODE_MONTHLY_BILLING;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.joda.time.DateTime;
import org.openmuc.jdlms.AttributeAddress;
import org.openmuc.jdlms.SelectiveAccessDescription;
import org.openmuc.jdlms.datatypes.DataObject;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.DlmsHelper;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.PeriodTypeDto;

public class AttributeAddressService {

    private final DlmsHelper dlmsHelper = new DlmsHelper();

    public AttributeAddress[] getProfileBufferAndScalerUnitForPeriodicMeterReads(final PeriodTypeDto periodType,
            final DateTime from, final DateTime to, final boolean isSelectingValuesSupported)
            throws ProtocolAdapterException {

        final List<AttributeAddress> profileBuffer = new ArrayList<>();

        profileBuffer.add(this.getProfileBuffer(periodType, from, to, isSelectingValuesSupported));
        profileBuffer.addAll(this.getScalerUnit(periodType));

        return profileBuffer.toArray(new AttributeAddress[0]);
    }

    private AttributeAddress getProfileBuffer(final PeriodTypeDto periodType, final DateTime from, final DateTime to,
            final boolean isSelectingValuesSupported) throws ProtocolAdapterException {
        final SelectiveAccessDescription access = this
                .getSelectiveAccessDescription(periodType, from, to, isSelectingValuesSupported);

        switch (periodType) {
        case INTERVAL:
            return new AttributeAddress(CLASS_ID_PROFILE_GENERIC, OBIS_CODE_INTERVAL_BILLING, ATTRIBUTE_ID_BUFFER,
                    access);
        case DAILY:
            return new AttributeAddress(CLASS_ID_PROFILE_GENERIC, OBIS_CODE_DAILY_BILLING, ATTRIBUTE_ID_BUFFER, access);
        case MONTHLY:
            return new AttributeAddress(CLASS_ID_PROFILE_GENERIC, OBIS_CODE_MONTHLY_BILLING, ATTRIBUTE_ID_BUFFER,
                    access);
        default:
            throw new ProtocolAdapterException(String.format("periodtype %s not supported", periodType));
        }
    }

    private List<AttributeAddress> getScalerUnit(final PeriodTypeDto periodType) throws ProtocolAdapterException {
        switch (periodType) {
        case INTERVAL:
            return this.createScalerUnitForInterval();
        case DAILY:
        case MONTHLY:
            return this.createScalerUnitForDailyOrMonthly();
        default:
            throw new ProtocolAdapterException(String.format("periodtype %s not supported", periodType));
        }
    }

    private List<AttributeAddress> createScalerUnitForInterval() {
        final List<AttributeAddress> scalerUnit = new ArrayList<>();
        scalerUnit.add(ATTRIBUTE_INTERVAL_IMPORT_SCALER_UNIT);
        scalerUnit.add(ATTRIBUTE_INTERVAL_EXPORT_SCALER_UNIT);
        return scalerUnit;
    }

    private List<AttributeAddress> createScalerUnitForDailyOrMonthly() {
        final List<AttributeAddress> scalerUnit = new ArrayList<>();
        scalerUnit.add(ATTRIBUTE_DAILY_OR_MONTHLY_IMPORT_RATE_1_SCALER_UNIT);
        scalerUnit.add(ATTRIBUTE_DAILY_OR_MONTHLY_IMPORT_RATE_2_SCALER_UNIT);
        scalerUnit.add(ATTRIBUTE_DAILY_OR_MONTHLY_EXPORT_RATE_1_SCALER_UNIT);
        scalerUnit.add(ATTRIBUTE_DAILY_OR_MONTHLY_EXPORT_RATE_2_SCALER_UNIT);
        return scalerUnit;
    }

    private SelectiveAccessDescription getSelectiveAccessDescription(final PeriodTypeDto periodType,
            final DateTime from, final DateTime to, final boolean isSelectingValuesSupported) {
        /*
         * List of object definitions to determine which of the capture objects
         * to retrieve from the buffer.
         */
        final List<DataObject> objectDefinitions = new ArrayList<>();
        if (isSelectingValuesSupported) {
            this.addSelectedValues(periodType, objectDefinitions);
        }
        final DataObject selectedValues = DataObject.newArrayData(objectDefinitions);

        final DataObject accessParameter = this.dlmsHelper
                .getAccessSelectionTimeRangeParameter(from, to, selectedValues);

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

        objectDefinitions.add(this.dlmsHelper.getClockDefinition());

        objectDefinitions.add(this.dlmsHelper.getAMRProfileDefinition());

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

        objectDefinitions.add(this.dlmsHelper.getClockDefinition());

        this.addActiveEnergyImportRate1(objectDefinitions);
        this.addActiveEnergyImportRate2(objectDefinitions);

        this.addActiveEnergyExportRate1(objectDefinitions);
        this.addActiveEnergyExportRate2(objectDefinitions);
    }

    private void addActiveEnergyImportRate1(final List<DataObject> objectDefinitions) {
        // {3,1-0:1.8.1.255,2,0} - Active energy import (+A) rate 1
        objectDefinitions.add(DataObject.newStructureData(Arrays.asList(DataObject.newUInteger16Data(CLASS_ID_REGISTER),
                DataObject.newOctetStringData(OBIS_BYTES_ACTIVE_ENERGY_IMPORT_RATE_1),
                DataObject.newInteger8Data(ATTRIBUTE_ID_VALUE), DataObject.newUInteger16Data(0))));
    }

    private void addActiveEnergyImportRate2(final List<DataObject> objectDefinitions) {
        // {3,1-0:1.8.2.255,2,0} - Active energy import (+A) rate 2
        objectDefinitions.add(DataObject.newStructureData(Arrays.asList(DataObject.newUInteger16Data(CLASS_ID_REGISTER),
                DataObject.newOctetStringData(OBIS_BYTES_ACTIVE_ENERGY_IMPORT_RATE_2),
                DataObject.newInteger8Data(ATTRIBUTE_ID_VALUE), DataObject.newUInteger16Data(0))));
    }

    private void addActiveEnergyExportRate1(final List<DataObject> objectDefinitions) {
        // {3,1-0:2.8.1.255,2,0} - Active energy export (-A) rate 1
        objectDefinitions.add(DataObject.newStructureData(Arrays.asList(DataObject.newUInteger16Data(CLASS_ID_REGISTER),
                DataObject.newOctetStringData(OBIS_BYTES_ACTIVE_ENERGY_EXPORT_RATE_1),
                DataObject.newInteger8Data(ATTRIBUTE_ID_VALUE), DataObject.newUInteger16Data(0))));
    }

    private void addActiveEnergyExportRate2(final List<DataObject> objectDefinitions) {
        // {3,1-0:2.8.2.255,2,0} - Active energy export (-A) rate 2
        objectDefinitions.add(DataObject.newStructureData(Arrays.asList(DataObject.newUInteger16Data(CLASS_ID_REGISTER),
                DataObject.newOctetStringData(OBIS_BYTES_ACTIVE_ENERGY_EXPORT_RATE_2),
                DataObject.newInteger8Data(ATTRIBUTE_ID_VALUE), DataObject.newUInteger16Data(0))));
    }
}

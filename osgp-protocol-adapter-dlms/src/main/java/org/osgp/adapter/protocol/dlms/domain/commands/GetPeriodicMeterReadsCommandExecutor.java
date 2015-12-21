/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.osgp.adapter.protocol.dlms.domain.commands;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.openmuc.jdlms.AccessResultCode;
import org.openmuc.jdlms.ClientConnection;
import org.openmuc.jdlms.DataObject;
import org.openmuc.jdlms.GetRequestParameter;
import org.openmuc.jdlms.GetResult;
import org.openmuc.jdlms.ObisCode;
import org.openmuc.jdlms.SelectiveAccessDescription;
import org.osgp.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alliander.osgp.dto.valueobjects.smartmetering.PeriodType;
import com.alliander.osgp.dto.valueobjects.smartmetering.MeterReads;
import com.alliander.osgp.dto.valueobjects.smartmetering.PeriodicMeterReadsContainer;
import com.alliander.osgp.dto.valueobjects.smartmetering.PeriodicMeterReadsQuery;

@Component()
public class GetPeriodicMeterReadsCommandExecutor implements
        CommandExecutor<PeriodicMeterReadsQuery, PeriodicMeterReadsContainer> {

    private static final Logger LOGGER = LoggerFactory.getLogger(GetPeriodicMeterReadsCommandExecutor.class);

    private static final int CLASS_ID_PROFILE_GENERIC = 7;
    private static final ObisCode OBIS_CODE_INTERVAL_BILLING = new ObisCode("1.0.99.1.0.255");
    private static final ObisCode OBIS_CODE_DAILY_BILLING = new ObisCode("1.0.99.2.0.255");
    private static final ObisCode OBIS_CODE_MONTHLY_BILLING = new ObisCode("0.0.98.1.0.255");
    private static final byte ATTRIBUTE_ID_BUFFER = 2;

    private static final int CLASS_ID_CLOCK = 8;
    private static final byte[] OBIS_BYTES_CLOCK = new byte[] { 0, 0, 1, 0, 0, (byte) 255 };
    private static final byte ATTRIBUTE_ID_TIME = 2;

    private static final int CLASS_ID_DATA = 1;
    private static final byte[] OBIS_BYTES_AMR_PROFILE_STATUS = new byte[] { 0, 0, 96, 10, 2, (byte) 255 };

    private static final int CLASS_ID_REGISTER = 3;
    private static final byte[] OBIS_BYTES_ACTIVE_ENERGY_IMPORT = new byte[] { 1, 0, 1, 8, 0, (byte) 255 };
    private static final byte[] OBIS_BYTES_ACTIVE_ENERGY_IMPORT_RATE_1 = new byte[] { 1, 0, 1, 8, 1, (byte) 255 };
    private static final byte[] OBIS_BYTES_ACTIVE_ENERGY_IMPORT_RATE_2 = new byte[] { 1, 0, 1, 8, 2, (byte) 255 };
    private static final byte[] OBIS_BYTES_ACTIVE_ENERGY_EXPORT = new byte[] { 1, 0, 2, 8, 0, (byte) 255 };
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

    @Override
    public PeriodicMeterReadsContainer execute(final ClientConnection conn,
            final PeriodicMeterReadsQuery periodicMeterReadsRequest) throws IOException, ProtocolAdapterException {

        final PeriodType periodType;
        final DateTime beginDateTime;
        final DateTime endDateTime;
        if (periodicMeterReadsRequest != null) {
            periodType = periodicMeterReadsRequest.getPeriodType();
            beginDateTime = new DateTime(periodicMeterReadsRequest.getBeginDate());
            endDateTime = new DateTime(periodicMeterReadsRequest.getEndDate());
        } else {
            throw new IllegalArgumentException(
                    "PeriodicMeterReadsRequestData should contain PeriodType, BeginDate and EndDate.");
        }

        final GetRequestParameter profileBuffer = this.getProfileBuffer(periodType);
        final SelectiveAccessDescription selectiveAccessDescription = this.getSelectiveAccessDescription(periodType,
                beginDateTime, endDateTime);
        profileBuffer.setAccessSelection(selectiveAccessDescription);

        LOGGER.debug(
                "Retrieving current billing period and profiles for class id: {}, obis code: {}, attribute id: {}",
                profileBuffer.classId(), profileBuffer.obisCode(), profileBuffer.attributeId());
        if (selectiveAccessDescription != null) {
            LOGGER.debug("Selective access: selector=" + selectiveAccessDescription.accessSelector() + ", parameter="
                    + this.dlmsHelperService.getDebugInfo(selectiveAccessDescription.accessParameter()));
        }

        final List<GetResult> getResultList = conn.get(profileBuffer);

        checkResultList(getResultList);

        final List<MeterReads> periodicMeterReads = new ArrayList<>();

        final GetResult getResult = getResultList.get(0);
        final AccessResultCode resultCode = getResult.resultCode();
        LOGGER.debug("AccessResultCode: {}({})", resultCode.name(), resultCode.value());
        final DataObject resultData = getResult.resultData();
        LOGGER.debug(this.dlmsHelperService.getDebugInfo(resultData));
        final List<DataObject> bufferedObjectsList = resultData.value();

        for (final DataObject bufferedObject : bufferedObjectsList) {
            final List<DataObject> bufferedObjects = bufferedObject.value();
            this.processNextPeriodicMeterReads(periodType, beginDateTime, endDateTime, periodicMeterReads,
                    bufferedObjects);
        }

        return new PeriodicMeterReadsContainer(periodType, periodicMeterReads);
    }

    private void processNextPeriodicMeterReads(final PeriodType periodType, final DateTime beginDateTime,
            final DateTime endDateTime, final List<MeterReads> periodicMeterReads,
            final List<DataObject> bufferedObjects) {

        final DataObject clock = bufferedObjects.get(BUFFER_INDEX_CLOCK);
        final DateTime bufferedDateTime = this.dlmsHelperService.fromDateTimeValue((byte[]) clock.value());
        if (bufferedDateTime.isBefore(beginDateTime) || bufferedDateTime.isAfter(endDateTime)) {
            final DateTimeFormatter dtf = ISODateTimeFormat.dateTime();
            LOGGER.warn("Not using an object from capture buffer (clock=" + dtf.print(bufferedDateTime)
                    + "), because the date does not match the given period: [" + dtf.print(beginDateTime) + " .. "
                    + dtf.print(endDateTime) + "].");
            return;
        }

        LOGGER.debug("Processing profile (" + periodType + ") objects captured at: {}",
                this.dlmsHelperService.getDebugInfo(clock));

        switch (periodType) {
        case INTERVAL:
            this.processNextPeriodicMeterReadsForInterval(periodicMeterReads, bufferedObjects, bufferedDateTime);
            break;
        case DAILY:
            this.processNextPeriodicMeterReadsForDaily(periodicMeterReads, bufferedObjects, bufferedDateTime);
            break;
        case MONTHLY:
            this.processNextPeriodicMeterReadsForMonthly(periodicMeterReads, bufferedObjects, bufferedDateTime);
            break;
        default:
            throw new AssertionError("Unknown PeriodType: " + periodType);
        }
    }

    private void processNextPeriodicMeterReadsForInterval(final List<MeterReads> periodicMeterReads,
            final List<DataObject> bufferedObjects, final DateTime bufferedDateTime) {

        final DataObject amrStatus = bufferedObjects.get(BUFFER_INDEX_AMR_STATUS);
        LOGGER.warn("TODO - handle amrStatus ({})", this.dlmsHelperService.getDebugInfo(amrStatus));

        final DataObject positiveActiveEnergy = bufferedObjects.get(BUFFER_INDEX_A_POS);
        LOGGER.debug("positiveActiveEnergy: {}", this.dlmsHelperService.getDebugInfo(positiveActiveEnergy));

        final DataObject negativeActiveEnergy = bufferedObjects.get(BUFFER_INDEX_A_NEG);
        LOGGER.debug("negativeActiveEnergy: {}", this.dlmsHelperService.getDebugInfo(negativeActiveEnergy));

        final MeterReads nextMeterReads = new MeterReads(bufferedDateTime.toDate(),
                (Long) positiveActiveEnergy.value(), null, (Long) negativeActiveEnergy.value(), null);
        periodicMeterReads.add(nextMeterReads);
    }

    private void processNextPeriodicMeterReadsForDaily(final List<MeterReads> periodicMeterReads,
            final List<DataObject> bufferedObjects, final DateTime bufferedDateTime) {

        final DataObject amrStatus = bufferedObjects.get(BUFFER_INDEX_AMR_STATUS);
        LOGGER.warn("TODO - handle amrStatus ({})", this.dlmsHelperService.getDebugInfo(amrStatus));

        final DataObject positiveActiveEnergyTariff1 = bufferedObjects.get(BUFFER_INDEX_A_POS_RATE_1);
        LOGGER.debug("positiveActiveEnergyTariff1: {}",
                this.dlmsHelperService.getDebugInfo(positiveActiveEnergyTariff1));
        final DataObject positiveActiveEnergyTariff2 = bufferedObjects.get(BUFFER_INDEX_A_POS_RATE_2);
        LOGGER.debug("positiveActiveEnergyTariff2: {}",
                this.dlmsHelperService.getDebugInfo(positiveActiveEnergyTariff2));
        final DataObject negativeActiveEnergyTariff1 = bufferedObjects.get(BUFFER_INDEX_A_NEG_RATE_1);
        LOGGER.debug("negativeActiveEnergyTariff1: {}",
                this.dlmsHelperService.getDebugInfo(negativeActiveEnergyTariff1));
        final DataObject negativeActiveEnergyTariff2 = bufferedObjects.get(BUFFER_INDEX_A_NEG_RATE_2);
        LOGGER.debug("negativeActiveEnergyTariff2: {}",
                this.dlmsHelperService.getDebugInfo(negativeActiveEnergyTariff2));

        final MeterReads nextMeterReads = new MeterReads(bufferedDateTime.toDate(),
                (Long) positiveActiveEnergyTariff1.value(), (Long) positiveActiveEnergyTariff2.value(),
                (Long) negativeActiveEnergyTariff1.value(), (Long) negativeActiveEnergyTariff2.value());
        periodicMeterReads.add(nextMeterReads);
    }

    private void processNextPeriodicMeterReadsForMonthly(final List<MeterReads> periodicMeterReads,
            final List<DataObject> bufferedObjects, final DateTime bufferedDateTime) {

        /*
         * Buffer indexes minus one, since Monthly captured objects don't
         * include the AMR Profile status.
         */
        final DataObject positiveActiveEnergyTariff1 = bufferedObjects.get(BUFFER_INDEX_A_POS_RATE_1 - 1);
        LOGGER.debug("positiveActiveEnergyTariff1: {}",
                this.dlmsHelperService.getDebugInfo(positiveActiveEnergyTariff1));
        final DataObject positiveActiveEnergyTariff2 = bufferedObjects.get(BUFFER_INDEX_A_POS_RATE_2 - 1);
        LOGGER.debug("positiveActiveEnergyTariff2: {}",
                this.dlmsHelperService.getDebugInfo(positiveActiveEnergyTariff2));
        final DataObject negativeActiveEnergyTariff1 = bufferedObjects.get(BUFFER_INDEX_A_NEG_RATE_1 - 1);
        LOGGER.debug("negativeActiveEnergyTariff1: {}",
                this.dlmsHelperService.getDebugInfo(negativeActiveEnergyTariff1));
        final DataObject negativeActiveEnergyTariff2 = bufferedObjects.get(BUFFER_INDEX_A_NEG_RATE_2 - 1);
        LOGGER.debug("negativeActiveEnergyTariff2: {}",
                this.dlmsHelperService.getDebugInfo(negativeActiveEnergyTariff2));

        final MeterReads nextMeterReads = new MeterReads(bufferedDateTime.toDate(),
                (Long) positiveActiveEnergyTariff1.value(), (Long) positiveActiveEnergyTariff2.value(),
                (Long) negativeActiveEnergyTariff1.value(), (Long) negativeActiveEnergyTariff2.value());
        periodicMeterReads.add(nextMeterReads);
    }

    private static void checkResultList(final List<GetResult> getResultList) throws ProtocolAdapterException {
        if (getResultList.isEmpty()) {
            throw new ProtocolAdapterException(
                    "No GetResult received while retrieving current billing period and profiles.");
        }

        if (getResultList.size() > 1) {
            LOGGER.info("Expected 1 GetResult while retrieving current billing period and profiles, got "
                    + getResultList.size());
        }
    }

    private GetRequestParameter getProfileBuffer(final PeriodType periodType) throws ProtocolAdapterException {
        GetRequestParameter profileBuffer;

        switch (periodType) {
        case INTERVAL:
            profileBuffer = new GetRequestParameter(CLASS_ID_PROFILE_GENERIC, OBIS_CODE_INTERVAL_BILLING,
                    ATTRIBUTE_ID_BUFFER);
            break;
        case DAILY:
            profileBuffer = new GetRequestParameter(CLASS_ID_PROFILE_GENERIC, OBIS_CODE_DAILY_BILLING,
                    ATTRIBUTE_ID_BUFFER);
            break;
        case MONTHLY:
            profileBuffer = new GetRequestParameter(CLASS_ID_PROFILE_GENERIC, OBIS_CODE_MONTHLY_BILLING,
                    ATTRIBUTE_ID_BUFFER);
            break;
        default:
            throw new ProtocolAdapterException(String.format("periodtype %s not supported", periodType));
        }
        return profileBuffer;
    }

    private SelectiveAccessDescription getSelectiveAccessDescription(final PeriodType periodType,
            final DateTime beginDateTime, final DateTime endDateTime) {

        final int accessSelector = ACCESS_SELECTOR_RANGE_DESCRIPTOR;

        /*
         * Define the clock object {8,0-0:1.0.0.255,2,0} to be used as
         * restricting object in a range descriptor with a from value and to
         * value to determine which elements from the buffered array should be
         * retrieved.
         */
        final DataObject clockDefinition = DataObject.newStructureData(Arrays.asList(
                DataObject.newUInteger16Data(CLASS_ID_CLOCK), DataObject.newOctetStringData(OBIS_BYTES_CLOCK),
                DataObject.newInteger8Data(ATTRIBUTE_ID_TIME), DataObject.newUInteger16Data(0)));

        final DataObject fromValue = this.dlmsHelperService.asDataObject(beginDateTime);
        final DataObject toValue = this.dlmsHelperService.asDataObject(endDateTime);

        /*
         * List of object definitions to determine which of the capture objects
         * to retrieve from the buffer.
         */
        final List<DataObject> objectDefinitions = new ArrayList<>();

        switch (periodType) {
        case INTERVAL:
            this.addSelectedValuesForInterval(objectDefinitions);
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

        /*
         * For properly limiting data retrieved from the meter selectedValues
         * should be something like: DataObject.newArrayData(objectDefinitions);
         */
        LOGGER.warn("TODO - figure out how to set selectedValues to something like: "
                + this.dlmsHelperService.getDebugInfo(DataObject.newArrayData(objectDefinitions)));
        /*
         * As long as specifying a subset of captured objects from the buffer
         * through selectedValues does not work, retrieve all captured objects
         * by setting selectedValues to an empty array.
         */
        final DataObject selectedValues = DataObject.newArrayData(Collections.<DataObject> emptyList());

        final DataObject accessParameter = DataObject.newStructureData(Arrays.asList(clockDefinition, fromValue,
                toValue, selectedValues));

        return new SelectiveAccessDescription(accessSelector, accessParameter);
    }

    private void addSelectedValuesForInterval(final List<DataObject> objectDefinitions) {
        /*-
         * Available objects in the profile buffer (1-0:99.1.0.255):
         * {8,0-0:1.0.0.255,2,0}    -  clock
         * {1,0-0:96.10.2.255,2,0}  -  AMR profile status
         * {3,1-0:1.8.0.255,2,0}    -  Active energy import (+A)
         * {3,1-0:2.8.0.255,2,0}    -  Active energy export (-A)
         */

        /*
         * Do not include {8,0-0:1.0.0.255,2,0} - clock here, since it is
         * already used as restricting object.
         */

        // {1,0-0:96.10.2.255,2,0} - AMR profile status
        objectDefinitions.add(DataObject.newStructureData(Arrays.asList(DataObject.newUInteger16Data(CLASS_ID_DATA),
                DataObject.newOctetStringData(OBIS_BYTES_AMR_PROFILE_STATUS),
                DataObject.newInteger8Data(ATTRIBUTE_ID_VALUE), DataObject.newUInteger16Data(0))));

        // {3,1-0:1.8.0.255,2,0} - Active energy import (+A)
        objectDefinitions.add(DataObject.newStructureData(Arrays.asList(
                DataObject.newUInteger16Data(CLASS_ID_REGISTER),
                DataObject.newOctetStringData(OBIS_BYTES_ACTIVE_ENERGY_IMPORT),
                DataObject.newInteger8Data(ATTRIBUTE_ID_VALUE), DataObject.newUInteger16Data(0))));

        // {3,1-0:2.8.0.255,2,0} - Active energy export (-A)
        objectDefinitions.add(DataObject.newStructureData(Arrays.asList(
                DataObject.newUInteger16Data(CLASS_ID_REGISTER),
                DataObject.newOctetStringData(OBIS_BYTES_ACTIVE_ENERGY_EXPORT),
                DataObject.newInteger8Data(ATTRIBUTE_ID_VALUE), DataObject.newUInteger16Data(0))));
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

        /*
         * Do not include {8,0-0:1.0.0.255,2,0} - clock here, since it is
         * already used as restricting object.
         */

        // {1,0-0:96.10.2.255,2,0} - AMR profile status
        objectDefinitions.add(DataObject.newStructureData(Arrays.asList(DataObject.newUInteger16Data(CLASS_ID_DATA),
                DataObject.newOctetStringData(OBIS_BYTES_AMR_PROFILE_STATUS),
                DataObject.newInteger8Data(ATTRIBUTE_ID_VALUE), DataObject.newUInteger16Data(0))));

        // {3,1-0:1.8.1.255,2,0} - Active energy import (+A) rate 1
        objectDefinitions.add(DataObject.newStructureData(Arrays.asList(
                DataObject.newUInteger16Data(CLASS_ID_REGISTER),
                DataObject.newOctetStringData(OBIS_BYTES_ACTIVE_ENERGY_IMPORT_RATE_1),
                DataObject.newInteger8Data(ATTRIBUTE_ID_VALUE), DataObject.newUInteger16Data(0))));

        // {3,1-0:1.8.2.255,2,0} - Active energy import (+A) rate 2
        objectDefinitions.add(DataObject.newStructureData(Arrays.asList(
                DataObject.newUInteger16Data(CLASS_ID_REGISTER),
                DataObject.newOctetStringData(OBIS_BYTES_ACTIVE_ENERGY_IMPORT_RATE_2),
                DataObject.newInteger8Data(ATTRIBUTE_ID_VALUE), DataObject.newUInteger16Data(0))));

        // {3,1-0:2.8.1.255,2,0} - Active energy export (-A) rate 1
        objectDefinitions.add(DataObject.newStructureData(Arrays.asList(
                DataObject.newUInteger16Data(CLASS_ID_REGISTER),
                DataObject.newOctetStringData(OBIS_BYTES_ACTIVE_ENERGY_EXPORT_RATE_1),
                DataObject.newInteger8Data(ATTRIBUTE_ID_VALUE), DataObject.newUInteger16Data(0))));

        // {3,1-0:2.8.2.255,2,0} - Active energy export (-A) rate 2
        objectDefinitions.add(DataObject.newStructureData(Arrays.asList(
                DataObject.newUInteger16Data(CLASS_ID_REGISTER),
                DataObject.newOctetStringData(OBIS_BYTES_ACTIVE_ENERGY_EXPORT_RATE_2),
                DataObject.newInteger8Data(ATTRIBUTE_ID_VALUE), DataObject.newUInteger16Data(0))));
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

        /*
         * Do not include {8,0-0:1.0.0.255,2,0} - clock here, since it is
         * already used as restricting object.
         */

        // {3,1-0:1.8.1.255,2,0} - Active energy import (+A) rate 1
        objectDefinitions.add(DataObject.newStructureData(Arrays.asList(
                DataObject.newUInteger16Data(CLASS_ID_REGISTER),
                DataObject.newOctetStringData(OBIS_BYTES_ACTIVE_ENERGY_IMPORT_RATE_1),
                DataObject.newInteger8Data(ATTRIBUTE_ID_VALUE), DataObject.newUInteger16Data(0))));

        // {3,1-0:1.8.2.255,2,0} - Active energy import (+A) rate 2
        objectDefinitions.add(DataObject.newStructureData(Arrays.asList(
                DataObject.newUInteger16Data(CLASS_ID_REGISTER),
                DataObject.newOctetStringData(OBIS_BYTES_ACTIVE_ENERGY_IMPORT_RATE_2),
                DataObject.newInteger8Data(ATTRIBUTE_ID_VALUE), DataObject.newUInteger16Data(0))));

        // {3,1-0:2.8.1.255,2,0} - Active energy export (-A) rate 1
        objectDefinitions.add(DataObject.newStructureData(Arrays.asList(
                DataObject.newUInteger16Data(CLASS_ID_REGISTER),
                DataObject.newOctetStringData(OBIS_BYTES_ACTIVE_ENERGY_EXPORT_RATE_1),
                DataObject.newInteger8Data(ATTRIBUTE_ID_VALUE), DataObject.newUInteger16Data(0))));

        // {3,1-0:2.8.2.255,2,0} - Active energy export (-A) rate 2
        objectDefinitions.add(DataObject.newStructureData(Arrays.asList(
                DataObject.newUInteger16Data(CLASS_ID_REGISTER),
                DataObject.newOctetStringData(OBIS_BYTES_ACTIVE_ENERGY_EXPORT_RATE_2),
                DataObject.newInteger8Data(ATTRIBUTE_ID_VALUE), DataObject.newUInteger16Data(0))));
    }
}

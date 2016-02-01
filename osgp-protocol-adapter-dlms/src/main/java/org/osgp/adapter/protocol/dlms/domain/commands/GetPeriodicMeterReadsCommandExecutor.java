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
import java.util.Set;
import java.util.concurrent.TimeoutException;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.openmuc.jdlms.AccessResultCode;
import org.openmuc.jdlms.AttributeAddress;
import org.openmuc.jdlms.GetResult;
import org.openmuc.jdlms.LnClientConnection;
import org.openmuc.jdlms.ObisCode;
import org.openmuc.jdlms.SelectiveAccessDescription;
import org.openmuc.jdlms.datatypes.DataObject;
import org.osgp.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.osgp.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alliander.osgp.dto.valueobjects.smartmetering.AmrProfileStatusCode;
import com.alliander.osgp.dto.valueobjects.smartmetering.AmrProfileStatusCodeFlag;
import com.alliander.osgp.dto.valueobjects.smartmetering.PeriodType;
import com.alliander.osgp.dto.valueobjects.smartmetering.PeriodicMeterReads;
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

    @Autowired
    private AmrProfileStatusCodeHelperService amrProfileStatusCodeHelperService;

    @Override
    public PeriodicMeterReadsContainer execute(final LnClientConnection conn, final DlmsDevice device,
            final PeriodicMeterReadsQuery periodicMeterReadsRequest) throws IOException, TimeoutException,
            ProtocolAdapterException {

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

        final AttributeAddress profileBuffer = this.getProfileBuffer(periodType, beginDateTime, endDateTime);

        LOGGER.debug("Retrieving current billing period and profiles for period type: {}, from: {}, to: {}",
                periodType, beginDateTime, endDateTime);

        final List<GetResult> getResultList = conn.get(profileBuffer);

        checkResultList(getResultList);

        final List<PeriodicMeterReads> periodicMeterReads = new ArrayList<>();

        final GetResult getResult = getResultList.get(0);
        final AccessResultCode resultCode = getResult.resultCode();
        LOGGER.debug("AccessResultCode: {}", resultCode.name());
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
            final DateTime endDateTime, final List<PeriodicMeterReads> periodicMeterReads,
            final List<DataObject> bufferedObjects) throws ProtocolAdapterException {

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

    private void processNextPeriodicMeterReadsForInterval(final List<PeriodicMeterReads> periodicMeterReads,
            final List<DataObject> bufferedObjects, final DateTime bufferedDateTime) throws ProtocolAdapterException {

        final AmrProfileStatusCode amrProfileStatusCode = this.readAmrProfileStatusCode(bufferedObjects
                .get(BUFFER_INDEX_AMR_STATUS));

        final Long positiveActiveEnergy = this.dlmsHelperService.readLongNotNull(
                bufferedObjects.get(BUFFER_INDEX_A_POS), "positiveActiveEnergy");
        final Long negativeActiveEnergy = this.dlmsHelperService.readLongNotNull(
                bufferedObjects.get(BUFFER_INDEX_A_NEG), "negativeActiveEnergy");

        final PeriodicMeterReads nextMeterReads = new PeriodicMeterReads(bufferedDateTime.toDate(),
                positiveActiveEnergy, negativeActiveEnergy, amrProfileStatusCode);
        periodicMeterReads.add(nextMeterReads);
    }

    private void processNextPeriodicMeterReadsForDaily(final List<PeriodicMeterReads> periodicMeterReads,
            final List<DataObject> bufferedObjects, final DateTime bufferedDateTime) throws ProtocolAdapterException {

        final AmrProfileStatusCode amrProfileStatusCode = this.readAmrProfileStatusCode(bufferedObjects
                .get(BUFFER_INDEX_AMR_STATUS));

        final Long positiveActiveEnergyTariff1 = this.dlmsHelperService.readLongNotNull(
                bufferedObjects.get(BUFFER_INDEX_A_POS_RATE_1), "positiveActiveEnergyTariff1");
        final Long positiveActiveEnergyTariff2 = this.dlmsHelperService.readLongNotNull(
                bufferedObjects.get(BUFFER_INDEX_A_POS_RATE_2), "positiveActiveEnergyTariff2");
        final Long negativeActiveEnergyTariff1 = this.dlmsHelperService.readLongNotNull(
                bufferedObjects.get(BUFFER_INDEX_A_NEG_RATE_1), "negativeActiveEnergyTariff1");
        final Long negativeActiveEnergyTariff2 = this.dlmsHelperService.readLongNotNull(
                bufferedObjects.get(BUFFER_INDEX_A_NEG_RATE_2), "negativeActiveEnergyTariff2");

        final PeriodicMeterReads nextMeterReads = new PeriodicMeterReads(bufferedDateTime.toDate(),
                positiveActiveEnergyTariff1, positiveActiveEnergyTariff2, negativeActiveEnergyTariff1,
                negativeActiveEnergyTariff2, amrProfileStatusCode);
        periodicMeterReads.add(nextMeterReads);
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
    private AmrProfileStatusCode readAmrProfileStatusCode(final DataObject amrProfileStatusData)
            throws ProtocolAdapterException {
        AmrProfileStatusCode amrProfileStatusCode = null;

        if (!amrProfileStatusData.isNumber()) {
            throw new ProtocolAdapterException("Could not read AMR profile register data. Invalid data type.");
        }

        final Set<AmrProfileStatusCodeFlag> flags = this.amrProfileStatusCodeHelperService
                .toAmrProfileStatusCodeFlags((Number) amrProfileStatusData.value());
        amrProfileStatusCode = new AmrProfileStatusCode(flags);

        return amrProfileStatusCode;
    }

    private void processNextPeriodicMeterReadsForMonthly(final List<PeriodicMeterReads> periodicMeterReads,
            final List<DataObject> bufferedObjects, final DateTime bufferedDateTime) throws ProtocolAdapterException {

        /*
         * Buffer indexes minus one, since Monthly captured objects don't
         * include the AMR Profile status.
         */
        final Long positiveActiveEnergyTariff1 = this.dlmsHelperService.readLongNotNull(
                bufferedObjects.get(BUFFER_INDEX_A_POS_RATE_1 - 1), "positiveActiveEnergyTariff1");
        final Long positiveActiveEnergyTariff2 = this.dlmsHelperService.readLongNotNull(
                bufferedObjects.get(BUFFER_INDEX_A_POS_RATE_2 - 1), "positiveActiveEnergyTariff2");
        final Long negativeActiveEnergyTariff1 = this.dlmsHelperService.readLongNotNull(
                bufferedObjects.get(BUFFER_INDEX_A_NEG_RATE_1 - 1), "negativeActiveEnergyTariff1");
        final Long negativeActiveEnergyTariff2 = this.dlmsHelperService.readLongNotNull(
                bufferedObjects.get(BUFFER_INDEX_A_NEG_RATE_2 - 1), "negativeActiveEnergyTariff2");

        final PeriodicMeterReads nextMeterReads = new PeriodicMeterReads(bufferedDateTime.toDate(),
                positiveActiveEnergyTariff1, positiveActiveEnergyTariff2, negativeActiveEnergyTariff1,
                negativeActiveEnergyTariff2);
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

    private AttributeAddress getProfileBuffer(final PeriodType periodType, final DateTime beginDateTime,
            final DateTime endDateTime) throws ProtocolAdapterException {

        final SelectiveAccessDescription access = this.getSelectiveAccessDescription(periodType, beginDateTime,
                endDateTime);

        final AttributeAddress profileBuffer;
        switch (periodType) {
        case INTERVAL:
            profileBuffer = new AttributeAddress(CLASS_ID_PROFILE_GENERIC, OBIS_CODE_INTERVAL_BILLING,
                    ATTRIBUTE_ID_BUFFER, access);
            break;
        case DAILY:
            profileBuffer = new AttributeAddress(CLASS_ID_PROFILE_GENERIC, OBIS_CODE_DAILY_BILLING,
                    ATTRIBUTE_ID_BUFFER, access);
            break;
        case MONTHLY:
            profileBuffer = new AttributeAddress(CLASS_ID_PROFILE_GENERIC, OBIS_CODE_MONTHLY_BILLING,
                    ATTRIBUTE_ID_BUFFER, access);
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

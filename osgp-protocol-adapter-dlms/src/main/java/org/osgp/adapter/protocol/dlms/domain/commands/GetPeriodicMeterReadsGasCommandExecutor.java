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
import org.osgp.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alliander.osgp.dto.valueobjects.smartmetering.AmrProfileStatusCode;
import com.alliander.osgp.dto.valueobjects.smartmetering.AmrProfileStatusCodeFlag;
import com.alliander.osgp.dto.valueobjects.smartmetering.PeriodType;
import com.alliander.osgp.dto.valueobjects.smartmetering.PeriodicMeterReadsContainerGas;
import com.alliander.osgp.dto.valueobjects.smartmetering.PeriodicMeterReadsGas;
import com.alliander.osgp.dto.valueobjects.smartmetering.PeriodicMeterReadsQuery;

@Component()
public class GetPeriodicMeterReadsGasCommandExecutor implements
        CommandExecutor<PeriodicMeterReadsQuery, PeriodicMeterReadsContainerGas> {

    private static final Logger LOGGER = LoggerFactory.getLogger(GetPeriodicMeterReadsGasCommandExecutor.class);

    private static final int CLASS_ID_PROFILE_GENERIC = 7;
    private static final ObisCode OBIS_CODE_INTERVAL_MBUS_1 = new ObisCode("0.1.24.3.0.255");
    private static final ObisCode OBIS_CODE_INTERVAL_MBUS_2 = new ObisCode("0.2.24.3.0.255");
    private static final ObisCode OBIS_CODE_INTERVAL_MBUS_3 = new ObisCode("0.3.24.3.0.255");
    private static final ObisCode OBIS_CODE_INTERVAL_MBUS_4 = new ObisCode("0.4.24.3.0.255");
    private static final ObisCode OBIS_CODE_DAILY_BILLING = new ObisCode("1.0.99.2.0.255");
    private static final ObisCode OBIS_CODE_MONTHLY_BILLING = new ObisCode("0.0.98.1.0.255");
    private static final byte ATTRIBUTE_ID_BUFFER = 2;

    private static final int CLASS_ID_CLOCK = 8;
    private static final byte[] OBIS_BYTES_CLOCK = new byte[] { 0, 0, 1, 0, 0, (byte) 255 };
    private static final byte ATTRIBUTE_ID_TIME = 2;

    private static final int ACCESS_SELECTOR_RANGE_DESCRIPTOR = 1;

    private static final int BUFFER_INDEX_CLOCK = 0;
    private static final int BUFFER_INDEX_AMR_STATUS = 1;
    private static final int BUFFER_INDEX_MBUS_VALUE_FIRST = 6;
    private static final int BUFFER_INDEX_MBUS_CAPTURETIME_FIRST = 7;
    private static final int BUFFER_INDEX_MBUS_VALUE_INT = 2;
    private static final int BUFFER_INDEX_MBUS_CAPTURETIME_INT = 3;

    @Autowired
    private DlmsHelperService dlmsHelperService;

    @Autowired
    private AmrProfileStatusCodeHelperService amrProfileStatusCodeHelperService;

    @Override
    public PeriodicMeterReadsContainerGas execute(final LnClientConnection conn,
            final PeriodicMeterReadsQuery periodicMeterReadsQuery) throws IOException, TimeoutException,
            ProtocolAdapterException {

        final PeriodType periodType;
        final DateTime beginDateTime;
        final DateTime endDateTime;
        if (periodicMeterReadsQuery != null) {
            periodType = periodicMeterReadsQuery.getPeriodType();
            beginDateTime = new DateTime(periodicMeterReadsQuery.getBeginDate());
            endDateTime = new DateTime(periodicMeterReadsQuery.getEndDate());
        } else {
            throw new IllegalArgumentException(
                    "PeriodicMeterReadsQuery should contain PeriodType, BeginDate and EndDate.");
        }

        final AttributeAddress profileBuffer = this.getProfileBuffer(periodType, periodicMeterReadsQuery.getChannel(),
                beginDateTime, endDateTime);

        LOGGER.debug("Retrieving current billing period and profiles for gas for period type: {}, from: {}, to: {}",
                periodType, beginDateTime, endDateTime);

        final List<GetResult> getResultList = conn.get(profileBuffer);

        checkResultList(getResultList);

        final List<PeriodicMeterReadsGas> periodicMeterReads = new ArrayList<>();

        final GetResult getResult = getResultList.get(0);
        final AccessResultCode resultCode = getResult.resultCode();
        LOGGER.debug("AccessResultCode: {}({})", resultCode.name());
        final DataObject resultData = getResult.resultData();
        LOGGER.debug(this.dlmsHelperService.getDebugInfo(resultData));
        final List<DataObject> bufferedObjectsList = resultData.value();

        for (final DataObject bufferedObject : bufferedObjectsList) {
            final List<DataObject> bufferedObjects = bufferedObject.value();
            this.processNextPeriodicMeterReads(periodType, beginDateTime, endDateTime, periodicMeterReads,
                    bufferedObjects, periodicMeterReadsQuery.getChannel());
        }

        return new PeriodicMeterReadsContainerGas(periodType, periodicMeterReads);
    }

    private void processNextPeriodicMeterReads(final PeriodType periodType, final DateTime beginDateTime,
            final DateTime endDateTime, final List<PeriodicMeterReadsGas> periodicMeterReads,
            final List<DataObject> bufferedObjects, final int channel) throws ProtocolAdapterException {

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
            this.processNextPeriodicMeterReadsForDaily(periodicMeterReads, bufferedObjects, bufferedDateTime, channel);
            break;
        case MONTHLY:
            this.processNextPeriodicMeterReadsForMonthly(periodicMeterReads, bufferedObjects, bufferedDateTime, channel);
            break;
        default:
            throw new AssertionError("Unknown PeriodType: " + periodType);
        }
    }

    private void processNextPeriodicMeterReadsForInterval(final List<PeriodicMeterReadsGas> periodicMeterReads,
            final List<DataObject> bufferedObjects, final DateTime bufferedDateTime) throws ProtocolAdapterException {

        final AmrProfileStatusCode amrProfileStatusCode = this.readAmrProfileStatusCode(bufferedObjects
                .get(BUFFER_INDEX_AMR_STATUS));

        final DataObject gasValue = bufferedObjects.get(BUFFER_INDEX_MBUS_VALUE_INT);
        LOGGER.debug("gasValue: {}", this.dlmsHelperService.getDebugInfo(gasValue));

        final DataObject gasCaptureTime = bufferedObjects.get(BUFFER_INDEX_MBUS_CAPTURETIME_INT);
        LOGGER.debug("gasCaptureTime: {}", this.dlmsHelperService.getDebugInfo(gasCaptureTime));

        final PeriodicMeterReadsGas nextPeriodicMeterReads = new PeriodicMeterReadsGas(bufferedDateTime.toDate(),
                (Long) gasValue.value(), this.dlmsHelperService.fromDateTimeValue((byte[]) gasCaptureTime.value())
                        .toDate(), amrProfileStatusCode);
        periodicMeterReads.add(nextPeriodicMeterReads);
    }

    private void processNextPeriodicMeterReadsForDaily(final List<PeriodicMeterReadsGas> periodicMeterReads,
            final List<DataObject> bufferedObjects, final DateTime bufferedDateTime, final int channel)
            throws ProtocolAdapterException {

        final AmrProfileStatusCode amrProfileStatusCode = this.readAmrProfileStatusCode(bufferedObjects
                .get(BUFFER_INDEX_AMR_STATUS));

        // calculate offset from first entry
        final int offset = (channel - 1) * 2;

        final DataObject gasValue = bufferedObjects.get(BUFFER_INDEX_MBUS_VALUE_FIRST + offset);
        LOGGER.debug("gasValue: {}", this.dlmsHelperService.getDebugInfo(gasValue));
        final DataObject gasCaptureTime = bufferedObjects.get(BUFFER_INDEX_MBUS_CAPTURETIME_FIRST + offset);
        LOGGER.debug("gasCaptureTime: {}", this.dlmsHelperService.getDebugInfo(gasCaptureTime));

        final PeriodicMeterReadsGas nextPeriodicMeterReads = new PeriodicMeterReadsGas(bufferedDateTime.toDate(),
                (Long) gasValue.value(), this.dlmsHelperService.fromDateTimeValue((byte[]) gasCaptureTime.value())
                        .toDate(), amrProfileStatusCode);
        periodicMeterReads.add(nextPeriodicMeterReads);
    }

    private void processNextPeriodicMeterReadsForMonthly(final List<PeriodicMeterReadsGas> periodicMeterReads,
            final List<DataObject> bufferedObjects, final DateTime bufferedDateTime, final int channel) {

        // calculate offset from first entry, -1 because MONTHLY has no AMR
        // entry
        final int offset = ((channel - 1) * 2) - 1;

        final DataObject gasValue = bufferedObjects.get(BUFFER_INDEX_MBUS_VALUE_FIRST + offset);
        LOGGER.debug("gasValue: {}", this.dlmsHelperService.getDebugInfo(gasValue));
        final DataObject gasCaptureTime = bufferedObjects.get(BUFFER_INDEX_MBUS_CAPTURETIME_FIRST + offset);
        LOGGER.debug("gasCaptureTime: {}", this.dlmsHelperService.getDebugInfo(gasCaptureTime));

        final PeriodicMeterReadsGas nextPeriodicMeterReads = new PeriodicMeterReadsGas(bufferedDateTime.toDate(),
                (Long) gasValue.value(), this.dlmsHelperService.fromDateTimeValue((byte[]) gasCaptureTime.value())
                        .toDate());
        periodicMeterReads.add(nextPeriodicMeterReads);
    }

    private static void checkResultList(final List<GetResult> getResultList) throws ProtocolAdapterException {
        if (getResultList.isEmpty()) {
            throw new ProtocolAdapterException(
                    "No GetResult received while retrieving current billing period and profiles for gas.");
        }

        if (getResultList.size() > 1) {
            LOGGER.info("Expected 1 GetResult while retrieving current billing period and profiles for gas, got "
                    + getResultList.size());
        }
    }

    private ObisCode intervalForChannel(final int channel) throws ProtocolAdapterException {
        switch (channel) {
        case 1:
            return OBIS_CODE_INTERVAL_MBUS_1;
        case 2:
            return OBIS_CODE_INTERVAL_MBUS_2;
        case 3:
            return OBIS_CODE_INTERVAL_MBUS_3;
        case 4:
            return OBIS_CODE_INTERVAL_MBUS_4;
        default:
            throw new ProtocolAdapterException(String.format("channel %s not supported", channel));
        }
    }

    private AttributeAddress getProfileBuffer(final PeriodType periodType, final int channel,
            final DateTime beginDateTime, final DateTime endDateTime) throws ProtocolAdapterException {

        final SelectiveAccessDescription access = this.getSelectiveAccessDescription(periodType, beginDateTime,
                endDateTime);

        final AttributeAddress profileBuffer;
        switch (periodType) {
        case INTERVAL:
            profileBuffer = new AttributeAddress(CLASS_ID_PROFILE_GENERIC, this.intervalForChannel(channel),
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
         * As long as specifying a subset of captured objects from the buffer
         * through selectedValues does not work, retrieve all captured objects
         * by setting selectedValues to an empty array.
         */
        final DataObject selectedValues = DataObject.newArrayData(Collections.<DataObject> emptyList());

        final DataObject accessParameter = DataObject.newStructureData(Arrays.asList(clockDefinition, fromValue,
                toValue, selectedValues));

        return new SelectiveAccessDescription(accessSelector, accessParameter);
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
}

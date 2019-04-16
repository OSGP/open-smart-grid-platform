/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.periodicmeterreads;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.joda.time.DateTime;
import org.openmuc.jdlms.AttributeAddress;
import org.openmuc.jdlms.GetResult;
import org.openmuc.jdlms.datatypes.DataObject;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.AttributeAddressHelper;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.DlmsObjectType;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.Medium;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.AmrProfileStatusCodeHelper;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.DlmsHelper;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.JdlmsObjectToStringUtil;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.Protocol;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.BufferedDateTimeValidationException;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ActionRequestDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.AmrProfileStatusCodeDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.AmrProfileStatusCodeFlagDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ChannelDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.CosemDateTimeDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.PeriodTypeDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.PeriodicMeterReadGasResponseDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.PeriodicMeterReadsGasRequestDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.PeriodicMeterReadsGasResponseItemDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.PeriodicMeterReadsRequestDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component()
public class GetPeriodicMeterReadsGasCommandExecutor extends
        AbstractPeriodicMeterReadsCommandExecutor<PeriodicMeterReadsRequestDto, PeriodicMeterReadGasResponseDto> {

    private static final Logger LOGGER = LoggerFactory.getLogger(GetPeriodicMeterReadsGasCommandExecutor.class);

    private static final int RESULT_INDEX_SCALER_UNIT = 1;

    private static final int BUFFER_INDEX_CLOCK = 0;
    private static final int BUFFER_INDEX_AMR_STATUS = 1;
    private static final int BUFFER_INDEX_MBUS_VALUE_INT = 2;
    private static final int BUFFER_INDEX_MBUS_CAPTURETIME_INT = 3;

    private static final Map<Integer, Integer> INDEX_MONTHLY_SELECTIVE_ACCESS_MBUS_VALUE_MAP = new HashMap<>();
    private static final Map<Integer, Integer> INDEX_MONTHLY_SELECTIVE_ACCESS_MBUS_VALUE_CAPTURE_TIME_MAP =
            new HashMap<>();
    private static final Map<Integer, Integer> INDEX_MONTHLY_MBUS_VALUE_MAP = new HashMap<>();
    private static final Map<Integer, Integer> INDEX_MONTHLY_MBUS_VALUE_CAPTURE_TIME_MAP = new HashMap<>();

    static {
        // Indicates the position of the value or value_capture_time for
        // channel 1, 2, 3 and 4

        /*-
         * When specific capture_objects are selected with selective access:
         *
         * {8,0-0:1.0.0.255,2,0};                                       position 0
         *
         * Value and capture time for one of the four channels will be selected,
         * for which position 1 and 2 are used.
         *
         * {4,0-1:24.2.1.255,2,0};   value channel 1                    position 1
         * {4,0-1:24.2.1.255,5,0};   value capture time channel 1       position 2
         * {4,0-2:24.2.1.255,2,0};   value channel 2                    position 1
         * {4,0-2:24.2.1.255,5,0};   value capture time channel 2       position 2
         * {4,0-3:24.2.1.255,2,0};   value channel 3                    position 1
         * {4,0-3:24.2.1.255,5,0};   value capture time channel 3       position 2
         * {4,0-4:24.2.1.255,2,0};   value channel 4                    position 1
         * {4,0-4:24.2.1.255,5,0};   value capture time channel 4       position 2
         */
        INDEX_MONTHLY_SELECTIVE_ACCESS_MBUS_VALUE_MAP.put(1, 1);
        INDEX_MONTHLY_SELECTIVE_ACCESS_MBUS_VALUE_CAPTURE_TIME_MAP.put(1, 2);
        INDEX_MONTHLY_SELECTIVE_ACCESS_MBUS_VALUE_MAP.put(2, 1);
        INDEX_MONTHLY_SELECTIVE_ACCESS_MBUS_VALUE_CAPTURE_TIME_MAP.put(2, 2);
        INDEX_MONTHLY_SELECTIVE_ACCESS_MBUS_VALUE_MAP.put(3, 1);
        INDEX_MONTHLY_SELECTIVE_ACCESS_MBUS_VALUE_CAPTURE_TIME_MAP.put(3, 2);
        INDEX_MONTHLY_SELECTIVE_ACCESS_MBUS_VALUE_MAP.put(4, 1);
        INDEX_MONTHLY_SELECTIVE_ACCESS_MBUS_VALUE_CAPTURE_TIME_MAP.put(4, 2);

        /*-
         * When no specific capture_objects are selected with selective access:
         *
         * {8,0-0:1.0.0.255,2,0};                                    position 0
         * {3,1-0:1.8.1.255,2,0};                                    position 1
         * {3,1-0:1.8.2.255,2,0};                                    position 2
         * {3,1-0:2.8.1.255,2,0};                                    position 3
         * {3,1-0:2.8.2.255,2,0};                                    position 4
         * {4,0-1:24.2.1.255,2,0};   value channel 1                 position 5
         * {4,0-1:24.2.1.255,5,0};   value capture time channel 1    position 6
         * {4,0-2:24.2.1.255,2,0};   value channel 2                 position 7
         * {4,0-2:24.2.1.255,5,0};   value capture time channel 2    position 8
         * {4,0-3:24.2.1.255,2,0};   value channel 3                 position 9
         * {4,0-3:24.2.1.255,5,0};   value capture time channel 3    position 10
         * {4,0-4:24.2.1.255,2,0};   value channel 4                 position 11
         * {4,0-4:24.2.1.255,5,0};   value capture time channel 4    position 12
         */
        INDEX_MONTHLY_MBUS_VALUE_MAP.put(1, 5);
        INDEX_MONTHLY_MBUS_VALUE_CAPTURE_TIME_MAP.put(1, 6);
        INDEX_MONTHLY_MBUS_VALUE_MAP.put(2, 7);
        INDEX_MONTHLY_MBUS_VALUE_CAPTURE_TIME_MAP.put(2, 8);
        INDEX_MONTHLY_MBUS_VALUE_MAP.put(3, 9);
        INDEX_MONTHLY_MBUS_VALUE_CAPTURE_TIME_MAP.put(3, 10);
        INDEX_MONTHLY_MBUS_VALUE_MAP.put(4, 11);
        INDEX_MONTHLY_MBUS_VALUE_CAPTURE_TIME_MAP.put(4, 12);
    }

    private static final Map<Integer, Integer> INDEX_DAILY_MBUS_VALUE_MAP = new HashMap<>();
    private static final Map<Integer, Integer> INDEX_DAILY_MBUS_VALUE_CAPTURE_TIME_MAP = new HashMap<>();
    private static final Map<Integer, Integer> INDEX_DAILY_SELECTIVE_ACCESS_MBUS_VALUE_MAP = new HashMap<>();
    private static final Map<Integer, Integer> INDEX_DAILY_SELECTIVE_ACCESS_MBUS_VALUE_CAPTURE_TIME_MAP =
            new HashMap<>();

    static {
        // Indicates the position of the value or value_capture_time for
        // channel 1, 2, 3 and 4

        /*-
         * When no specific capture_objects are selected with selective access:
         *
         * {8,0-0:1.0.0.255,2,0};                                    position 0
         * {1,0-0:96.10.2.255,2,0}                                   position 1
         * {3,1-0:1.8.1.255,2,0};                                    position 2
         * {3,1-0:1.8.2.255,2,0};                                    position 3
         * {3,1-0:2.8.1.255,2,0};                                    position 4
         * {3,1-0:2.8.2.255,2,0};                                    position 5
         * {4,0-1:24.2.1.255,2,0};   value channel 1                 position 6
         * {4,0-1:24.2.1.255,5,0};   value capture time channel 1    position 7
         * {4,0-2:24.2.1.255,2,0};   value channel 2                 position 8
         * {4,0-2:24.2.1.255,5,0};   value capture time channel 2    position 9
         * {4,0-3:24.2.1.255,2,0};   value channel 3                 position 10
         * {4,0-3:24.2.1.255,5,0};   value capture time channel 3    position 11
         * {4,0-4:24.2.1.255,2,0};   value channel 4                 position 12
         * {4,0-4:24.2.1.255,5,0};   value capture time channel 4    position 13
         */
        INDEX_DAILY_MBUS_VALUE_MAP.put(1, 6);
        INDEX_DAILY_MBUS_VALUE_CAPTURE_TIME_MAP.put(1, 7);
        INDEX_DAILY_MBUS_VALUE_MAP.put(2, 8);
        INDEX_DAILY_MBUS_VALUE_CAPTURE_TIME_MAP.put(2, 9);
        INDEX_DAILY_MBUS_VALUE_MAP.put(3, 10);
        INDEX_DAILY_MBUS_VALUE_CAPTURE_TIME_MAP.put(3, 11);
        INDEX_DAILY_MBUS_VALUE_MAP.put(4, 12);
        INDEX_DAILY_MBUS_VALUE_CAPTURE_TIME_MAP.put(4, 13);

        /*-
         * When specific capture_objects are selected with selective access:
         *
         * {8,0-0:1.0.0.255,2,0};                                    position 0
         * {1,0-0:96.10.2.255,2,0}                                   position 1
         *
         * Value and capture time for one of the four channels will be selected,
         * for which position 2 and 3 are used.
         *
         * {4,0-1:24.2.1.255,2,0};   value channel 1                 position 2
         * {4,0-1:24.2.1.255,5,0};   value capture time channel 1    position 3
         * {4,0-2:24.2.1.255,2,0};   value channel 2                 position 2
         * {4,0-2:24.2.1.255,5,0};   value capture time channel 2    position 3
         * {4,0-3:24.2.1.255,2,0};   value channel 3                 position 2
         * {4,0-3:24.2.1.255,5,0};   value capture time channel 3    position 3
         * {4,0-4:24.2.1.255,2,0};   value channel 4                 position 2
         * {4,0-4:24.2.1.255,5,0};   value capture time channel 4    position 3
         */
        INDEX_DAILY_SELECTIVE_ACCESS_MBUS_VALUE_MAP.put(1, 2);
        INDEX_DAILY_SELECTIVE_ACCESS_MBUS_VALUE_CAPTURE_TIME_MAP.put(1, 3);
        INDEX_DAILY_SELECTIVE_ACCESS_MBUS_VALUE_MAP.put(2, 2);
        INDEX_DAILY_SELECTIVE_ACCESS_MBUS_VALUE_CAPTURE_TIME_MAP.put(2, 3);
        INDEX_DAILY_SELECTIVE_ACCESS_MBUS_VALUE_MAP.put(3, 2);
        INDEX_DAILY_SELECTIVE_ACCESS_MBUS_VALUE_CAPTURE_TIME_MAP.put(3, 3);
        INDEX_DAILY_SELECTIVE_ACCESS_MBUS_VALUE_MAP.put(4, 2);
        INDEX_DAILY_SELECTIVE_ACCESS_MBUS_VALUE_CAPTURE_TIME_MAP.put(4, 3);
    }

    private static final String GAS_VALUE = "gasValue";
    private static final String GAS_VALUES = "gasValue: {}";
    private static final String UNEXPECTED_VALUE = "Unexpected null/unspecified value for Gas Capture Time";

    private final DlmsHelper dlmsHelper;

    private final AmrProfileStatusCodeHelper amrProfileStatusCodeHelper;

    private final AttributeAddressHelper attributeAddressHelper;

    @Autowired
    public GetPeriodicMeterReadsGasCommandExecutor(final DlmsHelper dlmsHelper,
            final AmrProfileStatusCodeHelper amrProfileStatusCodeHelper,
            final AttributeAddressHelper attributeAddressHelper) {
        super(PeriodicMeterReadsGasRequestDto.class);
        this.dlmsHelper = dlmsHelper;
        this.amrProfileStatusCodeHelper = amrProfileStatusCodeHelper;
        this.attributeAddressHelper = attributeAddressHelper;
    }

    @Override
    public PeriodicMeterReadsRequestDto fromBundleRequestInput(final ActionRequestDto bundleInput)
            throws ProtocolAdapterException {

        this.checkActionRequestType(bundleInput);
        final PeriodicMeterReadsGasRequestDto periodicMeterReadsGasRequestDto =
                (PeriodicMeterReadsGasRequestDto) bundleInput;

        return new PeriodicMeterReadsRequestDto(periodicMeterReadsGasRequestDto.getPeriodType(),
                periodicMeterReadsGasRequestDto.getBeginDate(), periodicMeterReadsGasRequestDto.getEndDate(),
                periodicMeterReadsGasRequestDto.getChannel());
    }

    @Override
    public PeriodicMeterReadGasResponseDto execute(final DlmsConnectionManager conn, final DlmsDevice device,
            final PeriodicMeterReadsRequestDto periodicMeterReadsQuery) throws ProtocolAdapterException {

        if (periodicMeterReadsQuery == null) {
            throw new IllegalArgumentException(
                    "PeriodicMeterReadsQuery should contain PeriodType, BeginDate and EndDate.");
        }

        final PeriodTypeDto queryPeriodType = periodicMeterReadsQuery.getPeriodType();
        final DateTime queryBeginDateTime = new DateTime(periodicMeterReadsQuery.getBeginDate());
        final DateTime queryEndDateTime = new DateTime(periodicMeterReadsQuery.getEndDate());
        final Protocol protocol = Protocol.withNameAndVersion(device.getProtocol(), device.getProtocolVersion());

        final AttributeAddress[] profileBufferAndScalerUnit = this
                .getProfileBufferAndScalerUnit(queryPeriodType, periodicMeterReadsQuery.getChannel(),
                        queryBeginDateTime, queryEndDateTime, protocol);

        LOGGER.debug("Retrieving current billing period and profiles for gas for period type: {}, from: {}, to: {}",
                queryPeriodType, queryBeginDateTime, queryEndDateTime);

        /*
         * workaround for a problem when using with_list and retrieving a profile
         * buffer, this will be returned erroneously.
         */
        final List<GetResult> getResultList = new ArrayList<>(profileBufferAndScalerUnit.length);
        for (final AttributeAddress address : profileBufferAndScalerUnit) {

            conn.getDlmsMessageListener().setDescription(
                    "GetPeriodicMeterReadsGas for channel " + periodicMeterReadsQuery.getChannel() + ", "
                            + queryPeriodType + " from " + queryBeginDateTime + " until " + queryEndDateTime
                            + ", retrieve attribute: " + JdlmsObjectToStringUtil.describeAttributes(address));

            getResultList.addAll(this.dlmsHelper.getAndCheck(conn, device,
                    "retrieve periodic meter reads for " + queryPeriodType + ", channel " + periodicMeterReadsQuery
                            .getChannel(), address));
        }

        final DataObject resultData = this.dlmsHelper.readDataObject(getResultList.get(0), "Periodic G-Meter Reads");
        final List<DataObject> bufferedObjectsList = resultData.getValue();

        final List<PeriodicMeterReadsGasResponseItemDto> periodicMeterReads = new ArrayList<>();
        for (final DataObject bufferedObject : bufferedObjectsList) {
            final List<DataObject> bufferedObjectValue = bufferedObject.getValue();

            final ParametersObject parameters = new ParametersObject(queryPeriodType, queryBeginDateTime,
                    queryEndDateTime, bufferedObjectValue, periodicMeterReadsQuery.getChannel(),
                    device.isSelectiveAccessSupported(), getResultList);
            try {
                periodicMeterReads.add(this.getNextPeriodicMeterReads(parameters));
            } catch (final BufferedDateTimeValidationException e) {
                LOGGER.warn(e.getMessage(), e);
            }
        }

        return new PeriodicMeterReadGasResponseDto(queryPeriodType, periodicMeterReads);
    }

    private PeriodicMeterReadsGasResponseItemDto getNextPeriodicMeterReads(final ParametersObject parameters)
            throws ProtocolAdapterException, BufferedDateTimeValidationException {

        final CosemDateTimeDto cosemDateTime = this.dlmsHelper
                .readDateTime(parameters.getBufferedObjects().get(BUFFER_INDEX_CLOCK),
                        "Clock from " + parameters.getPeriodType() + " buffer gas");
        final DateTime bufferedDateTime = cosemDateTime == null ? null : cosemDateTime.asDateTime();

        this.dlmsHelper.validateBufferedDateTime(bufferedDateTime, cosemDateTime, parameters.getBeginDateTime(),
                parameters.getEndDateTime());

        LOGGER.debug("Processing profile ({}) objects captured at: {}", parameters.getPeriodType(), cosemDateTime);

        return this.getNextPeriodicMeterReadsBasedOnPeriodType(parameters, bufferedDateTime);
    }

    private PeriodicMeterReadsGasResponseItemDto getNextPeriodicMeterReadsBasedOnPeriodType(
            final ParametersObject parameters, final DateTime bufferedDateTime)
            throws ProtocolAdapterException, AssertionError {
        switch (parameters.getPeriodType()) {
        case INTERVAL:
            return this.getNextPeriodicMeterReadsForInterval(parameters.getBufferedObjects(), bufferedDateTime,
                    parameters.getResults());
        case DAILY:
            return this.getNextPeriodicMeterReadsForDaily(parameters.getBufferedObjects(), bufferedDateTime,
                    parameters.getChannel(), parameters.isSelectiveAccessSupported(), parameters.getResults());
        case MONTHLY:
            return this.getNextPeriodicMeterReadsForMonthly(parameters.getBufferedObjects(), bufferedDateTime,
                    parameters.getChannel(), parameters.isSelectiveAccessSupported(), parameters.getResults());
        default:
            throw new AssertionError("Unknown PeriodType: " + parameters.getPeriodType());
        }
    }

    private PeriodicMeterReadsGasResponseItemDto getNextPeriodicMeterReadsForInterval(
            final List<DataObject> bufferedObjects, final DateTime bufferedDateTime, final List<GetResult> results)
            throws ProtocolAdapterException {

        final AmrProfileStatusCodeDto amrProfileStatusCode = this
                .readAmrProfileStatusCode(bufferedObjects.get(BUFFER_INDEX_AMR_STATUS));

        final DataObject gasValue = bufferedObjects.get(BUFFER_INDEX_MBUS_VALUE_INT);
        LOGGER.debug(GAS_VALUES, this.dlmsHelper.getDebugInfo(gasValue));

        final CosemDateTimeDto cosemDateTime = this.dlmsHelper
                .readDateTime(bufferedObjects.get(BUFFER_INDEX_MBUS_CAPTURETIME_INT),
                        "Clock from mbus interval extended register");
        final Date captureTime;
        if (cosemDateTime.isDateTimeSpecified()) {
            captureTime = cosemDateTime.asDateTime().toDate();
        } else {
            throw new ProtocolAdapterException(UNEXPECTED_VALUE);
        }
        return new PeriodicMeterReadsGasResponseItemDto(bufferedDateTime.toDate(), this.dlmsHelper
                .getScaledMeterValue(gasValue, results.get(RESULT_INDEX_SCALER_UNIT).getResultData(), GAS_VALUE),
                captureTime, amrProfileStatusCode);
    }

    private PeriodicMeterReadsGasResponseItemDto getNextPeriodicMeterReadsForDaily(
            final List<DataObject> bufferedObjects, final DateTime bufferedDateTime, final ChannelDto channel,
            final boolean isSelectiveAccessSupported, final List<GetResult> results) throws ProtocolAdapterException {

        final AmrProfileStatusCodeDto amrProfileStatusCode = this
                .readAmrProfileStatusCode(bufferedObjects.get(BUFFER_INDEX_AMR_STATUS));

        final DataObject gasValue;
        final DataObject gasCaptureTime;
        if (isSelectiveAccessSupported) {
            gasValue = bufferedObjects.get(INDEX_DAILY_SELECTIVE_ACCESS_MBUS_VALUE_MAP.get(channel.getChannelNumber()));
            gasCaptureTime = bufferedObjects
                    .get(INDEX_DAILY_SELECTIVE_ACCESS_MBUS_VALUE_CAPTURE_TIME_MAP.get(channel.getChannelNumber()));
        } else {
            gasValue = bufferedObjects.get(INDEX_DAILY_MBUS_VALUE_MAP.get(channel.getChannelNumber()));
            gasCaptureTime = bufferedObjects
                    .get(INDEX_DAILY_MBUS_VALUE_CAPTURE_TIME_MAP.get(channel.getChannelNumber()));
        }

        LOGGER.debug(GAS_VALUES, this.dlmsHelper.getDebugInfo(gasValue));
        LOGGER.debug("gasCaptureTime: {}", this.dlmsHelper.getDebugInfo(gasCaptureTime));

        final CosemDateTimeDto cosemDateTime = this.dlmsHelper
                .readDateTime(gasCaptureTime, "Clock from daily mbus daily extended register");
        final Date captureTime;
        if (cosemDateTime.isDateTimeSpecified()) {
            captureTime = cosemDateTime.asDateTime().toDate();
        } else {
            throw new ProtocolAdapterException(UNEXPECTED_VALUE);
        }
        return new PeriodicMeterReadsGasResponseItemDto(bufferedDateTime.toDate(), this.dlmsHelper
                .getScaledMeterValue(gasValue, results.get(RESULT_INDEX_SCALER_UNIT).getResultData(), GAS_VALUE),
                captureTime, amrProfileStatusCode);
    }

    private PeriodicMeterReadsGasResponseItemDto getNextPeriodicMeterReadsForMonthly(
            final List<DataObject> bufferedObjects, final DateTime bufferedDateTime, final ChannelDto channel,
            final boolean isSelectiveAccessSupported, final List<GetResult> results) throws ProtocolAdapterException {

        final DataObject gasValue;
        final DataObject gasCaptureTime;
        if (isSelectiveAccessSupported) {
            gasValue = bufferedObjects
                    .get(INDEX_MONTHLY_SELECTIVE_ACCESS_MBUS_VALUE_MAP.get(channel.getChannelNumber()));
            gasCaptureTime = bufferedObjects
                    .get(INDEX_MONTHLY_SELECTIVE_ACCESS_MBUS_VALUE_CAPTURE_TIME_MAP.get(channel.getChannelNumber()));
        } else {
            gasValue = bufferedObjects.get(INDEX_MONTHLY_MBUS_VALUE_MAP.get(channel.getChannelNumber()));
            gasCaptureTime = bufferedObjects
                    .get(INDEX_MONTHLY_MBUS_VALUE_CAPTURE_TIME_MAP.get(channel.getChannelNumber()));
        }

        LOGGER.debug(GAS_VALUES, this.dlmsHelper.getDebugInfo(gasValue));
        LOGGER.debug("gasCaptureTime: {}", this.dlmsHelper.getDebugInfo(gasCaptureTime));

        final CosemDateTimeDto cosemDateTime = this.dlmsHelper
                .readDateTime(gasCaptureTime, "gas capture time for mbus monthly");
        final Date captureTime;
        if (cosemDateTime.isDateTimeSpecified()) {
            captureTime = cosemDateTime.asDateTime().toDate();
        } else {
            throw new ProtocolAdapterException(UNEXPECTED_VALUE);
        }
        return new PeriodicMeterReadsGasResponseItemDto(bufferedDateTime.toDate(), this.dlmsHelper
                .getScaledMeterValue(gasValue, results.get(RESULT_INDEX_SCALER_UNIT).getResultData(), GAS_VALUE),
                captureTime);
    }

    private AttributeAddress[] getProfileBufferAndScalerUnit(final PeriodTypeDto periodType, final ChannelDto channel,
            final DateTime beginDateTime, final DateTime endDateTime, final Protocol protocol)
            throws ProtocolAdapterException {

        final DlmsObjectType type;
        switch (periodType) {
        case INTERVAL:
            type = DlmsObjectType.INTERVAL_VALUES_G;
            break;
        case DAILY:
            type = DlmsObjectType.DAILY_LOAD_PROFILE_COMBINED;
            break;
        case MONTHLY:
            type = DlmsObjectType.MONTHLY_BILLING_VALUES_COMBINED;
            break;
        default:
            throw new ProtocolAdapterException(String.format("periodtype %s not supported", periodType));
        }

        final List<AttributeAddress> profileBuffer = this.attributeAddressHelper
                .getAttributeAddressWithScalerUnitAddresses(protocol, type, channel.getChannelNumber(), beginDateTime,
                        endDateTime, Medium.GAS);

        return profileBuffer.toArray(new AttributeAddress[0]);
    }

    /**
     * Reads AmrProfileStatusCode from DataObject holding a bitvalue in a numeric
     * datatype.
     *
     * @param amrProfileStatusData
     *         AMR profile register value.
     *
     * @return AmrProfileStatusCode object holding status enum values.
     *
     * @throws ProtocolAdapterException
     *         on invalid register data.
     */
    private AmrProfileStatusCodeDto readAmrProfileStatusCode(final DataObject amrProfileStatusData)
            throws ProtocolAdapterException {

        if (!amrProfileStatusData.isNumber()) {
            throw new ProtocolAdapterException("Could not read AMR profile register data. Invalid data type.");
        }

        final Set<AmrProfileStatusCodeFlagDto> flags = this.amrProfileStatusCodeHelper
                .toAmrProfileStatusCodeFlags(amrProfileStatusData.getValue());
        return new AmrProfileStatusCodeDto(flags);
    }
}

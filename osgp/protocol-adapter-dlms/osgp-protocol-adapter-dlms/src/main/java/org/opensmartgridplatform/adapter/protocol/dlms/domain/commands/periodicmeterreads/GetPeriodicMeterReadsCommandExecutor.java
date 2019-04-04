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
import java.util.List;
import java.util.Set;

import org.joda.time.DateTime;
import org.openmuc.jdlms.AttributeAddress;
import org.openmuc.jdlms.GetResult;
import org.openmuc.jdlms.datatypes.DataObject;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.AbstractPeriodicMeterReadsCommandExecutor;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.AmrProfileStatusCodeHelperService;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.DlmsHelper;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.JdlmsObjectToStringUtil;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.Protocol;
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
public class GetPeriodicMeterReadsCommandExecutor
        extends AbstractPeriodicMeterReadsCommandExecutor<PeriodicMeterReadsRequestDto, PeriodicMeterReadsResponseDto> {

    private static final Logger LOGGER = LoggerFactory.getLogger(GetPeriodicMeterReadsCommandExecutor.class);

    private static final int RESULT_INDEX_IMPORT = 1;
    private static final int RESULT_INDEX_IMPORT_2_OR_EXPORT = 2;
    private static final int RESULT_INDEX_EXPORT = 3;
    private static final int RESULT_INDEX_EXPORT_2 = 4;

    private static final int BUFFER_INDEX_CLOCK = 0;
    private static final int BUFFER_INDEX_AMR_STATUS = 1;
    private static final int BUFFER_INDEX_A_POS_RATE_1 = 2;
    private static final int BUFFER_INDEX_A_POS_RATE_2 = 3;
    private static final int BUFFER_INDEX_A_NEG_RATE_1 = 4;
    private static final int BUFFER_INDEX_A_NEG_RATE_2 = 5;
    private static final int BUFFER_INDEX_A_POS = 2;
    private static final int BUFFER_INDEX_A_NEG = 3;

    private final DlmsHelper dlmsHelper = new DlmsHelper();
    private final AttributeAddressService attributeAddressService = new AttributeAddressService();
    private final AmrProfileStatusCodeHelperService amrProfileStatusCodeHelperService;

    @Autowired
    public GetPeriodicMeterReadsCommandExecutor(
            final AmrProfileStatusCodeHelperService amrProfileStatusCodeHelperService) {
        super(PeriodicMeterReadsRequestDataDto.class);
        this.amrProfileStatusCodeHelperService = amrProfileStatusCodeHelperService;
    }

    @Override
    public PeriodicMeterReadsRequestDto fromBundleRequestInput(final ActionRequestDto bundleInput)
            throws ProtocolAdapterException {

        this.checkActionRequestType(bundleInput);
        final PeriodicMeterReadsRequestDataDto periodicMeterReadsRequestDataDto =
                (PeriodicMeterReadsRequestDataDto) bundleInput;

        return new PeriodicMeterReadsRequestDto(periodicMeterReadsRequestDataDto.getPeriodType(),
                periodicMeterReadsRequestDataDto.getBeginDate(), periodicMeterReadsRequestDataDto.getEndDate());
    }

    @Override
    public PeriodicMeterReadsResponseDto execute(final DlmsConnectionManager conn, final DlmsDevice device,
            final PeriodicMeterReadsRequestDto periodicMeterReadsRequest) throws ProtocolAdapterException {

        final PeriodTypeDto periodType = periodicMeterReadsRequest.getPeriodType();
        final DateTime beginDateTime = new DateTime(periodicMeterReadsRequest.getBeginDate());
        final DateTime endDateTime = new DateTime(periodicMeterReadsRequest.getEndDate());
        final Protocol protocol = Protocol.withNameAndVersion(device.getProtocol(), device.getProtocolVersion());

        final AttributeAddress[] profileBufferAndScalerUnit = this.attributeAddressService
                .getProfileBufferAndScalerUnitForPeriodicMeterReads(periodType, beginDateTime, endDateTime,
                        protocol.isSelectValuesInSelectiveAccessSupported());

        LOGGER.debug("Retrieving current billing period and profiles for period type: {}, from: {}, to: {}", periodType,
                beginDateTime, endDateTime);

        /*
         * workaround for a problem when using with_list and retrieving a
         * profile buffer, this will be returned erroneously.
         */
        final List<GetResult> getResultList = new ArrayList<>(profileBufferAndScalerUnit.length);
        for (final AttributeAddress address : profileBufferAndScalerUnit) {

            conn.getDlmsMessageListener().setDescription(
                    "GetPeriodicMeterReads " + periodType + " from " + beginDateTime + " until " + endDateTime
                            + ", retrieve attribute: " + JdlmsObjectToStringUtil.describeAttributes(address));

            getResultList.addAll(this.dlmsHelper
                    .getAndCheck(conn, device, "retrieve periodic meter reads for " + periodType, address));
        }

        final DataObject resultData = this.dlmsHelper.readDataObject(getResultList.get(0), "Periodic E-Meter Reads");
        final List<DataObject> bufferedObjectsList = resultData.getValue();

        final List<PeriodicMeterReadsResponseItemDto> periodicMeterReads = new ArrayList<>();
        for (final DataObject bufferedObject : bufferedObjectsList) {
            final List<DataObject> bufferedObjects = bufferedObject.getValue();
            try {
                periodicMeterReads.add(this
                        .processNextPeriodicMeterReads(periodType, beginDateTime, endDateTime, bufferedObjects,
                                getResultList));
            } catch (final BufferedDateTimeValidationException e) {
                LOGGER.warn(e.getMessage(), e);
            }
        }

        return new PeriodicMeterReadsResponseDto(periodType, periodicMeterReads);
    }

    private PeriodicMeterReadsResponseItemDto processNextPeriodicMeterReads(final PeriodTypeDto periodType,
            final DateTime beginDateTime, final DateTime endDateTime, final List<DataObject> bufferedObjects,
            final List<GetResult> results) throws ProtocolAdapterException, BufferedDateTimeValidationException {

        final CosemDateTimeDto cosemDateTime = this.dlmsHelper
                .readDateTime(bufferedObjects.get(BUFFER_INDEX_CLOCK), "Clock from " + periodType + " buffer");
        final DateTime bufferedDateTime = cosemDateTime == null ? null : cosemDateTime.asDateTime();

        this.dlmsHelper.validateBufferedDateTime(bufferedDateTime, cosemDateTime, beginDateTime, endDateTime);

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

        final AmrProfileStatusCodeDto amrProfileStatusCode = this
                .readAmrProfileStatusCode(bufferedObjects.get(BUFFER_INDEX_AMR_STATUS));

        final DlmsMeterValueDto positiveActiveEnergy = this.dlmsHelper
                .getScaledMeterValue(bufferedObjects.get(BUFFER_INDEX_A_POS),
                        results.get(RESULT_INDEX_IMPORT).getResultData(), "positiveActiveEnergy");
        final DlmsMeterValueDto negativeActiveEnergy = this.dlmsHelper
                .getScaledMeterValue(bufferedObjects.get(BUFFER_INDEX_A_NEG),
                        results.get(RESULT_INDEX_IMPORT_2_OR_EXPORT).getResultData(), "negativeActiveEnergy");

        return new PeriodicMeterReadsResponseItemDto(bufferedDateTime.toDate(), positiveActiveEnergy,
                negativeActiveEnergy, amrProfileStatusCode);
    }

    private PeriodicMeterReadsResponseItemDto getNextPeriodicMeterReadsForDaily(final List<DataObject> bufferedObjects,
            final DateTime bufferedDateTime, final List<GetResult> results) throws ProtocolAdapterException {

        final AmrProfileStatusCodeDto amrProfileStatusCode = this
                .readAmrProfileStatusCode(bufferedObjects.get(BUFFER_INDEX_AMR_STATUS));

        final DlmsMeterValueDto positiveActiveEnergyTariff1 = this.dlmsHelper
                .getScaledMeterValue(bufferedObjects.get(BUFFER_INDEX_A_POS_RATE_1),
                        results.get(RESULT_INDEX_IMPORT).getResultData(), "positiveActiveEnergyTariff1");
        final DlmsMeterValueDto positiveActiveEnergyTariff2 = this.dlmsHelper
                .getScaledMeterValue(bufferedObjects.get(BUFFER_INDEX_A_POS_RATE_2),
                        results.get(RESULT_INDEX_IMPORT_2_OR_EXPORT).getResultData(), "positiveActiveEnergyTariff2");
        final DlmsMeterValueDto negativeActiveEnergyTariff1 = this.dlmsHelper
                .getScaledMeterValue(bufferedObjects.get(BUFFER_INDEX_A_NEG_RATE_1),
                        results.get(RESULT_INDEX_EXPORT).getResultData(), "negativeActiveEnergyTariff1");
        final DlmsMeterValueDto negativeActiveEnergyTariff2 = this.dlmsHelper
                .getScaledMeterValue(bufferedObjects.get(BUFFER_INDEX_A_NEG_RATE_2),
                        results.get(RESULT_INDEX_EXPORT_2).getResultData(), "negativeActiveEnergyTariff2");

        return new PeriodicMeterReadsResponseItemDto(bufferedDateTime.toDate(), positiveActiveEnergyTariff1,
                positiveActiveEnergyTariff2, negativeActiveEnergyTariff1, negativeActiveEnergyTariff2,
                amrProfileStatusCode);
    }

    /**
     * Reads AmrProfileStatusCode from DataObject holding a bitvalue in a
     * numeric datatype.
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
        final DlmsMeterValueDto positiveActiveEnergyTariff1 = this.dlmsHelper
                .getScaledMeterValue(bufferedObjects.get(BUFFER_INDEX_A_POS_RATE_1 - 1),
                        results.get(RESULT_INDEX_IMPORT).getResultData(), "positiveActiveEnergyTariff1");
        final DlmsMeterValueDto positiveActiveEnergyTariff2 = this.dlmsHelper
                .getScaledMeterValue(bufferedObjects.get(BUFFER_INDEX_A_POS_RATE_2 - 1),
                        results.get(RESULT_INDEX_IMPORT_2_OR_EXPORT).getResultData(), "positiveActiveEnergyTariff2");
        final DlmsMeterValueDto negativeActiveEnergyTariff1 = this.dlmsHelper
                .getScaledMeterValue(bufferedObjects.get(BUFFER_INDEX_A_NEG_RATE_1 - 1),
                        results.get(RESULT_INDEX_EXPORT).getResultData(), "negativeActiveEnergyTariff1");
        final DlmsMeterValueDto negativeActiveEnergyTariff2 = this.dlmsHelper
                .getScaledMeterValue(bufferedObjects.get(BUFFER_INDEX_A_NEG_RATE_2 - 1),
                        results.get(RESULT_INDEX_EXPORT_2).getResultData(), "negativeActiveEnergyTariff2");

        return new PeriodicMeterReadsResponseItemDto(bufferedDateTime.toDate(), positiveActiveEnergyTariff1,
                positiveActiveEnergyTariff2, negativeActiveEnergyTariff1, negativeActiveEnergyTariff2);
    }
}

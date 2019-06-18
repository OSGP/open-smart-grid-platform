/**
 * Copyright 2016 Smart Society Services B.V.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.periodicmeterreads;

import org.joda.time.DateTime;
import org.openmuc.jdlms.datatypes.DataObject;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.AbstractCommandExecutor;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.AttributeAddressForProfile;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.DlmsObjectConfigService;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.DlmsObjectType;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.model.DlmsObject;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.model.DlmsProfile;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.model.Medium;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.model.ProfileCaptureTime;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.DlmsHelper;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.Protocol;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.BufferedDateTimeValidationException;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.CosemDateTimeDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.PeriodTypeDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.PeriodicMeterReadsRequestDataDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.PeriodicMeterReadsRequestDto;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

public abstract class AbstractPeriodicMeterReadsCommandExecutor<T, R> extends AbstractCommandExecutor<T, R> {

    public AbstractPeriodicMeterReadsCommandExecutor(final Class<? extends PeriodicMeterReadsRequestDataDto> clazz) {
        super(clazz);
    }

    protected Date readClock(final PeriodicMeterReadsRequestDto periodicMeterReadsQuery,
                             final List<DataObject> bufferedObjects,
                             final AttributeAddressForProfile attributeAddressForProfile,
                             final Date previousLogTime,
                             final ProfileCaptureTime intervalTime,
                             final DlmsHelper dlmsHelper)
            throws ProtocolAdapterException, BufferedDateTimeValidationException {

        final PeriodTypeDto queryPeriodType = periodicMeterReadsQuery.getPeriodType();
        final DateTime queryBeginDateTime = new DateTime(periodicMeterReadsQuery.getBeginDate());
        final DateTime queryEndDateTime = new DateTime(periodicMeterReadsQuery.getEndDate());

        final Integer clockIndex = attributeAddressForProfile.getIndex(DlmsObjectType.CLOCK, null);

        CosemDateTimeDto cosemDateTime = null;

        if (clockIndex != null) {
            cosemDateTime = dlmsHelper.readDateTime(bufferedObjects.get(clockIndex),
                    "Clock from " + queryPeriodType + " buffer gas");
        }

        final DateTime bufferedDateTime = cosemDateTime == null ? null : cosemDateTime.asDateTime();

        if (bufferedDateTime != null) {
            dlmsHelper.validateBufferedDateTime(bufferedDateTime, cosemDateTime, queryBeginDateTime, queryEndDateTime);
            return bufferedDateTime.toDate();
        } else {

            // no date was returned, calculate date based on previous value
            return calculateIntervalDate(periodicMeterReadsQuery.getPeriodType(), previousLogTime, intervalTime);
        }
    }

    /**
     * Calculates/derives the next interval date in case it was not present in the current meter read record.
     *
     * @param periodTypeDto   the time interval period.
     * @param previousLogTime the logTime of the previous meter read record
     * @param intervalTime    the interval time for this device to be taken into account when the periodTypeDto is INTERVAL
     * @return the derived date based on the previous meter read record, or null if it cannot be determined
     */
    private Date calculateIntervalDate(final PeriodTypeDto periodTypeDto,
                                       final Date previousLogTime,
                                       final ProfileCaptureTime intervalTime) {

        if (previousLogTime == null) {
            return null;
        }

        switch (periodTypeDto) {
            case DAILY:
                return Date.from(previousLogTime.toInstant().plus(Duration.ofDays(1)));
            case MONTHLY:
                LocalDateTime localDateTime = LocalDateTime.ofInstant(previousLogTime.toInstant(), ZoneId.systemDefault()).plusMonths(1);

                return Date.from(localDateTime.atZone(ZoneId.systemDefault())
                        .toInstant());
            case INTERVAL:

                int intervalTimeMinutes = 0;
                if (intervalTime == ProfileCaptureTime.QUARTER_HOUR) {
                    intervalTimeMinutes = 15;
                } else if (intervalTime == ProfileCaptureTime.HOUR) {
                    intervalTimeMinutes = 60;
                }

                return Date.from(previousLogTime.toInstant().plus(Duration.ofMinutes(intervalTimeMinutes)));
            default:
                return null;
        }
    }

    /**
     * Get the interval time for given device and medium.
     *
     * @param device
     * @param dlmsObjectConfigService
     * @param medium
     * @return
     */
    protected ProfileCaptureTime getProfileCaptureTime(DlmsDevice device, DlmsObjectConfigService dlmsObjectConfigService, Medium medium) {
        DlmsObject dlmsObject =
                dlmsObjectConfigService.findDlmsObject(Protocol.withNameAndVersion(device.getProtocol(),
                        device.getProtocolVersion()),
                        DlmsObjectType.INTERVAL_VALUES,
                        medium)
                        .orElse(null);

        if (dlmsObject instanceof DlmsProfile) {
            DlmsProfile profile = (DlmsProfile) dlmsObject;

            return profile.getCaptureTime();
        }

        return null;
    }

}

/**
 * Copyright 2016 Smart Society Services B.V.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.periodicmeterreads;

import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.AbstractCommandExecutor;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.PeriodTypeDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.PeriodicMeterReadsRequestDataDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.PeriodicMeterReadsResponseItemDto;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

public abstract class AbstractPeriodicMeterReadsCommandExecutor<T, R> extends AbstractCommandExecutor<T, R> {

    public AbstractPeriodicMeterReadsCommandExecutor(final Class<? extends PeriodicMeterReadsRequestDataDto> clazz) {
        super(clazz);
    }

    /**
     * Calculates/derives the next interval date in case it was not present in the re
     *
     * @param periodTypeDto
     * @param periodicMeterReads
     * @return
     */
    public Date calculateIntervalDate(PeriodTypeDto periodTypeDto, List<PeriodicMeterReadsResponseItemDto> periodicMeterReads) {

        if (periodicMeterReads.isEmpty()) {
            return null;
        }

        PeriodicMeterReadsResponseItemDto last = periodicMeterReads.get(periodicMeterReads.size() - 1);

        if (last.getLogTime() == null) {
            return null;
        }

        switch (periodTypeDto) {
            case DAILY:
                return Date.from(last.getLogTime().toInstant().plus(Duration.ofDays(1)));
            case MONTHLY:
                LocalDateTime localDateTime = LocalDateTime.ofInstant(last.getLogTime().toInstant(), ZoneId.systemDefault()).plusMonths(1);

                return Date.from(localDateTime.atZone(ZoneId.systemDefault())
                        .toInstant());
            case INTERVAL:
                return Date.from(last.getLogTime().toInstant().plus(Duration.ofMinutes(15)));
            default:
                return null;
        }
    }


}

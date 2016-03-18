/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.domain.smartmetering.application.mapping;

import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.metadata.Type;

import com.alliander.osgp.domain.core.valueobjects.smartmetering.MeterReads;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.OsgpMeterValue;
import com.alliander.osgp.dto.valueobjects.smartmetering.DlmsMeterValue;

public class ActualMeterReadsConverter extends
CustomConverter<com.alliander.osgp.dto.valueobjects.smartmetering.MeterReads, MeterReads> {

    @Override
    public MeterReads convert(final com.alliander.osgp.dto.valueobjects.smartmetering.MeterReads source,
            final Type<? extends MeterReads> destinationType) {
        return new MeterReads(source.getLogTime(), this.convert(source.getActiveEnergyImport()), this.convert(source
                .getActiveEnergyExport()), this.convert(source.getActiveEnergyImportTariffOne()), this.convert(source
                        .getActiveEnergyImportTariffTwo()), this.convert(source.getActiveEnergyExportTariffOne()),
                        this.convert(source.getActiveEnergyExportTariffTwo()));
    }

    private OsgpMeterValue convert(final DlmsMeterValue dlmsMeterValue) {
        return this.mapperFacade.map(dlmsMeterValue, OsgpMeterValue.class);
    }

}

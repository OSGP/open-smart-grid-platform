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

import com.alliander.osgp.domain.core.valueobjects.smartmetering.AmrProfileStatusCode;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.OsgpMeterValue;
import com.alliander.osgp.dto.valueobjects.smartmetering.DlmsMeterValue;
import com.alliander.osgp.dto.valueobjects.smartmetering.PeriodicMeterReads;

public class PeriodicMeterReadsConverter
extends
CustomConverter<PeriodicMeterReads, com.alliander.osgp.domain.core.valueobjects.smartmetering.PeriodicMeterReads> {

    @Override
    public com.alliander.osgp.domain.core.valueobjects.smartmetering.PeriodicMeterReads convert(
            final PeriodicMeterReads pmr,
            final Type<? extends com.alliander.osgp.domain.core.valueobjects.smartmetering.PeriodicMeterReads> destinationType) {
        final AmrProfileStatusCode amrProfileStatusCode = this.mapperFacade.map(pmr.getAmrProfileStatusCode(),
                AmrProfileStatusCode.class);
        return new com.alliander.osgp.domain.core.valueobjects.smartmetering.PeriodicMeterReads(pmr.getLogTime(),
                this.convert(pmr.getActiveEnergyImport()), this.convert(pmr.getActiveEnergyExport()), this.convert(pmr
                        .getActiveEnergyImportTariffOne()), this.convert(pmr.getActiveEnergyImportTariffTwo()),
                this.convert(pmr.getActiveEnergyExportTariffOne()), this.convert(pmr.getActiveEnergyExportTariffTwo()),
                amrProfileStatusCode);
    }

    private OsgpMeterValue convert(final DlmsMeterValue dlmsMeterValue) {
        return this.mapperFacade.map(dlmsMeterValue, OsgpMeterValue.class);
    }

}

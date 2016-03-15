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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alliander.osgp.domain.core.valueobjects.smartmetering.AmrProfileStatusCode;
import com.alliander.osgp.dto.valueobjects.smartmetering.PeriodicMeterReads;

@Component
public class PeriodicMeterReadsConverter
        extends
        CustomConverter<PeriodicMeterReads, com.alliander.osgp.domain.core.valueobjects.smartmetering.PeriodicMeterReads> {
    @Autowired
    private StandardUnitConverter standardUnitConverter;

    @Override
    public com.alliander.osgp.domain.core.valueobjects.smartmetering.PeriodicMeterReads convert(
            final PeriodicMeterReads pmr,
            final Type<? extends com.alliander.osgp.domain.core.valueobjects.smartmetering.PeriodicMeterReads> destinationType) {
        final AmrProfileStatusCode amrProfileStatusCode = this.mapperFacade.map(pmr.getAmrProfileStatusCode(),
                AmrProfileStatusCode.class);
        return new com.alliander.osgp.domain.core.valueobjects.smartmetering.PeriodicMeterReads(pmr.getLogTime(),
                this.standardUnitConverter.calculateStandardizedValue(pmr.getActiveEnergyImport()),
                this.standardUnitConverter.calculateStandardizedValue(pmr.getActiveEnergyExport()),
                this.standardUnitConverter.calculateStandardizedValue(pmr.getActiveEnergyImportTariffOne()),
                this.standardUnitConverter.calculateStandardizedValue(pmr.getActiveEnergyImportTariffTwo()),
                this.standardUnitConverter.calculateStandardizedValue(pmr.getActiveEnergyExportTariffOne()),
                this.standardUnitConverter.calculateStandardizedValue(pmr.getActiveEnergyExportTariffTwo()),
                amrProfileStatusCode);
    }

}

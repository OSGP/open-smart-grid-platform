/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.domain.smartmetering.application.mapping;

import java.util.ArrayList;
import java.util.List;

import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.metadata.Type;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alliander.osgp.domain.core.valueobjects.smartmetering.AmrProfileStatusCode;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.PeriodicMeterReadContainer;
import com.alliander.osgp.dto.valueobjects.smartmetering.PeriodicMeterReads;
import com.alliander.osgp.dto.valueobjects.smartmetering.PeriodicMeterReadsContainer;

@Component
public class PeriodicMeterReadsResponseConverter
        extends
        CustomConverter<com.alliander.osgp.dto.valueobjects.smartmetering.PeriodicMeterReadsContainer, PeriodicMeterReadContainer> {
    @Autowired
    private StandardUnitConverter standardUnitConverter;

    @Override
    public PeriodicMeterReadContainer convert(final PeriodicMeterReadsContainer source,
            final Type<? extends PeriodicMeterReadContainer> destinationType) {
        final List<com.alliander.osgp.domain.core.valueobjects.smartmetering.PeriodicMeterReads> periodicMeterReads = new ArrayList<>(
                source.getMeterReads().size());
        for (final PeriodicMeterReads pmr : source.getMeterReads()) {
            final AmrProfileStatusCode amrProfileStatusCode = this.mapperFacade.map(pmr.getAmrProfileStatusCode(),
                    AmrProfileStatusCode.class);

            // no mapping here because the converter would need source to do the
            // calculation of the standardized value
            periodicMeterReads
            .add(new com.alliander.osgp.domain.core.valueobjects.smartmetering.PeriodicMeterReads(pmr
                    .getLogTime(), this.standardUnitConverter.calculateStandardizedValue(
                            pmr.getActiveEnergyImport(), source), this.standardUnitConverter
                            .calculateStandardizedValue(pmr.getActiveEnergyExport(), source),
                            this.standardUnitConverter.calculateStandardizedValue(pmr.getActiveEnergyImportTariffOne(),
                                    source), this.standardUnitConverter.calculateStandardizedValue(
                                    pmr.getActiveEnergyImportTariffTwo(), source), this.standardUnitConverter
                                    .calculateStandardizedValue(pmr.getActiveEnergyExportTariffOne(), source),
                            this.standardUnitConverter.calculateStandardizedValue(pmr.getActiveEnergyExportTariffTwo(),
                                    source), amrProfileStatusCode));
        }

        return new PeriodicMeterReadContainer(
                com.alliander.osgp.domain.core.valueobjects.smartmetering.PeriodType.valueOf(source.getPeriodType()
                        .name()), periodicMeterReads, this.standardUnitConverter.toStandardUnit(source));
    }

}

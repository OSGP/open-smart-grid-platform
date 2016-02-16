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

import com.alliander.osgp.domain.core.valueobjects.smartmetering.ActualMeterReads;

public class ActualMeterReadsConverter extends
        CustomConverter<com.alliander.osgp.dto.valueobjects.smartmetering.ActualMeterReads, ActualMeterReads> {

    private final StandardUnitConverter standardUnitConverter;

    public ActualMeterReadsConverter(final StandardUnitConverter standardUnitConverter) {
        this.standardUnitConverter = standardUnitConverter;
    }

    @Override
    public ActualMeterReads convert(final com.alliander.osgp.dto.valueobjects.smartmetering.ActualMeterReads source,
            final Type<? extends ActualMeterReads> destinationType) {
        return new ActualMeterReads(
                source.getLogTime(),
                this.standardUnitConverter.calculateStandardizedValue(source.getActiveEnergyImport(), source),
                this.standardUnitConverter.calculateStandardizedValue(source.getActiveEnergyExport(), source),
                this.standardUnitConverter.calculateStandardizedValue(source.getActiveEnergyImportTariffOne(), source),
                this.standardUnitConverter.calculateStandardizedValue(source.getActiveEnergyImportTariffTwo(), source),
                this.standardUnitConverter.calculateStandardizedValue(source.getActiveEnergyExportTariffOne(), source),
                source.getActiveEnergyExportTariffTwo(), this.standardUnitConverter.toStandardUnit(source));
    }
}

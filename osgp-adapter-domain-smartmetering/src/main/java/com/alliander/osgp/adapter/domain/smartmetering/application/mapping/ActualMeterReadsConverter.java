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
import com.alliander.osgp.dto.valueobjects.smartmetering.ScalerUnitResponse;

public class ActualMeterReadsConverter extends
CustomConverter<com.alliander.osgp.dto.valueobjects.smartmetering.ActualMeterReads, ActualMeterReads> {

    private final StandardUnitCalculator standardUnitCalculator;

    private double toStandard(final long value, final ScalerUnitResponse scalerUnitResponse) {
        return this.standardUnitCalculator.calculateStandardizedValue(value, scalerUnitResponse.getScalerUnit());
    }

    public ActualMeterReadsConverter(final StandardUnitCalculator standardUnitCalculator) {
        this.standardUnitCalculator = standardUnitCalculator;
    }

    @Override
    public ActualMeterReads convert(final com.alliander.osgp.dto.valueobjects.smartmetering.ActualMeterReads source,
            final Type<? extends ActualMeterReads> destinationType) {
        return new ActualMeterReads(source.getLogTime(), this.standardUnitCalculator.calculateStandardizedValue(
                source.getActiveEnergyImport(), source.getScalerUnit()), this.toStandard(
                        source.getActiveEnergyExport(), source), this.toStandard(source.getActiveEnergyImportTariffOne(),
                                source), this.toStandard(source.getActiveEnergyImportTariffTwo(), source), this.toStandard(
                                        source.getActiveEnergyExportTariffOne(), source), source.getActiveEnergyExportTariffTwo());
    }
}

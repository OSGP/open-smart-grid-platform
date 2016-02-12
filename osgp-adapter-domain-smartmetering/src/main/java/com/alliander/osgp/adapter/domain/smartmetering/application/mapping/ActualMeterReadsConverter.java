/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.domain.smartmetering.application.mapping;

import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;

import com.alliander.osgp.domain.core.valueobjects.smartmetering.ActualMeterReads;
import com.alliander.osgp.dto.valueobjects.smartmetering.ScalerUnitResponse;

public class ActualMeterReadsConverter extends
        BidirectionalConverter<com.alliander.osgp.dto.valueobjects.smartmetering.ActualMeterReads, ActualMeterReads> {

    private final StandardUnitCalculator standardUnitCalculator;

    private double toStandard(final long value, final ScalerUnitResponse scalerUnitResponse) {
        return this.standardUnitCalculator.calculateStandardizedValue(value, scalerUnitResponse.getScalerUnit());
    }

    public ActualMeterReadsConverter(final StandardUnitCalculator standardUnitCalculator) {
        this.standardUnitCalculator = standardUnitCalculator;
    }

    @Override
    public ActualMeterReads convertTo(final com.alliander.osgp.dto.valueobjects.smartmetering.ActualMeterReads source,
            final Type<ActualMeterReads> destinationType) {

        return new ActualMeterReads(source.getLogTime(), this.standardUnitCalculator.calculateStandardizedValue(
                source.getActiveEnergyImport(), source.getScalerUnit()), this.toStandard(
                source.getActiveEnergyExport(), source), this.toStandard(source.getActiveEnergyImportTariffOne(),
                source), this.toStandard(source.getActiveEnergyImportTariffTwo(), source), this.toStandard(
                source.getActiveEnergyExportTariffOne(), source), source.getActiveEnergyExportTariffTwo());
    }

    @Override
    public com.alliander.osgp.dto.valueobjects.smartmetering.ActualMeterReads convertFrom(
            final ActualMeterReads source,
            final Type<com.alliander.osgp.dto.valueobjects.smartmetering.ActualMeterReads> destinationType) {

        throw new IllegalStateException(
                "mapping a response meant for the platform layer to a response from the protocol layer should not be necessary");
    }
}

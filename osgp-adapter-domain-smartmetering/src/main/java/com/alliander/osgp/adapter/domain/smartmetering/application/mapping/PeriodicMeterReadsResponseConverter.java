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

import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;

import com.alliander.osgp.domain.core.valueobjects.smartmetering.AmrProfileStatusCode;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.PeriodicMeterReadContainer;
import com.alliander.osgp.dto.valueobjects.smartmetering.PeriodicMeterReads;
import com.alliander.osgp.dto.valueobjects.smartmetering.PeriodicMeterReadsContainer;
import com.alliander.osgp.dto.valueobjects.smartmetering.ScalerUnitResponse;

public class PeriodicMeterReadsResponseConverter
extends
BidirectionalConverter<com.alliander.osgp.dto.valueobjects.smartmetering.PeriodicMeterReadsContainer, PeriodicMeterReadContainer> {
    private final StandardUnitCalculator standardUnitCalculator;

    public PeriodicMeterReadsResponseConverter(final StandardUnitCalculator standardUnitCalculator) {
        super();
        this.standardUnitCalculator = standardUnitCalculator;
    }

    private double toStandard(final long value, final ScalerUnitResponse scalerUnitResponse) {
        return this.standardUnitCalculator.calculateStandardizedValue(value, scalerUnitResponse.getScalerUnit());
    }

    @Override
    public PeriodicMeterReadContainer convertTo(final PeriodicMeterReadsContainer source,
            final Type<PeriodicMeterReadContainer> destinationType) {
        final List<com.alliander.osgp.domain.core.valueobjects.smartmetering.PeriodicMeterReads> periodicMeterReads = new ArrayList<>(
                source.getMeterReads().size());
        for (final PeriodicMeterReads pmr : source.getMeterReads()) {
            final AmrProfileStatusCode amrProfileStatusCode = this.mapperFacade.map(pmr.getAmrProfileStatusCode(),
                    AmrProfileStatusCode.class);

            periodicMeterReads.add(new com.alliander.osgp.domain.core.valueobjects.smartmetering.PeriodicMeterReads(pmr
                    .getLogTime(), this.toStandard(pmr.getActiveEnergyImport(), source), this.toStandard(
                    pmr.getActiveEnergyExport(), source),
                    this.toStandard(pmr.getActiveEnergyImportTariffOne(), source), this.toStandard(
                            pmr.getActiveEnergyImportTariffTwo(), source), this.toStandard(
                                            pmr.getActiveEnergyExportTariffOne(), source), this.toStandard(
                            pmr.getActiveEnergyExportTariffTwo(), source), amrProfileStatusCode));
        }

        return new PeriodicMeterReadContainer(
                com.alliander.osgp.domain.core.valueobjects.smartmetering.PeriodType.valueOf(source.getPeriodType()
                        .name()), periodicMeterReads);
    }

    @Override
    public PeriodicMeterReadsContainer convertFrom(final PeriodicMeterReadContainer source,
            final Type<PeriodicMeterReadsContainer> destinationType) {
        throw new IllegalStateException(
                "mapping a response meant for the platform layer to a response from the protocol layer should not be necessary");
    }

}

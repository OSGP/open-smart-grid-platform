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

import com.alliander.osgp.domain.core.valueobjects.smartmetering.MeterReads;

@Component
public class ActualMeterReadsConverter extends
CustomConverter<com.alliander.osgp.dto.valueobjects.smartmetering.MeterReads, MeterReads> {

    @Autowired
    private StandardUnitConverter standardUnitConverter;

    @Override
    public MeterReads convert(final com.alliander.osgp.dto.valueobjects.smartmetering.MeterReads source,
            final Type<? extends MeterReads> destinationType) {
        return new MeterReads(source.getLogTime(), this.standardUnitConverter.calculateStandardizedValue(source
                .getActiveEnergyImport()), this.standardUnitConverter.calculateStandardizedValue(source
                        .getActiveEnergyExport()), this.standardUnitConverter.calculateStandardizedValue(source
                                .getActiveEnergyImportTariffOne()), this.standardUnitConverter.calculateStandardizedValue(source
                                        .getActiveEnergyImportTariffTwo()), this.standardUnitConverter.calculateStandardizedValue(source
                                                .getActiveEnergyExportTariffOne()), this.standardUnitConverter.calculateStandardizedValue(source
                                                        .getActiveEnergyExportTariffTwo()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((this.standardUnitConverter == null) ? 0 : this.standardUnitConverter.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        final ActualMeterReadsConverter other = (ActualMeterReadsConverter) obj;
        if (this.standardUnitConverter == null) {
            if (other.standardUnitConverter != null) {
                return false;
            }
        } else if (!this.standardUnitConverter.equals(other.standardUnitConverter)) {
            return false;
        }
        return true;
    }
}

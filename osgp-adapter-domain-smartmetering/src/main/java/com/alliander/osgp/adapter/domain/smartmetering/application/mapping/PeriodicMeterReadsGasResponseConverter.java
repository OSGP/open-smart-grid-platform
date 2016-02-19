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
import com.alliander.osgp.domain.core.valueobjects.smartmetering.PeriodicMeterReadsContainerGas;
import com.alliander.osgp.dto.valueobjects.smartmetering.PeriodicMeterReadsGas;

@Component
public class PeriodicMeterReadsGasResponseConverter
        extends
        CustomConverter<com.alliander.osgp.dto.valueobjects.smartmetering.PeriodicMeterReadsContainerGas, PeriodicMeterReadsContainerGas> {

    @Autowired
    private StandardUnitConverter standardUnitConverter;

    @Override
    public PeriodicMeterReadsContainerGas convert(
            final com.alliander.osgp.dto.valueobjects.smartmetering.PeriodicMeterReadsContainerGas source,
            final Type<? extends PeriodicMeterReadsContainerGas> destinationType) {
        final List<com.alliander.osgp.domain.core.valueobjects.smartmetering.PeriodicMeterReadsGas> meterReadsGas = new ArrayList<>(
                source.getMeterReadsGas().size());
        for (final PeriodicMeterReadsGas pmr : source.getMeterReadsGas()) {

            final AmrProfileStatusCode amrProfileStatusCode = this.mapperFacade.map(pmr.getAmrProfileStatusCode(),
                    AmrProfileStatusCode.class);
            // no mapping here because the converter would need source to do the
            // calculation of the standardized value
            meterReadsGas.add(new com.alliander.osgp.domain.core.valueobjects.smartmetering.PeriodicMeterReadsGas(pmr
                    .getLogTime(), this.standardUnitConverter.calculateStandardizedValue(pmr.getConsumption(), source),
                    pmr.getCaptureTime(), amrProfileStatusCode));
        }

        return new PeriodicMeterReadsContainerGas(
                com.alliander.osgp.domain.core.valueobjects.smartmetering.PeriodType.valueOf(source.getPeriodType()
                        .name()), meterReadsGas, this.standardUnitConverter.toStandardUnit(source));
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
        final PeriodicMeterReadsGasResponseConverter other = (PeriodicMeterReadsGasResponseConverter) obj;
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

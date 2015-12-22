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

import com.alliander.osgp.domain.core.valueobjects.smartmetering.AmrProfileStatusCode;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.PeriodicMeterReads;

public class PeriodicMeterReadsConverter
extends
BidirectionalConverter<com.alliander.osgp.dto.valueobjects.smartmetering.PeriodicMeterReads, PeriodicMeterReads> {

    public static final String CONVERTER_ID = "periodicMeterReadsConverter";

    @Override
    public PeriodicMeterReads convertTo(
            final com.alliander.osgp.dto.valueobjects.smartmetering.PeriodicMeterReads source,
            final Type<PeriodicMeterReads> destinationType) {

        final AmrProfileStatusCode amrProfileStatusCode = this.mapperFacade.convert(source.getAmrProfileStatusCode(),
                AmrProfileStatusCode.class, AmrProfileStatusCodeConverter.CONVERTER_ID);

        return new PeriodicMeterReads(source.getLogTime(), source.getActiveEnergyImportTariffOne(),
                source.getActiveEnergyImportTariffTwo(), source.getActiveEnergyExportTariffOne(),
                source.getActiveEnergyExportTariffTwo(), amrProfileStatusCode);
    }

    @Override
    public com.alliander.osgp.dto.valueobjects.smartmetering.PeriodicMeterReads convertFrom(
            final PeriodicMeterReads source,
            final Type<com.alliander.osgp.dto.valueobjects.smartmetering.PeriodicMeterReads> destinationType) {

        final com.alliander.osgp.dto.valueobjects.smartmetering.AmrProfileStatusCode amrProfileStatusCode = this.mapperFacade
                .convert(source.getAmrProfileStatusCode(),
                        com.alliander.osgp.dto.valueobjects.smartmetering.AmrProfileStatusCode.class,
                        AmrProfileStatusCodeConverter.CONVERTER_ID);

        return new com.alliander.osgp.dto.valueobjects.smartmetering.PeriodicMeterReads(source.getLogTime(),
                source.getActiveEnergyImportTariffOne(), source.getActiveEnergyImportTariffTwo(),
                source.getActiveEnergyExportTariffOne(), source.getActiveEnergyExportTariffTwo(), amrProfileStatusCode);
    }
}

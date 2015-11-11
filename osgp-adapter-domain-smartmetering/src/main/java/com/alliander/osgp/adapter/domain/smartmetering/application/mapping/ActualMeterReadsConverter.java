package com.alliander.osgp.adapter.domain.smartmetering.application.mapping;

import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;

import com.alliander.osgp.domain.core.valueobjects.smartmetering.ActualMeterReads;

public class ActualMeterReadsConverter extends
BidirectionalConverter<com.alliander.osgp.dto.valueobjects.smartmetering.ActualMeterReads, ActualMeterReads> {

    @Override
    public ActualMeterReads convertTo(final com.alliander.osgp.dto.valueobjects.smartmetering.ActualMeterReads source,
            final Type<ActualMeterReads> destinationType) {

        return new ActualMeterReads(source.getLogTime(), source.getActiveEnergyImportTariffOne(),
                source.getActiveEnergyImportTariffTwo(), source.getActiveEnergyExportTariffOne(),
                source.getActiveEnergyExportTariffTwo());
    }

    @Override
    public com.alliander.osgp.dto.valueobjects.smartmetering.ActualMeterReads convertFrom(
            final ActualMeterReads source,
            final Type<com.alliander.osgp.dto.valueobjects.smartmetering.ActualMeterReads> destinationType) {

        return new com.alliander.osgp.dto.valueobjects.smartmetering.ActualMeterReads(source.getLogTime(),
                source.getActiveEnergyImportTariffOne(), source.getActiveEnergyImportTariffTwo(),
                source.getActiveEnergyExportTariffOne(), source.getActiveEnergyExportTariffTwo());
    }
}

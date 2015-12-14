package com.alliander.osgp.adapter.domain.smartmetering.application.mapping;

import java.util.ArrayList;
import java.util.List;

import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;

import com.alliander.osgp.domain.core.valueobjects.smartmetering.PeriodicMeterReadContainer;
import com.alliander.osgp.dto.valueobjects.smartmetering.AmrProfileStatusses;
import com.alliander.osgp.dto.valueobjects.smartmetering.PeriodType;
import com.alliander.osgp.dto.valueobjects.smartmetering.PeriodicMeterReads;
import com.alliander.osgp.dto.valueobjects.smartmetering.PeriodicMeterReadsContainer;

public class PeriodicMeterReadsContainerConverter
        extends
        BidirectionalConverter<com.alliander.osgp.dto.valueobjects.smartmetering.PeriodicMeterReadsContainer, PeriodicMeterReadContainer> {

    @Override
    public PeriodicMeterReadContainer convertTo(final PeriodicMeterReadsContainer source,
            final Type<PeriodicMeterReadContainer> destinationType) {

        final List<com.alliander.osgp.domain.core.valueobjects.smartmetering.PeriodicMeterReads> resultReads = new ArrayList();
        final List<PeriodicMeterReads> sourceReads = source.getPeriodicMeterReads();
        for (final PeriodicMeterReads sourceRead : sourceReads) {

            final com.alliander.osgp.domain.core.valueobjects.smartmetering.AmrProfileStatusses amrProfileStatusses = this.mapperFacade
                    .convert(sourceRead.getAmrProfileStatusses(),
                            com.alliander.osgp.domain.core.valueobjects.smartmetering.AmrProfileStatusses.class,
                            "amrProfileStatussesConverter");

            resultReads.add(new com.alliander.osgp.domain.core.valueobjects.smartmetering.PeriodicMeterReads(sourceRead
                    .getLogTime(), sourceRead.getActiveEnergyImportTariffOne(), sourceRead
                    .getActiveEnergyImportTariffOne(), sourceRead.getActiveEnergyExportTariffOne(), sourceRead
                    .getActiveEnergyExportTariffTwo(),
                    com.alliander.osgp.domain.core.valueobjects.smartmetering.PeriodType.valueOf(sourceRead
                            .getPeriodType().value()), amrProfileStatusses));
        }

        final PeriodicMeterReadContainer result = new PeriodicMeterReadContainer(source.getDeviceIdentification(),
                resultReads);
        return result;
    }

    @Override
    public PeriodicMeterReadsContainer convertFrom(final PeriodicMeterReadContainer source,
            final Type<PeriodicMeterReadsContainer> destinationType) {

        final List<PeriodicMeterReads> resultReads = new ArrayList();
        final List<com.alliander.osgp.domain.core.valueobjects.smartmetering.PeriodicMeterReads> sourceReads = source
                .getPeriodicMeterReads();
        for (final com.alliander.osgp.domain.core.valueobjects.smartmetering.PeriodicMeterReads sourceRead : sourceReads) {
            final AmrProfileStatusses amrProfileStatusses = this.mapperFacade.convert(
                    sourceRead.getAmrProfileStatusses(), AmrProfileStatusses.class, "amrProfileStatussesConverter");

            resultReads.add(new PeriodicMeterReads(sourceRead.getLogTime(),
                    sourceRead.getActiveEnergyImportTariffOne(), sourceRead.getActiveEnergyImportTariffOne(),
                    sourceRead.getActiveEnergyExportTariffOne(), sourceRead.getActiveEnergyExportTariffTwo(),
                    PeriodType.valueOf(sourceRead.getPeriodType().value()), amrProfileStatusses));
        }

        final PeriodicMeterReadsContainer result = new PeriodicMeterReadsContainer(source.getDeviceIdentification(),
                resultReads);

        return result;
    }
}

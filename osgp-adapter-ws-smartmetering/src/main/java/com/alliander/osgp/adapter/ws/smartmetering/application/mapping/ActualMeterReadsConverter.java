package com.alliander.osgp.adapter.ws.smartmetering.application.mapping;

import java.util.GregorianCalendar;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alliander.osgp.domain.core.valueobjects.smartmetering.ActualMeterReads;

public class ActualMeterReadsConverter
extends
BidirectionalConverter<ActualMeterReads, com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.ActualMeterReads> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ActualMeterReadsConverter.class);

    @Override
    public com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.ActualMeterReads convertTo(
            final ActualMeterReads source,
            final Type<com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.ActualMeterReads> destinationType) {

        final com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.ActualMeterReads destination = new com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.ActualMeterReads();

        final GregorianCalendar c = new GregorianCalendar();
        c.setTime(source.getLogTime());
        XMLGregorianCalendar convertedDate;
        try {
            convertedDate = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
        } catch (final DatatypeConfigurationException e) {
            LOGGER.error("JAXB mapping: An error occured while converting calendar types.", e);
            convertedDate = null;
        }

        destination.setLogTime(convertedDate);
        destination.setActiveEnergyExportTariffOne(source.getActiveEnergyExportTariffOne());
        destination.setActiveEnergyExportTariffTwo(source.getActiveEnergyExportTariffTwo());
        destination.setActiveEnergyImportTariffOne(source.getActiveEnergyImportTariffOne());
        destination.setActiveEnergyImportTariffTwo(source.getActiveEnergyImportTariffTwo());

        return destination;
    }

    @Override
    public ActualMeterReads convertFrom(
            final com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.ActualMeterReads source,
            final Type<ActualMeterReads> destinationType) {

        return new ActualMeterReads(source.getLogTime().toGregorianCalendar().getTime(),
                source.getActiveEnergyImportTariffOne(), source.getActiveEnergyImportTariffTwo(),
                source.getActiveEnergyExportTariffOne(), source.getActiveEnergyExportTariffTwo());
    }

}

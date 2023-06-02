//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.adapter.ws.smartmetering.application.mapping;

import java.util.GregorianCalendar;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.metadata.Type;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.monitoring.ActualMeterReadsResponseData;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.monitoring.MeterValue;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.monitoring.ObjectFactory;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.MeterReads;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.OsgpMeterValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ActualMeterReadsResponseConverter
    extends CustomConverter<MeterReads, ActualMeterReadsResponseData> {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(ActualMeterReadsResponseConverter.class);

  @Override
  public ActualMeterReadsResponseData convert(
      final MeterReads source,
      final Type<? extends ActualMeterReadsResponseData> destinationType,
      final MappingContext context) {

    final org.opensmartgridplatform.adapter.ws.schema.smartmetering.monitoring
            .ActualMeterReadsResponseData
        destination = new ObjectFactory().createActualMeterReadsResponseData();

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
    destination.setActiveEnergyImport(this.getMeterValue(source.getActiveEnergyImport()));
    destination.setActiveEnergyExport(this.getMeterValue(source.getActiveEnergyExport()));
    destination.setActiveEnergyExportTariffOne(
        this.getMeterValue(source.getActiveEnergyExportTariffOne()));
    destination.setActiveEnergyExportTariffTwo(
        this.getMeterValue(source.getActiveEnergyExportTariffTwo()));
    destination.setActiveEnergyImportTariffOne(
        this.getMeterValue(source.getActiveEnergyImportTariffOne()));
    destination.setActiveEnergyImportTariffTwo(
        this.getMeterValue(source.getActiveEnergyImportTariffTwo()));

    destination.setException(source.getException());
    destination.setResultString(source.getResultString());

    return destination;
  }

  private MeterValue getMeterValue(final OsgpMeterValue source) {
    return this.mapperFacade.map(source, MeterValue.class);
  }
}

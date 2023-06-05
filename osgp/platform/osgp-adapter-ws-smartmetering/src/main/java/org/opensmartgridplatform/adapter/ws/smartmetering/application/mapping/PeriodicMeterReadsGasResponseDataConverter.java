// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.ws.smartmetering.application.mapping;

import java.util.GregorianCalendar;
import java.util.List;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.metadata.Type;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.monitoring.AmrProfileStatusCode;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.monitoring.MeterValue;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.monitoring.PeriodType;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.monitoring.PeriodicMeterReadsGas;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.monitoring.PeriodicMeterReadsGasResponseData;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.OsgpMeterValue;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.PeriodicMeterReadsContainerGas;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PeriodicMeterReadsGasResponseDataConverter
    extends CustomConverter<
        PeriodicMeterReadsContainerGas,
        org.opensmartgridplatform.adapter.ws.schema.smartmetering.monitoring
            .PeriodicMeterReadsGasResponseData> {
  private static final Logger LOGGER =
      LoggerFactory.getLogger(PeriodicMeterReadsGasResponseDataConverter.class);

  @Override
  public PeriodicMeterReadsGasResponseData convert(
      final PeriodicMeterReadsContainerGas source,
      final Type<? extends PeriodicMeterReadsGasResponseData> destinationType,
      final MappingContext context) {
    final PeriodicMeterReadsGasResponseData periodicMeterReadsResponse =
        new PeriodicMeterReadsGasResponseData();
    periodicMeterReadsResponse.setPeriodType(PeriodType.valueOf(source.getPeriodType().name()));
    final List<PeriodicMeterReadsGas> periodicMeterReads =
        periodicMeterReadsResponse.getPeriodicMeterReadsGas();
    for (final org.opensmartgridplatform.domain.core.valueobjects.smartmetering
            .PeriodicMeterReadsGas
        m : source.getPeriodicMeterReadsGas()) {
      periodicMeterReads.add(this.convert(m));
    }
    return periodicMeterReadsResponse;
  }

  private PeriodicMeterReadsGas convert(
      final org.opensmartgridplatform.domain.core.valueobjects.smartmetering.PeriodicMeterReadsGas
          source) {
    final PeriodicMeterReadsGas meterReads = new PeriodicMeterReadsGas();
    final GregorianCalendar c = new GregorianCalendar();
    c.setTime(source.getLogTime());
    XMLGregorianCalendar convertedDate;
    try {
      convertedDate = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
    } catch (final DatatypeConfigurationException e) {
      LOGGER.error("JAXB mapping: An error occured while converting calendar types.", e);
      convertedDate = null;
    }
    final AmrProfileStatusCode amrProfileStatusCode =
        this.mapperFacade.map(source.getAmrProfileStatusCode(), AmrProfileStatusCode.class);

    meterReads.setLogTime(convertedDate);
    meterReads.setConsumption(this.getMeterValue(source.getConsumption()));
    c.setTime(source.getCaptureTime());
    try {
      convertedDate = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
    } catch (final DatatypeConfigurationException e) {
      LOGGER.error("JAXB mapping: An error occured while converting calendar types.", e);
      convertedDate = null;
    }
    meterReads.setCaptureTime(convertedDate);
    meterReads.setAmrProfileStatusCode(amrProfileStatusCode);
    return meterReads;
  }

  private MeterValue getMeterValue(final OsgpMeterValue source) {
    return this.mapperFacade.map(source, MeterValue.class);
  }
}

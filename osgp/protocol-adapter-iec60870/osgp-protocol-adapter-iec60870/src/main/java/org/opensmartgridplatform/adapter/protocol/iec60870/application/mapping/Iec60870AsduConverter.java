/*
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.iec60870.application.mapping;

import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.metadata.Type;
import org.openmuc.j60870.ASdu;
import org.opensmartgridplatform.dto.da.measurements.MeasurementGroupDto;
import org.opensmartgridplatform.dto.da.measurements.MeasurementReportDto;
import org.opensmartgridplatform.dto.da.measurements.MeasurementReportHeaderDto;

public class Iec60870AsduConverter extends CustomConverter<ASdu, MeasurementReportDto> {

  @Override
  public MeasurementReportDto convert(
      final ASdu source,
      final Type<? extends MeasurementReportDto> destinationType,
      final MappingContext mappingContext) {

    return new MeasurementReportDto(
        new MeasurementReportHeaderDto(
            source.getTypeIdentification().name(),
            source.getCauseOfTransmission().name(),
            source.getOriginatorAddress(),
            source.getCommonAddress()),
        this.mapperFacade.mapAsList(source.getInformationObjects(), MeasurementGroupDto.class));
  }
}

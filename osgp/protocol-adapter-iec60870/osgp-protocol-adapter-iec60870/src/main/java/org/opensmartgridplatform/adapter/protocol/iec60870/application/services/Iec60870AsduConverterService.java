/*
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.iec60870.application.services;

import ma.glasnost.orika.MapperFacade;
import org.openmuc.j60870.ASdu;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.services.AsduConverterService;
import org.opensmartgridplatform.dto.da.measurements.MeasurementReportDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class Iec60870AsduConverterService implements AsduConverterService {

  @Autowired private MapperFacade iec60870Mapper;

  @Override
  public MeasurementReportDto convert(final ASdu asdu) {
    return this.iec60870Mapper.map(asdu, MeasurementReportDto.class);
  }
}

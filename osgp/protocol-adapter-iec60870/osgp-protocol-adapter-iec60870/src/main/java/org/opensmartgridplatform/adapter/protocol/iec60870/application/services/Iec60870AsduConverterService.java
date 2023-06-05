// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

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

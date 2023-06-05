// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

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

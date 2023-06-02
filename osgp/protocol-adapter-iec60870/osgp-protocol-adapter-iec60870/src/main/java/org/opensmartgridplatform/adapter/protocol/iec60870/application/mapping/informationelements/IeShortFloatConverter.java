//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.adapter.protocol.iec60870.application.mapping.informationelements;

import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.metadata.Type;
import org.openmuc.j60870.ie.IeShortFloat;
import org.opensmartgridplatform.dto.da.measurements.elements.FloatMeasurementElementDto;

public class IeShortFloatConverter
    extends CustomConverter<IeShortFloat, FloatMeasurementElementDto> {

  @Override
  public FloatMeasurementElementDto convert(
      final IeShortFloat source,
      final Type<? extends FloatMeasurementElementDto> destinationType,
      final MappingContext mappingContext) {
    return new FloatMeasurementElementDto(source.getValue());
  }
}

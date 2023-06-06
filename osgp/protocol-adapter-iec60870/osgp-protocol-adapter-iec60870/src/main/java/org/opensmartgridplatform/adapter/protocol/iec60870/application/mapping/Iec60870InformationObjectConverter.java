// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.iec60870.application.mapping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.metadata.Type;
import org.openmuc.j60870.ie.IeQuality;
import org.openmuc.j60870.ie.IeShortFloat;
import org.openmuc.j60870.ie.IeSinglePointWithQuality;
import org.openmuc.j60870.ie.IeTime56;
import org.openmuc.j60870.ie.InformationElement;
import org.openmuc.j60870.ie.InformationObject;
import org.opensmartgridplatform.dto.da.measurements.MeasurementDto;
import org.opensmartgridplatform.dto.da.measurements.MeasurementElementDto;
import org.opensmartgridplatform.dto.da.measurements.MeasurementGroupDto;
import org.opensmartgridplatform.dto.da.measurements.elements.BitmaskMeasurementElementDto;
import org.opensmartgridplatform.dto.da.measurements.elements.FloatMeasurementElementDto;
import org.opensmartgridplatform.dto.da.measurements.elements.TimestampMeasurementElementDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Iec60870InformationObjectConverter
    extends CustomConverter<InformationObject, MeasurementGroupDto> {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(Iec60870InformationObjectConverter.class);
  private static final Map<
          Class<? extends InformationElement>, Class<? extends MeasurementElementDto>>
      CLASS_MAP;

  static {
    CLASS_MAP = new HashMap<>();
    CLASS_MAP.put(IeQuality.class, BitmaskMeasurementElementDto.class);
    CLASS_MAP.put(IeShortFloat.class, FloatMeasurementElementDto.class);
    CLASS_MAP.put(IeSinglePointWithQuality.class, BitmaskMeasurementElementDto.class);
    CLASS_MAP.put(IeTime56.class, TimestampMeasurementElementDto.class);
  }

  @Override
  public MeasurementGroupDto convert(
      final InformationObject source,
      final Type<? extends MeasurementGroupDto> destinationType,
      final MappingContext mappingContext) {

    final String identification = String.valueOf(source.getInformationObjectAddress());
    final List<MeasurementDto> measurements = new ArrayList<>();

    for (final InformationElement[] ieArray : source.getInformationElements()) {
      measurements.add(this.convert(ieArray));
    }

    return new MeasurementGroupDto(identification, measurements);
  }

  private MeasurementDto convert(final InformationElement[] source) {
    final List<MeasurementElementDto> elements = new ArrayList<>();

    for (final InformationElement ie : source) {

      final Class<? extends MeasurementElementDto> clazz = CLASS_MAP.get(ie.getClass());
      if (clazz == null) {
        LOGGER.warn(
            "Could not convert unknown information element {}", ie.getClass().getSimpleName());
      } else {
        elements.add(this.mapperFacade.map(ie, clazz));
      }
    }
    return new MeasurementDto(elements);
  }
}

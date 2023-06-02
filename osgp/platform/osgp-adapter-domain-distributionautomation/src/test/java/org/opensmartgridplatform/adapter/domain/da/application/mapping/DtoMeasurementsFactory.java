//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.adapter.domain.da.application.mapping;

import java.util.ArrayList;
import java.util.List;
import org.opensmartgridplatform.domain.da.measurements.Measurement;
import org.opensmartgridplatform.domain.da.measurements.MeasurementElement;
import org.opensmartgridplatform.domain.da.measurements.MeasurementGroup;
import org.opensmartgridplatform.domain.da.measurements.MeasurementReportHeader;
import org.opensmartgridplatform.dto.da.measurements.MeasurementDto;
import org.opensmartgridplatform.dto.da.measurements.MeasurementElementDto;
import org.opensmartgridplatform.dto.da.measurements.MeasurementGroupDto;
import org.opensmartgridplatform.dto.da.measurements.MeasurementReportHeaderDto;
import org.opensmartgridplatform.dto.da.measurements.elements.BitmaskMeasurementElementDto;
import org.opensmartgridplatform.dto.da.measurements.elements.FloatMeasurementElementDto;
import org.opensmartgridplatform.dto.da.measurements.elements.TimestampMeasurementElementDto;

/**
 * Factory for easy creation of {@link MeasurementElement}, {@link Measurement}, {@link
 * MeasurementReportHeader} and {@link MeasurementGroup} objects for use in unit tests.
 */
public class DtoMeasurementsFactory {

  public static MeasurementDto measurementFrom(
      final List<MeasurementElementDto> measurementElements) {
    return new MeasurementDto(measurementElements);
  }

  public static MeasurementDto gasFlowMeasurement(final float gasFlow) {
    final BitmaskMeasurementElementDto flags = new BitmaskMeasurementElementDto((byte) 96);
    final TimestampMeasurementElementDto from = new TimestampMeasurementElementDto(1546800000501L);
    final TimestampMeasurementElementDto until = new TimestampMeasurementElementDto(1546700000444L);
    final FloatMeasurementElementDto cubicMeters = new FloatMeasurementElementDto(gasFlow);

    final List<MeasurementElementDto> measurementElements = new ArrayList<>();
    measurementElements.add(flags);
    measurementElements.add(from);
    measurementElements.add(until);
    measurementElements.add(cubicMeters);

    return DtoMeasurementsFactory.measurementFrom(measurementElements);
  }

  public static MeasurementReportHeaderDto spontaneousReportHeader(final int commonAddress) {
    return new MeasurementReportHeaderDto("M_ME_TF_1", "SPONTANEOUS", 1055, commonAddress);
  }

  public static MeasurementGroupDto gasFlowMeasurementGroup(
      final String groupIdentifier, final float[] gasFlowMeasurements) {
    final List<MeasurementDto> measurements = new ArrayList<>();
    for (final float gasFlowMeasurement : gasFlowMeasurements) {
      measurements.add(DtoMeasurementsFactory.gasFlowMeasurement(gasFlowMeasurement));
    }

    return new MeasurementGroupDto(groupIdentifier, measurements);
  }
}

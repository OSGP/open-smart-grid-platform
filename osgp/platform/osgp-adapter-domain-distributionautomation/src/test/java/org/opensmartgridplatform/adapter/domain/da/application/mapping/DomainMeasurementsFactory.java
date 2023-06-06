// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.domain.da.application.mapping;

import java.util.ArrayList;
import java.util.List;
import org.opensmartgridplatform.domain.da.measurements.Measurement;
import org.opensmartgridplatform.domain.da.measurements.MeasurementElement;
import org.opensmartgridplatform.domain.da.measurements.MeasurementGroup;
import org.opensmartgridplatform.domain.da.measurements.MeasurementReportHeader;
import org.opensmartgridplatform.domain.da.measurements.elements.BitmaskMeasurementElement;
import org.opensmartgridplatform.domain.da.measurements.elements.FloatMeasurementElement;
import org.opensmartgridplatform.domain.da.measurements.elements.TimestampMeasurementElement;

/**
 * Factory for easy creation of {@link MeasurementElement}, {@link Measurement}, {@link
 * MeasurementReportHeader} and {@link MeasurementGroup} objects for use in unit tests.
 */
public class DomainMeasurementsFactory {

  public static BitmaskMeasurementElement bitmaskFrom(final byte value) {
    return new BitmaskMeasurementElement(value);
  }

  public static FloatMeasurementElement floatingPointFrom(final float value) {
    return new FloatMeasurementElement(value);
  }

  public static TimestampMeasurementElement timestampFrom(final long value) {
    return new TimestampMeasurementElement(value);
  }

  public static Measurement measurementFrom(final List<MeasurementElement> measurementElements) {
    return new Measurement(measurementElements);
  }

  public static Measurement gasFlowMeasurement(final float gasFlow) {
    final BitmaskMeasurementElement flags = new BitmaskMeasurementElement((byte) 96);
    final TimestampMeasurementElement from = new TimestampMeasurementElement(1546800000501l);
    final TimestampMeasurementElement until = new TimestampMeasurementElement(1546700000444l);
    final FloatMeasurementElement cubicMeters = new FloatMeasurementElement(gasFlow);

    final List<MeasurementElement> measurementElements = new ArrayList<>();
    measurementElements.add(flags);
    measurementElements.add(from);
    measurementElements.add(until);
    measurementElements.add(cubicMeters);

    return DomainMeasurementsFactory.measurementFrom(measurementElements);
  }

  public static MeasurementReportHeader spontaneousReportHeader(final int commonAddress) {
    return new org.opensmartgridplatform.domain.da.measurements.MeasurementReportHeader.Builder()
        .withCommonAddress(commonAddress)
        .withMeasurementType(
            org.opensmartgridplatform.domain.da.measurements.MeasurementType
                .MEASURED_SHORT_FLOAT_WITH_TIME_TAG)
        .withOriginatorAddress(1055)
        .withReasonType(org.opensmartgridplatform.domain.da.measurements.ReasonType.SPONTANEOUS)
        .build();
  }

  public static MeasurementGroup gasFlowMeasurementGroup(
      final String groupIdentification, final float[] gasFlowMeasurements) {
    final List<Measurement> measurements = new ArrayList<>();
    for (final float gasFlowMeasurement : gasFlowMeasurements) {
      measurements.add(DomainMeasurementsFactory.gasFlowMeasurement(gasFlowMeasurement));
    }

    return new MeasurementGroup(groupIdentification, measurements);
  }
}

//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.adapter.ws.da.application.mapping;

import java.util.Arrays;
import java.util.List;
import org.opensmartgridplatform.adapter.ws.schema.distributionautomation.generic.BitmaskMeasurementElement;
import org.opensmartgridplatform.adapter.ws.schema.distributionautomation.generic.FloatMeasurementElement;
import org.opensmartgridplatform.adapter.ws.schema.distributionautomation.generic.Measurement;
import org.opensmartgridplatform.adapter.ws.schema.distributionautomation.generic.MeasurementElement;
import org.opensmartgridplatform.adapter.ws.schema.distributionautomation.generic.MeasurementElements;
import org.opensmartgridplatform.adapter.ws.schema.distributionautomation.generic.MeasurementGroup;
import org.opensmartgridplatform.adapter.ws.schema.distributionautomation.generic.MeasurementGroups;
import org.opensmartgridplatform.adapter.ws.schema.distributionautomation.generic.MeasurementReportHeader;
import org.opensmartgridplatform.adapter.ws.schema.distributionautomation.generic.MeasurementType;
import org.opensmartgridplatform.adapter.ws.schema.distributionautomation.generic.Measurements;
import org.opensmartgridplatform.adapter.ws.schema.distributionautomation.generic.ReasonType;
import org.opensmartgridplatform.adapter.ws.schema.distributionautomation.generic.TimestampMeasurementElement;

/**
 * Factory for easy creation of {@link MeasurementElement}, {@link Measurement}, {@link
 * MeasurementReportHeader}, {@link MeasurementGroup} and {@link MeasurementGroups} objects for use
 * in unit tests.
 */
public class WSMeasurementsFactory {

  public static BitmaskMeasurementElement bitmaskFrom(final byte value) {
    final BitmaskMeasurementElement element = new BitmaskMeasurementElement();
    element.setValue(value);

    return element;
  }

  public static FloatMeasurementElement floatingPointFrom(final float value) {
    final FloatMeasurementElement element = new FloatMeasurementElement();
    element.setValue(value);

    return element;
  }

  public static TimestampMeasurementElement timestampFrom(final long value) {
    final TimestampMeasurementElement element = new TimestampMeasurementElement();
    element.setValue(value);

    return element;
  }

  public static Measurement measurementFrom(final MeasurementElement[] measurementElements) {
    final MeasurementElements elements = new MeasurementElements();

    final List<MeasurementElement> elementsToAdd = Arrays.asList(measurementElements);
    elements.getMeasurementElementList().addAll(elementsToAdd);

    final Measurement measurement = new Measurement();
    measurement.setMeasurementElements(elements);

    return measurement;
  }

  /**
   * Creates a fixed {@link Measurement} containing four {@link MeasurementElement}s.
   *
   * @return
   */
  public static Measurement gasFlowMeasurement(final float gasFlow) {
    final BitmaskMeasurementElement flags = WSMeasurementsFactory.bitmaskFrom((byte) 96);
    final TimestampMeasurementElement from = WSMeasurementsFactory.timestampFrom(1546800000501l);
    final TimestampMeasurementElement until = WSMeasurementsFactory.timestampFrom(1546700000444l);
    final FloatMeasurementElement cubicMeters = WSMeasurementsFactory.floatingPointFrom(gasFlow);

    final MeasurementElement[] wsElements = {flags, from, until, cubicMeters};

    return WSMeasurementsFactory.measurementFrom(wsElements);
  }

  public static MeasurementReportHeader spontaneousReportHeader(final int commonAddress) {
    final MeasurementReportHeader spontaneousReportHeader = new MeasurementReportHeader();

    spontaneousReportHeader.setCommonAddress(commonAddress);
    spontaneousReportHeader.setMeasurementType(MeasurementType.MEASURED_SHORT_FLOAT_WITH_TIME_TAG);
    spontaneousReportHeader.setOriginatorAddress(1055);
    spontaneousReportHeader.setReasonType(ReasonType.SPONTANEOUS);

    return spontaneousReportHeader;
  }

  public static MeasurementGroup gasFlowMeasurementGroup(
      final String groupIdentification, final float[] gasFlowMeasurements) {
    final MeasurementGroup gasFlowGroup = new MeasurementGroup();
    gasFlowGroup.setIdentification(groupIdentification);

    final Measurements wsMeasurements = new Measurements();
    for (final float gasFlowMeasurement : gasFlowMeasurements) {
      wsMeasurements
          .getMeasurementList()
          .add(WSMeasurementsFactory.gasFlowMeasurement(gasFlowMeasurement));
    }

    gasFlowGroup.setMeasurements(wsMeasurements);

    return gasFlowGroup;
  }

  public static MeasurementGroups gasFlowMeasurementGroups(
      final String groupIdentification, final float[] gasFlowMeasurements) {
    final MeasurementGroups gasFlowGroups = new MeasurementGroups();
    gasFlowGroups
        .getMeasurementGroupList()
        .add(gasFlowMeasurementGroup(groupIdentification, gasFlowMeasurements));

    return gasFlowGroups;
  }
}

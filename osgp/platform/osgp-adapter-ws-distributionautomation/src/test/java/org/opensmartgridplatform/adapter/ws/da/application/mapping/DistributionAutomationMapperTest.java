// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.ws.da.application.mapping;

import java.util.ArrayList;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.opensmartgridplatform.adapter.ws.schema.distributionautomation.generic.BitmaskMeasurementElement;
import org.opensmartgridplatform.adapter.ws.schema.distributionautomation.generic.FloatMeasurementElement;
import org.opensmartgridplatform.adapter.ws.schema.distributionautomation.generic.GetHealthStatusResponse;
import org.opensmartgridplatform.adapter.ws.schema.distributionautomation.generic.HealthStatusType;
import org.opensmartgridplatform.adapter.ws.schema.distributionautomation.generic.Measurement;
import org.opensmartgridplatform.adapter.ws.schema.distributionautomation.generic.MeasurementGroup;
import org.opensmartgridplatform.adapter.ws.schema.distributionautomation.generic.MeasurementGroups;
import org.opensmartgridplatform.adapter.ws.schema.distributionautomation.generic.MeasurementReport;
import org.opensmartgridplatform.adapter.ws.schema.distributionautomation.generic.MeasurementReportHeader;
import org.opensmartgridplatform.adapter.ws.schema.distributionautomation.generic.TimestampMeasurementElement;

public class DistributionAutomationMapperTest {

  private DistributionAutomationMapper mapper = new DistributionAutomationMapper();

  @Test
  public void testBitmaskMeasurementElementMapping() {
    // Arrange
    final BitmaskMeasurementElement expected = WSMeasurementsFactory.bitmaskFrom((byte) 40);
    final org.opensmartgridplatform.domain.da.measurements.elements.BitmaskMeasurementElement
        domainMeasurementElement =
            new org.opensmartgridplatform.domain.da.measurements.elements.BitmaskMeasurementElement(
                (byte) 40);

    // Act
    final BitmaskMeasurementElement actual =
        this.mapper.map(domainMeasurementElement, BitmaskMeasurementElement.class);

    // Assert
    Assertions.assertThat(actual.getValue()).isEqualTo(expected.getValue());
  }

  @Test
  public void testFloatingPointMeasurementElementMapping() {
    // Arrange
    final FloatMeasurementElement expected = WSMeasurementsFactory.floatingPointFrom(83.4999f);
    final org.opensmartgridplatform.domain.da.measurements.elements.FloatMeasurementElement
        domainMeasurementElement =
            new org.opensmartgridplatform.domain.da.measurements.elements.FloatMeasurementElement(
                83.4999f);

    // Act
    final FloatMeasurementElement actual =
        this.mapper.map(domainMeasurementElement, FloatMeasurementElement.class);

    // Assert
    Assertions.assertThat(actual.getValue()).isEqualTo(expected.getValue());
  }

  @Test
  public void testHealthStatusTypeMapping() {
    // Arrange
    final HealthStatusType expected = HealthStatusType.ALARM;
    final org.opensmartgridplatform.domain.da.valueobjects.GetHealthStatusResponse response =
        new org.opensmartgridplatform.domain.da.valueobjects.GetHealthStatusResponse("ALARM");

    // Act
    final GetHealthStatusResponse actual = this.mapper.map(response, GetHealthStatusResponse.class);

    // Assert
    Assertions.assertThat(actual.getHealthStatus()).isEqualTo(expected);
  }

  @Test
  public void testMeasurementGroupMapping() {
    // Arrange
    final String groupIdentification = "215";
    final float[] gasFlowMeasurements = {401.70001f, 88.575f};

    final MeasurementGroup expected =
        WSMeasurementsFactory.gasFlowMeasurementGroup(groupIdentification, gasFlowMeasurements);
    final org.opensmartgridplatform.domain.da.measurements.MeasurementGroup domainMeasurementGroup =
        DomainMeasurementsFactory.gasFlowMeasurementGroup(groupIdentification, gasFlowMeasurements);

    // Act
    final MeasurementGroup actual = this.mapper.map(domainMeasurementGroup, MeasurementGroup.class);

    // Assert
    Assertions.assertThat(actual).isEqualToComparingFieldByFieldRecursively(expected);
  }

  @Test
  public void testMeasurementMapping() {
    // Arrange
    final float gasFlowMeasurement = 3077f;
    final Measurement expected = WSMeasurementsFactory.gasFlowMeasurement(gasFlowMeasurement);
    final org.opensmartgridplatform.domain.da.measurements.Measurement domainMeasurement =
        DomainMeasurementsFactory.gasFlowMeasurement(gasFlowMeasurement);

    // Act
    final Measurement actual = this.mapper.map(domainMeasurement, Measurement.class);

    // Assert
    Assertions.assertThat(actual).isEqualToComparingFieldByFieldRecursively(expected);
  }

  @Test
  public void testMeasurementReportHeaderMapping() {
    // Arrange
    final int commonAddress = 234;
    final MeasurementReportHeader expected =
        WSMeasurementsFactory.spontaneousReportHeader(commonAddress);
    final org.opensmartgridplatform.domain.da.measurements.MeasurementReportHeader
        domainMeasurementReportHeader =
            DomainMeasurementsFactory.spontaneousReportHeader(commonAddress);

    // Act
    final MeasurementReportHeader actual =
        this.mapper.map(domainMeasurementReportHeader, MeasurementReportHeader.class);

    // Assert
    Assertions.assertThat(actual).isEqualToComparingFieldByField(expected);
  }

  @Test
  public void testMeasurementReportMapping() {
    // Arrange
    final int commonAddress = 55;
    final String groupIdentification = "137";
    final float[] gasFlowMeasurements = {78.733f, 21.000f};

    // Arrange WS
    final MeasurementReport expected = new MeasurementReport();
    expected.setReportHeader(WSMeasurementsFactory.spontaneousReportHeader(commonAddress));

    final MeasurementGroups wsGroups =
        WSMeasurementsFactory.gasFlowMeasurementGroups(groupIdentification, gasFlowMeasurements);

    expected.setMeasurementGroups(wsGroups);

    // Arrange domain
    final org.opensmartgridplatform.domain.da.measurements.MeasurementGroup domainGroup =
        DomainMeasurementsFactory.gasFlowMeasurementGroup(groupIdentification, gasFlowMeasurements);
    final List<org.opensmartgridplatform.domain.da.measurements.MeasurementGroup> domainGroups =
        new ArrayList<>();
    domainGroups.add(domainGroup);

    final org.opensmartgridplatform.domain.da.measurements.MeasurementReport
        domainMeasurementReport =
            new org.opensmartgridplatform.domain.da.measurements.MeasurementReport(
                DomainMeasurementsFactory.spontaneousReportHeader(commonAddress), domainGroups);

    // Act
    final MeasurementReport actual =
        this.mapper.map(domainMeasurementReport, MeasurementReport.class);

    // Assert
    Assertions.assertThat(actual).isEqualToComparingFieldByFieldRecursively(expected);
  }

  @Test
  public void testTimestampMeasurementElementMapping() {
    // Arrange
    final TimestampMeasurementElement expected =
        WSMeasurementsFactory.timestampFrom(1556808340428l);
    final org.opensmartgridplatform.domain.da.measurements.elements.TimestampMeasurementElement
        domainMeasurementElement =
            new org.opensmartgridplatform.domain.da.measurements.elements
                .TimestampMeasurementElement(1556808340428l);

    // Act
    final TimestampMeasurementElement actual =
        this.mapper.map(domainMeasurementElement, TimestampMeasurementElement.class);

    // Assert
    Assertions.assertThat(actual.getValue()).isEqualTo(expected.getValue());
  }
}

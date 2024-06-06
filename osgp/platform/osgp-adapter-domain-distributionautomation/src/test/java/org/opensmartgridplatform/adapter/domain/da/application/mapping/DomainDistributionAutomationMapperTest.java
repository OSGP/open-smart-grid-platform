// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.domain.da.application.mapping;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.opensmartgridplatform.domain.da.measurements.Measurement;
import org.opensmartgridplatform.domain.da.measurements.MeasurementGroup;
import org.opensmartgridplatform.domain.da.measurements.MeasurementReport;
import org.opensmartgridplatform.domain.da.measurements.MeasurementReportHeader;
import org.opensmartgridplatform.domain.da.measurements.MeasurementType;
import org.opensmartgridplatform.domain.da.measurements.elements.BitmaskMeasurementElement;
import org.opensmartgridplatform.domain.da.measurements.elements.FloatMeasurementElement;
import org.opensmartgridplatform.domain.da.measurements.elements.TimestampMeasurementElement;
import org.opensmartgridplatform.dto.da.measurements.MeasurementDto;
import org.opensmartgridplatform.dto.da.measurements.MeasurementGroupDto;
import org.opensmartgridplatform.dto.da.measurements.MeasurementReportDto;
import org.opensmartgridplatform.dto.da.measurements.MeasurementReportHeaderDto;
import org.opensmartgridplatform.dto.da.measurements.elements.BitmaskMeasurementElementDto;
import org.opensmartgridplatform.dto.da.measurements.elements.FloatMeasurementElementDto;
import org.opensmartgridplatform.dto.da.measurements.elements.TimestampMeasurementElementDto;

public class DomainDistributionAutomationMapperTest {

  private final DomainDistributionAutomationMapper mapper =
      new DomainDistributionAutomationMapper();

  @Test
  public void testStringToMeasurementTypeConverter() {
    // Arrange
    final MeasurementType expected = MeasurementType.MEASURED_SHORT_FLOAT_WITH_TIME_TAG;
    final String dtoMeasurementType = "M_ME_TF_1";

    // Act
    final MeasurementType actual = this.mapper.map(dtoMeasurementType, MeasurementType.class);

    // Assert
    assertThat(actual).isEqualTo(expected);
  }

  @Test
  public void testBitmaskMeasurementElementMapping() {
    // Arrange
    final byte value = (byte) 40;
    final BitmaskMeasurementElement expected = new BitmaskMeasurementElement(value);
    final BitmaskMeasurementElementDto dtoMeasurementElement =
        new BitmaskMeasurementElementDto(value);

    // Act
    final BitmaskMeasurementElement actual =
        this.mapper.map(dtoMeasurementElement, BitmaskMeasurementElement.class);

    // Assert
    assertThat(actual).isEqualTo(expected);
  }

  @Test
  public void testFloatingPointMeasurementElementMapping() {
    // Arrange
    final float value = 83.4999f;
    final FloatMeasurementElement expected = new FloatMeasurementElement(value);
    final FloatMeasurementElementDto dtoMeasurementElement = new FloatMeasurementElementDto(value);

    // Act
    final FloatMeasurementElement actual =
        this.mapper.map(dtoMeasurementElement, FloatMeasurementElement.class);

    // Assert
    assertThat(actual).isEqualTo(expected);
  }

  @Test
  public void testTimestampMeasurementElementMapping() {
    // Arrange
    final long value = 1556808340428l;
    final TimestampMeasurementElement expected = new TimestampMeasurementElement(value);
    final TimestampMeasurementElementDto dtoMeasurementElement =
        new TimestampMeasurementElementDto(value);

    // Act
    final TimestampMeasurementElement actual =
        this.mapper.map(dtoMeasurementElement, TimestampMeasurementElement.class);

    // Assert
    assertThat(actual).isEqualTo(expected);
  }

  @Test
  public void testMeasurementMapping() {
    // Arrange
    final float gasFlowMeasurement = 3077f;
    final Measurement expected = DomainMeasurementsFactory.gasFlowMeasurement(gasFlowMeasurement);
    final MeasurementDto dtoMeasurement =
        DtoMeasurementsFactory.gasFlowMeasurement(gasFlowMeasurement);

    // Act
    final Measurement actual = this.mapper.map(dtoMeasurement, Measurement.class);

    // Assert
    assertThat(actual).isEqualTo(expected);
  }

  @Test
  public void testMeasurementGroupMapping() {
    // Arrange
    final String groupIdentification = "215";
    final float[] gasFlowMeasurements = {401.70001f, 88.575f};

    final MeasurementGroup expected =
        DomainMeasurementsFactory.gasFlowMeasurementGroup(groupIdentification, gasFlowMeasurements);
    final MeasurementGroupDto dtoMeasurementGroup =
        DtoMeasurementsFactory.gasFlowMeasurementGroup(groupIdentification, gasFlowMeasurements);

    // Act
    final MeasurementGroup actual = this.mapper.map(dtoMeasurementGroup, MeasurementGroup.class);

    // Assert
    assertThat(actual).isEqualTo(expected);
  }

  @Test
  public void testMeasurementReportHeaderMapping() {
    // Arrange
    final int commonAddress = 234;
    final MeasurementReportHeader expected =
        DomainMeasurementsFactory.spontaneousReportHeader(commonAddress);
    final MeasurementReportHeaderDto dtoMeasurementReportHeader =
        DtoMeasurementsFactory.spontaneousReportHeader(commonAddress);

    // Act
    final MeasurementReportHeader actual =
        this.mapper.map(dtoMeasurementReportHeader, MeasurementReportHeader.class);

    // Assert
    assertThat(actual).isEqualTo(expected);
  }

  @Test
  public void testMeasurementReportMapping() {
    // Arrange
    final int commonAddress = 55;
    final String groupIdentification = "137";
    final float[] gasFlowMeasurements = {78.733f, 21.000f};

    // Arrange domain
    final MeasurementGroup expectedGroup =
        DomainMeasurementsFactory.gasFlowMeasurementGroup(groupIdentification, gasFlowMeasurements);
    final List<MeasurementGroup> expectedGroups = new ArrayList<>();
    expectedGroups.add(expectedGroup);

    final MeasurementReport expected =
        new MeasurementReport(
            DomainMeasurementsFactory.spontaneousReportHeader(commonAddress), expectedGroups);

    // Arrange DTO
    final MeasurementReportHeaderDto dtoHeader =
        DtoMeasurementsFactory.spontaneousReportHeader(commonAddress);
    final MeasurementGroupDto dtoGroup =
        DtoMeasurementsFactory.gasFlowMeasurementGroup(groupIdentification, gasFlowMeasurements);
    final List<MeasurementGroupDto> dtoGroups = new ArrayList<>();
    dtoGroups.add(dtoGroup);
    final MeasurementReportDto dtoReport = new MeasurementReportDto(dtoHeader, dtoGroups);

    // Act
    final MeasurementReport actual = this.mapper.map(dtoReport, MeasurementReport.class);

    // Assert
    assertThat(actual).isEqualTo(expected);
  }
}

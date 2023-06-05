// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.domain.smartmetering.application.services;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import org.junit.jupiter.api.Test;
import org.opensmartgridplatform.adapter.domain.smartmetering.application.mapping.MonitoringMapper;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.ActiveEnergyValues;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.AmrProfileStatusCode;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.AmrProfileStatusCodeFlag;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.OsgpMeterValue;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.OsgpUnit;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.PeriodicMeterReads;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.PeriodicMeterReadsContainer;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ActiveEnergyValuesDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.AmrProfileStatusCodeDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.AmrProfileStatusCodeFlagDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.DlmsMeterValueDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.DlmsUnitTypeDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.PeriodTypeDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.PeriodicMeterReadsResponseDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.PeriodicMeterReadsResponseItemDto;

public class PeriodicMeterReadContainerMappingTest {

  private final MonitoringMapper monitoringMapper = new MonitoringMapper();

  // the List is not allowed to be null because of the way the constructor is
  // defined
  @Test
  public void testWithNullList() {
    final List<PeriodicMeterReadsResponseItemDto> meterReads = null;
    final PeriodTypeDto periodType = PeriodTypeDto.DAILY;

    assertThatExceptionOfType(NullPointerException.class)
        .isThrownBy(
            () -> {
              new PeriodicMeterReadsResponseDto(periodType, meterReads);
            });
  }

  // Test if mapping with an empty List succeeds
  @Test
  public void testWithEmptyList() {

    final List<PeriodicMeterReadsResponseItemDto> meterReads = new ArrayList<>();
    final PeriodTypeDto periodType = PeriodTypeDto.DAILY;

    final PeriodicMeterReadsResponseDto periodicMeterReadsContainerDto =
        new PeriodicMeterReadsResponseDto(periodType, meterReads);

    final PeriodicMeterReadsContainer periodicMeterReadContainer =
        this.monitoringMapper.map(
            periodicMeterReadsContainerDto, PeriodicMeterReadsContainer.class);

    assertThat(periodicMeterReadContainer).isNotNull();
    assertThat(periodicMeterReadContainer.getPeriodicMeterReads()).isEmpty();
    assertThat(periodicMeterReadContainer.getPeriodType().name())
        .isEqualTo(periodicMeterReadsContainerDto.getPeriodType().name());
  }

  // Test if mapping with a non-empty List succeeds
  @Test
  public void testWithNonEmptyList() {
    // build test data
    final DlmsMeterValueDto activeEnergyImport =
        new DlmsMeterValueDto(new BigDecimal(1.0), DlmsUnitTypeDto.M3);
    final DlmsMeterValueDto activeEnergyExport =
        new DlmsMeterValueDto(new BigDecimal(1.0), DlmsUnitTypeDto.M3);

    final Set<AmrProfileStatusCodeFlagDto> amrProfileStatusCodeFlagSet = new TreeSet<>();
    amrProfileStatusCodeFlagSet.add(AmrProfileStatusCodeFlagDto.CRITICAL_ERROR);
    final AmrProfileStatusCodeDto amrProfileStatusCodeDto =
        new AmrProfileStatusCodeDto(amrProfileStatusCodeFlagSet);

    final PeriodicMeterReadsResponseItemDto periodicMeterReadsDto =
        new PeriodicMeterReadsResponseItemDto(
            new Date(), activeEnergyImport, activeEnergyExport, amrProfileStatusCodeDto);
    final List<PeriodicMeterReadsResponseItemDto> meterReads = new ArrayList<>();
    meterReads.add(periodicMeterReadsDto);

    final PeriodTypeDto periodType = PeriodTypeDto.DAILY;

    final PeriodicMeterReadsResponseDto periodicMeterReadsContainerDto =
        new PeriodicMeterReadsResponseDto(periodType, meterReads);
    // actual mapping
    final PeriodicMeterReadsContainer periodicMeterReadsContainer =
        this.monitoringMapper.map(
            periodicMeterReadsContainerDto, PeriodicMeterReadsContainer.class);
    // test mapping
    assertThat(periodicMeterReadsContainer).isNotNull();
    assertThat(periodicMeterReadsContainer.getPeriodType().name())
        .isEqualTo(periodicMeterReadsContainerDto.getPeriodType().name());
    assertThat(periodicMeterReadsContainer.getPeriodicMeterReads().size())
        .isEqualTo(periodicMeterReadsContainerDto.getPeriodicMeterReads().size());
    assertThat(periodicMeterReadsContainer.getPeriodicMeterReads().get(0).getLogTime())
        .isEqualTo(periodicMeterReadsContainerDto.getPeriodicMeterReads().get(0).getLogTime());
    assertThat(
            periodicMeterReadsContainer
                .getPeriodicMeterReads()
                .get(0)
                .getActiveEnergyImport()
                .getValue())
        .isEqualTo(new BigDecimal("1.0"));
    assertThat(
            periodicMeterReadsContainer
                .getPeriodicMeterReads()
                .get(0)
                .getActiveEnergyImport()
                .getOsgpUnit())
        .isEqualTo(OsgpUnit.M3);
    assertThat(
            periodicMeterReadsContainer
                .getPeriodicMeterReads()
                .get(0)
                .getActiveEnergyExport()
                .getValue())
        .isEqualTo(new BigDecimal("1.0"));
    assertThat(
            periodicMeterReadsContainer
                .getPeriodicMeterReads()
                .get(0)
                .getActiveEnergyExport()
                .getOsgpUnit())
        .isEqualTo(OsgpUnit.M3);

    assertThat(
            periodicMeterReadsContainer
                .getPeriodicMeterReads()
                .get(0)
                .getAmrProfileStatusCode()
                .getAmrProfileStatusCodeFlags()
                .size())
        .isEqualTo(
            periodicMeterReadsContainerDto
                .getPeriodicMeterReads()
                .get(0)
                .getAmrProfileStatusCode()
                .getAmrProfileStatusCodeFlags()
                .size());

    assertThat(
            periodicMeterReadsContainer
                .getPeriodicMeterReads()
                .get(0)
                .getAmrProfileStatusCode()
                .getAmrProfileStatusCodeFlags()
                .contains(AmrProfileStatusCodeFlag.CRITICAL_ERROR))
        .isTrue();
  }

  @Test
  public void mapsPeriodicMeterReadsResponseItemDto() {
    final Date logTime = new Date();
    final ActiveEnergyValuesDto valuesDto =
        new ActiveEnergyValuesDto(
            new DlmsMeterValueDto(new BigDecimal("12.34"), DlmsUnitTypeDto.M3),
            new DlmsMeterValueDto(new BigDecimal("12.35"), DlmsUnitTypeDto.M3),
            new DlmsMeterValueDto(new BigDecimal("12.36"), DlmsUnitTypeDto.M3),
            new DlmsMeterValueDto(new BigDecimal("12.37"), DlmsUnitTypeDto.M3),
            new DlmsMeterValueDto(new BigDecimal("12.38"), DlmsUnitTypeDto.M3),
            new DlmsMeterValueDto(new BigDecimal("12.39"), DlmsUnitTypeDto.M3));
    final AmrProfileStatusCodeDto amrProfileStatusCodeDto =
        new AmrProfileStatusCodeDto(
            new HashSet<>(
                asList(
                    AmrProfileStatusCodeFlagDto.CRITICAL_ERROR,
                    AmrProfileStatusCodeFlagDto.CLOCK_ADJUSTED)));
    final PeriodicMeterReadsResponseItemDto source =
        new PeriodicMeterReadsResponseItemDto(logTime, valuesDto, amrProfileStatusCodeDto);

    final PeriodicMeterReads readsResult =
        this.monitoringMapper.map(source, PeriodicMeterReads.class);

    final ActiveEnergyValues expectedValues =
        new ActiveEnergyValues(
            new OsgpMeterValue(new BigDecimal("12.340"), OsgpUnit.M3),
            new OsgpMeterValue(new BigDecimal("12.350"), OsgpUnit.M3),
            new OsgpMeterValue(new BigDecimal("12.360"), OsgpUnit.M3),
            new OsgpMeterValue(new BigDecimal("12.370"), OsgpUnit.M3),
            new OsgpMeterValue(new BigDecimal("12.380"), OsgpUnit.M3),
            new OsgpMeterValue(new BigDecimal("12.390"), OsgpUnit.M3));
    final AmrProfileStatusCode amrProfileStatusCode =
        new AmrProfileStatusCode(
            new HashSet<>(
                asList(
                    AmrProfileStatusCodeFlag.CRITICAL_ERROR,
                    AmrProfileStatusCodeFlag.CLOCK_ADJUSTED)));
    final PeriodicMeterReads expectedReads =
        new PeriodicMeterReads(logTime, expectedValues, amrProfileStatusCode);
    assertThat(readsResult).usingRecursiveComparison().isEqualTo(expectedReads);
  }
}

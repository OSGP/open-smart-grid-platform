/*
 * Copyright 2014-2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.domain.smartmetering.application.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import org.junit.jupiter.api.Test;
import org.opensmartgridplatform.adapter.domain.smartmetering.application.mapping.MonitoringMapper;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.AmrProfileStatusCodeFlag;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.OsgpUnit;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.PeriodicMeterReadsContainerGas;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.AmrProfileStatusCodeDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.AmrProfileStatusCodeFlagDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.DlmsMeterValueDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.DlmsUnitTypeDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.PeriodTypeDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.PeriodicMeterReadGasResponseDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.PeriodicMeterReadsGasResponseItemDto;

public class PeriodicMeterReadsContainerGasMappingTest {

  private final MonitoringMapper monitoringMapper = new MonitoringMapper();

  // the List is not allowed to be null because of the way the constructor is
  // defined
  @Test
  public void testWithNullList() {
    final List<PeriodicMeterReadsGasResponseItemDto> meterReads = null;
    final PeriodTypeDto periodType = PeriodTypeDto.DAILY;

    assertThatExceptionOfType(NullPointerException.class)
        .isThrownBy(
            () -> {
              new PeriodicMeterReadGasResponseDto(periodType, meterReads);
            });
  }

  // Test if mapping with an empty List succeeds
  @Test
  public void testWithEmptyList() {

    final List<PeriodicMeterReadsGasResponseItemDto> meterReads = new ArrayList<>();
    final PeriodTypeDto periodType = PeriodTypeDto.DAILY;

    final PeriodicMeterReadGasResponseDto periodicMeterReadsContainerGasDto =
        new PeriodicMeterReadGasResponseDto(periodType, meterReads);

    final PeriodicMeterReadsContainerGas periodicMeterReadContainerGas =
        this.monitoringMapper.map(
            periodicMeterReadsContainerGasDto, PeriodicMeterReadsContainerGas.class);

    assertThat(periodicMeterReadContainerGas).isNotNull();
    assertThat(periodicMeterReadContainerGas.getPeriodicMeterReadsGas()).isEmpty();

    assertThat(periodicMeterReadContainerGas.getPeriodType().name())
        .isEqualTo(periodicMeterReadsContainerGasDto.getPeriodType().name());
  }

  // Test if mapping with a non-empty List succeeds
  @Test
  public void testWithNonEmptyList() {
    // build test data
    final DlmsMeterValueDto consumption =
        new DlmsMeterValueDto(new BigDecimal(1.0), DlmsUnitTypeDto.M3);

    final Set<AmrProfileStatusCodeFlagDto> amrProfileStatusCodeFlagSet = new TreeSet<>();
    amrProfileStatusCodeFlagSet.add(AmrProfileStatusCodeFlagDto.CRITICAL_ERROR);
    final AmrProfileStatusCodeDto amrProfileStatusCodeDto =
        new AmrProfileStatusCodeDto(amrProfileStatusCodeFlagSet);

    final PeriodicMeterReadsGasResponseItemDto periodicMeterReadsGasDto =
        new PeriodicMeterReadsGasResponseItemDto(
            new Date(), consumption, new Date(), amrProfileStatusCodeDto);
    final List<PeriodicMeterReadsGasResponseItemDto> meterReads = new ArrayList<>();
    meterReads.add(periodicMeterReadsGasDto);

    final PeriodTypeDto periodType = PeriodTypeDto.DAILY;

    final PeriodicMeterReadGasResponseDto periodicMeterReadsContainerDto =
        new PeriodicMeterReadGasResponseDto(periodType, meterReads);
    // actual mapping
    final PeriodicMeterReadsContainerGas periodicMeterReadsContainerGas =
        this.monitoringMapper.map(
            periodicMeterReadsContainerDto, PeriodicMeterReadsContainerGas.class);
    // test mapping

    assertThat(periodicMeterReadsContainerGas)
        .withFailMessage("Mapping must take place. So the result cannot be null.")
        .isNotNull();
    assertThat(periodicMeterReadsContainerGas.getPeriodType().name())
        .withFailMessage("After the mapping the name of the period must be the same.")
        .isEqualTo(periodicMeterReadsContainerDto.getPeriodType().name());

    assertThat(periodicMeterReadsContainerGas.getPeriodicMeterReadsGas().size())
        .withFailMessage(
            "The number of periodic meter reads before and after the mapping must be equal.")
        .isEqualTo(periodicMeterReadsContainerDto.getPeriodicMeterReadsGas().size());

    assertThat(periodicMeterReadsContainerGas.getPeriodicMeterReadsGas().get(0).getLogTime())
        .withFailMessage("After the mapping the log time of the first entry must be the same.")
        .isEqualTo(periodicMeterReadsContainerDto.getPeriodicMeterReadsGas().get(0).getLogTime());

    assertThat(periodicMeterReadsContainerGas.getPeriodicMeterReadsGas().get(0).getCaptureTime())
        .withFailMessage("After the mapping the capture time of the first entry must be the same.")
        .isEqualTo(
            periodicMeterReadsContainerDto.getPeriodicMeterReadsGas().get(0).getCaptureTime());

    assertThat(
            periodicMeterReadsContainerGas
                .getPeriodicMeterReadsGas()
                .get(0)
                .getConsumption()
                .getValue())
        .withFailMessage("After the mapping the consumption must be equal.")
        .isEqualTo(new BigDecimal("1.0"));

    assertThat(
            periodicMeterReadsContainerGas
                .getPeriodicMeterReadsGas()
                .get(0)
                .getConsumption()
                .getOsgpUnit())
        .withFailMessage("After the mapping the osgp unit value must be the same.")
        .isEqualTo(OsgpUnit.M3);

    assertThat(
            periodicMeterReadsContainerGas
                .getPeriodicMeterReadsGas()
                .get(0)
                .getAmrProfileStatusCode()
                .getAmrProfileStatusCodeFlags()
                .size())
        .withFailMessage(
            "After the mapping the size of the arm profile status code flags must be the same.")
        .isEqualTo(
            periodicMeterReadsContainerDto
                .getPeriodicMeterReadsGas()
                .get(0)
                .getAmrProfileStatusCode()
                .getAmrProfileStatusCodeFlags()
                .size());

    assertThat(
            periodicMeterReadsContainerGas
                .getPeriodicMeterReadsGas()
                .get(0)
                .getAmrProfileStatusCode()
                .getAmrProfileStatusCodeFlags()
                .contains(AmrProfileStatusCodeFlag.CRITICAL_ERROR))
        .withFailMessage(
            "After the mapping the amr profile status code flags must contain the CRITICAL_ERROR flag.")
        .isTrue();
  }
}

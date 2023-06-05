// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.domain.smartmetering.application.services;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.opensmartgridplatform.adapter.domain.smartmetering.application.mapping.ConfigurationMapper;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.CosemDate;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.SpecialDay;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.SpecialDaysRequest;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.SpecialDaysRequestData;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.CosemDateDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.SpecialDayDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.SpecialDaysRequestDataDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.SpecialDaysRequestDto;

// Tests the mapping of SpecialDaysRequest objects in ConfigurationService.
public class SpecialDaysRequestMappingTest {

  private final ConfigurationMapper configurationMapper = new ConfigurationMapper();

  // To check mapping of SpecialDaysRequest, when its SpecialDaysRequestData
  // is null
  @Test
  public void testSpecialDaysRequestMappingNull() {
    final String deviceIdentification = "nr1";
    final SpecialDaysRequestData specialDaysRequestData = null;
    final SpecialDaysRequest specialDaysRequestValueObject =
        new SpecialDaysRequest(deviceIdentification, specialDaysRequestData);

    final SpecialDaysRequestDto specialDaysRequestDto =
        this.configurationMapper.map(specialDaysRequestValueObject, SpecialDaysRequestDto.class);

    assertThat(specialDaysRequestDto).isNotNull();
    assertThat(specialDaysRequestDto.getDeviceIdentification()).isEqualTo(deviceIdentification);
    assertThat(specialDaysRequestDto.getSpecialDaysRequestData()).isNull();
  }

  // To check mapping of SpecialDaysRequest, when its SpecialDaysRequestData
  // has an empty List.
  @Test
  public void testSpecialDaysRequestMappingEmptyList() {
    final String deviceIdentification = "nr1";
    final SpecialDaysRequestData specialDaysRequestData =
        new SpecialDaysRequestDataBuilder().build();
    final SpecialDaysRequest specialDaysRequestValueObject =
        new SpecialDaysRequest(deviceIdentification, specialDaysRequestData);

    final SpecialDaysRequestDto specialDaysRequestDto =
        this.configurationMapper.map(specialDaysRequestValueObject, SpecialDaysRequestDto.class);

    assertThat(specialDaysRequestDto.getDeviceIdentification()).isEqualTo(deviceIdentification);
    assertThat(specialDaysRequestDto.getSpecialDaysRequestData()).isNotNull();
  }

  // To check mapping of SpecialDaysRequest, when its SpecialDaysRequestData
  // has a filled List (1 value).
  @Test
  public void testSpecialDaysRequestMappingNonEmptyList() {
    final String deviceIdentification = "nr1";
    final int year = 2016;
    final int month = 3;
    final int dayOfMonth = 11;
    final int dayId = 1;
    final SpecialDay specialDay = new SpecialDay(new CosemDate(year, month, dayOfMonth), dayId);
    final SpecialDaysRequestData specialDaysRequestData =
        new SpecialDaysRequestDataBuilder().addSpecialDay(specialDay).build();
    final SpecialDaysRequest specialDaysRequestValueObject =
        new SpecialDaysRequest(deviceIdentification, specialDaysRequestData);

    final SpecialDaysRequestDto specialDaysRequestDto =
        this.configurationMapper.map(specialDaysRequestValueObject, SpecialDaysRequestDto.class);
    assertThat(specialDaysRequestDto.getDeviceIdentification()).isEqualTo(deviceIdentification);

    final SpecialDaysRequestDataDto requestDataDto =
        specialDaysRequestDto.getSpecialDaysRequestData();
    assertThat(requestDataDto).isNotNull();
    assertThat(requestDataDto.getSpecialDays()).isNotNull();
    assertThat(requestDataDto.getSpecialDays().size()).isEqualTo(dayId);

    final SpecialDayDto specialDayDto = requestDataDto.getSpecialDays().get(0);
    assertThat(specialDayDto.getDayId()).isEqualTo(dayId);

    final CosemDateDto specialDayDateDto = specialDayDto.getSpecialDayDate();
    assertThat(specialDayDateDto.getYear()).isEqualTo(year);
    assertThat(specialDayDateDto.getMonth()).isEqualTo(month);
    assertThat(specialDayDateDto.getDayOfMonth()).isEqualTo(dayOfMonth);
  }

  // To check mapping of SpecialDaysRequest, when its SpecialDaysRequestData
  // has a filled List (1 value), where CosemDate is not specified.
  @Test
  public void testSpecialDaysRequestMappingNonEmptyListNoCosemDate() {
    final String deviceIdentification = "nr1";
    final int dayId = 1;
    final SpecialDay specialDay = new SpecialDay(new CosemDate(), dayId);
    final SpecialDaysRequestData specialDaysRequestData =
        new SpecialDaysRequestDataBuilder().addSpecialDay(specialDay).build();
    final SpecialDaysRequest specialDaysRequestValueObject =
        new SpecialDaysRequest(deviceIdentification, specialDaysRequestData);

    final SpecialDaysRequestDto specialDaysRequestDto =
        this.configurationMapper.map(specialDaysRequestValueObject, SpecialDaysRequestDto.class);
    assertThat(specialDaysRequestDto.getDeviceIdentification()).isEqualTo(deviceIdentification);

    final SpecialDaysRequestDataDto requestDataDto =
        specialDaysRequestDto.getSpecialDaysRequestData();
    assertThat(requestDataDto).isNotNull();
    assertThat(requestDataDto.getSpecialDays()).isNotNull();
    assertThat(requestDataDto.getSpecialDays().size()).isEqualTo(dayId);

    final SpecialDayDto specialDayDto = requestDataDto.getSpecialDays().get(0);
    assertThat(specialDayDto.getDayId()).isEqualTo(dayId);

    final CosemDate specialDayDate = specialDay.getSpecialDayDate();
    final CosemDateDto specialDayDateDto = specialDayDto.getSpecialDayDate();
    assertThat(specialDayDateDto.getYear()).isEqualTo(specialDayDate.getYear());
    assertThat(specialDayDateDto.getMonth()).isEqualTo(specialDayDate.getMonth());
    assertThat(specialDayDateDto.getDayOfMonth()).isEqualTo(specialDayDate.getDayOfMonth());
  }
}

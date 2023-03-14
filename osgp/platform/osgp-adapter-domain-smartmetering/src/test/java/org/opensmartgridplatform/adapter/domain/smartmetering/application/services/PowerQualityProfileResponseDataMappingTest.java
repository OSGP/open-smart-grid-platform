/*
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.domain.smartmetering.application.services;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.opensmartgridplatform.adapter.domain.smartmetering.application.mapping.MonitoringMapper;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.CaptureObject;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.OsgpUnit;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.PowerQualityProfileData;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.ProfileEntry;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.ProfileEntryValue;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.ProfileType;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.CaptureObjectDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ObisCodeValuesDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.PowerQualityProfileDataDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ProfileEntryDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ProfileEntryValueDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ProfileTypeDto;

public class PowerQualityProfileResponseDataMappingTest {

  private final MonitoringMapper mapper = new MonitoringMapper();

  @ParameterizedTest
  @ValueSource(strings = {"PUBLIC", "PRIVATE"})
  public void testConvertGetPowerQualityProfileResponseDto(final String profileType) {
    final PowerQualityProfileDataDto dto = this.makePowerQualityProfileResponseDataDto(profileType);
    final PowerQualityProfileData result = this.mapper.map(dto, PowerQualityProfileData.class);
    assertThat(result)
        .withFailMessage("mapping GetPowerQualityProfileResponseData should not return null")
        .isNotNull();
    assertThat(result)
        .withFailMessage("mapping GetPowerQualityProfileResponseData should return correct type")
        .isOfAnyClassIn(PowerQualityProfileData.class);
    assertThat(result.getProfileType()).isEqualTo(ProfileType.valueOf(profileType));
  }

  @Test
  public void testConvertcaptureObjectDto() {
    final CaptureObject result = this.mapper.map(this.captureObjectDto(), CaptureObject.class);
    assertThat(result)
        .withFailMessage("mapping CaptureObjectDto should not return null")
        .isNotNull();
    assertThat(result)
        .withFailMessage("mapping CaptureObjectDto should return correct type")
        .isOfAnyClassIn(CaptureObject.class);
  }

  @Test
  public void testConvertProfileEntryValueDto() {
    final ProfileEntryValue result =
        this.mapper.map(this.profileEntryDtoDate(), ProfileEntryValue.class);
    assertThat(result)
        .withFailMessage("mapping ProfileEntryValueDto should not return null")
        .isNotNull();
    assertThat(result)
        .withFailMessage("mapping ProfileEntryValueDto should return correct type")
        .isOfAnyClassIn(ProfileEntryValue.class);
  }

  @Test
  public void testConvertProfileEntryDto() {
    final ProfileEntry result = this.mapper.map(this.profileEntryDto(), ProfileEntry.class);
    assertThat(result)
        .withFailMessage("mapping ProfileEntryDto should not return null")
        .isNotNull();
    assertThat(result)
        .withFailMessage("mapping ProfileEntryDto should return correct type")
        .isOfAnyClassIn(ProfileEntry.class);
  }

  private PowerQualityProfileDataDto makePowerQualityProfileResponseDataDto(
      final String profileType) {
    return new PowerQualityProfileDataDto(
        this.obisCodeDto(),
        this.makeCaptureObjectsDto(),
        this.makeProfileEntryDtoList(),
        ProfileTypeDto.valueOf(profileType));
  }

  private List<CaptureObjectDto> makeCaptureObjectsDto() {
    final List<CaptureObjectDto> captureObjects = new ArrayList<>();
    captureObjects.add(this.captureObjectDto());
    return captureObjects;
  }

  private List<ProfileEntryDto> makeProfileEntryDtoList() {
    final List<ProfileEntryDto> result = new ArrayList<>();
    result.add(this.profileEntryDto());
    return result;
  }

  private ProfileEntryValueDto makeProfileEntryValueDto() {
    final ProfileEntryValueDto result = new ProfileEntryValueDto("test");
    return result;
  }

  private CaptureObjectDto captureObjectDto() {
    return new CaptureObjectDto(10L, "0.0.1.0.0.255", 2, 0, OsgpUnit.KWH.name());
  }

  private ProfileEntryDto profileEntryDto() {
    final List<ProfileEntryValueDto> entriesDto = new ArrayList<>();
    entriesDto.add(this.makeProfileEntryValueDto());
    return new ProfileEntryDto(entriesDto);
  }

  private ProfileEntryValueDto profileEntryDtoDate() {
    return new ProfileEntryValueDto(new Date());
  }

  private ObisCodeValuesDto obisCodeDto() {
    return new ObisCodeValuesDto((byte) 1, (byte) 1, (byte) 1, (byte) 1, (byte) 1, (byte) 1);
  }

  XMLGregorianCalendar createXMLGregorianCalendar(final Date date) {
    try {
      final GregorianCalendar gregCal = new GregorianCalendar();
      gregCal.setTime(date);
      return DatatypeFactory.newInstance().newXMLGregorianCalendar(gregCal);
    } catch (final DatatypeConfigurationException e) {
      throw new RuntimeException(e);
    }
  }
}

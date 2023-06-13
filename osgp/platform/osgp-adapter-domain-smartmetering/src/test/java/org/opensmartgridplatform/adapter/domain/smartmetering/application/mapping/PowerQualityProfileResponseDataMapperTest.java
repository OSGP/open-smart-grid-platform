// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.domain.smartmetering.application.mapping;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.PowerQualityProfileData;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.ProfileEntryValue;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.ProfileType;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.CaptureObjectDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ObisCodeValuesDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.PowerQualityProfileDataDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ProfileEntryDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ProfileEntryValueDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ProfileTypeDto;

public class PowerQualityProfileResponseDataMapperTest {

  private final MonitoringMapper mapper = new MonitoringMapper();

  private static final Class<?>[] EXPECTED_CLASS =
      new Class<?>[] {String.class, Date.class, BigDecimal.class, Long.class};

  @ParameterizedTest
  @ValueSource(strings = {"PUBLIC", "PRIVATE"})
  public void testConvertGetPowerQualityProfileResponseVo(final String profileType) {
    final PowerQualityProfileDataDto responseDto = this.makeResponseDataDto(profileType);
    final PowerQualityProfileData responseVo =
        this.mapper.map(responseDto, PowerQualityProfileData.class);
    assertThat(responseVo).withFailMessage("response object should not be null").isNotNull();

    assertThat(responseVo.getProfileEntries().get(0).getProfileEntryValues().size())
        .withFailMessage("response object should return same number of profilentries")
        .isEqualTo(EXPECTED_CLASS.length);
    assertThat(responseVo.getProfileType()).isEqualTo(ProfileType.valueOf(profileType));

    int i = 0;
    for (final ProfileEntryValue profileEntryValueVo :
        responseVo.getProfileEntries().get(0).getProfileEntryValues()) {
      final Class<?> clazz = profileEntryValueVo.getValue().getClass();
      assertThat(clazz)
          .withFailMessage("the return class should be of the same type")
          .isEqualTo(EXPECTED_CLASS[i++]);
    }
  }

  private PowerQualityProfileDataDto makeResponseDataDto(final String profileType) {
    final ObisCodeValuesDto obisCodeValuesDto =
        new ObisCodeValuesDto((byte) 1, (byte) 1, (byte) 1, (byte) 1, (byte) 1, (byte) 1);
    return new PowerQualityProfileDataDto(
        obisCodeValuesDto,
        this.makeCaptureObjectDtos(),
        this.makeProfileEntryDtos(),
        ProfileTypeDto.valueOf(profileType));
  }

  private List<CaptureObjectDto> makeCaptureObjectDtos() {
    return new ArrayList<>();
  }

  private List<ProfileEntryDto> makeProfileEntryDtos() {
    final List<ProfileEntryDto> result = new ArrayList<>();
    result.add(new ProfileEntryDto(this.makeProfileEntryValueDtos()));
    return result;
  }

  private List<ProfileEntryValueDto> makeProfileEntryValueDtos() {
    final List<ProfileEntryValueDto> result = new ArrayList<>();
    result.add(new ProfileEntryValueDto("Test"));
    result.add(new ProfileEntryValueDto(new Date()));
    result.add(new ProfileEntryValueDto(new BigDecimal(123.45d)));
    result.add(new ProfileEntryValueDto(12345L));
    return result;
  }
}

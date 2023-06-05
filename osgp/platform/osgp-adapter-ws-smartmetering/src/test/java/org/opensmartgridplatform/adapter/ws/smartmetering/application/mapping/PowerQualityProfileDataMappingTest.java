// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.ws.smartmetering.application.mapping;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.xml.datatype.XMLGregorianCalendar;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.common.CaptureObject;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.common.OsgpUnitType;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.common.ProfileEntryValue;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.ObisCodeValues;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.PowerQualityProfileData;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.ProfileEntry;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.ProfileType;

class PowerQualityProfileDataMappingTest {

  private static final String[] EXPECTED_CLASS =
      new String[] {
        String.class.getSimpleName(),
        "XMLGregorianCalendarImpl",
        BigDecimal.class.getSimpleName(),
        Long.class.getSimpleName()
      };

  private final MonitoringMapper monitoringMapper = new MonitoringMapper();

  private List<org.opensmartgridplatform.domain.core.valueobjects.smartmetering.CaptureObject>
      makeCaptureObjectsVo() {
    final List<org.opensmartgridplatform.domain.core.valueobjects.smartmetering.CaptureObject>
        captureObjectVos = new ArrayList<>();
    captureObjectVos.add(this.makeCaptureObjectVo());
    return captureObjectVos;
  }

  private org.opensmartgridplatform.domain.core.valueobjects.smartmetering.CaptureObject
      makeCaptureObjectVo() {
    return new org.opensmartgridplatform.domain.core.valueobjects.smartmetering.CaptureObject(
        10L, "0.0.1.0.0.255", 10, 1, OsgpUnitType.UNDEFINED.name());
  }

  private ObisCodeValues makeObisCode() {
    return new ObisCodeValues((byte) 1, (byte) 1, (byte) 1, (byte) 1, (byte) 1, (byte) 1);
  }

  private List<ProfileEntry> makeProfileEntriesVo() {
    final List<ProfileEntry> profileEntries = new ArrayList<>();
    profileEntries.add(this.makeProfileEntryVo());
    profileEntries.add(this.makeProfileEntryVo());
    return profileEntries;
  }

  private ProfileEntry makeProfileEntryVo() {
    final List<org.opensmartgridplatform.domain.core.valueobjects.smartmetering.ProfileEntryValue>
        entriesVo = new ArrayList<>();
    entriesVo.add(
        new org.opensmartgridplatform.domain.core.valueobjects.smartmetering.ProfileEntryValue(
            "test"));
    entriesVo.add(
        new org.opensmartgridplatform.domain.core.valueobjects.smartmetering.ProfileEntryValue(
            new Date()));
    entriesVo.add(
        new org.opensmartgridplatform.domain.core.valueobjects.smartmetering.ProfileEntryValue(
            new BigDecimal(100.5d)));
    entriesVo.add(
        new org.opensmartgridplatform.domain.core.valueobjects.smartmetering.ProfileEntryValue(
            12345L));
    return new ProfileEntry(entriesVo);
  }

  private PowerQualityProfileData makeresponseVo() {
    final PowerQualityProfileData result =
        new PowerQualityProfileData(
            this.makeObisCode(),
            this.makeCaptureObjectsVo(),
            this.makeProfileEntriesVo(),
            ProfileType.PUBLIC);
    return result;
  }

  @Test
  void testCaptureObject() {
    final org.opensmartgridplatform.domain.core.valueobjects.smartmetering.CaptureObject
        captureObjectVo = this.makeCaptureObjectVo();
    final CaptureObject captureObject =
        this.monitoringMapper.map(captureObjectVo, CaptureObject.class);
    assertThat(captureObject).as("mapping CaptureObject should not return null").isNotNull();
  }

  @Test
  void testProfileEntry() {
    org.opensmartgridplatform.domain.core.valueobjects.smartmetering.ProfileEntryValue
        profileEntryValueVo =
            new org.opensmartgridplatform.domain.core.valueobjects.smartmetering.ProfileEntryValue(
                "test");
    ProfileEntryValue profileEntryValue =
        this.monitoringMapper.map(profileEntryValueVo, ProfileEntryValue.class);

    assertThat(profileEntryValue.getStringValueOrDateValueOrFloatValue().get(0)).isEqualTo("test");

    profileEntryValueVo =
        new org.opensmartgridplatform.domain.core.valueobjects.smartmetering.ProfileEntryValue(
            new Date());
    profileEntryValue = this.monitoringMapper.map(profileEntryValueVo, ProfileEntryValue.class);
    assertThat(profileEntryValue.getStringValueOrDateValueOrFloatValue().get(0))
        .isInstanceOf(XMLGregorianCalendar.class);

    profileEntryValueVo =
        new org.opensmartgridplatform.domain.core.valueobjects.smartmetering.ProfileEntryValue(
            new BigDecimal(100.0d));
    profileEntryValue = this.monitoringMapper.map(profileEntryValueVo, ProfileEntryValue.class);
    assertThat(
            ((BigDecimal) profileEntryValue.getStringValueOrDateValueOrFloatValue().get(0))
                    .doubleValue()
                == 100.d)
        .isTrue();

    profileEntryValueVo =
        new org.opensmartgridplatform.domain.core.valueobjects.smartmetering.ProfileEntryValue(
            12345L);
    profileEntryValue = this.monitoringMapper.map(profileEntryValueVo, ProfileEntryValue.class);
    assertThat(
            ((Long) profileEntryValue.getStringValueOrDateValueOrFloatValue().get(0)).doubleValue()
                == 12345L)
        .isTrue();
  }

  @Test
  void testPowerQualityProfileDataResponse() {
    final PowerQualityProfileData source = this.makeresponseVo();
    final org.opensmartgridplatform.adapter.ws.schema.smartmetering.monitoring
            .PowerQualityProfileData
        target =
            this.monitoringMapper.map(
                source,
                org.opensmartgridplatform.adapter.ws.schema.smartmetering.monitoring
                    .PowerQualityProfileData.class);

    assertThat(target)
        .as("mapping GetPowerQualityProfileResponseData should not return null")
        .isNotNull();
    Assertions.assertThat(target.getCaptureObjectList().getCaptureObjects()).hasSize(1);
    Assertions.assertThat(
            target.getProfileEntryList().getProfileEntries().get(0).getProfileEntryValue())
        .isNotNull();
    Assertions.assertThat(
            target.getProfileEntryList().getProfileEntries().get(0).getProfileEntryValue())
        .hasSize(4);
    assertThat(target.getProfileType()).hasToString(ProfileType.PUBLIC.toString());

    int i = 0;
    for (final ProfileEntryValue profileEntryValue :
        target.getProfileEntryList().getProfileEntries().get(0).getProfileEntryValue()) {
      assertThat(profileEntryValue.getStringValueOrDateValueOrFloatValue()).isNotNull();
      assertThat(profileEntryValue.getStringValueOrDateValueOrFloatValue()).hasSize(1);
      final Class<?> clazz =
          profileEntryValue.getStringValueOrDateValueOrFloatValue().get(0).getClass();

      assertThat(clazz.getSimpleName()).isEqualTo(EXPECTED_CLASS[i++]);
      assertThat(
              profileEntryValue.getStringValueOrDateValueOrFloatValue() != null
                  && !profileEntryValue.getStringValueOrDateValueOrFloatValue().isEmpty())
          .isTrue();
    }
  }
}

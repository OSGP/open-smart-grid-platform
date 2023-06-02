//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.adapter.ws.smartmetering.application.mapping;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import javax.xml.datatype.XMLGregorianCalendar;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.jupiter.api.Test;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.common.OsgpUnitType;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.CaptureObject;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.GetPowerQualityProfileResponse;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.ObisCodeValues;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.PowerQualityProfileData;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.ProfileEntry;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.ProfileEntryValue;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.ProfileType;

public class GetPowerQualityProfileResponseMappingTest {

  private static final String MAPPED_FIELD_VALUE_MESSAGE =
      "Mapped fields should have the same value.";
  private static final String MAPPED_LIST_SIZE_MESSAGE = "Mapped lists should have the same size.";

  private final MonitoringMapper monitoringMapper = new MonitoringMapper();

  private void assertCaptureObject(
      final org.opensmartgridplatform.adapter.ws.schema.smartmetering.common.CaptureObject
          actualCaptureObject,
      final CaptureObject sourceCaptureObject)
      throws AssertionError {

    assertThat(actualCaptureObject.getClassId())
        .as(MAPPED_FIELD_VALUE_MESSAGE)
        .isEqualTo(sourceCaptureObject.getClassId());
    assertThat(actualCaptureObject.getLogicalName())
        .as(MAPPED_FIELD_VALUE_MESSAGE)
        .isEqualTo(sourceCaptureObject.getLogicalName());
    assertThat(actualCaptureObject.getAttributeIndex().intValue())
        .as(MAPPED_FIELD_VALUE_MESSAGE)
        .isEqualTo(sourceCaptureObject.getAttributeIndex());
    assertThat(actualCaptureObject.getDataIndex())
        .as(MAPPED_FIELD_VALUE_MESSAGE)
        .isEqualTo(sourceCaptureObject.getDataIndex());
  }

  private void assertCaptureObjects(
      final List<org.opensmartgridplatform.adapter.ws.schema.smartmetering.common.CaptureObject>
          actualCaptureObjects,
      final List<CaptureObject> sourceCaptureObjects)
      throws AssertionError {

    assertThat(actualCaptureObjects.size())
        .as(MAPPED_LIST_SIZE_MESSAGE)
        .isEqualTo(sourceCaptureObjects.size());
    for (int i = 0; i < actualCaptureObjects.size(); i++) {
      this.assertCaptureObject(actualCaptureObjects.get(i), sourceCaptureObjects.get(i));
    }
  }

  private void assertObisCode(
      final org.opensmartgridplatform.adapter.ws.schema.smartmetering.common.ObisCodeValues
          actualObisCode,
      final ObisCodeValues sourceObisCode)
      throws AssertionError {

    assertThat(actualObisCode.getA())
        .as(MAPPED_FIELD_VALUE_MESSAGE)
        .isEqualTo(sourceObisCode.getA());
    assertThat(actualObisCode.getB())
        .as(MAPPED_FIELD_VALUE_MESSAGE)
        .isEqualTo(sourceObisCode.getB());
    assertThat(actualObisCode.getC())
        .as(MAPPED_FIELD_VALUE_MESSAGE)
        .isEqualTo(sourceObisCode.getC());
    assertThat(actualObisCode.getD())
        .as(MAPPED_FIELD_VALUE_MESSAGE)
        .isEqualTo(sourceObisCode.getD());
    assertThat(actualObisCode.getE())
        .as(MAPPED_FIELD_VALUE_MESSAGE)
        .isEqualTo(sourceObisCode.getE());
    assertThat(actualObisCode.getF())
        .as(MAPPED_FIELD_VALUE_MESSAGE)
        .isEqualTo(sourceObisCode.getF());
  }

  private void assertProfileEntries(
      final List<org.opensmartgridplatform.adapter.ws.schema.smartmetering.common.ProfileEntry>
          actualProfileEntries,
      final List<ProfileEntry> sourceProfileEntries)
      throws AssertionError {

    assertThat(actualProfileEntries.size())
        .as(MAPPED_LIST_SIZE_MESSAGE)
        .isEqualTo(sourceProfileEntries.size());
    for (int i = 0; i < actualProfileEntries.size(); i++) {
      this.assertProfileEntry(actualProfileEntries.get(i), sourceProfileEntries.get(i));
    }
  }

  private void assertProfileEntry(
      final org.opensmartgridplatform.adapter.ws.schema.smartmetering.common.ProfileEntry
          actualProfileEntry,
      final ProfileEntry sourceProfileEntry)
      throws AssertionError {

    assertThat(actualProfileEntry.getProfileEntryValue().size())
        .as(MAPPED_LIST_SIZE_MESSAGE)
        .isEqualTo(sourceProfileEntry.getProfileEntryValues().size());
    for (int i = 0; i < actualProfileEntry.getProfileEntryValue().size(); i++) {
      this.assertProfileEntryValue(
          actualProfileEntry.getProfileEntryValue().get(i),
          sourceProfileEntry.getProfileEntryValues().get(i));
    }
  }

  private void assertProfileEntryValue(
      final org.opensmartgridplatform.adapter.ws.schema.smartmetering.common.ProfileEntryValue
          actualProfileEntryValue,
      final ProfileEntryValue sourceProfileEntryValue)
      throws AssertionError {

    final Object actual = actualProfileEntryValue.getStringValueOrDateValueOrFloatValue().get(0);
    if (actual instanceof XMLGregorianCalendar) {
      final Date actualDate = ((XMLGregorianCalendar) actual).toGregorianCalendar().getTime();
      assertThat(actualDate)
          .as(MAPPED_FIELD_VALUE_MESSAGE)
          .isEqualTo(sourceProfileEntryValue.getValue());
    } else {
      assertThat(actual)
          .as(MAPPED_FIELD_VALUE_MESSAGE)
          .isEqualTo(sourceProfileEntryValue.getValue());
    }
  }

  private List<CaptureObject> makeCaptureObjects() {
    final List<CaptureObject> captureObjects = new ArrayList<>();
    captureObjects.add(new CaptureObject(8L, "0.0.1.0.0.255", 2, 0, OsgpUnitType.UNDEFINED.name()));
    captureObjects.add(new CaptureObject(3L, "1.1.1.0.0.255", 2, 0, OsgpUnitType.W.name()));
    captureObjects.add(new CaptureObject(3L, "2.2.2.0.0.255", 2, 0, OsgpUnitType.W.name()));
    captureObjects.add(new CaptureObject(4L, "3.3.3.0.0.255", 2, 0, OsgpUnitType.W.name()));
    return captureObjects;
  }

  private ObisCodeValues makeObisCode() {
    return new ObisCodeValues((byte) 1, (byte) 2, (byte) 3, (byte) 4, (byte) 5, (byte) 6);
  }

  private List<ProfileEntry> makeProfileEntries() {
    final List<ProfileEntry> profileEntries = new ArrayList<>();
    profileEntries.add(
        this.makeProfileEntry(
            new DateTime(2017, 1, 1, 1, 0, 0, DateTimeZone.UTC).toDate(),
            "test1",
            new BigDecimal(1.1d),
            111L));
    profileEntries.add(
        this.makeProfileEntry(
            new DateTime(2017, 2, 2, 2, 0, 0, DateTimeZone.UTC).toDate(),
            "test2",
            new BigDecimal(2.2d),
            222L));
    return profileEntries;
  }

  private ProfileEntry makeProfileEntry(final Serializable... values) {
    final List<ProfileEntryValue> profileEntries = new ArrayList<>();
    for (final Serializable value : values) {
      profileEntries.add(new ProfileEntryValue(value));
    }
    return new ProfileEntry(profileEntries);
  }

  @Test
  public void shouldConvertGetPowerQualityProfileResponse() {
    // Arrange
    final ObisCodeValues obisCode = this.makeObisCode();
    final List<CaptureObject> captureObjects = this.makeCaptureObjects();
    final List<ProfileEntry> profileEntries = this.makeProfileEntries();

    final PowerQualityProfileData responseData =
        new PowerQualityProfileData(obisCode, captureObjects, profileEntries, ProfileType.PUBLIC);

    final GetPowerQualityProfileResponse source = new GetPowerQualityProfileResponse();
    source.setPowerQualityProfileDatas(Collections.singletonList(responseData));

    // Act
    final org.opensmartgridplatform.adapter.ws.schema.smartmetering.bundle
            .GetPowerQualityProfileResponse
        target =
            this.monitoringMapper.map(
                source,
                org.opensmartgridplatform.adapter.ws.schema.smartmetering.bundle
                    .GetPowerQualityProfileResponse.class);

    // Assert
    final org.opensmartgridplatform.adapter.ws.schema.smartmetering.monitoring
            .PowerQualityProfileData
        mappedResponseData = target.getPowerQualityProfileDatas().get(0);

    this.assertObisCode(mappedResponseData.getLogicalName(), obisCode);
    this.assertCaptureObjects(
        mappedResponseData.getCaptureObjectList().getCaptureObjects(), captureObjects);
    this.assertProfileEntries(
        mappedResponseData.getProfileEntryList().getProfileEntries(), profileEntries);
  }
}

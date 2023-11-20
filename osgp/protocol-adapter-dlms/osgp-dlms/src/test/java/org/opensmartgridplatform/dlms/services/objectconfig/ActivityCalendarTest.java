// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.dlms.services.objectconfig;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.opensmartgridplatform.dlms.exceptions.ObjectConfigException;
import org.opensmartgridplatform.dlms.objectconfig.AccessType;
import org.opensmartgridplatform.dlms.objectconfig.Attribute;
import org.opensmartgridplatform.dlms.objectconfig.CosemObject;
import org.opensmartgridplatform.dlms.objectconfig.DlmsDataType;
import org.opensmartgridplatform.dlms.objectconfig.DlmsObjectType;
import org.opensmartgridplatform.dlms.objectconfig.ValueType;
import org.opensmartgridplatform.dlms.services.ObjectConfigService;

class ActivityCalendarTest {

  ObjectConfigService objectConfigService;

  @BeforeEach
  void setup() throws IOException, ObjectConfigException {
    this.objectConfigService = new ObjectConfigService();
  }

  @ParameterizedTest
  @CsvSource({"DSMR,2.2", "DSMR,4.2.2", "SMR,4.3", "SMR,5.1", "SMR,5.2", "SMR,5.5"})
  void shouldHaveActivityCalendarAttributes(final String protocol, final String version)
      throws ObjectConfigException {

    final CosemObject cosemObject =
        this.objectConfigService.getCosemObject(
            protocol, version, DlmsObjectType.ACTIVITY_CALENDAR);

    assertThat(cosemObject).isNotNull();
    assertThat(cosemObject.getAttributes()).isNotNull();
    assertThat(cosemObject.getAttributes()).isNotEmpty();
    assertThat(cosemObject.getAttributes()).hasSize(9);

    assertThat(cosemObject.getTag()).isEqualTo("ACTIVITY_CALENDAR");
    assertThat(cosemObject.getDescription()).isEqualTo("Activity calendar");
    assertThat(cosemObject.getNote()).isNull();
    assertThat(cosemObject.getClassId()).isEqualTo(20);
    assertThat(cosemObject.getVersion()).isZero();
    assertThat(cosemObject.getObis()).isEqualTo("0.0.13.0.0.255");
    assertThat(cosemObject.getGroup()).isEqualTo("ABSTRACT");
    assertThat(cosemObject.getMeterTypes()).hasToString("[SP, PP]");

    final AttributeValidatorDTO calendarNameActive =
        AttributeValidatorDTO.builder()
            .id(2)
            .description("calendar_name_active")
            .datatype(DlmsDataType.OCTET_STRING)
            .valuetype(ValueType.DYNAMIC)
            .value(null)
            .access(AccessType.R)
            .build();

    final AttributeValidatorDTO seasonProfileActive =
        AttributeValidatorDTO.builder()
            .id(3)
            .description("season_profile_active")
            .datatype(DlmsDataType.ARRAY)
            .valuetype(ValueType.DYNAMIC)
            .value(null)
            .access(AccessType.R)
            .build();

    final AttributeValidatorDTO weekProfileTableActive =
        AttributeValidatorDTO.builder()
            .id(4)
            .description("week_profile_table_active")
            .datatype(DlmsDataType.ARRAY)
            .valuetype(ValueType.DYNAMIC)
            .value(null)
            .access(AccessType.R)
            .build();

    final AttributeValidatorDTO dayProfileTableActive =
        AttributeValidatorDTO.builder()
            .id(5)
            .description("day_profile_table_active")
            .datatype(DlmsDataType.ARRAY)
            .valuetype(ValueType.DYNAMIC)
            .value(null)
            .access(AccessType.R)
            .build();

    final AttributeValidatorDTO calendarNamePassive =
        AttributeValidatorDTO.builder()
            .id(6)
            .description("calendar_name_passive")
            .datatype(DlmsDataType.OCTET_STRING)
            .valuetype(ValueType.DYNAMIC)
            .value(null)
            .access(AccessType.RW)
            .build();

    final AttributeValidatorDTO seasonProfilePassive =
        AttributeValidatorDTO.builder()
            .id(7)
            .description("season_profile_passive")
            .datatype(DlmsDataType.ARRAY)
            .valuetype(ValueType.DYNAMIC)
            .value(null)
            .access(AccessType.RW)
            .build();

    final AttributeValidatorDTO weekProfileTablePassive =
        AttributeValidatorDTO.builder()
            .id(8)
            .description("week_profile_table_passive")
            .datatype(DlmsDataType.ARRAY)
            .valuetype(ValueType.DYNAMIC)
            .value(null)
            .access(AccessType.RW)
            .build();

    final AttributeValidatorDTO dayProfileTablePassive =
        AttributeValidatorDTO.builder()
            .id(9)
            .description("day_profile_table_passive")
            .datatype(DlmsDataType.ARRAY)
            .valuetype(ValueType.DYNAMIC)
            .value(null)
            .access(AccessType.RW)
            .build();

    final AttributeValidatorDTO activatePassiveCalendarTime =
        AttributeValidatorDTO.builder()
            .id(10)
            .description("activate_passive_calendar_time")
            .datatype(DlmsDataType.OCTET_STRING)
            .valuetype(ValueType.DYNAMIC)
            .value(null)
            .access(AccessType.RW)
            .build();

    validateAttribute(cosemObject, calendarNameActive);
    validateAttribute(cosemObject, seasonProfileActive);
    validateAttribute(cosemObject, weekProfileTableActive);
    validateAttribute(cosemObject, dayProfileTableActive);
    validateAttribute(cosemObject, calendarNamePassive);
    validateAttribute(cosemObject, seasonProfilePassive);
    validateAttribute(cosemObject, weekProfileTablePassive);
    validateAttribute(cosemObject, dayProfileTablePassive);
    validateAttribute(cosemObject, activatePassiveCalendarTime);
  }

  private static void validateAttribute(
      final CosemObject cosemObject, final AttributeValidatorDTO attributeValidatorDTO) {
    final Attribute attribute = cosemObject.getAttribute(attributeValidatorDTO.getId());
    assertThat(attribute.getId()).isEqualTo(attributeValidatorDTO.getId());
    assertThat(attribute.getDescription()).isEqualTo(attributeValidatorDTO.getDescription());
    assertThat(attribute.getDatatype()).isEqualTo(attributeValidatorDTO.getDatatype());
    assertThat(attribute.getValuetype()).isEqualTo(attributeValidatorDTO.getValuetype());
    if (attributeValidatorDTO.getValue() == null) {
      assertThat(attribute.getValue()).isNull();
    } else {
      assertThat(attribute.getValue()).isEqualTo(attributeValidatorDTO.getValue());
    }
    assertThat(attribute.getAccess()).isEqualTo(attributeValidatorDTO.getAccess());
  }
}

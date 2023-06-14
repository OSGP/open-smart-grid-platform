// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.dlms.services;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.opensmartgridplatform.dlms.exceptions.ObjectConfigException;
import org.opensmartgridplatform.dlms.objectconfig.CosemObject;
import org.opensmartgridplatform.dlms.objectconfig.DlmsObjectType;
import org.opensmartgridplatform.dlms.objectconfig.DlmsProfile;
import org.opensmartgridplatform.dlms.objectconfig.ObjectProperty;
import org.springframework.core.io.ClassPathResource;

@Slf4j
class ObjectConfigServiceTest {

  final ObjectMapper objectMapper = new ObjectMapper();

  private ObjectConfigService objectConfigService;

  @BeforeEach
  void setUp() throws IOException, ObjectConfigException {
    final List<DlmsProfile> dlmsProfileList = this.getDlmsProfileList();
    this.objectConfigService = new ObjectConfigService(dlmsProfileList);
  }

  @Test
  void testGetCosemObjects() throws ObjectConfigException {
    final String protocolName = "SMR";
    final String protocolVersion50 = "5.0.0";
    final String protocolVersion51 = "5.1";
    final String protocolVersion52 = "5.2";

    final Map<DlmsObjectType, CosemObject> cosemObjects50 =
        this.objectConfigService.getCosemObjects(protocolName, protocolVersion50);

    assertNotNull(cosemObjects50);
    assertThat(cosemObjects50).hasSize(13);
    assertNull(cosemObjects50.get(DlmsObjectType.NUMBER_OF_POWER_FAILURES));

    final Map<DlmsObjectType, CosemObject> cosemObjects51 =
        this.objectConfigService.getCosemObjects(protocolName, protocolVersion51);

    assertNotNull(cosemObjects51);
    assertThat(cosemObjects51).hasSize(14);
    assertNotNull(cosemObjects51.get(DlmsObjectType.NUMBER_OF_POWER_FAILURES));

    final Map<DlmsObjectType, CosemObject> cosemObjects52 =
        this.objectConfigService.getCosemObjects(protocolName, protocolVersion52);

    assertNotNull(cosemObjects52);
    assertThat(cosemObjects52).hasSize(15);
    assertNotNull(cosemObjects52.get(DlmsObjectType.NUMBER_OF_LONG_POWER_FAILURES));
  }

  @Test
  void testDlmsProfileNotFound() {
    assertThrows(
        ObjectConfigException.class,
        () -> {
          this.objectConfigService.getCosemObjects("ABC", "12");
        });
  }

  @Test
  void testGetCosemObject() throws ObjectConfigException {
    final String protocolVersion50 = "5.0.0";
    final String protocolName = "SMR";

    assertThrows(
        IllegalArgumentException.class,
        () -> {
          this.objectConfigService.getCosemObject(
              protocolName, protocolVersion50, DlmsObjectType.AVERAGE_CURRENT_L1);
        });

    final CosemObject cosemObject =
        this.objectConfigService.getCosemObject(
            protocolName, protocolVersion50, DlmsObjectType.AVERAGE_VOLTAGE_L1);

    assertNotNull(cosemObject);
    assertThat(cosemObject.getTag()).isEqualTo(DlmsObjectType.AVERAGE_VOLTAGE_L1.value());
  }

  @Test
  void testGetCosemObjectsWithProperty() throws ObjectConfigException {
    final String protocolName = "SMR";
    final String protocolVersion50 = "5.0.0";

    final List<CosemObject> cosemObjectsWithSelectableObjects =
        this.objectConfigService.getCosemObjectsWithProperty(
            protocolName, protocolVersion50, ObjectProperty.SELECTABLE_OBJECTS, null);

    assertNotNull(cosemObjectsWithSelectableObjects);
    assertThat(cosemObjectsWithSelectableObjects).hasSize(3);

    final List<CosemObject> cosemObjectsWithPqProfile =
        this.objectConfigService.getCosemObjectsWithProperty(
            protocolName, protocolVersion50, ObjectProperty.PQ_PROFILE, null);

    assertNotNull(cosemObjectsWithPqProfile);
    assertThat(cosemObjectsWithPqProfile).hasSize(9);

    final List<CosemObject> cosemObjectsWithPqProfileWithWrongValue =
        this.objectConfigService.getCosemObjectsWithProperty(
            protocolName,
            protocolVersion50,
            ObjectProperty.PQ_PROFILE,
            Collections.singletonList("INVALID"));

    assertNotNull(cosemObjectsWithPqProfileWithWrongValue);
    assertThat(cosemObjectsWithPqProfileWithWrongValue).isEmpty();
  }

  @Test
  void testGetCosemObjectsWithPropertiesWithMultipleAllowedValues() throws ObjectConfigException {
    final String protocolName = "SMR";
    final String protocolVersion50 = "5.0.0";

    final Map<ObjectProperty, List<Object>> requestMap = new HashMap<>();
    final List<Object> wantedValues = new ArrayList<>();
    wantedValues.add("PRIVATE");
    wantedValues.add("PUBLIC");
    requestMap.put(ObjectProperty.PQ_PROFILE, wantedValues);
    final List<CosemObject> cosemObjectsWithSelectableObjects =
        this.objectConfigService.getCosemObjectsWithProperties(
            protocolName, protocolVersion50, requestMap);

    assertNotNull(cosemObjectsWithSelectableObjects);
    assertThat(cosemObjectsWithSelectableObjects).hasSize(9);
  }

  @Test
  void testGetCosemObjectsWithPropertiesWithMultipleProperties() throws ObjectConfigException {
    final String protocolName = "SMR";
    final String protocolVersion50 = "5.0.0";

    final Map<ObjectProperty, List<Object>> requestMap = new HashMap<>();
    final List<Object> wantedValuesPqProfile = new ArrayList<>();
    wantedValuesPqProfile.add("PUBLIC");
    requestMap.put(ObjectProperty.PQ_PROFILE, wantedValuesPqProfile);
    final List<Object> wantedValuesPqRequest = new ArrayList<>();
    wantedValuesPqRequest.add("PERIODIC");
    requestMap.put(ObjectProperty.PQ_REQUEST, wantedValuesPqRequest);
    final List<CosemObject> cosemObjectsWithSelectableObjects =
        this.objectConfigService.getCosemObjectsWithProperties(
            protocolName, protocolVersion50, requestMap);

    assertNotNull(cosemObjectsWithSelectableObjects);
    assertThat(cosemObjectsWithSelectableObjects).hasSize(3);
  }

  private List<DlmsProfile> getDlmsProfileList() throws IOException {
    final List<DlmsProfile> DlmsProfileList = new ArrayList<>();

    DlmsProfileList.add(this.loadProfile("/dlmsprofile-smr50.json"));
    DlmsProfileList.add(this.loadProfile("/dlmsprofile-smr51.json"));
    DlmsProfileList.add(this.loadProfile("/dlmsprofile-smr52.json"));
    return DlmsProfileList;
  }

  private DlmsProfile loadProfile(final String profileJsonFile) throws IOException {

    return this.objectMapper.readValue(
        new ClassPathResource(profileJsonFile).getFile(), DlmsProfile.class);
  }
}

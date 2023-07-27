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
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.opensmartgridplatform.dlms.exceptions.ObjectConfigException;
import org.opensmartgridplatform.dlms.objectconfig.CosemObject;
import org.opensmartgridplatform.dlms.objectconfig.DlmsObjectType;
import org.opensmartgridplatform.dlms.objectconfig.DlmsProfile;
import org.opensmartgridplatform.dlms.objectconfig.ObjectProperty;
import org.opensmartgridplatform.dlms.objectconfig.PowerQualityRequest;
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

  @ParameterizedTest
  @EnumSource(Protocol.class)
  void testProtocolSpecifics(final Protocol protocol) throws ObjectConfigException {

    final Map<DlmsObjectType, CosemObject> cosemObjects =
        this.objectConfigService.getCosemObjects(protocol.getName(), protocol.getVersion());
    assertThat(cosemObjects).hasSize(protocol.getNrOfCosemObjects());

    final List<CosemObject> cosemObjectsOnDemandPrivate =
        this.getCosemObjectsWithProperties(protocol, PowerQualityRequest.ONDEMAND, Profile.PRIVATE);
    assertThat(cosemObjectsOnDemandPrivate)
        .hasSize(protocol.getNrOfCosemObjects(PowerQualityRequest.ONDEMAND, Profile.PRIVATE));

    final List<CosemObject> cosemObjectsOnDemandPublic =
        this.getCosemObjectsWithProperties(protocol, PowerQualityRequest.ONDEMAND, Profile.PUBLIC);
    assertThat(cosemObjectsOnDemandPublic)
        .hasSize(protocol.getNrOfCosemObjects(PowerQualityRequest.ONDEMAND, Profile.PUBLIC));

    final List<CosemObject> cosemObjectsOnPeriodicPrivate =
        this.getCosemObjectsWithProperties(protocol, PowerQualityRequest.PERIODIC, Profile.PRIVATE);
    assertThat(cosemObjectsOnPeriodicPrivate)
        .hasSize(protocol.getNrOfCosemObjects(PowerQualityRequest.PERIODIC, Profile.PRIVATE));

    final List<CosemObject> cosemObjectsPeriodicPublic =
        this.getCosemObjectsWithProperties(protocol, PowerQualityRequest.PERIODIC, Profile.PUBLIC);
    assertThat(cosemObjectsPeriodicPublic)
        .hasSize(protocol.getNrOfCosemObjects(PowerQualityRequest.PERIODIC, Profile.PUBLIC));
  }

  private final List<CosemObject> getCosemObjectsWithProperties(
      final Protocol protocol, final PowerQualityRequest powerQualityRequest, final Profile profile)
      throws ObjectConfigException {
    final EnumMap<ObjectProperty, List<String>> pqProperties = new EnumMap<>(ObjectProperty.class);
    pqProperties.put(ObjectProperty.PQ_PROFILE, Collections.singletonList(profile.name()));
    pqProperties.put(ObjectProperty.PQ_REQUEST, List.of(powerQualityRequest.name()));

    return this.objectConfigService.getCosemObjectsWithProperties(
        protocol.getName(), protocol.getVersion(), pqProperties);
  }

  @Test
  void getCosemObjectIsNull() throws ObjectConfigException {
    final String protocolName = "SMR";
    final String protocolVersion43 = "4.3";

    final Map<DlmsObjectType, CosemObject> cosemObjects =
        this.objectConfigService.getCosemObjects(protocolName, protocolVersion43);

    assertNotNull(cosemObjects);
    assertThat(cosemObjects).hasSize(44);
    assertNull(cosemObjects.get(DlmsObjectType.MBUS_DIAGNOSTIC));
  }

  @Test
  void testGetCosemObjects() throws ObjectConfigException {
    Map<DlmsObjectType, CosemObject> cosemObjects =
        this.objectConfigService.getCosemObjects("DSMR", "2.2");

    assertNotNull(cosemObjects);
    assertThat(cosemObjects).hasSize(20);
    assertNotNull(cosemObjects.get(DlmsObjectType.NUMBER_OF_VOLTAGE_SWELLS_FOR_L1));

    cosemObjects = this.objectConfigService.getCosemObjects("DSMR", "4.2.2");

    assertNotNull(cosemObjects);
    assertThat(cosemObjects).hasSize(43);
    assertNotNull(cosemObjects.get(DlmsObjectType.NUMBER_OF_VOLTAGE_SWELLS_FOR_L1));
    assertNull(cosemObjects.get(DlmsObjectType.CDMA_DIAGNOSTIC));

    cosemObjects = this.objectConfigService.getCosemObjects("SMR", "4.3");

    assertNotNull(cosemObjects);
    assertThat(cosemObjects).hasSize(44);
    assertNotNull(cosemObjects.get(DlmsObjectType.NUMBER_OF_VOLTAGE_SWELLS_FOR_L1));
    assertNotNull(cosemObjects.get(DlmsObjectType.CDMA_DIAGNOSTIC));

    cosemObjects = this.objectConfigService.getCosemObjects("SMR", "5.0.0");

    assertNotNull(cosemObjects);
    assertThat(cosemObjects).hasSize(49);
    assertNotNull(cosemObjects.get(DlmsObjectType.NUMBER_OF_POWER_FAILURES));
    assertNull(cosemObjects.get(DlmsObjectType.LTE_DIAGNOSTIC));
    assertNull(cosemObjects.get(DlmsObjectType.PUSH_SETUP_UDP));

    cosemObjects = this.objectConfigService.getCosemObjects("SMR", "5.1");

    assertNotNull(cosemObjects);
    assertThat(cosemObjects).hasSize(49);
    assertNotNull(cosemObjects.get(DlmsObjectType.NUMBER_OF_POWER_FAILURES));
    assertNull(cosemObjects.get(DlmsObjectType.LTE_DIAGNOSTIC));
    assertNull(cosemObjects.get(DlmsObjectType.PUSH_SETUP_UDP));

    cosemObjects = this.objectConfigService.getCosemObjects("SMR", "5.2");

    assertNotNull(cosemObjects);
    assertThat(cosemObjects).hasSize(50);
    assertNotNull(cosemObjects.get(DlmsObjectType.NUMBER_OF_POWER_FAILURES));
    assertNotNull(cosemObjects.get(DlmsObjectType.LTE_DIAGNOSTIC));
    assertNull(cosemObjects.get(DlmsObjectType.PUSH_SETUP_UDP));

    cosemObjects = this.objectConfigService.getCosemObjects("SMR", "5.5");

    assertNotNull(cosemObjects);
    assertThat(cosemObjects).hasSize(51);
    assertNotNull(cosemObjects.get(DlmsObjectType.NUMBER_OF_POWER_FAILURES));
    assertNotNull(cosemObjects.get(DlmsObjectType.LTE_DIAGNOSTIC));
    assertNotNull(cosemObjects.get(DlmsObjectType.PUSH_SETUP_UDP));
  }

  @Test
  void testDlmsProfileNotFound() {
    assertThrows(
        ObjectConfigException.class, () -> this.objectConfigService.getCosemObjects("ABC", "12"));
  }

  @Test
  void getCosemObjectNotFoundShouldThrowIllegalArgumentException() {
    final String protocolVersion43 = "4.3";
    final String protocolName = "SMR";

    assertThrows(
        IllegalArgumentException.class,
        () ->
            this.objectConfigService.getCosemObject(
                protocolName, protocolVersion43, DlmsObjectType.MBUS_DIAGNOSTIC));
  }

  @Test
  void getCosemObjectShouldFindObject() throws ObjectConfigException {
    final String protocolVersion43 = "4.3";
    final String protocolName = "SMR";

    final CosemObject cosemObject =
        this.objectConfigService.getCosemObject(
            protocolName, protocolVersion43, DlmsObjectType.AVERAGE_VOLTAGE_L1);

    assertNotNull(cosemObject);
    assertThat(cosemObject.getTag()).isEqualTo(DlmsObjectType.AVERAGE_VOLTAGE_L1.value());
  }

  @Test
  void getOptionalCosemObject() throws ObjectConfigException {
    assertThat(
            this.objectConfigService.getOptionalCosemObject(
                "DSMR", "4.2.2", DlmsObjectType.POWER_QUALITY_PROFILE_1))
        .isEmpty();

    assertThat(
            this.objectConfigService.getOptionalCosemObject(
                "SMR", "5.0.0", DlmsObjectType.POWER_QUALITY_PROFILE_1))
        .isPresent();
  }

  @Test
  void testGetCosemObjectsWithPropertiesWithMultipleAllowedValues() throws ObjectConfigException {
    final String protocolName = "SMR";
    final String protocolVersion50 = "5.0.0";

    final Map<ObjectProperty, List<String>> requestMap = new HashMap<>();
    final List<String> wantedValues = new ArrayList<>();
    wantedValues.add("PRIVATE");
    wantedValues.add("PUBLIC");
    requestMap.put(ObjectProperty.PQ_PROFILE, wantedValues);
    final List<CosemObject> cosemObjectsWithSelectableObjects =
        this.objectConfigService.getCosemObjectsWithProperties(
            protocolName, protocolVersion50, requestMap);

    assertNotNull(cosemObjectsWithSelectableObjects);
    assertThat(cosemObjectsWithSelectableObjects).hasSize(45);
  }

  @Test
  void testGetCosemObjectsWithPropertiesWithMultipleProperties() throws ObjectConfigException {
    final String protocolName = "SMR";
    final String protocolVersion50 = "5.0.0";

    final Map<ObjectProperty, List<String>> requestMap = new HashMap<>();
    final List<String> wantedValuesPqProfile = new ArrayList<>();
    wantedValuesPqProfile.add("PUBLIC");
    requestMap.put(ObjectProperty.PQ_PROFILE, wantedValuesPqProfile);
    final List<String> wantedValuesPqRequest = new ArrayList<>();
    wantedValuesPqRequest.add("PERIODIC");
    requestMap.put(ObjectProperty.PQ_REQUEST, wantedValuesPqRequest);
    final List<CosemObject> cosemObjectsWithSelectableObjects =
        this.objectConfigService.getCosemObjectsWithProperties(
            protocolName, protocolVersion50, requestMap);

    assertNotNull(cosemObjectsWithSelectableObjects);
    assertThat(cosemObjectsWithSelectableObjects).hasSize(18);
  }

  @Test
  void getListOfDlmsProfiles() {

    assertNotNull(this.objectConfigService.getConfiguredDlmsProfiles());
  }

  private List<DlmsProfile> getDlmsProfileList() throws IOException {
    final List<DlmsProfile> DlmsProfileList = new ArrayList<>();

    DlmsProfileList.add(this.loadProfile("/dlmsprofiles/dlmsprofile-dsmr22.json"));
    DlmsProfileList.add(this.loadProfile("/dlmsprofiles/dlmsprofile-dsmr422.json"));
    DlmsProfileList.add(this.loadProfile("/dlmsprofiles/dlmsprofile-smr43.json"));
    DlmsProfileList.add(this.loadProfile("/dlmsprofiles/dlmsprofile-smr50.json"));
    DlmsProfileList.add(this.loadProfile("/dlmsprofiles/dlmsprofile-smr51.json"));
    DlmsProfileList.add(this.loadProfile("/dlmsprofiles/dlmsprofile-smr52.json"));
    DlmsProfileList.add(this.loadProfile("/dlmsprofiles/dlmsprofile-smr55.json"));
    return DlmsProfileList;
  }

  private DlmsProfile loadProfile(final String profileJsonFile) throws IOException {

    return this.objectMapper.readValue(
        new ClassPathResource(profileJsonFile).getFile(), DlmsProfile.class);
  }
}

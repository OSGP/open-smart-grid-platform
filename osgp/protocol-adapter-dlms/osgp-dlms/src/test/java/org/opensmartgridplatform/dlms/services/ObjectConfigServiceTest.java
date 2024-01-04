// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.dlms.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.opensmartgridplatform.dlms.services.ObjectConfigService.getAttributeIdFromCaptureObjectDefinition;
import static org.opensmartgridplatform.dlms.services.ObjectConfigService.getCaptureObjectDefinitions;
import static org.opensmartgridplatform.dlms.services.ObjectConfigService.getCosemObjectTypeFromCaptureObjectDefinition;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.opensmartgridplatform.dlms.exceptions.ObjectConfigException;
import org.opensmartgridplatform.dlms.objectconfig.AccessType;
import org.opensmartgridplatform.dlms.objectconfig.Attribute;
import org.opensmartgridplatform.dlms.objectconfig.CaptureObject;
import org.opensmartgridplatform.dlms.objectconfig.CosemObject;
import org.opensmartgridplatform.dlms.objectconfig.DlmsDataType;
import org.opensmartgridplatform.dlms.objectconfig.DlmsObjectType;
import org.opensmartgridplatform.dlms.objectconfig.ObjectProperty;
import org.opensmartgridplatform.dlms.objectconfig.PowerQualityRequest;
import org.opensmartgridplatform.dlms.objectconfig.TypeBasedValue;
import org.opensmartgridplatform.dlms.objectconfig.ValueBasedOnModel;
import org.opensmartgridplatform.dlms.objectconfig.ValueType;

@Slf4j
class ObjectConfigServiceTest {

  private ObjectConfigService objectConfigService;

  private final int PROFILE_GENERIC_CLASS_ID = 7;
  private final int PROFILE_GENERIC_CAPTURE_OBJECTS_ATTR_ID = 3;
  private final int REGISTER_CLASS_ID = 3;
  private final int REGISTER_SCALER_UNIT_ATTR_ID = 3;

  @BeforeEach
  void setUp() throws IOException, ObjectConfigException {
    this.objectConfigService = new ObjectConfigService();
  }

  @ParameterizedTest
  @EnumSource(Protocol.class)
  void testProtocolSpecifics(final Protocol protocol) throws ObjectConfigException {

    final Map<DlmsObjectType, CosemObject> cosemObjects =
        this.objectConfigService.getCosemObjects(protocol.getName(), protocol.getVersion());
    assertThat(cosemObjects).hasSize(protocol.getNrOfCosemObjects());

    final List<CosemObject> cosemObjectsOnDemandPrivate =
        this.getCosemObjectsWithProperties(
            protocol, PowerQualityRequest.ACTUAL_SP, Profile.PRIVATE);
    assertThat(cosemObjectsOnDemandPrivate)
        .hasSize(protocol.getNrOfCosemObjects(PowerQualityRequest.ACTUAL_SP, Profile.PRIVATE));

    final List<CosemObject> cosemObjectsOnDemandPublic =
        this.getCosemObjectsWithProperties(protocol, PowerQualityRequest.ACTUAL_SP, Profile.PUBLIC);
    assertThat(cosemObjectsOnDemandPublic)
        .hasSize(protocol.getNrOfCosemObjects(PowerQualityRequest.ACTUAL_SP, Profile.PUBLIC));

    final List<CosemObject> cosemObjectsOnPeriodicSpPrivate =
        this.getCosemObjectsWithProperties(
            protocol, PowerQualityRequest.PERIODIC_SP, Profile.PRIVATE);
    assertThat(cosemObjectsOnPeriodicSpPrivate)
        .hasSize(protocol.getNrOfCosemObjects(PowerQualityRequest.PERIODIC_SP, Profile.PRIVATE));

    final List<CosemObject> cosemObjectsOnPeriodicPpPrivate =
        this.getCosemObjectsWithProperties(
            protocol, PowerQualityRequest.PERIODIC_PP, Profile.PRIVATE);
    assertThat(cosemObjectsOnPeriodicPpPrivate)
        .hasSize(protocol.getNrOfCosemObjects(PowerQualityRequest.PERIODIC_PP, Profile.PRIVATE));

    final List<CosemObject> cosemObjectsPeriodicSpPublic =
        this.getCosemObjectsWithProperties(
            protocol, PowerQualityRequest.PERIODIC_SP, Profile.PUBLIC);
    assertThat(cosemObjectsPeriodicSpPublic)
        .hasSize(protocol.getNrOfCosemObjects(PowerQualityRequest.PERIODIC_SP, Profile.PUBLIC));

    final List<CosemObject> cosemObjectsPeriodicPpPublic =
        this.getCosemObjectsWithProperties(
            protocol, PowerQualityRequest.PERIODIC_PP, Profile.PUBLIC);
    assertThat(cosemObjectsPeriodicPpPublic)
        .hasSize(protocol.getNrOfCosemObjects(PowerQualityRequest.PERIODIC_PP, Profile.PUBLIC));
  }

  @ParameterizedTest
  @EnumSource(Protocol.class)
  void testProfilesObjectsContainPqObjects(final Protocol protocol) throws ObjectConfigException {
    this.assertAllInConfig(
        protocol, DlmsObjectType.DEFINABLE_LOAD_PROFILE, protocol.hasDefinableLoadProfile());
    this.assertAllInConfig(
        protocol, DlmsObjectType.POWER_QUALITY_PROFILE_1, protocol.hasPqProfiles());
    this.assertAllInConfig(
        protocol, DlmsObjectType.POWER_QUALITY_PROFILE_2, protocol.hasPqProfiles());
  }

  @ParameterizedTest
  @EnumSource(Protocol.class)
  void testAllPqObjectMapToProfile(final Protocol protocol) throws ObjectConfigException {
    final List<DlmsObjectType> dlmsProfiles = new ArrayList<>();
    if (protocol.hasDefinableLoadProfile()) {
      assertThat(
              this.objectConfigService.getCosemObject(
                  protocol.getName(), protocol.getVersion(), DlmsObjectType.DEFINABLE_LOAD_PROFILE))
          .isNotNull();
      dlmsProfiles.add(DlmsObjectType.DEFINABLE_LOAD_PROFILE);
    }
    if (protocol.hasPqProfiles()) {
      assertThat(
              this.objectConfigService.getCosemObject(
                  protocol.getName(),
                  protocol.getVersion(),
                  DlmsObjectType.POWER_QUALITY_PROFILE_1))
          .isNotNull();
      assertThat(
              this.objectConfigService.getCosemObject(
                  protocol.getName(),
                  protocol.getVersion(),
                  DlmsObjectType.POWER_QUALITY_PROFILE_2))
          .isNotNull();
      dlmsProfiles.addAll(
          List.of(DlmsObjectType.POWER_QUALITY_PROFILE_1, DlmsObjectType.POWER_QUALITY_PROFILE_2));
    }
    if (!dlmsProfiles.isEmpty()) {
      this.assertAllPqPeriodicObjectsInProfiles(protocol, dlmsProfiles);
    }
  }

  private List<CosemObject> getCosemObjectsWithProperties(
      final Protocol protocol, final PowerQualityRequest powerQualityRequest, final Profile profile)
      throws ObjectConfigException {
    final EnumMap<ObjectProperty, List<String>> pqProperties = new EnumMap<>(ObjectProperty.class);
    pqProperties.put(ObjectProperty.PQ_PROFILE, Collections.singletonList(profile.name()));
    pqProperties.put(ObjectProperty.PQ_REQUEST, List.of(powerQualityRequest.name()));

    return this.objectConfigService.getCosemObjectsWithProperties(
        protocol.getName(), protocol.getVersion(), pqProperties);
  }

  @Test
  void testGetCosemObjectsDsmr22() throws ObjectConfigException {
    final Map<DlmsObjectType, CosemObject> cosemObjects =
        this.objectConfigService.getCosemObjects("DSMR", "2.2");

    assertNotNull(cosemObjects);
    assertThat(cosemObjects).hasSize(Protocol.DSMR_2_2.getNrOfCosemObjects());
    assertNotNull(cosemObjects.get(DlmsObjectType.ALARM_REGISTER_1));
    assertNotNull(cosemObjects.get(DlmsObjectType.NUMBER_OF_VOLTAGE_SWELLS_FOR_L1));
  }

  @Test
  void testGetCosemObjectsDsmr422() throws ObjectConfigException {
    final Map<DlmsObjectType, CosemObject> cosemObjects =
        this.objectConfigService.getCosemObjects("DSMR", "4.2.2");

    assertNotNull(cosemObjects);
    assertThat(cosemObjects).hasSize(Protocol.DSMR_4_2_2.getNrOfCosemObjects());
    assertNotNull(cosemObjects.get(DlmsObjectType.ALARM_REGISTER_1));
    assertNotNull(cosemObjects.get(DlmsObjectType.NUMBER_OF_VOLTAGE_SWELLS_FOR_L1));
    assertNull(cosemObjects.get(DlmsObjectType.CDMA_DIAGNOSTIC));
  }

  @Test
  void getCosemObjectIsNull() throws ObjectConfigException {
    final String protocolName = "SMR";
    final String protocolVersion43 = "4.3";

    final Map<DlmsObjectType, CosemObject> cosemObjects =
        this.objectConfigService.getCosemObjects(protocolName, protocolVersion43);

    assertNotNull(cosemObjects);
    assertThat(cosemObjects).hasSize(Protocol.SMR_4_3.getNrOfCosemObjects());
    assertNotNull(cosemObjects.get(DlmsObjectType.ALARM_REGISTER_1));
  }

  @Test
  void testGetCosemObjectsSmr43_shouldComplyWithAssertionChecks() throws ObjectConfigException {
    final Map<DlmsObjectType, CosemObject> cosemObjects =
        this.objectConfigService.getCosemObjects("SMR", "4.3");

    assertNotNull(cosemObjects);
    assertThat(cosemObjects).hasSize(Protocol.SMR_4_3.getNrOfCosemObjects());
    assertNotNull(cosemObjects.get(DlmsObjectType.ALARM_REGISTER_1));
    assertNotNull(cosemObjects.get(DlmsObjectType.NUMBER_OF_VOLTAGE_SWELLS_FOR_L1));
    assertNotNull(cosemObjects.get(DlmsObjectType.CDMA_DIAGNOSTIC));
    assertNull(cosemObjects.get(DlmsObjectType.MBUS_DIAGNOSTIC));
  }

  @Test
  void testGetCosemObjectsSmr500() throws ObjectConfigException {
    final Map<DlmsObjectType, CosemObject> cosemObjects =
        this.objectConfigService.getCosemObjects("SMR", "5.0.0");

    assertNotNull(cosemObjects);
    assertThat(cosemObjects).hasSize(Protocol.SMR_5_0_0.getNrOfCosemObjects());
    assertNotNull(cosemObjects.get(DlmsObjectType.ALARM_REGISTER_1));
    assertNotNull(cosemObjects.get(DlmsObjectType.NUMBER_OF_POWER_FAILURES));
    assertNull(cosemObjects.get(DlmsObjectType.LTE_DIAGNOSTIC));
    assertNull(cosemObjects.get(DlmsObjectType.PUSH_SETUP_UDP));
  }

  @Test
  void testGetCosemObjectsSmr51() throws ObjectConfigException {
    final Map<DlmsObjectType, CosemObject> cosemObjects =
        this.objectConfigService.getCosemObjects("SMR", "5.1");

    assertNotNull(cosemObjects);
    assertThat(cosemObjects).hasSize(Protocol.SMR_5_1.getNrOfCosemObjects());
    assertNotNull(cosemObjects.get(DlmsObjectType.ALARM_REGISTER_1));
    assertNotNull(cosemObjects.get(DlmsObjectType.NUMBER_OF_POWER_FAILURES));
    assertNull(cosemObjects.get(DlmsObjectType.LTE_DIAGNOSTIC));
    assertNull(cosemObjects.get(DlmsObjectType.PUSH_SETUP_UDP));
  }

  @Test
  void testGetCosemObjectsSmr52() throws ObjectConfigException {
    final Map<DlmsObjectType, CosemObject> cosemObjects =
        this.objectConfigService.getCosemObjects("SMR", "5.2");

    assertNotNull(cosemObjects);
    assertThat(cosemObjects).hasSize(Protocol.SMR_5_2.getNrOfCosemObjects());
    assertNotNull(cosemObjects.get(DlmsObjectType.ALARM_REGISTER_1));
    assertNotNull(cosemObjects.get(DlmsObjectType.ALARM_REGISTER_2));
    assertNotNull(cosemObjects.get(DlmsObjectType.NUMBER_OF_POWER_FAILURES));
    assertNotNull(cosemObjects.get(DlmsObjectType.LTE_DIAGNOSTIC));
    assertNull(cosemObjects.get(DlmsObjectType.PUSH_SETUP_UDP));
  }

  @Test
  void testGetCosemObjectsSmr55() throws ObjectConfigException {
    final Map<DlmsObjectType, CosemObject> cosemObjects =
        this.objectConfigService.getCosemObjects("SMR", "5.5");

    assertNotNull(cosemObjects);
    assertThat(cosemObjects).hasSize(Protocol.SMR_5_5.getNrOfCosemObjects());
    assertNotNull(cosemObjects.get(DlmsObjectType.ALARM_REGISTER_1));
    assertNotNull(cosemObjects.get(DlmsObjectType.ALARM_REGISTER_2));
    assertNotNull(cosemObjects.get(DlmsObjectType.ALARM_REGISTER_3));
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
    wantedValuesPqRequest.add("PERIODIC_SP");
    requestMap.put(ObjectProperty.PQ_REQUEST, wantedValuesPqRequest);
    final List<CosemObject> cosemObjectsWithSelectableObjects =
        this.objectConfigService.getCosemObjectsWithProperties(
            protocolName, protocolVersion50, requestMap);

    assertNotNull(cosemObjectsWithSelectableObjects);
    assertThat(cosemObjectsWithSelectableObjects).hasSize(9);
  }

  @Test
  void getListOfDlmsProfiles() {

    assertNotNull(this.objectConfigService.getConfiguredDlmsProfiles());
  }

  @Test
  void testGetCaptureObjectDefinitions() {
    final Attribute captureObjectsAttribute =
        this.createAttribute(
            this.PROFILE_GENERIC_CAPTURE_OBJECTS_ATTR_ID, "CLOCK,10|AMR_PROFILE_STATUS,11");
    final CosemObject profile =
        this.createCosemObject(this.PROFILE_GENERIC_CLASS_ID, List.of(captureObjectsAttribute));

    final List<String> captureObjectDefinitions = getCaptureObjectDefinitions(profile);

    assertThat(captureObjectDefinitions).hasSize(2);
    final String clockDef = captureObjectDefinitions.get(0);
    assertThat(clockDef).isEqualTo("CLOCK,10");
    assertThat(getCosemObjectTypeFromCaptureObjectDefinition(clockDef))
        .isEqualTo(DlmsObjectType.CLOCK);
    assertThat(getAttributeIdFromCaptureObjectDefinition(clockDef)).isEqualTo(10);
    final String statusDef = captureObjectDefinitions.get(1);
    assertThat(statusDef).isEqualTo("AMR_PROFILE_STATUS,11");
    assertThat(getCosemObjectTypeFromCaptureObjectDefinition(statusDef))
        .isEqualTo(DlmsObjectType.AMR_PROFILE_STATUS);
    assertThat(getAttributeIdFromCaptureObjectDefinition(statusDef)).isEqualTo(11);
  }

  @Test
  void testGetCaptureObjectDefinitionsEmpty() {
    final Attribute captureObjectsAttribute =
        this.createAttribute(this.PROFILE_GENERIC_CAPTURE_OBJECTS_ATTR_ID, null);
    final CosemObject profile =
        this.createCosemObject(this.PROFILE_GENERIC_CLASS_ID, List.of(captureObjectsAttribute));

    final List<String> captureObjectDefinitions = getCaptureObjectDefinitions(profile);

    assertThat(captureObjectDefinitions).isEmpty();
  }

  @Test
  void testGetCaptureObjectDefinitionsWrongClassId() {
    final CosemObject profile = this.createCosemObject(5, List.of());

    assertThrows(IllegalArgumentException.class, () -> getCaptureObjectDefinitions(profile));
  }

  @Test
  void testGetCaptureObjects() throws ObjectConfigException {
    final Attribute captureObjectsAttribute =
        this.createAttribute(
            this.PROFILE_GENERIC_CAPTURE_OBJECTS_ATTR_ID, "CLOCK,10|AMR_PROFILE_STATUS,11");
    final CosemObject profile =
        this.createCosemObject(this.PROFILE_GENERIC_CLASS_ID, List.of(captureObjectsAttribute));

    final List<CaptureObject> captureObjectDefinitions =
        this.objectConfigService.getCaptureObjects(profile, "DSMR", "4.2.2", "DeviceModel");

    assertThat(captureObjectDefinitions).hasSize(2);
    final CaptureObject clock = captureObjectDefinitions.get(0);
    assertThat(clock.getCosemObject().getTag()).isEqualTo(DlmsObjectType.CLOCK.name());
    assertThat(clock.getAttributeId()).isEqualTo(10);
    final CaptureObject status = captureObjectDefinitions.get(1);
    assertThat(status.getCosemObject().getTag())
        .isEqualTo(DlmsObjectType.AMR_PROFILE_STATUS.name());
    assertThat(status.getAttributeId()).isEqualTo(11);
  }

  @Test
  void HandleValueBasedOnModel() throws ObjectConfigException {
    final ValueBasedOnModel valueBasedOnModel =
        new ValueBasedOnModel(
            "GAS_METER_TYPE",
            List.of(
                new TypeBasedValue(List.of("G4-G6"), "-3, M3"),
                new TypeBasedValue(List.of("G10-G25"), "-2, M3)")));
    final Attribute attribute =
        this.createAttribute(
            this.REGISTER_SCALER_UNIT_ATTR_ID, null, ValueType.BASED_ON_MODEL, valueBasedOnModel);
    final CosemObject origObject =
        this.createCosemObject(this.REGISTER_CLASS_ID, List.of(attribute));

    // When the device model can be matched, then the value should be replaced with the value for
    // this model. The valuetype should be set to FIXED_IN_METER.
    final CosemObject handledObject1 =
        this.objectConfigService.handleValueBasedOnModel(origObject, "DeviceModelG4");
    this.verifyHandledObject(handledObject1, origObject, "-3, M3", ValueType.FIXED_IN_METER);

    // When the device model cannot be matched, then the value should be set to null. The valuetype
    // should be set to DYNAMIC to indicate that the value should be read from the meter.
    final CosemObject handledObject2 =
        this.objectConfigService.handleValueBasedOnModel(origObject, "UnknownDeviceModel");
    this.verifyHandledObject(handledObject2, origObject, null, ValueType.DYNAMIC);
  }

  private void verifyHandledObject(
      final CosemObject handledObject,
      final CosemObject originalObject,
      final String expectedValue,
      final ValueType expectedValuetype) {
    assertThat(handledObject)
        .usingRecursiveComparison()
        .ignoringFields("attributes")
        .isEqualTo(originalObject);
    final Attribute handledAttribute =
        handledObject.getAttribute(this.REGISTER_SCALER_UNIT_ATTR_ID);
    assertThat(handledAttribute)
        .usingRecursiveComparison()
        .ignoringFields("value", "valuetype")
        .isEqualTo(originalObject.getAttribute(this.REGISTER_SCALER_UNIT_ATTR_ID));
    assertThat(handledAttribute.getValue()).isEqualTo(expectedValue);
    assertThat(handledAttribute.getValuetype()).isEqualTo(expectedValuetype);
  }

  private void assertAllInConfig(
      final Protocol protocol,
      final DlmsObjectType dlmsObjectType,
      final boolean shouldHaveObjectType)
      throws ObjectConfigException {

    final Optional<CosemObject> optionalProfile =
        this.objectConfigService.getOptionalCosemObject(
            protocol.getName(), protocol.getVersion(), dlmsObjectType);
    if (!shouldHaveObjectType) {
      assertThat(optionalProfile).isEmpty();
    } else {
      assertThat(optionalProfile).isPresent();

      final List<String> selectableObjects =
          optionalProfile.orElseThrow().getListProperty(ObjectProperty.SELECTABLE_OBJECTS);

      final Map<DlmsObjectType, CosemObject> cosemObjects =
          this.objectConfigService.getCosemObjects(protocol.getName(), protocol.getVersion());

      // All selectable objects must be in config
      selectableObjects.forEach(
          selectableObject ->
              assertThat(cosemObjects.get(DlmsObjectType.valueOf(selectableObject))).isNotNull());
    }
  }

  private void assertAllPqPeriodicObjectsInProfiles(
      final Protocol protocol, final List<DlmsObjectType> pqProfiles) throws ObjectConfigException {
    final List<String> allSelectableObjects = new ArrayList<>();
    for (final DlmsObjectType pqProfile : pqProfiles) {
      final CosemObject cosemObjectProfile =
          this.objectConfigService.getCosemObject(
              protocol.getName(), protocol.getVersion(), pqProfile);
      allSelectableObjects.addAll(
          cosemObjectProfile.getListProperty(ObjectProperty.SELECTABLE_OBJECTS));
    }

    final Map<DlmsObjectType, CosemObject> pqObjects =
        this.objectConfigService.getCosemObjects(protocol.getName(), protocol.getVersion());
    pqObjects.forEach(
        (dlmsObjectType, pqObject) -> {
          if (isPeriodicRequest(pqObject)) {
            assertThat(allSelectableObjects).contains(pqObject.getTag());
          }
        });
  }

  private static boolean isPeriodicRequest(final CosemObject object) {
    return object.getProperty(ObjectProperty.PQ_REQUEST) != null
        && (object.getProperty(ObjectProperty.PQ_REQUEST).equals(PowerQualityRequest.PERIODIC_SP)
            || object
                .getProperty(ObjectProperty.PQ_REQUEST)
                .equals(PowerQualityRequest.PERIODIC_PP));
  }

  private Attribute createAttribute(final int id, final String value) {
    return this.createAttribute(id, value, ValueType.DYNAMIC, null);
  }

  private Attribute createAttribute(
      final int id,
      final String value,
      final ValueType valueType,
      final ValueBasedOnModel valueBasedOnModel) {
    return new Attribute(
        id,
        "descr",
        null,
        DlmsDataType.DONT_CARE,
        valueType,
        value,
        valueBasedOnModel,
        AccessType.RW);
  }

  private CosemObject createCosemObject(final int classId, final List<Attribute> attributes) {
    return new CosemObject(
        "TAG", "descr", classId, 0, "1.2.3", "group", null, List.of(), null, attributes);
  }
}

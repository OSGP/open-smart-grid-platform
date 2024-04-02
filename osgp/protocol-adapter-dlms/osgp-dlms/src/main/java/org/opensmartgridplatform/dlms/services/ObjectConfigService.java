// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.dlms.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.opensmartgridplatform.dlms.exceptions.ObjectConfigException;
import org.opensmartgridplatform.dlms.interfaceclass.InterfaceClass;
import org.opensmartgridplatform.dlms.interfaceclass.attribute.ProfileGenericAttribute;
import org.opensmartgridplatform.dlms.objectconfig.Attribute;
import org.opensmartgridplatform.dlms.objectconfig.CaptureObject;
import org.opensmartgridplatform.dlms.objectconfig.CosemObject;
import org.opensmartgridplatform.dlms.objectconfig.DlmsObjectType;
import org.opensmartgridplatform.dlms.objectconfig.DlmsProfile;
import org.opensmartgridplatform.dlms.objectconfig.DlmsProfileValidator;
import org.opensmartgridplatform.dlms.objectconfig.ObjectProperty;
import org.opensmartgridplatform.dlms.objectconfig.ParentProfile;
import org.opensmartgridplatform.dlms.objectconfig.TypeBasedValue;
import org.opensmartgridplatform.dlms.objectconfig.ValueType;
import org.opensmartgridplatform.dlms.objectconfig.configlookup.ConfigLookupGroup;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ObjectConfigService {

  public final List<DlmsProfile> dlmsProfiles;
  public final List<ConfigLookupGroup> configLookupGroups;

  /*
   * Profiles are loaded from the classpath resource '/dlmsprofiles'.
   */
  public ObjectConfigService() throws ObjectConfigException, IOException {
    this.dlmsProfiles = this.getDlmsProfileListFromResources();
    DlmsProfileValidator.validate(this.dlmsProfiles);
    this.dlmsProfiles.forEach(DlmsProfile::createMap);

    this.configLookupGroups = this.getConfigLookupGroupsFromResources();
  }

  public CosemObject getCosemObject(
      final String protocolName, final String protocolVersion, final DlmsObjectType dlmsObjectType)
      throws IllegalArgumentException, ObjectConfigException {
    return this.getCosemObject(protocolName, protocolVersion, dlmsObjectType, null);
  }

  public CosemObject getCosemObject(
      final String protocolName,
      final String protocolVersion,
      final DlmsObjectType dlmsObjectType,
      final String deviceModel)
      throws IllegalArgumentException, ObjectConfigException {
    final Optional<CosemObject> optionalCosemObject =
        this.getOptionalCosemObject(protocolName, protocolVersion, dlmsObjectType, deviceModel);
    return optionalCosemObject.orElseThrow(
        () ->
            new IllegalArgumentException(
                String.format(
                    "No object found of type %s in profile %s version %s",
                    dlmsObjectType.value(), protocolName, protocolVersion)));
  }

  public Optional<CosemObject> getOptionalCosemObject(
      final String protocolName, final String protocolVersion, final DlmsObjectType dlmsObjectType)
      throws ObjectConfigException {
    return this.getOptionalCosemObject(protocolName, protocolVersion, dlmsObjectType, null);
  }

  public Optional<CosemObject> getOptionalCosemObject(
      final String protocolName,
      final String protocolVersion,
      final DlmsObjectType dlmsObjectType,
      final String deviceModel)
      throws ObjectConfigException {
    final Map<DlmsObjectType, CosemObject> cosemObjects =
        this.getCosemObjects(protocolName, protocolVersion);
    if (cosemObjects.containsKey(dlmsObjectType)) {
      final CosemObject cosemObject = cosemObjects.get(dlmsObjectType);
      return Optional.of(this.handleValueBasedOnModel(cosemObject, deviceModel));
    } else {
      return Optional.empty();
    }
  }

  public List<CosemObject> getCosemObjects(
      final String protocolName,
      final String protocolVersion,
      final List<DlmsObjectType> dlmsObjectTypes)
      throws IllegalArgumentException, ObjectConfigException {
    final Map<DlmsObjectType, CosemObject> cosemObjects =
        this.getCosemObjects(protocolName, protocolVersion);

    final List<CosemObject> objects = new ArrayList<>();

    dlmsObjectTypes.forEach(
        objectType -> {
          if (cosemObjects.containsKey(objectType)) {
            objects.add(cosemObjects.get(objectType));
          } else {
            throw new IllegalArgumentException(
                String.format(
                    "No object found of type %s in protocol %s version %s",
                    objectType.value(), protocolName, protocolVersion));
          }
        });

    return objects;
  }

  public List<CosemObject> getCosemObjectsWithProperties(
      final String protocolName,
      final String protocolVersion,
      final Map<ObjectProperty, List<String>> properties)
      throws ObjectConfigException {
    final Map<DlmsObjectType, CosemObject> cosemObjects =
        this.getCosemObjects(protocolName, protocolVersion);

    return cosemObjects.values().stream()
        .filter(object -> this.hasProperties(object, properties))
        .toList();
  }

  public Map<DlmsObjectType, CosemObject> getCosemObjects(
      final String protocolName, final String protocolVersion) throws ObjectConfigException {
    final DlmsProfile dlmsProfile = this.getDlmsProfile(protocolName, protocolVersion);
    return dlmsProfile.getObjectMap();
  }

  public DlmsProfile getDlmsProfile(final String protocolName, final String protocolVersion)
      throws ObjectConfigException {

    if (this.dlmsProfiles.isEmpty()) {
      throw new ObjectConfigException("No DLMS Profile available");
    }

    final Optional<DlmsProfile> dlmsProfile =
        this.dlmsProfiles.stream()
            .filter(profile -> protocolVersion.equalsIgnoreCase(profile.getVersion()))
            .filter(profile -> protocolName.equalsIgnoreCase(profile.getProfile()))
            .findAny();
    if (dlmsProfile.isEmpty()) {
      throw new ObjectConfigException(
          "DLMS Profile for " + protocolName + " " + protocolVersion + " is not available");
    }
    return dlmsProfile.get();
  }

  private String getConfigLookupType(final String matchGroup, final String deviceModel)
      throws ObjectConfigException {

    final Optional<ConfigLookupGroup> configLookupGroupOptional =
        this.configLookupGroups.stream()
            .filter(group -> group.getName().equals(matchGroup))
            .findFirst();

    if (configLookupGroupOptional.isEmpty()) {
      throw new ObjectConfigException("Matchgroup " + matchGroup + " not found");
    } else {
      return configLookupGroupOptional.get().getMatchingType(deviceModel);
    }
  }

  private boolean hasProperty(
      final CosemObject object,
      final ObjectProperty wantedProperty,
      final List<String> wantedPropertyValues) {
    final Object objectPropertyValue = object.getProperty(wantedProperty);

    if (objectPropertyValue == null) {
      return false;
    } else if (wantedPropertyValues != null && !wantedPropertyValues.isEmpty()) {
      if (objectPropertyValue instanceof String) {
        return wantedPropertyValues.contains(objectPropertyValue);
      } else if (objectPropertyValue instanceof List<?>) {
        final List<String> objectProperyValues = object.getListProperty(wantedProperty);
        return new HashSet<>(objectProperyValues).containsAll(wantedPropertyValues);
      }
      throw new IllegalArgumentException("Unexpected type");
    }

    return true;
  }

  private boolean hasProperties(
      final CosemObject object, final Map<ObjectProperty, List<String>> properties) {

    return properties.entrySet().stream()
        .allMatch(entry -> this.hasProperty(object, entry.getKey(), entry.getValue()));
  }

  private List<DlmsProfile> getDlmsProfileListFromResources()
      throws IOException, ObjectConfigException {
    final String scannedPackage = "dlmsprofiles/*";
    final PathMatchingResourcePatternResolver scanner = new PathMatchingResourcePatternResolver();
    final Resource[] resources = scanner.getResources(scannedPackage);

    final ObjectMapper objectMapper = new ObjectMapper();

    final List<DlmsProfile> dlmsProfilesFromResources = new ArrayList<>();

    Stream.of(resources)
        .filter(
            resource ->
                resource.getFilename() != null
                    && resource.getFilename().endsWith(".json")
                    && !resource.getFilename().equals("configlookup.json"))
        .forEach(
            resource -> {
              try {
                final DlmsProfile dlmsProfile =
                    objectMapper.readValue(resource.getInputStream(), DlmsProfile.class);
                dlmsProfilesFromResources.add(dlmsProfile);
              } catch (final IOException e) {
                log.error(String.format("Cannot read config file %s", resource.getFilename()), e);
              }
            });

    this.handleInheritance(dlmsProfilesFromResources);

    return dlmsProfilesFromResources;
  }

  private void handleInheritance(final List<DlmsProfile> dlmsProfilesFromResources)
      throws ObjectConfigException {
    for (final DlmsProfile dlmsProfile : dlmsProfilesFromResources) {
      final ParentProfile parentProfile = dlmsProfile.getInherit();
      if (parentProfile != null) {
        log.info(
            "Handle inheritance of "
                + parentProfile.getVersion()
                + " for profile: "
                + dlmsProfile.getProfileWithVersion());

        this.addInheritedObjects(parentProfile, dlmsProfilesFromResources, dlmsProfile);
      }
    }
  }

  private void addInheritedObjects(
      final ParentProfile parentProfile,
      final List<DlmsProfile> dlmsProfilesFromResources,
      final DlmsProfile dlmsProfile)
      throws ObjectConfigException {

    final DlmsProfile parentDlmsProfile =
        this.getDlmsProfile(parentProfile, dlmsProfilesFromResources);

    parentDlmsProfile
        .getObjects()
        .forEach(
            parentCosemObject -> {
              final boolean objectAlreadyDefined =
                  dlmsProfile.getObjects().stream()
                      .anyMatch(
                          cosemObject -> cosemObject.getTag().equals(parentCosemObject.getTag()));
              if (!objectAlreadyDefined) {
                dlmsProfile.getObjects().add(parentCosemObject);
              }
            });
    if (parentDlmsProfile.getInherit() != null) {
      this.addInheritedObjects(
          parentDlmsProfile.getInherit(), dlmsProfilesFromResources, dlmsProfile);
    }
  }

  private DlmsProfile getDlmsProfile(
      final ParentProfile parentProfile, final List<DlmsProfile> dlmsProfilesFromResources)
      throws ObjectConfigException {
    return dlmsProfilesFromResources.stream()
        .filter(profile -> parentProfile.getVersion().equalsIgnoreCase(profile.getVersion()))
        .filter(profile -> parentProfile.getProfile().equalsIgnoreCase(profile.getProfile()))
        .findFirst()
        .orElseThrow(
            () -> new ObjectConfigException("Parent profile " + parentProfile + " not found."));
  }

  /*
   * Get a list of configured DlmsProfile.
   */
  public List<DlmsProfile> getConfiguredDlmsProfiles() {
    return this.dlmsProfiles;
  }

  public static List<String> getCaptureObjectDefinitions(final CosemObject profile) {
    if (profile.getClassId() != InterfaceClass.PROFILE_GENERIC.id()) {
      throw new IllegalArgumentException(
          "Can't get capture objects, object " + profile.getTag() + " is not a Profile Generic");
    }

    final Attribute captureObjectsAttribute =
        profile.getAttribute(ProfileGenericAttribute.CAPTURE_OBJECTS.attributeId());

    final String captureObjectsAttributeValue = captureObjectsAttribute.getValue();

    if (captureObjectsAttributeValue == null || captureObjectsAttributeValue.isEmpty()) {
      return List.of();
    }

    final String[] captureObjectDefinitions = captureObjectsAttribute.getValue().split("\\|");

    return List.of(captureObjectDefinitions);
  }

  public static DlmsObjectType getCosemObjectTypeFromCaptureObjectDefinition(
      final String captureObjectDefinition) {
    return DlmsObjectType.fromValue(captureObjectDefinition.split(",")[0]);
  }

  public static int getAttributeIdFromCaptureObjectDefinition(
      final String captureObjectDefinition) {
    return Integer.parseInt(captureObjectDefinition.split(",")[1]);
  }

  public List<CaptureObject> getCaptureObjects(
      final CosemObject profile,
      final String protocol,
      final String version,
      final String deviceModel)
      throws ObjectConfigException {
    final List<String> captureObjectDefinitions = getCaptureObjectDefinitions(profile);

    final List<CaptureObject> captureObjects = new ArrayList<>();

    for (final String def : captureObjectDefinitions) {
      try {
        final DlmsObjectType objectType = getCosemObjectTypeFromCaptureObjectDefinition(def);
        final int attributeId = getAttributeIdFromCaptureObjectDefinition(def);
        final CosemObject cosemObject =
            this.getCosemObject(protocol, version, objectType, deviceModel);
        captureObjects.add(new CaptureObject(cosemObject, attributeId));
      } catch (final ObjectConfigException e) {
        throw new ObjectConfigException("Capture object " + def + " not found in object config", e);
      }
    }
    return captureObjects;
  }

  protected CosemObject handleValueBasedOnModel(
      final CosemObject cosemObject, final String deviceModel) throws ObjectConfigException {
    if (deviceModel == null) {
      return cosemObject;
    }

    final Optional<Attribute> attributeOptional =
        cosemObject.getAttributes().stream()
            .filter(attr -> attr.getValuetype() == ValueType.BASED_ON_MODEL)
            .findFirst();

    if (attributeOptional.isEmpty()) {
      return cosemObject;
    } else {
      final Attribute originalAttribute = attributeOptional.get();

      final String configLookupType =
          this.getConfigLookupType(originalAttribute.getValuebasedonmodel().getType(), deviceModel);

      final Optional<TypeBasedValue> typeBasedValueOptional =
          originalAttribute.getValuebasedonmodel().getValues().stream()
              .filter(tbv -> tbv.getTypes().contains(configLookupType))
              .findFirst();

      if (typeBasedValueOptional.isPresent()) {
        final String value = typeBasedValueOptional.get().getValue();
        final Attribute newAttribute =
            originalAttribute.copyWithNewValueAndType(value, ValueType.FIXED_IN_METER);
        return cosemObject.copyWithNewAttribute(newAttribute);
      } else {
        // If no value was found for this device model, then set the value to Dynamic and log the
        // message
        // The command executor should get the value from the meter.
        log.info(
            "Could not find value for devicemodel {} for {}, set the value to Dynamic and get the value from the meter",
            deviceModel,
            originalAttribute.getValuebasedonmodel().getType());
        final Attribute newAttribute =
            originalAttribute.copyWithNewValueAndType(null, ValueType.DYNAMIC);
        return cosemObject.copyWithNewAttribute(newAttribute);
      }
    }
  }

  private List<ConfigLookupGroup> getConfigLookupGroupsFromResources() throws IOException {
    final PathMatchingResourcePatternResolver scanner = new PathMatchingResourcePatternResolver();
    final Resource resource = scanner.getResource("dlmsprofiles/configlookup.json");

    final ObjectMapper objectMapper = new ObjectMapper();

    return objectMapper.readValue(resource.getInputStream(), new TypeReference<>() {});
  }
}

// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.dlms.services;

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
import org.opensmartgridplatform.dlms.objectconfig.CosemObject;
import org.opensmartgridplatform.dlms.objectconfig.DlmsObjectType;
import org.opensmartgridplatform.dlms.objectconfig.DlmsProfile;
import org.opensmartgridplatform.dlms.objectconfig.DlmsProfileValidator;
import org.opensmartgridplatform.dlms.objectconfig.ObjectProperty;
import org.opensmartgridplatform.dlms.objectconfig.ParentProfile;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ObjectConfigService {

  private final List<DlmsProfile> dlmsProfiles;

  /*
   * Provide a list of DlmsProfile to the constructor or add a null value and profiles are loaded from the classpath resource '/dlmsprofiles'.
   */
  public ObjectConfigService() throws ObjectConfigException, IOException {
    this.dlmsProfiles = this.getDlmsProfileListFromResources();

    DlmsProfileValidator.validate(this.dlmsProfiles);
    this.dlmsProfiles.forEach(DlmsProfile::createMap);
  }

  public CosemObject getCosemObject(
      final String protocolName, final String protocolVersion, final DlmsObjectType dlmsObjectType)
      throws IllegalArgumentException, ObjectConfigException {
    final Optional<CosemObject> optionalCosemObject =
        this.getOptionalCosemObject(protocolName, protocolVersion, dlmsObjectType);
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
    final Map<DlmsObjectType, CosemObject> cosemObjects =
        this.getCosemObjects(protocolName, protocolVersion);
    return Optional.ofNullable(cosemObjects.get(dlmsObjectType));
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
    return dlmsProfile.get().getObjectMap();
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
            resource -> resource.getFilename() != null && resource.getFilename().endsWith(".json"))
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
}

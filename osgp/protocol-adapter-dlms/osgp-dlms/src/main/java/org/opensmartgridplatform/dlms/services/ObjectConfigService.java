/*
 * Copyright 2022 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 */

package org.opensmartgridplatform.dlms.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
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

  private List<DlmsProfile> dlmsProfiles;

  public ObjectConfigService() {}

  public ObjectConfigService(final List<DlmsProfile> dlmsProfiles)
      throws ObjectConfigException, IOException {
    if (dlmsProfiles == null) {
      this.dlmsProfiles = this.getDlmsProfileListFromResources();
    } else {
      this.dlmsProfiles = dlmsProfiles;
    }

    DlmsProfileValidator.validate(this.dlmsProfiles);
    this.handleInheritance();
    this.dlmsProfiles.forEach(DlmsProfile::createMap);
  }

  public CosemObject getCosemObject(
      final String protocolName, final String protocolVersion, final DlmsObjectType dlmsObjectType)
      throws IllegalArgumentException, ObjectConfigException {
    final Map<DlmsObjectType, CosemObject> cosemObjects =
        this.getCosemObjects(protocolName, protocolVersion);
    if (cosemObjects.containsKey(dlmsObjectType)) {
      return cosemObjects.get(dlmsObjectType);
    } else {
      throw new IllegalArgumentException(
          String.format(
              "No object found of type %s in profile %s version %s",
              dlmsObjectType.value(), protocolName, protocolVersion));
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
      final Map<ObjectProperty, List<Object>> properties)
      throws ObjectConfigException {
    final Map<DlmsObjectType, CosemObject> cosemObjects =
        this.getCosemObjects(protocolName, protocolVersion);

    return cosemObjects.values().stream()
        .filter(object -> this.hasProperties(object, properties))
        .collect(Collectors.toList());
  }

  public List<CosemObject> getCosemObjectsWithProperty(
      final String protocolName,
      final String protocolVersion,
      final ObjectProperty wantedProperty,
      final List<Object> wantedPropertyValues)
      throws ObjectConfigException {
    final Map<DlmsObjectType, CosemObject> cosemObjects =
        this.getCosemObjects(protocolName, protocolVersion);

    return cosemObjects.values().stream()
        .filter(object -> this.hasProperty(object, wantedProperty, wantedPropertyValues))
        .collect(Collectors.toList());
  }

  public Map<DlmsObjectType, CosemObject> getCosemObjects(
      final String protocolName, final String protocolVersion) throws ObjectConfigException {

    if (this.dlmsProfiles == null || this.dlmsProfiles.isEmpty()) {
      throw new ObjectConfigException("No DLMS Profile available");
    }

    final Optional<DlmsProfile> dlmsProfile =
        this.dlmsProfiles.stream()
            .filter(profile -> protocolVersion.equalsIgnoreCase(profile.getVersion()))
            .filter(profile -> protocolName.equalsIgnoreCase(profile.getProfile()))
            .findAny();
    if (!dlmsProfile.isPresent()) {
      throw new ObjectConfigException(
          "DLMS Profile for " + protocolName + " " + protocolVersion + " is not available");
    }
    return dlmsProfile.get().getObjectMap();
  }

  private boolean hasProperty(
      final CosemObject object,
      final ObjectProperty wantedProperty,
      final List<Object> wantedPropertyValues) {
    final Object property = object.getProperty(wantedProperty);

    if (property == null) {
      return false;
    } else if (wantedPropertyValues != null && !wantedPropertyValues.isEmpty()) {
      return wantedPropertyValues.contains(property);
    }

    return true;
  }

  private boolean hasProperties(
      final CosemObject object, final Map<ObjectProperty, List<Object>> properties) {

    return properties.entrySet().stream()
        .allMatch(entry -> this.hasProperty(object, entry.getKey(), entry.getValue()));
  }

  private List<DlmsProfile> getDlmsProfileListFromResources() throws IOException {
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

    return dlmsProfilesFromResources;
  }

  private void handleInheritance() throws ObjectConfigException {
    for (final DlmsProfile dlmsProfile : this.dlmsProfiles) {
      this.getInheritedObjects(dlmsProfile);
    }
  }

  private void getInheritedObjects(final DlmsProfile dlmsProfile) throws ObjectConfigException {
    final ParentProfile parentProfile = dlmsProfile.getInherit();

    if (parentProfile != null) {
      final Optional<DlmsProfile> parentDlmsProfile =
          this.dlmsProfiles.stream()
              .filter(profile -> parentProfile.getVersion().equalsIgnoreCase(profile.getVersion()))
              .filter(profile -> parentProfile.getProfile().equalsIgnoreCase(profile.getProfile()))
              .findAny();

      if (parentDlmsProfile.isPresent()) {
        dlmsProfile.getObjects().addAll(parentDlmsProfile.get().getObjects());
      } else {
        throw new ObjectConfigException(
            "Parent profile "
                + parentProfile
                + " for profile "
                + dlmsProfile.getProfileWithVersion()
                + " not found.");
      }
    }
  }
}

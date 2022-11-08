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
import java.util.EnumMap;
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
import org.opensmartgridplatform.dlms.objectconfig.ObjectProperties;
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

  public List<CosemObject> getCosemObjectsWithProperties(
      final String protocolName,
      final String protocolVersion,
      final ObjectProperties wantedProperty)
      throws ObjectConfigException {
    final Map<DlmsObjectType, CosemObject> cosemObjects =
        this.getCosemObjects(protocolName, protocolVersion);

    return cosemObjects.values().stream()
        .filter(object -> this.hasProperties(object, wantedProperty))
        .collect(Collectors.toList());
  }

  private boolean hasProperties(final CosemObject object, final ObjectProperties wantedProperty) {
    if (wantedProperty.getPqProfile() != null) {
      return object.getProperties().getPqProfile() == null;
    }

    if (wantedProperty.getSelectableObjects() != null) {
      return object.getProperties().getSelectableObjects() == null;
    }

    return false;
  }

  public Map<DlmsObjectType, CosemObject> getCosemObjects(
      final String protocolName, final String protocolVersion) throws ObjectConfigException {

    if (this.dlmsProfiles == null || this.dlmsProfiles.isEmpty()) {
      throw new ObjectConfigException("No DLMS Profile available");
    }

    final Optional<DlmsProfile> dlmsProfile =
        this.dlmsProfiles.stream()
            .filter(profile -> protocolVersion.equalsIgnoreCase(profile.version))
            .filter(profile -> protocolName.equalsIgnoreCase(profile.profile))
            .findAny();
    if (!dlmsProfile.isPresent()) {
      return new EnumMap<>(DlmsObjectType.class);
    }
    return dlmsProfile.get().getObjectMap();
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
              .filter(profile -> parentProfile.getVersion().equalsIgnoreCase(profile.version))
              .filter(profile -> parentProfile.getProfile().equalsIgnoreCase(profile.profile))
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

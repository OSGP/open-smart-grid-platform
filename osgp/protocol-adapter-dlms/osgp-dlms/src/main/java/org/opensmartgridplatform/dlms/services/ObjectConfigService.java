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
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.opensmartgridplatform.dlms.objectconfig.CosemObject;
import org.opensmartgridplatform.dlms.objectconfig.DlmsProfile;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ObjectConfigService {

  private List<DlmsProfile> meterConfigList;

  public ObjectConfigService() {}

  public ObjectConfigService(final List<DlmsProfile> meterConfigList) {
    this.meterConfigList = meterConfigList;
  }

  public CosemObject getCosemObject(
      final String protocolName, final String protocolVersion, final DlmsObjectType dlmsObjectType)
      throws IllegalArgumentException {
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

  public Map<DlmsObjectType, CosemObject> getCosemObjects(
      final String protocolName, final String protocolVersion) {

    try {
      if (this.meterConfigList == null || this.meterConfigList.isEmpty()) {
        this.meterConfigList = this.getMeterConfigListFromResources();
      }

      final Optional<DlmsProfile> meterConfig =
          this.meterConfigList.stream()
              .filter(config -> protocolVersion.equalsIgnoreCase(config.version))
              .filter(config -> protocolName.equalsIgnoreCase(config.profile))
              .findAny();
      if (!meterConfig.isPresent()) {
        return new EnumMap<>(DlmsObjectType.class);
      }
      return this.getCosemObjectFromMeterConfig(meterConfig.get())
          .orElseThrow(
              () ->
                  new IllegalArgumentException(
                      String.format(
                          "no config found for protocol '%s' version '%s' ",
                          protocolName, protocolVersion)));

    } catch (final IOException exception) {
      throw new IllegalArgumentException(
          String.format(
              "no config found for protocol '%s' version '%s' ", protocolName, protocolVersion));
    }
  }

  private Optional<Map<DlmsObjectType, CosemObject>> getCosemObjectFromMeterConfig(
      final DlmsProfile meterConfig) {
    final Map<DlmsObjectType, CosemObject> cosemObjectMap = new EnumMap<>(DlmsObjectType.class);
    meterConfig
        .getObjects()
        .forEach(
            cosemObject ->
                cosemObjectMap.put(DlmsObjectType.fromValue(cosemObject.getTag()), cosemObject));
    return Optional.of(cosemObjectMap);
  }

  private List<DlmsProfile> getMeterConfigListFromResources() throws IOException {
    final String scannedPackage = "meterconfig/*";
    final PathMatchingResourcePatternResolver scanner = new PathMatchingResourcePatternResolver();
    final Resource[] resources = scanner.getResources(scannedPackage);

    final ObjectMapper objectMapper = new ObjectMapper();

    final List<DlmsProfile> meterConfigs = new ArrayList<>();

    Stream.of(resources)
        .filter(
            resource -> resource.getFilename() != null && resource.getFilename().endsWith(".json"))
        .forEach(
            resource -> {
              try {
                final DlmsProfile dlmsProfile =
                    objectMapper.readValue(resource.getInputStream(), DlmsProfile.class);
                meterConfigs.add(dlmsProfile);
              } catch (final IOException e) {
                log.error(String.format("Cannot read config file %s", resource.getFilename()), e);
              }
            });

    return meterConfigs;
  }
}

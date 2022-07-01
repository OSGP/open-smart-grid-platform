/*
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.domain.smartmetering.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;
import org.opensmartgridplatform.domain.smartmetering.config.CosemObject;
import org.opensmartgridplatform.domain.smartmetering.config.MeterConfig;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Service;

@Service
public class DlmsObjectService {

  private List<MeterConfig> meterConfigList;

  public DlmsObjectService() {}

  public DlmsObjectService(final List<MeterConfig> meterConfigList) {
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

      final Optional<MeterConfig> meterConfig =
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
      final MeterConfig meterConfig) {
    final Map<DlmsObjectType, CosemObject> cosemObjectMap = new EnumMap<>(DlmsObjectType.class);
    meterConfig
        .getCosemObjects()
        .forEach(
            cosemObject ->
                cosemObjectMap.put(DlmsObjectType.fromValue(cosemObject.getTag()), cosemObject));
    return Optional.of(cosemObjectMap);
  }

  private List<MeterConfig> getMeterConfigListFromResources() throws IOException {
    final String scannedPackage = "meter_config/*";
    final PathMatchingResourcePatternResolver scanner = new PathMatchingResourcePatternResolver();
    final Resource[] resources = scanner.getResources(scannedPackage);

    final ObjectMapper objectMapper = new ObjectMapper();

    final List<MeterConfig> meterConfigs = new ArrayList<>();

    Stream.of(resources)
        .filter(
            resource -> resource.getFilename() != null && resource.getFilename().endsWith(".json"))
        .forEach(
            resource -> {
              try {
                final MeterConfig meterConfig =
                    objectMapper.readValue(resource.getInputStream(), MeterConfig.class);
                meterConfigs.add(meterConfig);
              } catch (final IOException e) {
                e.printStackTrace();
              }
            });

    return meterConfigs;
  }
}

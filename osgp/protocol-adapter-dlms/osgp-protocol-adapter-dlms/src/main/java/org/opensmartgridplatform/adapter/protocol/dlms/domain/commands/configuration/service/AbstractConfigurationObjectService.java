/*
 * Copyright 2024 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 */

package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.configuration.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.repositories.DlmsDeviceRepository;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ConfigurationFlagDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ConfigurationFlagTypeDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ConfigurationObjectDto;

public abstract class AbstractConfigurationObjectService implements ConfigurationObjectService {

  private final DlmsDeviceRepository dlmsDeviceRepository;

  protected AbstractConfigurationObjectService(final DlmsDeviceRepository dlmsDeviceRepository) {
    this.dlmsDeviceRepository = dlmsDeviceRepository;
  }

  protected void updateHlsActive(
      final DlmsDevice device, final ConfigurationObjectDto configurationObject) {

    final List<ConfigurationFlagDto> configurationFlags =
        configurationObject.getConfigurationFlags().getFlags();

    final Map<ConfigurationFlagTypeDto, Boolean> flagMap =
        configurationFlags.stream()
            .collect(
                Collectors.toMap(
                    ConfigurationFlagDto::getConfigurationFlagType,
                    ConfigurationFlagDto::isEnabled));
    this.dlmsDeviceRepository.updateHlsActive(
        device.getDeviceIdentification(),
        flagMap.get(ConfigurationFlagTypeDto.HLS_3_ON_P3_ENABLE),
        flagMap.get(ConfigurationFlagTypeDto.HLS_4_ON_P3_ENABLE),
        flagMap.get(ConfigurationFlagTypeDto.HLS_5_ON_P3_ENABLE));
  }
}

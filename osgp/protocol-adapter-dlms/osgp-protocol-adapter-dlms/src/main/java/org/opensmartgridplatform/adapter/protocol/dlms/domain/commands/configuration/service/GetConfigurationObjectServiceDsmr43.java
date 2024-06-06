// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.configuration.service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.DlmsHelper;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.ObjectConfigServiceHelper;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.Protocol;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.repositories.DlmsDeviceRepository;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ConfigurationFlagDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ConfigurationFlagTypeDto;
import org.springframework.stereotype.Component;

@Component
public class GetConfigurationObjectServiceDsmr43 extends GetConfigurationObjectServiceDsmr4 {

  public GetConfigurationObjectServiceDsmr43(
      final DlmsHelper dlmsHelper,
      final ObjectConfigServiceHelper objectConfigServiceHelper,
      final DlmsDeviceRepository dlmsDeviceRepository) {
    super(dlmsHelper, objectConfigServiceHelper, dlmsDeviceRepository);
  }

  @Override
  public boolean handles(final Protocol protocol) {
    return protocol != null && protocol.isDsmr43();
  }

  @Override
  Optional<ConfigurationFlagTypeDto> getFlagType(final int bitPosition) {
    return ConfigurationFlagTypeDto.getDsmr43FlagType(bitPosition);
  }

  @Override
  protected void addLowFlags(final List<ConfigurationFlagDto> configurationFlags) {
    final List<ConfigurationFlagTypeDto> highFlags =
        configurationFlags.stream().map(ConfigurationFlagDto::getConfigurationFlagType).toList();
    final Stream<ConfigurationFlagTypeDto> missingFlags =
        Arrays.stream(ConfigurationFlagTypeDto.values())
            .filter(
                configurationFlagTypeDto ->
                    !highFlags.contains(configurationFlagTypeDto)
                        && configurationFlagTypeDto.getBitPositionDsmr43().isPresent());
    missingFlags.forEach(
        missingFlag -> configurationFlags.add(new ConfigurationFlagDto(missingFlag, false)));
  }
}

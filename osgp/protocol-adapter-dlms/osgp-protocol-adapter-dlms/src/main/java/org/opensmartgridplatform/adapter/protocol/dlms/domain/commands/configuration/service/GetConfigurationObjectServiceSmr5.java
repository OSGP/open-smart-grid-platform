// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.configuration.service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import org.openmuc.jdlms.AttributeAddress;
import org.openmuc.jdlms.GetResult;
import org.openmuc.jdlms.datatypes.BitString;
import org.openmuc.jdlms.datatypes.DataObject;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.DlmsHelper;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.ObjectConfigServiceHelper;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.Protocol;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.repositories.DlmsDeviceRepository;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.dlms.objectconfig.DlmsObjectType;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ConfigurationFlagDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ConfigurationFlagTypeDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ConfigurationFlagsDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ConfigurationObjectDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class GetConfigurationObjectServiceSmr5 extends GetConfigurationObjectService {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(GetConfigurationObjectServiceSmr5.class);

  private final DlmsHelper dlmsHelper;

  private final ObjectConfigServiceHelper objectConfigServiceHelper;

  public GetConfigurationObjectServiceSmr5(
      final DlmsHelper dlmsHelper,
      final ObjectConfigServiceHelper objectConfigServiceHelper,
      final DlmsDeviceRepository dlmsDeviceRepository) {
    super(dlmsDeviceRepository);
    this.dlmsHelper = dlmsHelper;
    this.objectConfigServiceHelper = objectConfigServiceHelper;
  }

  @Override
  public boolean handles(final Protocol protocol) {
    return protocol != null && protocol.isSmr5();
  }

  @Override
  ConfigurationObjectDto getConfigurationObject(final GetResult result)
      throws ProtocolAdapterException {

    final DataObject resultData = result.getResultData();
    if (resultData == null || !resultData.isBitString()) {
      LOGGER.warn("Configuration object result data is not a BitString: {}", resultData);
      throw new ProtocolAdapterException(
          "Expected bit-string data as Configuration object result data, but got: "
              + (resultData == null ? "null" : resultData.getType()));
    }
    LOGGER.debug(
        "SMR5 Configuration object current BitString: {}",
        this.dlmsHelper.getDebugInfo(resultData));

    final BitString bitString = resultData.getValue();
    final byte[] flagByteArray = bitString.getBitString();
    final List<ConfigurationFlagDto> configurationFlagDtos =
        this.toConfigurationFlags(flagByteArray);
    this.addLowFlags(configurationFlagDtos);
    final ConfigurationFlagsDto configurationFlagsDto =
        new ConfigurationFlagsDto(configurationFlagDtos);
    return new ConfigurationObjectDto(configurationFlagsDto);
  }

  private void addLowFlags(final List<ConfigurationFlagDto> configurationFlags) {
    final List<ConfigurationFlagTypeDto> highFlags =
        configurationFlags.stream().map(ConfigurationFlagDto::getConfigurationFlagType).toList();
    final Stream<ConfigurationFlagTypeDto> missingFlags =
        Arrays.stream(ConfigurationFlagTypeDto.values())
            .filter(
                configurationFlagTypeDto ->
                    !highFlags.contains(configurationFlagTypeDto)
                        && configurationFlagTypeDto.getBitPositionSmr5().isPresent());
    missingFlags.forEach(
        missingFlag -> configurationFlags.add(new ConfigurationFlagDto(missingFlag, false)));
  }

  @Override
  Optional<ConfigurationFlagTypeDto> getFlagType(final int bitPosition) {
    return ConfigurationFlagTypeDto.getSmr5FlagType(bitPosition);
  }

  @Override
  AttributeAddress getAttributeAddress(final Protocol protocol) throws ProtocolAdapterException {
    return this.objectConfigServiceHelper
        .findOptionalDefaultAttributeAddress(protocol, DlmsObjectType.CONFIGURATION_OBJECT)
        .orElseThrow();
  }
}

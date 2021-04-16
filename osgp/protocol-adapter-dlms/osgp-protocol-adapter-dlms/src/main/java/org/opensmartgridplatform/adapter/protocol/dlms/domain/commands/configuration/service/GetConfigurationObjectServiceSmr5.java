/*
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.configuration.service;

import java.util.List;
import java.util.Optional;
import org.openmuc.jdlms.GetResult;
import org.openmuc.jdlms.datatypes.BitString;
import org.openmuc.jdlms.datatypes.DataObject;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.DlmsHelper;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.Protocol;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
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

  public GetConfigurationObjectServiceSmr5(final DlmsHelper dlmsHelper) {
    this.dlmsHelper = dlmsHelper;
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
    LOGGER.info(
        "SMR5 Configuration object current BitString: {}",
        this.dlmsHelper.getDebugInfo(resultData));

    final BitString bitString = resultData.getValue();
    final byte[] flagByteArray = bitString.getBitString();
    final List<ConfigurationFlagDto> configurationFlagDtos =
        this.toConfigurationFlags(flagByteArray);
    final ConfigurationFlagsDto configurationFlagsDto =
        new ConfigurationFlagsDto(configurationFlagDtos);
    return new ConfigurationObjectDto(configurationFlagsDto);
  }

  @Override
  Optional<ConfigurationFlagTypeDto> getFlagType(final int bitPosition) {
    return ConfigurationFlagTypeDto.getSmr5FlagType(bitPosition);
  }
}

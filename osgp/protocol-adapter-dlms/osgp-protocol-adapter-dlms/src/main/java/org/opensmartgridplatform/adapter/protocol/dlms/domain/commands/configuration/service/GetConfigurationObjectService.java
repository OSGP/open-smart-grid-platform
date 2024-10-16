// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.configuration.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.openmuc.jdlms.AccessResultCode;
import org.openmuc.jdlms.AttributeAddress;
import org.openmuc.jdlms.GetResult;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.JdlmsObjectToStringUtil;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.Protocol;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.repositories.DlmsDeviceRepository;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ConnectionException;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ConfigurationFlagDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ConfigurationFlagTypeDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ConfigurationObjectDto;

@Slf4j
public abstract class GetConfigurationObjectService extends AbstractConfigurationObjectService {

  protected DlmsDeviceRepository dlmsDeviceRepository;

  protected GetConfigurationObjectService(final DlmsDeviceRepository dlmsDeviceRepository) {
    super(dlmsDeviceRepository);
  }

  public ConfigurationObjectDto getConfigurationObject(
      final DlmsConnectionManager conn, final Protocol protocol, final DlmsDevice device)
      throws ProtocolAdapterException {

    final AttributeAddress attributeAddress = this.getAttributeAddress(protocol);

    conn.getDlmsMessageListener()
        .setDescription(
            String.format(
                "Retrieve current ConfigurationObject, attribute: %s",
                JdlmsObjectToStringUtil.describeAttributes(attributeAddress)));
    final ConfigurationObjectDto configurationObject =
        this.getConfigurationObject(this.getGetResult(conn, attributeAddress));

    this.updateHlsActive(device, configurationObject);
    return configurationObject;
  }

  abstract AttributeAddress getAttributeAddress(final Protocol protocol)
      throws ProtocolAdapterException;

  private GetResult getGetResult(
      final DlmsConnectionManager conn, final AttributeAddress attributeAddress)
      throws ProtocolAdapterException {
    log.debug("Get current ConfigurationObject using AttributeAddress {}", attributeAddress);
    try {
      return this.handleBadResults(conn.getConnection().get(attributeAddress));
    } catch (final IOException e) {
      throw new ConnectionException(e);
    }
  }

  private GetResult handleBadResults(final GetResult getResult) throws ProtocolAdapterException {
    if (getResult == null) {
      throw new ProtocolAdapterException(
          "No result received while retrieving current configuration object.");
    } else if (getResult.getResultCode() != AccessResultCode.SUCCESS) {
      throw new ProtocolAdapterException(
          String.format(
              "Non-successful result received retrieving configuration object: %s",
              getResult.getResultCode()));
    }
    return getResult;
  }

  /** Extracts the configuration object from the GetResult */
  abstract ConfigurationObjectDto getConfigurationObject(final GetResult result)
      throws ProtocolAdapterException;

  List<ConfigurationFlagDto> toConfigurationFlags(final byte[] flagBytes) {
    final List<ConfigurationFlagDto> flags = new ArrayList<>();
    final String word = this.toBinary(flagBytes[0]) + this.toBinary(flagBytes[1]);
    for (int index = 0; index < word.length(); index++) {
      if (word.charAt(index) == '1') {
        this.getFlagType(index)
            .ifPresent(
                configurationFlagType ->
                    flags.add(new ConfigurationFlagDto(configurationFlagType, true)));
      }
    }
    return flags;
  }

  private String toBinary(final byte flagByte) {
    return Integer.toBinaryString((flagByte & 0xFF) + 256).substring(1);
  }

  abstract Optional<ConfigurationFlagTypeDto> getFlagType(final int bitPosition);
}

// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.configuration;

import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.AbstractCommandExecutor;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.configuration.service.ProtocolServiceLookup;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.Protocol;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ActionRequestDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ActionResponseDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ConfigurationObjectDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.GetConfigurationObjectRequestDataDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.GetConfigurationObjectResponseDto;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.springframework.stereotype.Component;

@Component
public class GetConfigurationObjectCommandExecutor
    extends AbstractCommandExecutor<Void, ConfigurationObjectDto> {

  private final ProtocolServiceLookup protocolServiceLookup;

  public GetConfigurationObjectCommandExecutor(final ProtocolServiceLookup protocolServiceLookup) {
    super(GetConfigurationObjectRequestDataDto.class);
    this.protocolServiceLookup = protocolServiceLookup;
  }

  @Override
  public Void fromBundleRequestInput(final ActionRequestDto bundleInput)
      throws ProtocolAdapterException {
    this.checkActionRequestType(bundleInput);
    return null;
  }

  @Override
  public ActionResponseDto asBundleResponse(final ConfigurationObjectDto executionResult)
      throws ProtocolAdapterException {
    return new GetConfigurationObjectResponseDto(executionResult);
  }

  @Override
  public ConfigurationObjectDto execute(
      final DlmsConnectionManager conn,
      final DlmsDevice device,
      final Void object,
      final MessageMetadata messageMetadata)
      throws ProtocolAdapterException {
    final Protocol protocol = Protocol.forDevice(device);
    return this.protocolServiceLookup
        .lookupGetService(protocol)
        .getConfigurationObject(conn, protocol, device);
  }
}

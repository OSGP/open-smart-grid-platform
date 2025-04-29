// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.monitoring;

import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.AbstractCommandExecutor;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.GetPowerQualityProfileRequestDataDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.GetPowerQualityProfileResponseDto;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.springframework.stereotype.Component;

@Component
public class GetPowerQualityProfileCommandExecutor
    extends AbstractCommandExecutor<
        GetPowerQualityProfileRequestDataDto, GetPowerQualityProfileResponseDto> {

  private final GetPowerQualityProfileNoSelectiveAccessHandler
      getPowerQualityProfileNoSelectiveAccessHandler;

  private final GetPowerQualityProfileSelectiveAccessHandler
      getPowerQualityProfileSelectiveAccessHandler;

  public GetPowerQualityProfileCommandExecutor(
      final GetPowerQualityProfileNoSelectiveAccessHandler
          getPowerQualityProfileNoSelectiveAccessHandler,
      final GetPowerQualityProfileSelectiveAccessHandler
          getPowerQualityProfileSelectiveAccessHandler) {
    super(GetPowerQualityProfileRequestDataDto.class);
    this.getPowerQualityProfileNoSelectiveAccessHandler =
        getPowerQualityProfileNoSelectiveAccessHandler;
    this.getPowerQualityProfileSelectiveAccessHandler =
        getPowerQualityProfileSelectiveAccessHandler;
  }

  @Override
  public GetPowerQualityProfileResponseDto execute(
      final DlmsConnectionManager conn,
      final DlmsDevice device,
      final GetPowerQualityProfileRequestDataDto getPowerQualityProfileRequestDataDto,
      final MessageMetadata messageMetadata)
      throws ProtocolAdapterException {

    if (device.isSelectiveAccessSupported()) {
      return this.getPowerQualityProfileSelectiveAccessHandler.handle(
          conn, device, getPowerQualityProfileRequestDataDto);
    } else {
      return this.getPowerQualityProfileNoSelectiveAccessHandler.handle(
          conn, device, getPowerQualityProfileRequestDataDto);
    }
  }
}

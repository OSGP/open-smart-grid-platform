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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GetPowerQualityProfileCommandExecutor
    extends AbstractCommandExecutor<
        GetPowerQualityProfileRequestDataDto, GetPowerQualityProfileResponseDto> {

  @Autowired
  private GetPowerQualityProfileNoSelectiveAccessHandler
      getPowerQualityProfileNoSelectiveAccessHandler;

  @Autowired
  private GetPowerQualityProfileSelectiveAccessHandler getPowerQualityProfileSelectiveAccessHandler;

  public GetPowerQualityProfileCommandExecutor() {
    super(GetPowerQualityProfileRequestDataDto.class);
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

// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.monitoring;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.GetPowerQualityProfileRequestDataDto;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;

@ExtendWith(MockitoExtension.class)
public class GetPowerQualityProfileCommandExecutorTest {

  @Mock
  private GetPowerQualityProfileNoSelectiveAccessHandler
      getPowerQualityProfileNoSelectiveAccessHandler;

  @Mock
  private GetPowerQualityProfileSelectiveAccessHandler getPowerQualityProfileSelectiveAccessHandler;

  @Mock private DlmsConnectionManager conn;

  @Mock GetPowerQualityProfileRequestDataDto getPowerQualityProfileRequestDataDto;

  @InjectMocks private GetPowerQualityProfileCommandExecutor executor;

  private static MessageMetadata messageMetadata;

  @BeforeAll
  public static void init() {
    messageMetadata = MessageMetadata.newBuilder().withCorrelationUid("123456").build();
  }

  @Test
  public void executeWithSelectiveAccess() throws ProtocolAdapterException {

    final DlmsDevice dlmsDevice = new DlmsDevice();
    dlmsDevice.setSelectiveAccessSupported(true);

    this.executor.execute(
        this.conn, dlmsDevice, this.getPowerQualityProfileRequestDataDto, messageMetadata);

    verify(this.getPowerQualityProfileSelectiveAccessHandler)
        .handle(
            any(DlmsConnectionManager.class),
            any(DlmsDevice.class),
            any(GetPowerQualityProfileRequestDataDto.class));
  }

  @Test
  public void executeWithoutSelectiveAccess() throws ProtocolAdapterException {

    final DlmsDevice dlmsDevice = new DlmsDevice();
    dlmsDevice.setSelectiveAccessSupported(false);

    this.executor.execute(
        this.conn, dlmsDevice, this.getPowerQualityProfileRequestDataDto, messageMetadata);

    verify(this.getPowerQualityProfileNoSelectiveAccessHandler)
        .handle(
            any(DlmsConnectionManager.class),
            any(DlmsDevice.class),
            any(GetPowerQualityProfileRequestDataDto.class));
  }
}

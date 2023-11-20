// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.configuration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openmuc.jdlms.AccessResultCode;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.configuration.service.GetConfigurationObjectService;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.configuration.service.ProtocolServiceLookup;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.configuration.service.SetConfigurationObjectService;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.Protocol;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ConfigurationObjectDto;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;

@ExtendWith(MockitoExtension.class)
public class SetConfigurationObjectCommandExecutorTest {

  @InjectMocks private SetConfigurationObjectCommandExecutor instance;
  @Mock private ProtocolServiceLookup protocolServiceLookup;
  @Mock private DlmsConnectionManager conn;
  @Mock private ConfigurationObjectDto configurationOnDevice;
  @Mock private GetConfigurationObjectService getService;
  @Mock private SetConfigurationObjectService setService;

  @Mock private ConfigurationObjectDto configurationToSet;

  @Test
  public void execute() throws ProtocolAdapterException {

    // SETUP
    final DlmsDevice device = new DlmsDevice();
    final Protocol protocol = Protocol.DSMR_4_2_2;
    final MessageMetadata messageMetadata =
        MessageMetadata.newBuilder().withCorrelationUid("123456").build();
    device.setProtocol(protocol);

    when(this.protocolServiceLookup.lookupGetService(protocol)).thenReturn(this.getService);
    when(this.getService.getConfigurationObject(this.conn, protocol))
        .thenReturn(this.configurationOnDevice);

    when(this.protocolServiceLookup.lookupSetService(protocol)).thenReturn(this.setService);
    final AccessResultCode accessResultCode = AccessResultCode.SUCCESS;
    when(this.setService.setConfigurationObject(
            this.conn, this.configurationToSet, this.configurationOnDevice, protocol))
        .thenReturn(accessResultCode);

    // CALL
    final AccessResultCode result =
        this.instance.execute(this.conn, device, this.configurationToSet, messageMetadata);

    // VERIFY
    assertThat(result).isSameAs(accessResultCode);
  }
}

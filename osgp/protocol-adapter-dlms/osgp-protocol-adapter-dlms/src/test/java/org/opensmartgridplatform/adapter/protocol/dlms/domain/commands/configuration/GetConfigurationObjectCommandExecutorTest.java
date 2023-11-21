// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.configuration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.configuration.service.GetConfigurationObjectService;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.configuration.service.ProtocolServiceLookup;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.Protocol;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ConfigurationObjectDto;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;

@ExtendWith(MockitoExtension.class)
public class GetConfigurationObjectCommandExecutorTest {

  @InjectMocks private GetConfigurationObjectCommandExecutor instance;
  @Mock private ProtocolServiceLookup protocolServiceLookup;
  @Mock private DlmsConnectionManager conn;
  @Mock private ConfigurationObjectDto configurationObjectDto;
  @Mock private GetConfigurationObjectService getService;

  @ParameterizedTest
  @EnumSource(
      value = Protocol.class,
      names = {"DSMR_4_2_2", "SMR_5_0_0"})
  public void execute(final Protocol protocol) throws ProtocolAdapterException {

    // SETUP
    final DlmsDevice device = new DlmsDevice();
    final MessageMetadata messageMetadata =
        MessageMetadata.newBuilder().withCorrelationUid("123456").build();
    device.setProtocol(protocol);

    when(this.protocolServiceLookup.lookupGetService(protocol)).thenReturn(this.getService);
    when(this.getService.getConfigurationObject(this.conn, protocol))
        .thenReturn(this.configurationObjectDto);

    // CALL
    final ConfigurationObjectDto result =
        this.instance.execute(this.conn, device, null, messageMetadata);

    // VERIFY
    assertThat(result).isSameAs(this.configurationObjectDto);
  }
}

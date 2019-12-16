package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.configuration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.configuration.service.GetConfigurationObjectService;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.configuration.service.ProtocolServiceLookup;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.Protocol;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ConfigurationObjectDto;

@RunWith(MockitoJUnitRunner.class)
public class GetConfigurationObjectCommandExecutorTest {

    @InjectMocks
    private GetConfigurationObjectCommandExecutor instance;
    @Mock
    private ProtocolServiceLookup protocolServiceLookup;
    @Mock
    private DlmsConnectionManager conn;
    @Mock
    private ConfigurationObjectDto configurationObjectDto;
    @Mock
    private GetConfigurationObjectService getService;

    @Test
    public void execute() throws ProtocolAdapterException {

        // SETUP
        final DlmsDevice device = new DlmsDevice();
        final Protocol protocol = Protocol.DSMR_4_2_2;
        device.setProtocol(protocol);

        when(this.protocolServiceLookup.lookupGetService(protocol)).thenReturn(this.getService);
        when(this.getService.getConfigurationObject(this.conn)).thenReturn(this.configurationObjectDto);

        // CALL
        final ConfigurationObjectDto result = this.instance.execute(this.conn, device, null);

        // VERIFY
        assertThat(result).isSameAs(this.configurationObjectDto);
    }
}
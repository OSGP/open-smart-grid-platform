package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.configuration.service;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openmuc.jdlms.AccessResultCode;
import org.openmuc.jdlms.AttributeAddress;
import org.openmuc.jdlms.DlmsConnection;
import org.openmuc.jdlms.GetResult;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.Protocol;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ConnectionException;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging.DlmsMessageListener;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ConfigurationFlagTypeDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ConfigurationObjectDto;

@RunWith(MockitoJUnitRunner.class)
public class GetConfigurationObjectServiceTest {

    private final GetConfigurationObjectService instance = new GetConfigurationObjectService() {
        @Override
        ConfigurationObjectDto getConfigurationObject(final GetResult result) {
            return null;
        }

        @Override
        Optional<ConfigurationFlagTypeDto> getFlagType(final int bitPosition) {
            return Optional.empty();
        }

        @Override
        public boolean handles(final Protocol protocol) {
            return true;
        }
    };

    @Mock
    private DlmsConnectionManager conn;
    @Mock
    private DlmsMessageListener dlmsMessageListener;
    @Mock
    private DlmsConnection dlmsConnection;
    @Mock
    private GetResult getResult;

    @Before
    public void setUp() {
        when(this.conn.getDlmsMessageListener()).thenReturn(this.dlmsMessageListener);
        when(this.conn.getConnection()).thenReturn(this.dlmsConnection);
    }

    @Test(expected = ConnectionException.class)
    public void getConfigurationObjectIOException() throws Exception {

        // SETUP
        when(this.dlmsConnection.get(any(AttributeAddress.class))).thenThrow(new IOException());

        // CALL
        this.instance.getConfigurationObject(this.conn);
    }

    @Test(expected = ProtocolAdapterException.class)
    public void getConfigurationObjectGetResultNull() throws Exception {

        // SETUP
        when(this.dlmsConnection.get(any(AttributeAddress.class))).thenReturn(null);

        // CALL
        this.instance.getConfigurationObject(this.conn);
    }

    @Test(expected = ProtocolAdapterException.class)
    public void getConfigurationObjectGetResultUnsuccessful() throws Exception {

        // SETUP
        when(this.getResult.getResultCode()).thenReturn(AccessResultCode.READ_WRITE_DENIED);
        when(this.dlmsConnection.get(any(AttributeAddress.class))).thenReturn(this.getResult);

        // CALL
        this.instance.getConfigurationObject(this.conn);
    }

    // happy flows covered in IT's
}
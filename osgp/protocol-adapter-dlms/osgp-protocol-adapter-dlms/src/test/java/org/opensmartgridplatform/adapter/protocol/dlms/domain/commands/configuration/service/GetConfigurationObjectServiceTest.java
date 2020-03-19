package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.configuration.service;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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

@ExtendWith(MockitoExtension.class)
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

    @BeforeEach
    public void setUp() {
        when(this.conn.getDlmsMessageListener()).thenReturn(this.dlmsMessageListener);
        when(this.conn.getConnection()).thenReturn(this.dlmsConnection);
    }

    @Test
    public void getConfigurationObjectIOException() throws Exception {

        // SETUP
        when(this.dlmsConnection.get(any(AttributeAddress.class))).thenThrow(new IOException());

        // CALL
        assertThatExceptionOfType(ConnectionException.class).isThrownBy(() -> {
            this.instance.getConfigurationObject(this.conn);
        });
    }

    @Test
    public void getConfigurationObjectGetResultNull() throws Exception {

        // SETUP
        when(this.dlmsConnection.get(any(AttributeAddress.class))).thenReturn(null);

        // CALL
        assertThatExceptionOfType(ProtocolAdapterException.class).isThrownBy(() -> {
            this.instance.getConfigurationObject(this.conn);
        });
    }

    @Test
    public void getConfigurationObjectGetResultUnsuccessful() throws Exception {

        // SETUP
        when(this.getResult.getResultCode()).thenReturn(AccessResultCode.READ_WRITE_DENIED);
        when(this.dlmsConnection.get(any(AttributeAddress.class))).thenReturn(this.getResult);

        // CALL
        assertThatExceptionOfType(ProtocolAdapterException.class).isThrownBy(() -> {
            this.instance.getConfigurationObject(this.conn);
        });
    }

    // happy flows covered in IT's
}

package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.configuration.service;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.openmuc.jdlms.DlmsConnection;
import org.openmuc.jdlms.SetParameter;
import org.openmuc.jdlms.datatypes.DataObject;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.DlmsHelper;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.Protocol;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ConnectionException;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging.DlmsMessageListener;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ConfigurationFlagDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ConfigurationFlagTypeDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ConfigurationFlagsDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ConfigurationObjectDto;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class SetConfigurationObjectServiceTest {

    private SetConfigurationObjectService instance;

    @Mock
    private DlmsHelper dlmsHelper;
    @Mock
    private DlmsConnectionManager conn;
    @Mock
    private DlmsMessageListener dlmsMessageListener;
    @Mock
    private DlmsConnection dlmsConnection;
    @Mock
    private ConfigurationObjectDto configurationToSet;
    @Mock
    private ConfigurationObjectDto configurationOnDevice;

    @BeforeEach
    public void setUp() {
        when(this.conn.getDlmsMessageListener()).thenReturn(this.dlmsMessageListener);
        when(this.conn.getConnection()).thenReturn(this.dlmsConnection);

        this.instance = new SetConfigurationObjectService(this.dlmsHelper) {

            @Override
            public boolean handles(final Protocol protocol) {
                return false;
            }

            @Override
            DataObject buildSetParameterData(final ConfigurationObjectDto configurationToSet,
                    final ConfigurationObjectDto configurationOnDevice) {
                return null;
            }

            @Override
            Optional<Integer> getBitPosition(final ConfigurationFlagTypeDto type) {
                return Optional.empty();
            }
        };
    }

    @Test
    public void setConfigurationObjectIOException() throws Exception {

        // SETUP
        when(this.dlmsConnection.set(any(SetParameter.class))).thenThrow(new IOException());

        assertThatExceptionOfType(ConnectionException.class).isThrownBy(() -> {
            // CALL
            this.instance.setConfigurationObject(this.conn, null, null);
        });
    }

    @Test
    public void getFlagsCannotFindBitPosition() throws Exception {

        // SETUP
        final ArrayList<ConfigurationFlagDto> flags = new ArrayList<>();
        flags.add(new ConfigurationFlagDto(ConfigurationFlagTypeDto.PO_ENABLE, true));
        final ConfigurationFlagsDto flagsToSet = new ConfigurationFlagsDto(flags);
        when(this.configurationToSet.getConfigurationFlags()).thenReturn(flagsToSet);
        when(this.configurationOnDevice.getConfigurationFlags()).thenReturn(this.emptyFlags());

        assertThatExceptionOfType(ProtocolAdapterException.class).isThrownBy(() -> {
            // CALL
            this.instance.getFlags(this.configurationToSet, this.configurationOnDevice);
        });
    }

    @Test
    public void getFlagsNullConfigurationFlags() throws Exception {

        // SETUP
        when(this.configurationToSet.getConfigurationFlags()).thenReturn(null);
        when(this.configurationOnDevice.getConfigurationFlags()).thenReturn(null);

        // CALL
        this.instance.getFlags(this.configurationToSet, this.configurationOnDevice);

        // VERIFY
        // no exception occurs
    }

    private ConfigurationFlagsDto emptyFlags() {
        return new ConfigurationFlagsDto(new ArrayList<>());
    }

    // happy flows covered in IT's
}

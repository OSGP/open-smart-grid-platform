package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.configuration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openmuc.jdlms.AccessResultCode;
import org.openmuc.jdlms.AttributeAddress;
import org.openmuc.jdlms.DlmsConnection;
import org.openmuc.jdlms.GetResult;
import org.openmuc.jdlms.SetParameter;
import org.openmuc.jdlms.datatypes.BitString;
import org.openmuc.jdlms.datatypes.DataObject;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.configuration.service.GetConfigurationObjectService;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.configuration.service.GetConfigurationObjectServiceDsmr4;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.configuration.service.ProtocolService;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.configuration.service.ProtocolServiceLookup;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.configuration.service.SetConfigurationObjectService;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.configuration.service.SetConfigurationObjectServiceDsmr4;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.DlmsHelper;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.Protocol;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging.DlmsMessageListener;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ConfigurationFlagDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ConfigurationFlagTypeDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ConfigurationFlagsDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ConfigurationObjectDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.GprsOperationModeTypeDto;

@RunWith(MockitoJUnitRunner.class)
public class SetConfigurationObjectCommandExecutorIT {

    private static final int INDEX_OF_GPRS_OPERATION_MODE = 0;
    private static final int INDEX_OF_CONFIGURATION_FLAGS = 1;

    private SetConfigurationObjectCommandExecutor instance;
    private ProtocolServiceLookup protocolServiceLookup;
    private List<ProtocolService> protocolServices;
    private GetConfigurationObjectService getService;
    private SetConfigurationObjectService setService;
    private DlmsHelper dlmsHelper;

    @Mock
    private DlmsConnectionManager conn;
    @Mock
    private DlmsMessageListener dlmsMessageListener;
    @Mock
    private DlmsConnection dlmsConnection;
    @Mock
    private GetResult getResult;
    @Captor
    private ArgumentCaptor<SetParameter> setParameterArgumentCaptor;

    @Before
    public void setUp() throws IOException {
        this.dlmsHelper = new DlmsHelper();
        this.getService = new GetConfigurationObjectServiceDsmr4(this.dlmsHelper);
        this.setService = new SetConfigurationObjectServiceDsmr4(this.dlmsHelper);
        this.protocolServices = new ArrayList<>();
        this.protocolServices.add(this.getService);
        this.protocolServices.add(this.setService);
        this.protocolServiceLookup = new ProtocolServiceLookup(this.protocolServices);
        this.instance = new SetConfigurationObjectCommandExecutor(this.protocolServiceLookup);

        when(this.conn.getConnection()).thenReturn(this.dlmsConnection);
        when(this.conn.getDlmsMessageListener()).thenReturn(this.dlmsMessageListener);
        when(this.dlmsConnection.get(any(AttributeAddress.class))).thenReturn(this.getResult);
        when(this.getResult.getResultCode()).thenReturn(AccessResultCode.SUCCESS);
        when(this.dlmsConnection.set(any(SetParameter.class))).thenReturn(AccessResultCode.SUCCESS);
    }

    @Test
    public void execute() throws IOException, ProtocolAdapterException {

        // SETUP
        // 1111000001
        final GprsOperationModeTypeDto gprsModeToSet = GprsOperationModeTypeDto.ALWAYS_ON;
        final ConfigurationObjectDto configurationToSet = this.createConfigurationObjectDto(
                gprsModeToSet, ConfigurationFlagTypeDto.DISCOVER_ON_OPEN_COVER,
                ConfigurationFlagTypeDto.DISCOVER_ON_POWER_ON, ConfigurationFlagTypeDto.DYNAMIC_MBUS_ADDRESS,
                ConfigurationFlagTypeDto.PO_ENABLE, ConfigurationFlagTypeDto.HLS_5_ON_PO_ENABLE);

        // 1000101010
        final byte[] flagsOnDevice = this.createFlagBytes(ConfigurationFlagTypeDto.DISCOVER_ON_OPEN_COVER,
                ConfigurationFlagTypeDto.HLS_3_ON_P_3_ENABLE, ConfigurationFlagTypeDto.HLS_5_ON_P_3_ENABLE,
                ConfigurationFlagTypeDto.HLS_4_ON_PO_ENABLE);

        // Expected merged flags: 1110101010
        // (HLS and P0 settings are read only)

        final GprsOperationModeTypeDto gprsModeOnDevice = GprsOperationModeTypeDto.ALWAYS_ON;
        final DataObject resultData = this.createResultData(flagsOnDevice, gprsModeOnDevice);
        when(this.getResult.getResultData()).thenReturn(resultData);

        final DlmsDevice device = new DlmsDevice();
        device.setProtocol(Protocol.DSMR_4_2_2);

        // CALL
        final AccessResultCode result = this.instance.execute(this.conn, device, configurationToSet);

        // VERIFY
        assertThat(result).isEqualTo(AccessResultCode.SUCCESS);

        verify(this.dlmsConnection).set(this.setParameterArgumentCaptor.capture());
        final SetParameter setParameter = this.setParameterArgumentCaptor.getValue();
        final DataObject setParameterData = setParameter.getData();
        final List<DataObject> elements = resultData.getValue();
        final DataObject gprsModeData = elements.get(INDEX_OF_GPRS_OPERATION_MODE);
        final Number gprsMode = gprsModeData.getValue();
        assertThat(gprsMode).isEqualTo(gprsModeToSet.getNumber());

        final DataObject flagsData = elements.get(INDEX_OF_CONFIGURATION_FLAGS);
        final BitString bitString = flagsData.getValue();

        final String expectedBits = "1110101010";
        final byte b1 = (byte) Integer.parseInt(expectedBits.substring(0, 8), 2);
        final byte b2 = (byte) Integer.parseInt(expectedBits.substring(8), 2);

        final byte[] gesultBytes = bitString.getBitString();
        assertThat(gesultBytes[0]).isEqualTo(b1);
        assertThat(gesultBytes[1]).isEqualTo(b2);
    }

    private ConfigurationObjectDto createConfigurationObjectDto(final GprsOperationModeTypeDto gprsMode,
            final ConfigurationFlagTypeDto... flagTypes) {
        final List<ConfigurationFlagDto> flags = new ArrayList<>();
        Arrays.stream(flagTypes).forEach(flagType -> flags.add(new ConfigurationFlagDto(flagType, true)));
        final ConfigurationFlagsDto configurationFlags = new ConfigurationFlagsDto(flags);
        return new ConfigurationObjectDto(gprsMode, configurationFlags);
    }

    private byte[] createFlagBytes(final ConfigurationFlagTypeDto... flags) {
        final BitSet bitSet = new BitSet(16);
        Arrays.stream(flags).forEach(flag -> bitSet.set(flag.getBitPositionDsmr4(), true));
        return bitSet.toByteArray();
    }

    private DataObject createResultData(final byte[] flagsOnDevice, final GprsOperationModeTypeDto gprsMode) {
        final DataObject gprsModeData = DataObject.newEnumerateData(gprsMode.getNumber());
        final BitString bitString = new BitString(flagsOnDevice, 16);
        final DataObject flags = DataObject.newBitStringData(bitString);
        return DataObject.newStructureData(gprsModeData, flags);
    }

}
package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.configuration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.openmuc.jdlms.AccessResultCode;
import org.openmuc.jdlms.SetParameter;
import org.openmuc.jdlms.datatypes.BitString;
import org.openmuc.jdlms.datatypes.DataObject;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.configuration.service.GetConfigurationObjectService;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.configuration.service.GetConfigurationObjectServiceDsmr4;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.configuration.service.SetConfigurationObjectService;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.configuration.service.SetConfigurationObjectServiceDsmr4;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.DlmsHelper;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.Protocol;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ConfigurationFlagTypeDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ConfigurationObjectDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.GprsOperationModeTypeDto;

@RunWith(MockitoJUnitRunner.class)
public class SetConfigurationObjectCommandExecutorDsmr4IT extends SetConfigurationObjectCommandExecutorITBase {

    private static final int INDEX_OF_GPRS_OPERATION_MODE = 0;
    private static final int INDEX_OF_CONFIGURATION_FLAGS = 1;

    @Before
    public void setUp() throws IOException {
        final DlmsHelper dlmsHelper = new DlmsHelper();
        final GetConfigurationObjectService getService = new GetConfigurationObjectServiceDsmr4(dlmsHelper);
        final SetConfigurationObjectService setService = new SetConfigurationObjectServiceDsmr4(dlmsHelper);
        super.setUp(getService, setService);
    }

    @Test
    public void execute() throws IOException, ProtocolAdapterException {

        // SETUP
        // configurationToSet: 0111-----1------
        final GprsOperationModeTypeDto gprsModeToSet = GprsOperationModeTypeDto.TRIGGERED;
        final ConfigurationObjectDto configurationToSet = this.createConfigurationObjectDto(gprsModeToSet,
                this.createFlagDto(ConfigurationFlagTypeDto.DISCOVER_ON_OPEN_COVER, false),
                this.createFlagDto(ConfigurationFlagTypeDto.DISCOVER_ON_POWER_ON, true),
                this.createFlagDto(ConfigurationFlagTypeDto.DYNAMIC_MBUS_ADDRESS, true),
                this.createFlagDto(ConfigurationFlagTypeDto.PO_ENABLE, true),
                this.createFlagDto(ConfigurationFlagTypeDto.HLS_5_ON_PO_ENABLE, true));

        // flagsOnDevice: 1000101010000000
        final byte[] flagsOnDevice = this.createFlagBytes(ConfigurationFlagTypeDto.DISCOVER_ON_OPEN_COVER,
                ConfigurationFlagTypeDto.HLS_3_ON_P_3_ENABLE, ConfigurationFlagTypeDto.HLS_5_ON_P_3_ENABLE,
                ConfigurationFlagTypeDto.HLS_4_ON_PO_ENABLE);

        // result of merging configurationToSet and flagsOnDevice
        final String expectedMergedFlags = "0111101011000000";

        final GprsOperationModeTypeDto gprsModeOnDevice = GprsOperationModeTypeDto.ALWAYS_ON;
        final DataObject deviceData = this.createStructureData(flagsOnDevice, gprsModeOnDevice);
        when(this.getResult.getResultData()).thenReturn(deviceData);

        final DlmsDevice device = new DlmsDevice();
        device.setProtocol(Protocol.DSMR_4_2_2);

        // CALL
        final AccessResultCode result = this.instance.execute(this.conn, device, configurationToSet);

        // VERIFY
        assertThat(result).isEqualTo(AccessResultCode.SUCCESS);

        final List<DataObject> elements = this.captureSetParameterStructureData();
        final Number gprsOperationMode = elements.get(INDEX_OF_GPRS_OPERATION_MODE).getValue();
        assertThat(gprsOperationMode).isEqualTo(gprsModeToSet.getNumber());

        final BitString configurationFlags = elements.get(INDEX_OF_CONFIGURATION_FLAGS).getValue();
        final byte[] flagsSentToDevice = configurationFlags.getBitString();

        final byte firstExpectedByte = (byte) Integer.parseInt(expectedMergedFlags.substring(0, 8), 2);
        final byte secondExpectedByte = (byte) Integer.parseInt(expectedMergedFlags.substring(8), 2);

        assertThat(flagsSentToDevice[0]).isEqualTo(firstExpectedByte);
        assertThat(flagsSentToDevice[1]).isEqualTo(secondExpectedByte);
    }

    @Override
    String createWord(final ConfigurationFlagTypeDto[] flags) {
        final StringBuilder sb = new StringBuilder("0000000000000000");
        for (final ConfigurationFlagTypeDto flag : flags) {
            sb.setCharAt(flag.getBitPositionDsmr4(), '1');
        }
        return sb.toString();
    }

    private DataObject createStructureData(final byte[] flags, final GprsOperationModeTypeDto gprsMode) {
        final DataObject gprsModeData = DataObject.newEnumerateData(gprsMode.getNumber());
        final BitString bitString = new BitString(flags, 16);
        final DataObject flagsData = DataObject.newBitStringData(bitString);
        return DataObject.newStructureData(gprsModeData, flagsData);
    }

    List<DataObject> captureSetParameterStructureData() throws IOException {
        verify(this.dlmsConnection).set(this.setParameterArgumentCaptor.capture());
        final SetParameter setParameter = this.setParameterArgumentCaptor.getValue();
        final DataObject setParameterData = setParameter.getData();
        return setParameterData.getValue();
    }

}
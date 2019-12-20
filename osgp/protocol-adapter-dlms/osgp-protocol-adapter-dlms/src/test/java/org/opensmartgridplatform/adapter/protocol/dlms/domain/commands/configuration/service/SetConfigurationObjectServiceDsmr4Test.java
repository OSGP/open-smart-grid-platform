package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.configuration.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.openmuc.jdlms.GetResult;
import org.openmuc.jdlms.datatypes.DataObject;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.Protocol;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ConfigurationFlagTypeDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ConfigurationFlagsDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ConfigurationObjectDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.GprsOperationModeTypeDto;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class SetConfigurationObjectServiceDsmr4Test {

    private static final GprsOperationModeTypeDto GPRS_OPERATION_MODE = GprsOperationModeTypeDto.ALWAYS_ON;

    private SetConfigurationObjectServiceDsmr4 instance;

    @Mock
    private GetResult getResult;
    @Mock
    ConfigurationObjectDto configurationToSet;
    @Mock
    ConfigurationObjectDto configurationOnDevice;

    @BeforeEach
    public void setUp() {
        this.instance = new SetConfigurationObjectServiceDsmr4(null);
        when(this.configurationToSet.getConfigurationFlags()).thenReturn(this.emptyFlags());
        when(this.configurationOnDevice.getConfigurationFlags()).thenReturn(this.emptyFlags());
    }

    @Test
    public void handles() {
        assertThat(this.instance.handles(Protocol.SMR_5_0)).isFalse();
        assertThat(this.instance.handles(Protocol.SMR_5_1)).isFalse();
        assertThat(this.instance.handles(Protocol.DSMR_4_2_2)).isTrue();
        assertThat(this.instance.handles(Protocol.OTHER_PROTOCOL)).isFalse();
        assertThat(this.instance.handles(null)).isFalse();
    }

    @Test
    public void getBitPosition() {
        for (final ConfigurationFlagTypeDto flagTypeDto : ConfigurationFlagTypeDto.values()) {
            flagTypeDto.getBitPositionDsmr4()
                    .ifPresent(bitPosition -> assertThat(
                            this.instance.getBitPosition(flagTypeDto).orElseThrow(IllegalArgumentException::new))
                                    .isEqualTo(bitPosition));
        }
    }

    @Test
    public void buildSetParameterDataGprsModeToSet() throws ProtocolAdapterException {

        // SETUP
        when(this.configurationToSet.getGprsOperationMode()).thenReturn(GPRS_OPERATION_MODE);
        when(this.configurationOnDevice.getGprsOperationMode()).thenReturn(null);

        // CALL
        final DataObject result = this.instance.buildSetParameterData(this.configurationToSet,
                this.configurationOnDevice);

        // VERIFY
        final List<DataObject> elements = result.getValue();
        final int value = elements.get(0).getValue();
        assertThat(value).isEqualTo(GPRS_OPERATION_MODE.getNumber());
    }

    @Test
    public void buildSetParameterDataGprsModeOnDevice() throws ProtocolAdapterException {

        // SETUP
        when(this.configurationToSet.getGprsOperationMode()).thenReturn(null);
        when(this.configurationOnDevice.getGprsOperationMode()).thenReturn(GPRS_OPERATION_MODE);

        // CALL
        final DataObject result = this.instance.buildSetParameterData(this.configurationToSet,
                this.configurationOnDevice);

        // VERIFY
        final List<DataObject> elements = result.getValue();
        final int value = elements.get(0).getValue();
        assertThat(value).isEqualTo(GPRS_OPERATION_MODE.getNumber());
    }

    private ConfigurationFlagsDto emptyFlags() {
        return new ConfigurationFlagsDto(new ArrayList<>());
    }

    // happy flows covered in IT's
}

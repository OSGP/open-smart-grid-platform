package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.configuration.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openmuc.jdlms.GetResult;
import org.openmuc.jdlms.datatypes.DataObject;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.Protocol;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ConfigurationFlagTypeDto;

@RunWith(MockitoJUnitRunner.class)
public class GetConfigurationObjectServiceSmr5Test {

    private GetConfigurationObjectServiceSmr5 instance;

    @Mock
    private GetResult getResult;
    @Mock
    private DataObject nonBitString;

    @Before
    public void setUp() {
        this.instance = new GetConfigurationObjectServiceSmr5(null);
        when(this.nonBitString.isBitString()).thenReturn(false);
    }

    @Test
    public void handles() {
        assertThat(this.instance.handles(Protocol.SMR_5_0)).isTrue();
        assertThat(this.instance.handles(Protocol.SMR_5_1)).isTrue();
        assertThat(this.instance.handles(Protocol.DSMR_4_2_2)).isFalse();
        assertThat(this.instance.handles(Protocol.OTHER_PROTOCOL)).isFalse();
        assertThat(this.instance.handles(null)).isFalse();
    }

    @Test
    public void getFlagType() {
        for (final ConfigurationFlagTypeDto flagTypeDto : ConfigurationFlagTypeDto.values()) {
            flagTypeDto.getBitPositionSmr5().ifPresent(bitPosition -> assertThat(
                    this.instance.getFlagType(bitPosition).orElseThrow(IllegalArgumentException::new)).isEqualTo(
                    flagTypeDto));
        }
    }

    @Test(expected = ProtocolAdapterException.class)
    public void getConfigurationObjectResultDataNull() throws ProtocolAdapterException {
        // SETUP
        when(this.getResult.getResultData()).thenReturn(null);

        // CALL
        this.instance.getConfigurationObject(this.getResult);
    }

    @Test(expected = ProtocolAdapterException.class)
    public void getConfigurationObjectResultDataNotBitString() throws ProtocolAdapterException {
        // SETUP
        when(this.getResult.getResultData()).thenReturn(this.nonBitString);

        // CALL
        this.instance.getConfigurationObject(this.getResult);
    }

    // happy flows covered in IT's
}
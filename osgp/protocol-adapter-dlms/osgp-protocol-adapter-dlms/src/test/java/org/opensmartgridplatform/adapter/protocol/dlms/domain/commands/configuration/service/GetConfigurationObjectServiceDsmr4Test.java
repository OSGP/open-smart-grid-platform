package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.configuration.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openmuc.jdlms.GetResult;
import org.openmuc.jdlms.datatypes.DataObject;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.DlmsHelper;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.Protocol;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ConfigurationFlagTypeDto;

@ExtendWith(MockitoExtension.class)
public class GetConfigurationObjectServiceDsmr4Test {

    private GetConfigurationObjectServiceDsmr4 instance;

    @Mock
    private GetResult getResult;
    @Mock
    private DlmsHelper dlmsHelper;

    @BeforeEach
    public void setUp() {
        this.instance = new GetConfigurationObjectServiceDsmr4(this.dlmsHelper);
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
    public void getFlagType() {
        for (final ConfigurationFlagTypeDto flagTypeDto : ConfigurationFlagTypeDto.values()) {
            flagTypeDto.getBitPositionDsmr4()
                    .ifPresent(bitPosition -> assertThat(
                            this.instance.getFlagType(bitPosition).orElseThrow(IllegalArgumentException::new))
                                    .isEqualTo(flagTypeDto));
        }
    }

    @Test
    public void getConfigurationObjectResultDataNull() throws ProtocolAdapterException {
        // SETUP
        when(this.getResult.getResultData()).thenReturn(null);

        assertThatExceptionOfType(ProtocolAdapterException.class).isThrownBy(() -> {
            // CALL
            this.instance.getConfigurationObject(this.getResult);
        });
    }

    @Test
    public void getConfigurationObjectResultDataNotComplex() throws ProtocolAdapterException {

        // SETUP
        final DataObject nonComplex = mock(DataObject.class);
        when(nonComplex.isComplex()).thenReturn(false);
        when(this.getResult.getResultData()).thenReturn(nonComplex);

        assertThatExceptionOfType(ProtocolAdapterException.class).isThrownBy(() -> {
            // CALL
            this.instance.getConfigurationObject(this.getResult);
        });
    }

    @Test
    public void getConfigurationObjectElementsNull() throws ProtocolAdapterException {

        // SETUP
        final DataObject structure = mock(DataObject.class);
        when(structure.isComplex()).thenReturn(true);
        when(this.getResult.getResultData()).thenReturn(structure);
        final List<DataObject> elements = null;
        when(structure.getValue()).thenReturn(elements);

        assertThatExceptionOfType(ProtocolAdapterException.class).isThrownBy(() -> {
            // CALL
            this.instance.getConfigurationObject(this.getResult);
        });
    }

    @Test
    public void getConfigurationObjectElementsSizeNotTwo() throws ProtocolAdapterException {

        // SETUP
        final DataObject structure = mock(DataObject.class);
        when(structure.isComplex()).thenReturn(true);
        when(this.getResult.getResultData()).thenReturn(structure);
        final List<DataObject> elements = new ArrayList<>();
        // elements is empty so size is not two
        when(structure.getValue()).thenReturn(elements);

        assertThatExceptionOfType(ProtocolAdapterException.class).isThrownBy(() -> {
            // CALL
            this.instance.getConfigurationObject(this.getResult);
        });
    }

    @Test
    public void getConfigurationObjectGprsModeNull() throws ProtocolAdapterException {

        // SETUP
        final DataObject structure = mock(DataObject.class);
        when(structure.isComplex()).thenReturn(true);
        when(this.getResult.getResultData()).thenReturn(structure);
        final List<DataObject> elements = new ArrayList<>();
        // gprs mode is null
        elements.add(null);
        elements.add(null);
        when(structure.getValue()).thenReturn(elements);

        assertThatExceptionOfType(ProtocolAdapterException.class).isThrownBy(() -> {
            // CALL
            this.instance.getConfigurationObject(this.getResult);
        });
    }

    @Test
    public void getConfigurationObjectGprsModeNotNumber() throws ProtocolAdapterException {

        // SETUP
        final DataObject structure = mock(DataObject.class);
        when(structure.isComplex()).thenReturn(true);
        when(this.getResult.getResultData()).thenReturn(structure);
        final List<DataObject> elements = new ArrayList<>();
        final DataObject gprsMode = mock(DataObject.class);
        // gprs mode is not a number
        when(gprsMode.isNumber()).thenReturn(false);
        elements.add(gprsMode);
        elements.add(null);
        when(structure.getValue()).thenReturn(elements);

        assertThatExceptionOfType(ProtocolAdapterException.class).isThrownBy(() -> {
            // CALL
            this.instance.getConfigurationObject(this.getResult);
        });
    }

    @Test
    public void getConfigurationObjectFlagsNull() throws ProtocolAdapterException {

        // SETUP
        final DataObject structure = mock(DataObject.class);
        when(structure.isComplex()).thenReturn(true);
        when(this.getResult.getResultData()).thenReturn(structure);
        final List<DataObject> elements = new ArrayList<>();
        final DataObject gprsMode = mock(DataObject.class);
        when(gprsMode.isNumber()).thenReturn(true);
        when(gprsMode.getValue()).thenReturn(42);
        elements.add(gprsMode);
        // flags is null
        elements.add(null);
        when(structure.getValue()).thenReturn(elements);

        assertThatExceptionOfType(ProtocolAdapterException.class).isThrownBy(() -> {
            // CALL
            this.instance.getConfigurationObject(this.getResult);
        });
    }

    @Test
    public void getConfigurationObjectFlagsNotBitString() throws ProtocolAdapterException {

        // SETUP
        final DataObject structure = mock(DataObject.class);
        when(structure.isComplex()).thenReturn(true);
        when(this.getResult.getResultData()).thenReturn(structure);
        final List<DataObject> elements = new ArrayList<>();
        final DataObject gprsMode = mock(DataObject.class);
        when(gprsMode.isNumber()).thenReturn(true);
        when(gprsMode.getValue()).thenReturn(42);
        elements.add(gprsMode);
        final DataObject flags = mock(DataObject.class);
        // flags is not a BitString
        when(flags.isBitString()).thenReturn(false);
        elements.add(flags);
        when(structure.getValue()).thenReturn(elements);

        assertThatExceptionOfType(ProtocolAdapterException.class).isThrownBy(() -> {
            // CALL
            this.instance.getConfigurationObject(this.getResult);
        });
    }

    // happy flows covered in IT's
}

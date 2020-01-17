package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.configuration.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.Protocol;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;

public class ProtocolServiceLookupTest {

    private ProtocolServiceLookup instance;
    private GetConfigurationObjectServiceDsmr4 getService;

    @BeforeEach
    public void setUp() {
        this.getService = new GetConfigurationObjectServiceDsmr4(null);
        final List<ProtocolService> services = new ArrayList<>();
        services.add(this.getService);
        this.instance = new ProtocolServiceLookup(services);
    }

    @Test
    public void lookupGetService() throws ProtocolAdapterException {

        // SETUP
        final Protocol protocol = Protocol.DSMR_4_2_2;

        // CALL
        final GetConfigurationObjectService result = this.instance.lookupGetService(protocol);

        // VERIFY
        assertThat(result).isSameAs(this.getService);
    }

    @Test
    public void lookupGetServiceNotFound() throws ProtocolAdapterException {

        // SETUP
        final Protocol protocol = Protocol.OTHER_PROTOCOL;

        assertThatExceptionOfType(ProtocolAdapterException.class).isThrownBy(() -> {
            // CALL
            this.instance.lookupSetService(protocol);
        });
    }
}

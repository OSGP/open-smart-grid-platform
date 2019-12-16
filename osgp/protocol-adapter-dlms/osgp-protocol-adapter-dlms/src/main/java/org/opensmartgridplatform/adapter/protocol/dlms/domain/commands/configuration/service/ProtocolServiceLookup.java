package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.configuration.service;

import java.util.List;

import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.Protocol;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.springframework.stereotype.Component;

@Component
public class ProtocolServiceLookup {

    private final List<ProtocolService> protocolServices;

    public ProtocolServiceLookup(final List<ProtocolService> protocolServices) {
        this.protocolServices = protocolServices;
    }

    public GetConfigurationObjectService lookupGetService(final Protocol protocol) throws ProtocolAdapterException {
        return this.lookupProtocolService(GetConfigurationObjectService.class, protocol);
    }

    public SetConfigurationObjectService lookupSetService(final Protocol protocol) throws ProtocolAdapterException {
        return this.lookupProtocolService(SetConfigurationObjectService.class, protocol);
    }

    private <T extends ProtocolService> T lookupProtocolService(final Class<T> type, final Protocol protocol)
            throws ProtocolAdapterException {
        return this.protocolServices.stream().filter(type::isInstance).map(type::cast).filter(
                s -> s.handles(protocol)).findAny().orElseThrow(
                () -> new ProtocolAdapterException(String.format("Cannot find %s for protocol %s", type, protocol)));
    }

}

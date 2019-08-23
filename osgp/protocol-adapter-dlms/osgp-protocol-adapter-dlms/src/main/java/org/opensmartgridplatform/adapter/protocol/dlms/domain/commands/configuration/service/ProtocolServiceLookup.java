package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.configuration.service;

import java.util.List;

import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.Protocol;

public class ProtocolServiceLookup {

    private final List<ProtocolService> protocolServices;

    public ProtocolServiceLookup(final List<ProtocolService> protocolServices) {
        this.protocolServices = protocolServices;
    }

    public GetConfigurationObjectService lookupGetService(final Protocol protocol) {
        return this.lookupProtocolService(GetConfigurationObjectService.class, protocol);
    }

    public SetConfigurationObjectService lookupSetService(final Protocol protocol) {
        return this.lookupProtocolService(SetConfigurationObjectService.class, protocol);
    }

    private <T extends ProtocolService> T lookupProtocolService(final Class<T> type, final Protocol protocol) {
        return this.protocolServices.stream().filter(type::isInstance).map(type::cast).filter(
                s -> s.handles(protocol)).findAny().orElseThrow(() -> new IllegalArgumentException(
                String.format("Cannot find %s for protocol %s", type, protocol)));
    }

}

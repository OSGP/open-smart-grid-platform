package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.configuration;

import java.util.List;

import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.Protocol;

public class GetConfigurationObjectServiceLookup {

    private final List<GetConfigurationObjectService> getConfigurationObjectServices;

    public GetConfigurationObjectServiceLookup(
            final List<GetConfigurationObjectService> getConfigurationObjectServices) {
        this.getConfigurationObjectServices = getConfigurationObjectServices;
    }

    public GetConfigurationObjectService lookupServiceForProtocol(final Protocol protocol) {
        return this.getConfigurationObjectServices.stream().filter(s -> s.handles(protocol)).findAny().orElseThrow(
                () -> new IllegalArgumentException(String.format("Cannot handle protocol %s", protocol)));
    }
}

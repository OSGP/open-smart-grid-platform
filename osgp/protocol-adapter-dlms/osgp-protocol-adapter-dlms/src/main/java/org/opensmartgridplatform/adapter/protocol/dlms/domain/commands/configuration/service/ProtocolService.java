package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.configuration.service;

import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.Protocol;

public interface ProtocolService {

    /**
     * Indicates which protocols this service handles
     */
    boolean handles(Protocol protocol);
}

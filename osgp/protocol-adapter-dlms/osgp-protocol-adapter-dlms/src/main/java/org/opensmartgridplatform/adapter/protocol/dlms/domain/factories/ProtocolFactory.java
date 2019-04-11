package org.opensmartgridplatform.adapter.protocol.dlms.domain.factories;

import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.Protocol;
import org.springframework.stereotype.Component;

@Component
public class ProtocolFactory {

    public Protocol getInstance(String name, String version) {
        return Protocol.withNameAndVersion(name, version);
    }
}

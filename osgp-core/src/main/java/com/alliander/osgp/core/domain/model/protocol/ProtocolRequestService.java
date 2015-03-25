package com.alliander.osgp.core.domain.model.protocol;

import com.alliander.osgp.domain.core.entities.ProtocolInfo;
import com.alliander.osgp.shared.infra.jms.ProtocolRequestMessage;

public interface ProtocolRequestService {
    public void send(ProtocolRequestMessage message, ProtocolInfo protocolInfo);
}

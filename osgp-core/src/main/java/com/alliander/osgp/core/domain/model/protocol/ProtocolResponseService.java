package com.alliander.osgp.core.domain.model.protocol;

import com.alliander.osgp.domain.core.entities.ProtocolInfo;
import com.alliander.osgp.shared.infra.jms.ResponseMessage;

public interface ProtocolResponseService {
    public void send(final ResponseMessage responseMessage, final String messageType, final ProtocolInfo protocolInfo);
}

package com.alliander.osgp.core.domain.model.domain;

import com.alliander.osgp.shared.infra.jms.ProtocolRequestMessage;
import com.alliander.osgp.shared.infra.jms.ProtocolResponseMessage;

public interface DomainResponseService {
    public void send(ProtocolResponseMessage message);

    public void send(ProtocolRequestMessage message, Exception e);
}

package com.alliander.osgp.core.domain.model.domain;

import com.alliander.osgp.domain.core.entities.DomainInfo;
import com.alliander.osgp.shared.infra.jms.RequestMessage;

public interface DomainRequestService {
    public void send(RequestMessage message, String messageType, DomainInfo domainInfo);
}

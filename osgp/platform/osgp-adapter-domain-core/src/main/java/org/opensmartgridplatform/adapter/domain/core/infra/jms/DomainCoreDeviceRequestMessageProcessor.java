/**
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.domain.core.infra.jms;

import javax.annotation.PostConstruct;

import org.opensmartgridplatform.shared.infra.jms.MessageProcessor;
import org.opensmartgridplatform.shared.infra.jms.MessageProcessorMap;
import org.opensmartgridplatform.shared.infra.jms.MessageType;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Base class for MessageProcessor implementations. Each MessageProcessor
 * implementation should be annotated with @Component. Further the MessageType
 * the MessageProcessor implementation can process should be passed in at
 * construction. The Singleton instance is added to the HashMap of
 * MessageProcessors after dependency injection has completed.
 */
public abstract class DomainCoreDeviceRequestMessageProcessor implements MessageProcessor {

    @Autowired
    protected MessageProcessorMap domainCoreDeviceRequestMessageProcessorMap;

    protected final MessageType messageType;

    /**
     * Each MessageProcessor should register its MessageType at construction.
     *
     * @param messageType
     *            The MessageType the MessageProcessor implementation can
     *            process.
     */
    protected DomainCoreDeviceRequestMessageProcessor(final MessageType messageType) {
        this.messageType = messageType;
    }

    /**
     * Initialization function executed after dependency injection has finished.
     * The MessageProcessor Singleton is added to the HashMap of
     * MessageProcessors.
     */
    @PostConstruct
    public void init() {
        this.domainCoreDeviceRequestMessageProcessorMap.addMessageProcessor(this.messageType, this);
    }

}

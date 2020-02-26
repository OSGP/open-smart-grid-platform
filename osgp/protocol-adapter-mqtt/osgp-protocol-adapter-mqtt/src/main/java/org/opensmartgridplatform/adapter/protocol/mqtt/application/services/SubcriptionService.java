/*
 * Copyright 2020 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.mqtt.application.services;

import org.opensmartgridplatform.adapter.protocol.mqtt.domain.commands.SubscriptionCommandExecutor;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service(value = "mqttSubcriptionService")
public class SubcriptionService {

    @Autowired
    private SubscriptionCommandExecutor subscriptionCommandExecutor;

    public void subscribe(final MessageMetadata messageMetadata) {
        final String ipAddress = messageMetadata.getIpAddress();
        final int port = 8883;
        final String topic = "test/topic";
        this.subscriptionCommandExecutor.execute(ipAddress, port, topic);
    }

}

/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.simulator.protocol.iec61850.server.eventlisteners;

import org.opensmartgridplatform.simulator.protocol.iec61850.server.events.ServerSapEvent;
import org.opensmartgridplatform.simulator.protocol.iec61850.server.logicaldevices.substation.Iec61850ServerHelper;
import org.opensmartgridplatform.simulator.protocol.iec61850.server.logicaldevices.substation.Node;
import com.beanit.openiec61850.BasicDataAttribute;
import com.beanit.openiec61850.ServerModel;
import com.beanit.openiec61850.ServerSap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UpdatePqValuesEventListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(UpdatePqValuesEventListener.class);

    @Value("${rtu.enableUpdatePqValuesEventListener:false}")
    private boolean enabled;

    @EventListener
    public void handle(final ServerSapEvent serverSapEvent) {
        if (this.enabled)
            updateServerValue(serverSapEvent.getServerSap());
    }

    private void updateServerValue(final ServerSap serverSap) {
        LOGGER.debug("updateServerValue");
        final ServerModel serverModel = serverSap.getModelCopy();
        final List<Node> nodes = Iec61850ServerHelper.initializeServerNodes(serverModel);
        final List<BasicDataAttribute> changedAttributes = Iec61850ServerHelper.getAllChangedAttributes(nodes);
        serverSap.setValues(changedAttributes);
    }
}

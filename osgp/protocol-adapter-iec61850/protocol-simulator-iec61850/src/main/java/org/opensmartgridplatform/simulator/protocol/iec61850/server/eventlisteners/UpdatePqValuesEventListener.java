// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.simulator.protocol.iec61850.server.eventlisteners;

import com.beanit.openiec61850.BasicDataAttribute;
import com.beanit.openiec61850.ServerModel;
import com.beanit.openiec61850.ServerSap;
import java.util.List;
import org.opensmartgridplatform.simulator.protocol.iec61850.server.events.ServerSapEvent;
import org.opensmartgridplatform.simulator.protocol.iec61850.server.logicaldevices.substation.Iec61850ServerHelper;
import org.opensmartgridplatform.simulator.protocol.iec61850.server.logicaldevices.substation.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class UpdatePqValuesEventListener {
  private static final Logger LOGGER = LoggerFactory.getLogger(UpdatePqValuesEventListener.class);

  @Value("${rtu.enableUpdatePqValuesEventListener:false}")
  private boolean enabled;

  @EventListener
  public void handle(final ServerSapEvent serverSapEvent) {
    if (this.enabled) {
      this.updateServerValue(serverSapEvent.getServerSap());
    }
  }

  private void updateServerValue(final ServerSap serverSap) {
    LOGGER.debug("updateServerValue");
    final ServerModel serverModel = serverSap.getModelCopy();
    final List<Node> nodes = Iec61850ServerHelper.initializeServerNodes(serverModel);
    final List<BasicDataAttribute> changedAttributes =
        Iec61850ServerHelper.getAllChangedAttributes(nodes);
    serverSap.setValues(changedAttributes);
  }
}

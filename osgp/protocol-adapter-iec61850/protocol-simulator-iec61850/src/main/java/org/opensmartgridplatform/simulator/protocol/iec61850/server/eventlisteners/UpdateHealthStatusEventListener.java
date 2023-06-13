// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.simulator.protocol.iec61850.server.eventlisteners;

import com.beanit.openiec61850.BasicDataAttribute;
import com.beanit.openiec61850.BdaInt8;
import com.beanit.openiec61850.Fc;
import com.beanit.openiec61850.ModelNode;
import com.beanit.openiec61850.ServerModel;
import com.beanit.openiec61850.ServerSap;
import java.util.ArrayList;
import java.util.List;
import org.opensmartgridplatform.simulator.protocol.iec61850.server.events.ServerSapEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class UpdateHealthStatusEventListener {
  private static final Logger LOGGER =
      LoggerFactory.getLogger(UpdateHealthStatusEventListener.class);
  private static final byte OK = (byte) 1;
  private static final byte WARNING = (byte) 2;
  private static final byte ALARM = (byte) 3;
  private static final String HEALTH_NODE = "WAGO61850ServerRTU1/LPHD1.PhyHealth";
  private static final String STATUS_VALUE = "stVal";

  @Value("${rtu.enableUpdateHealthStatusEventListener:false}")
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
    final ModelNode modelNode = serverModel.findModelNode(HEALTH_NODE, Fc.ST);
    if (modelNode != null) {
      final ModelNode dataNode = modelNode.getChild(STATUS_VALUE);
      if (dataNode != null && dataNode instanceof BdaInt8) {
        final List<BasicDataAttribute> changedAttributes = new ArrayList<>();
        final BdaInt8 bda = (BdaInt8) dataNode;
        bda.setValue(bda.getValue() == OK ? WARNING : (bda.getValue() == WARNING ? ALARM : OK));
        changedAttributes.add(bda);
        serverSap.setValues(changedAttributes);
      }
    }
  }
}

// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.simulator.protocol.iec61850.server.logicaldevices.substation;

import com.beanit.openiec61850.BasicDataAttribute;
import com.beanit.openiec61850.BdaFloat32;
import com.beanit.openiec61850.BdaTimestamp;
import com.beanit.openiec61850.Fc;
import com.beanit.openiec61850.ModelNode;
import com.beanit.openiec61850.ServerModel;
import java.util.ArrayList;
import java.util.List;

public class Node {
  private static final String MAGNITUDE_NODE = ".cVal.mag.f";
  private static final String TIMESTAMP_NODE = ".t";
  private static final Fc FC_MEASURANDS_VALUES = Fc.MX;

  private final LogicalNodeNode logicalNodeNode;
  private final String nodeId;
  private final double value;

  public Node(final LogicalNodeNode logicalNodeNode, final String nodeId, final double value) {
    this.logicalNodeNode = logicalNodeNode;
    this.nodeId = nodeId;
    this.value = value;
  }

  public List<BasicDataAttribute> getChangedAttributes() {
    final List<BasicDataAttribute> changedAttributes = new ArrayList<>();
    final BasicDataAttribute changedMagnitudeAttribute = this.getChangedMagnitudeAttribute();
    if (changedMagnitudeAttribute != null) {
      changedAttributes.add(changedMagnitudeAttribute);
    }
    final BasicDataAttribute changedTimestampAttribute = this.getChangedTimestampAttribute();
    if (changedTimestampAttribute != null) {
      changedAttributes.add(changedTimestampAttribute);
    }
    return changedAttributes;
  }

  protected String getMagnitudeNodeName() {
    return this.getServerName()
        + "/"
        + this.getLogicalNodeId()
        + "."
        + this.nodeId
        + MAGNITUDE_NODE;
  }

  protected String getNodeId() {
    return this.nodeId;
  }

  protected String getServerName() {
    return this.logicalNodeNode.getLogicalDeviceNode().getServerName();
  }

  protected String getLogicalNodeId() {
    return this.logicalNodeNode.getLogicalNodeType().getId();
  }

  protected double getValue() {
    return this.value;
  }

  private ServerModel getServerModel() {
    return this.logicalNodeNode.getLogicalDeviceNode().getServerModel();
  }

  private BasicDataAttribute getChangedMagnitudeAttribute() {
    final ModelNode node = this.getMagnitudeNode();
    if (node != null) {
      return this.getChangedMagnitudeAttributeForNode(node);
    }
    return null;
  }

  private ModelNode getMagnitudeNode() {
    final ModelNode modelNode =
        this.getServerModel().findModelNode(this.getMagnitudeNodeName(), FC_MEASURANDS_VALUES);
    if (modelNode != null && !(modelNode instanceof BdaFloat32)) {
      return null;
    }
    return modelNode;
  }

  private BasicDataAttribute getChangedMagnitudeAttributeForNode(final ModelNode node) {
    final BdaFloat32 bda = (BdaFloat32) node;
    bda.setFloat((float) this.value);
    return bda;
  }

  private BasicDataAttribute getChangedTimestampAttribute() {
    final ModelNode node = this.getTimestampNode();
    if (node != null) {
      return this.getChangedTimestampAttributeForNode(node);
    }
    return null;
  }

  private ModelNode getTimestampNode() {
    final ModelNode modelNode =
        this.getServerModel().findModelNode(this.getTimestampNodeName(), FC_MEASURANDS_VALUES);
    if (modelNode != null && !(modelNode instanceof BdaTimestamp)) {
      return null;
    }
    return modelNode;
  }

  private String getTimestampNodeName() {
    return this.getServerName()
        + "/"
        + this.getLogicalNodeId()
        + "."
        + this.nodeId
        + TIMESTAMP_NODE;
  }

  private BasicDataAttribute getChangedTimestampAttributeForNode(final ModelNode node) {
    final BdaTimestamp bda = (BdaTimestamp) node;
    bda.setCurrentTime();
    return bda;
  }
}

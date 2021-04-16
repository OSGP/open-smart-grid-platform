/*
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.iec61850.infra.messaging.processors;

import com.beanit.openiec61850.BdaFloat32;
import com.beanit.openiec61850.BdaQuality;
import com.beanit.openiec61850.BdaTimestamp;
import com.beanit.openiec61850.ConstructedDataAttribute;
import com.beanit.openiec61850.Fc;
import com.beanit.openiec61850.FcModelNode;
import com.beanit.openiec61850.LogicalDevice;
import com.beanit.openiec61850.LogicalNode;
import com.beanit.openiec61850.ModelNode;
import com.beanit.openiec61850.ServerModel;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.opensmartgridplatform.adapter.protocol.iec61850.device.da.rtu.DaDeviceRequest;
import org.opensmartgridplatform.adapter.protocol.iec61850.domain.valueobjects.DeviceMessageLog;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.messaging.DaRtuDeviceRequestMessageProcessor;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.Iec61850Client;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.helper.DeviceConnection;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.helper.Function;
import org.opensmartgridplatform.dto.da.GetPQValuesResponseDto;
import org.opensmartgridplatform.dto.da.iec61850.DataSampleDto;
import org.opensmartgridplatform.dto.da.iec61850.LogicalDeviceDto;
import org.opensmartgridplatform.dto.da.iec61850.LogicalNodeDto;
import org.opensmartgridplatform.shared.infra.jms.MessageType;
import org.springframework.stereotype.Component;

/** Class for processing distribution automation get pq values request messages */
@Component("iec61850DistributionAutomationGetPQValuesRequestMessageProcessor")
public class DistributionAutomationGetPQValuesRequestMessageProcessor
    extends DaRtuDeviceRequestMessageProcessor {
  public DistributionAutomationGetPQValuesRequestMessageProcessor() {
    super(MessageType.GET_POWER_QUALITY_VALUES);
  }

  @Override
  public Function<GetPQValuesResponseDto> getDataFunction(
      final Iec61850Client client,
      final DeviceConnection connection,
      final DaDeviceRequest deviceRequest) {
    return (final DeviceMessageLog deviceMessageLog) -> {
      final ServerModel serverModel = connection.getConnection().getServerModel();
      client.readAllDataValues(connection.getConnection().getClientAssociation());
      return new GetPQValuesResponseDto(this.processPQValuesLogicalDevice(serverModel));
    };
  }

  private synchronized List<LogicalDeviceDto> processPQValuesLogicalDevice(
      final ServerModel model) {
    final List<LogicalDeviceDto> logicalDevices = new ArrayList<>();
    for (final ModelNode node : model.getChildren()) {
      if (node instanceof LogicalDevice) {
        final List<LogicalNodeDto> logicalNodes =
            this.processPQValuesLogicalNodes((LogicalDevice) node);
        if (!logicalNodes.isEmpty()) {
          logicalDevices.add(new LogicalDeviceDto(node.getName(), logicalNodes));
        }
      }
    }
    return logicalDevices;
  }

  private List<LogicalNodeDto> processPQValuesLogicalNodes(final LogicalDevice node) {
    final List<LogicalNodeDto> logicalNodes = new ArrayList<>();
    for (final ModelNode subNode : node.getChildren()) {
      if (subNode instanceof LogicalNode) {
        final List<DataSampleDto> data = this.processPQValueNodeChildren((LogicalNode) subNode);
        if (!data.isEmpty()) {
          logicalNodes.add(new LogicalNodeDto(subNode.getName(), data));
        }
      }
    }
    return logicalNodes;
  }

  private List<DataSampleDto> processPQValueNodeChildren(final LogicalNode node) {
    final List<DataSampleDto> data = new ArrayList<>();
    final Collection<ModelNode> children = node.getChildren();
    final Map<String, Set<Fc>> childMap = new HashMap<>();
    for (final ModelNode child : children) {
      if (!childMap.containsKey(child.getName())) {
        childMap.put(child.getName(), new HashSet<>());
      }
      childMap.get(child.getName()).add(((FcModelNode) child).getFc());
    }
    for (final Map.Entry<String, Set<Fc>> childEntry : childMap.entrySet()) {
      final List<DataSampleDto> childData =
          this.processPQValuesFunctionalConstraintObject(
              node, childEntry.getKey(), childEntry.getValue());
      if (!childData.isEmpty()) {
        data.addAll(childData);
      }
    }
    return data;
  }

  private List<DataSampleDto> processPQValuesFunctionalConstraintObject(
      final LogicalNode parentNode, final String childName, final Set<Fc> childFcs) {
    final List<DataSampleDto> data = new ArrayList<>();
    for (final Fc constraint : childFcs) {
      final List<DataSampleDto> childData =
          this.processPQValuesFunctionalChildConstraintObject(parentNode, childName, constraint);
      if (!childData.isEmpty()) {
        data.addAll(childData);
      }
    }
    return data;
  }

  private List<DataSampleDto> processPQValuesFunctionalChildConstraintObject(
      final LogicalNode parentNode, final String childName, final Fc constraint) {
    final List<DataSampleDto> data = new ArrayList<>();
    final ModelNode node = parentNode.getChild(childName, constraint);
    if (Fc.MX == constraint && node.getChildren() != null) {
      if (this.nodeHasBdaQualityChild(node)) {
        data.add(this.processPQValue(node));
      } else {
        for (final ModelNode subNode : node.getChildren()) {
          data.add(this.processPQValue(node, subNode));
        }
      }
    }
    return data;
  }

  private boolean nodeHasBdaQualityChild(final ModelNode node) {
    for (final ModelNode subNode : node.getChildren()) {
      if (subNode instanceof BdaQuality) {
        return true;
      }
    }
    return false;
  }

  private DataSampleDto processPQValue(final ModelNode node) {
    Date ts = null;
    String type = null;
    BigDecimal value = null;
    if (node.getChildren() != null) {
      ts = this.findBdaTimestampNodeValue(node);
      final ModelNode floatNode = this.findBdaFloat32NodeInConstructedDataAttribute(node);
      if (floatNode != null) {
        type = node.getName() + "." + floatNode.getParent().getName() + "." + floatNode.getName();
        value =
            new BigDecimal(
                ((BdaFloat32) floatNode).getFloat(), new MathContext(3, RoundingMode.HALF_EVEN));
      }
    }
    return new DataSampleDto(type, ts, value);
  }

  private Date findBdaTimestampNodeValue(final ModelNode node) {
    Date timestamp = null;
    for (final ModelNode subNode : node.getChildren()) {
      if (subNode instanceof BdaTimestamp) {
        timestamp = ((BdaTimestamp) subNode).getDate();
      }
    }
    return timestamp;
  }

  private ModelNode findBdaFloat32NodeInConstructedDataAttribute(final ModelNode node) {
    ModelNode floatNode = null;
    for (final ModelNode subNode : node.getChildren()) {
      if (subNode instanceof ConstructedDataAttribute && subNode.getChildren() != null) {
        floatNode = this.findBdaFloat32Node(subNode);
      }
    }
    return floatNode;
  }

  private ModelNode findBdaFloat32Node(final ModelNode node) {
    ModelNode floatNode = null;
    for (final ModelNode subNode : node.getChildren()) {
      if (subNode instanceof BdaFloat32) {
        floatNode = subNode;
      }
    }
    return floatNode;
  }

  private DataSampleDto processPQValue(final ModelNode parentNode, final ModelNode node) {
    Date ts = null;
    String type = null;
    BigDecimal value = null;
    if (node.getChildren() != null) {
      ts = this.findBdaTimestampNodeValue(node);
      final ModelNode floatNode =
          this.findDeeperBdaFloat32NodeInConstructedDataAttributeChildren(node);
      if (floatNode != null) {
        type =
            parentNode.getName()
                + "."
                + node.getName()
                + "."
                + floatNode.getParent().getParent().getName()
                + "."
                + floatNode.getParent().getName()
                + "."
                + floatNode.getName();
        value = this.getNodeBigDecimal(floatNode);
      }
    }
    return new DataSampleDto(type, ts, value);
  }

  private ModelNode findDeeperBdaFloat32NodeInConstructedDataAttributeChildren(
      final ModelNode node) {
    ModelNode floatNode = null;
    for (final ModelNode subNode : node.getChildren()) {
      if (subNode instanceof ConstructedDataAttribute && subNode.getChildren() != null) {
        floatNode = this.findBdaFloat32NodeInConstructedDataAttribute(subNode);
      }
    }
    return floatNode;
  }

  private BigDecimal getNodeBigDecimal(final ModelNode node) {
    return new BigDecimal(
        ((BdaFloat32) node).getFloat(), new MathContext(3, RoundingMode.HALF_EVEN));
  }
}

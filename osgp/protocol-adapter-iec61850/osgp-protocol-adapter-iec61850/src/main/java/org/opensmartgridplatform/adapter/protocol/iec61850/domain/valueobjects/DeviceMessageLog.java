// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.iec61850.domain.valueobjects;

import com.beanit.openiec61850.Fc;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.helper.DataAttribute;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.helper.IED;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.helper.LogicalDevice;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.helper.LogicalNode;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.helper.SubDataAttribute;

public class DeviceMessageLog {

  private final IED ied;
  private final LogicalDevice logicalDevice;
  private final String messageType;
  private final Map<String, String> readVariables = new TreeMap<>();

  public DeviceMessageLog(
      final IED ied, final LogicalDevice logicalDevice, final String messageType) {
    this.ied = ied;
    this.logicalDevice = logicalDevice;
    this.messageType = messageType;
  }

  private void addReadVariable(final String variable, final String value) {
    this.readVariables.put(variable, value);
  }

  public String getMessage() {
    String result =
        "LogicalDevice: " + this.ied.getDescription() + this.logicalDevice.getDescription();
    result = result.concat(" messageType: ").concat(this.messageType).concat(" {\n");

    for (final Entry<String, String> entry : this.readVariables.entrySet()) {
      result = result.concat(entry.getKey()).concat(": ").concat(entry.getValue()).concat("\n");
    }

    result = result.concat(" }");
    return result;
  }

  /** Log data attribute with FC and value for a logical node. */
  public void addVariable(
      final LogicalNode logicalNode,
      final DataAttribute dataAttribute,
      final Fc functionalConstraint,
      final String value) {
    final String key =
        logicalNode
            .getDescription()
            .concat(".")
            .concat(dataAttribute.getDescription())
            .concat("[")
            .concat(functionalConstraint.name())
            .concat("]");

    this.addReadVariable(key, value);
  }

  /** Log data attribute, sub data attribute with FC and value for a logical node. */
  public void addVariable(
      final LogicalNode logicalNode,
      final DataAttribute dataAttribute,
      final Fc functionalConstraint,
      final SubDataAttribute subDataAttribute,
      final String value) {
    final String key =
        logicalNode
            .getDescription()
            .concat(".")
            .concat(dataAttribute.getDescription())
            .concat("[")
            .concat(functionalConstraint.name())
            .concat("].")
            .concat(subDataAttribute.getDescription());

    this.addReadVariable(key, value);
  }

  /**
   * Log data attribute, sub data attribute, sub sub data attribute with FC and value for a logical
   * node.
   */
  public void addVariable(
      final LogicalNode logicalNode,
      final DataAttribute dataAttribute,
      final Fc functionalConstraint,
      final SubDataAttribute subDataAttribute,
      final SubDataAttribute subSubDataAttribute,
      final String value) {
    final String key =
        logicalNode
            .getDescription()
            .concat(".")
            .concat(dataAttribute.getDescription())
            .concat("[")
            .concat(functionalConstraint.name())
            .concat("].")
            .concat(subDataAttribute.getDescription())
            .concat(".")
            .concat(subSubDataAttribute.getDescription());

    this.addReadVariable(key, value);
  }

  /**
   * Log data attribute, schedule entry name, sub sub data attribute with FC and value for a logical
   * node.
   */
  public void addVariable(
      final LogicalNode logicalNode,
      final DataAttribute dataAttribute,
      final Fc functionalConstraint,
      final String scheduleEntryName,
      final SubDataAttribute subSubDataAttribute,
      final String value) {
    final String key =
        logicalNode
            .getDescription()
            .concat(".")
            .concat(dataAttribute.getDescription())
            .concat("[")
            .concat(functionalConstraint.name())
            .concat("].")
            .concat(scheduleEntryName)
            .concat(".")
            .concat(subSubDataAttribute.getDescription());

    this.addReadVariable(key, value);
  }
}

/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.iec61850.domain.valueobjects;

import java.util.HashMap;
import java.util.Map;

import org.openmuc.openiec61850.Fc;

import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.helper.DataAttribute;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.helper.IED;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.helper.LogicalDevice;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.helper.LogicalNode;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.helper.SubDataAttribute;

public class DeviceMessageLog {

    private IED ied;
    private LogicalDevice logicalDevice;
    private String messageType;
    private Map<String, String> readVariables = new HashMap<>();

    public DeviceMessageLog(final IED ied, final LogicalDevice logicalDevice, final String messageType) {
        this.ied = ied;
        this.logicalDevice = logicalDevice;
        this.messageType = messageType;

    }

    private void addReadVariable(final String variable, final String value) {
        this.readVariables.put(variable, value);
    }

    public String getMessage() {
        String result = "LogicalDevice: " + this.ied.getDescription() + this.logicalDevice.getDescription();
        result = result.concat(" messageType: ").concat(this.messageType).concat(" {\n");

        for (final String key : this.readVariables.keySet()) {
            result = result.concat(key).concat(": ").concat(this.readVariables.get(key)).concat("\n");
        }

        result = result.concat(" }");
        return result;
    }

    public void addVariable(final LogicalNode logicalNode, final DataAttribute dataAttribute,
            final Fc functionalConstraint, final String value) {
        this.addReadVariable(logicalNode.getDescription().concat(".").concat(dataAttribute.getDescription())
                .concat("[").concat(functionalConstraint.name()).concat("]"), value);

    }

    public void addVariable(final LogicalNode logicalNode, final DataAttribute dataAttribute,
            final Fc functionalConstraint, final SubDataAttribute subDataAttribute, final String value) {
        this.addReadVariable(
                logicalNode.getDescription().concat(".").concat(dataAttribute.getDescription()).concat("[")
                .concat(functionalConstraint.name()).concat("].").concat(subDataAttribute.getDescription()),
                value);
    }

    public void addVariable(final LogicalNode logicalNode, final DataAttribute dataAttribute,
            final Fc functionalConstraint, final SubDataAttribute subDataAttribute,
            final SubDataAttribute subSubDataAttribute, final String value) {
        this.addReadVariable(logicalNode.getDescription().concat(".").concat(dataAttribute.getDescription())
                .concat("[").concat(functionalConstraint.name()).concat("].").concat(subDataAttribute.getDescription())
                .concat(".").concat(subSubDataAttribute.getDescription()), value);
    }

    public void addVariable(final LogicalNode logicalNode, final DataAttribute dataAttribute,
            final Fc functionalConstraint, final String scheduleEntryName, final SubDataAttribute subSubDataAttribute,
            final String value) {
        this.addReadVariable(logicalNode.getDescription().concat(".").concat(dataAttribute.getDescription())
                .concat("[").concat(functionalConstraint.name()).concat("].").concat(scheduleEntryName).concat(".")
                .concat(subSubDataAttribute.getDescription()), value);
    }
}

/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.simulator.protocol.iec61850.server.logicaldevices.wago;

import org.openmuc.openiec61850.BasicDataAttribute;
import org.openmuc.openiec61850.BdaFloat32;
import org.openmuc.openiec61850.BdaTimestamp;
import org.openmuc.openiec61850.Fc;
import org.openmuc.openiec61850.ModelNode;
import org.openmuc.openiec61850.ServerModel;

import java.util.ArrayList;
import java.util.List;

public class WagoNode {
    private static final String MAGNITUDE_NODE = ".cVal.mag.f";
    private static final String TIMESTAMP_NODE = ".t";
    private static final Fc FC_MEASURANDS_VALUES = Fc.MX;

    private final WagoServerField wagoServerField;
    private final String nodeId;
    private final double value;

    public WagoNode(final WagoServerField wagoServerField, final String nodeId, final double value) {
        this.wagoServerField = wagoServerField;
        this.nodeId = nodeId;
        this.value = value;
    }

    protected double getValue() {
        return this.value;
    }

    public List<BasicDataAttribute> getChangedAttributes() {
        final List<BasicDataAttribute> changedAttributes = new ArrayList<>();
        final BasicDataAttribute changedMagnitudeAttribute = getChangedMagnitudeAttribute();
        if (changedMagnitudeAttribute!=null) {
            changedAttributes.add(changedMagnitudeAttribute);
        }
        final BasicDataAttribute changedTimestampAttribute = getChangedTimestampAttribute();
        if (changedTimestampAttribute!=null) {
            changedAttributes.add(changedTimestampAttribute);
        }
        return changedAttributes;
    }

    private BasicDataAttribute getChangedMagnitudeAttribute() {
        final ModelNode node = getMagnitudeNode();
        if (node!=null) {
            return getChangedMagnitudeAttributeForNode(node);
        }
        return null;
    }

    private ModelNode getMagnitudeNode() {
        final ModelNode modelNode = this.getServerModel().findModelNode(getMagnitudeNodeName(), FC_MEASURANDS_VALUES);
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

    protected String getMagnitudeNodeName() {
        return this.getServerName() + "/" + this.getFieldId() + "." + this.nodeId + MAGNITUDE_NODE;
    }

    private BasicDataAttribute getChangedTimestampAttribute() {
        final ModelNode node = getTimestampNode();
        if (node!=null) {
            return getChangedTimestampAttributeForNode(node);
        }
        return null;
    }

    private ModelNode getTimestampNode() {
        final ModelNode modelNode = this.getServerModel().findModelNode(getTimestampNodeName(), FC_MEASURANDS_VALUES);
        if (modelNode != null && !(modelNode instanceof BdaTimestamp)) {
            return null;
        }
        return modelNode;
    }

    private String getTimestampNodeName() {
        return this.getServerName() + "/" + this.getFieldId() + "." + this.nodeId + TIMESTAMP_NODE;
    }

    private BasicDataAttribute getChangedTimestampAttributeForNode(final ModelNode node) {
        final BdaTimestamp bda = (BdaTimestamp) node;
        bda.setCurrentTime();
        return bda;
    }

    protected String getNodeId() {
        return this.nodeId;
    }

    private ServerModel getServerModel() {
        return this.wagoServerField.getServerModel();
    }

    protected String getServerName() {
        return this.wagoServerField.getServerName();
    }

    protected String getFieldId() {
        return this.wagoServerField.getWagoField().getId();
    }
}

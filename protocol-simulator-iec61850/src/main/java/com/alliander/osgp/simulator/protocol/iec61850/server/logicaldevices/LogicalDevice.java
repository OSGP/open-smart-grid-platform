/**
 * Copyright 2014-2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.simulator.protocol.iec61850.server.logicaldevices;

import java.nio.ByteBuffer;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.openmuc.openiec61850.BasicDataAttribute;
import org.openmuc.openiec61850.BdaBoolean;
import org.openmuc.openiec61850.BdaFloat32;
import org.openmuc.openiec61850.BdaInt32;
import org.openmuc.openiec61850.BdaInt64;
import org.openmuc.openiec61850.BdaInt8;
import org.openmuc.openiec61850.BdaQuality;
import org.openmuc.openiec61850.BdaTimestamp;
import org.openmuc.openiec61850.Fc;
import org.openmuc.openiec61850.ServerModel;
import org.springframework.util.StringUtils;

import com.alliander.osgp.simulator.protocol.iec61850.server.BasicDataAttributesHelper;

public abstract class LogicalDevice {

    private String physicalDeviceName;
    private String logicalDeviceName;
    private ServerModel serverModel;

    public LogicalDevice(final String physicalDeviceName, final String logicalDeviceName,
            final ServerModel serverModel) {
        this.physicalDeviceName = physicalDeviceName;
        this.logicalDeviceName = logicalDeviceName;
        this.serverModel = serverModel;
    }

    public void refreshServerModel(final ServerModel serverModel) {
        this.serverModel = serverModel;
    }

    public abstract List<BasicDataAttribute> getAttributesAndSetValues(Date timestamp);

    public abstract BasicDataAttribute getAttributeAndSetValue(String node, String value);

    protected abstract Fc getFunctionalConstraint(String node);

    public BasicDataAttribute getBasicDataAttribute(final String node) {
        final Fc fc = this.getFunctionalConstraint(node);
        if (fc == null) {
            throw this.illegalNodeException(node);
        }
        return this.getBasicDataAttribute(node, fc);
    }

    protected BasicDataAttribute getBasicDataAttribute(final String node, final Fc fc) {
        return (BasicDataAttribute) this.serverModel.findModelNode(this.createNodeName(node), fc);
    }

    public String getPhysicalDeviceName() {
        return this.physicalDeviceName;
    }

    public String getLogicalDeviceName() {
        return this.logicalDeviceName;
    }

    public String getCombinedName() {
        return this.physicalDeviceName + this.getLogicalDeviceName();
    }

    private String createNodeName(final String node) {
        return this.getCombinedName() + "/" + node;
    }

    protected BasicDataAttribute incrementInt(final String node, final Fc fc) {
        final BdaInt32 value = (BdaInt32) this.serverModel.findModelNode(this.createNodeName(node), fc);
        value.setValue(value.getValue() + 1);
        return value;
    }

    protected BasicDataAttribute setTime(final String node, final Fc fc, final Date date) {
        final BdaTimestamp value = (BdaTimestamp) this.serverModel.findModelNode(this.createNodeName(node), fc);
        value.setDate(date);
        return value;
    }

    protected BasicDataAttribute setRandomFloat(final String node, final Fc fc, final int min, final int max) {
        final BdaFloat32 value = (BdaFloat32) this.serverModel.findModelNode(this.createNodeName(node), fc);
        value.setFloat((float) ThreadLocalRandom.current().nextInt(min, max));
        return value;
    }

    protected BasicDataAttribute setFixedFloat(final String node, final Fc fc, final int val) {
        final BdaFloat32 value = (BdaFloat32) this.serverModel.findModelNode(this.createNodeName(node), fc);
        value.setFloat((float) val);
        return value;
    }

    protected BasicDataAttribute setRandomByte(final String node, final Fc fc, final int min, final int max) {
        final BdaInt8 value = (BdaInt8) this.serverModel.findModelNode(this.createNodeName(node), fc);
        value.setValue((byte) ThreadLocalRandom.current().nextInt(min, max));
        return value;
    }

    protected BasicDataAttribute setByte(final String node, final Fc fc, final byte val) {
        final BdaInt8 value = (BdaInt8) this.serverModel.findModelNode(this.createNodeName(node), fc);
        value.setValue(val);
        return value;
    }

    protected BasicDataAttribute setFixedInt(final String node, final Fc fc, final int val) {
        final BdaInt64 value = (BdaInt64) this.serverModel.findModelNode(this.createNodeName(node), fc);
        value.setValue((byte) val);
        return value;
    }

    protected BasicDataAttribute setRandomInt(final String node, final Fc fc, final int min, final int max) {
        final BdaInt32 value = (BdaInt32) this.serverModel.findModelNode(this.createNodeName(node), fc);
        value.setValue(ThreadLocalRandom.current().nextInt(min, max));
        return value;
    }

    protected BasicDataAttribute setInt(final String node, final Fc fc, final int val) {
        final BdaInt32 value = (BdaInt32) this.serverModel.findModelNode(this.createNodeName(node), fc);
        value.setValue(val);
        return value;
    }

    protected BasicDataAttribute setBoolean(final String node, final Fc fc, final boolean b) {
        final BdaBoolean value = (BdaBoolean) this.serverModel.findModelNode(this.createNodeName(node), fc);
        value.setValue(b);
        return value;
    }

    protected BasicDataAttribute setQuality(final String node, final Fc fc, final short q) {
        final BdaQuality value = (BdaQuality) this.serverModel.findModelNode(this.createNodeName(node), fc);
        value.setValue(this.shortToByteArray(q));
        return value;
    }

    private byte[] shortToByteArray(final short value) {
        return ByteBuffer.allocate(2).putShort(value).array();
    }

    protected Date parseDate(final String date) {
        if (StringUtils.isEmpty(date)) {
            return null;
        }
        return BasicDataAttributesHelper.parseDate(date);
    }

    protected IllegalArgumentException illegalNodeException(final String node) {
        return new IllegalArgumentException("Node \"" + node + "\" is not registered with logical device \""
                + this.getLogicalDeviceName() + "\" on simulated RTU device \"" + this.getPhysicalDeviceName() + "\".");
    }

    protected IllegalArgumentException nodeTypeNotConfiguredException(final String node) {
        return new IllegalArgumentException("The data type of node \"" + node
                + "\" is not configured with logical device \"" + this.getLogicalDeviceName()
                + "\" on simulated RTU device \"" + this.getPhysicalDeviceName() + "\".");
    }
}

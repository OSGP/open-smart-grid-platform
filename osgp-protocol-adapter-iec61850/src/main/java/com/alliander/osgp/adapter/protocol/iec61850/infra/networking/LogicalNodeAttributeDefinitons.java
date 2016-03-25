/**
 * Copyright 2014-2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.protocol.iec61850.infra.networking;

/**
 * Definitions of Logical Device, Logical Nodes and Attributes.
 */
public final class LogicalNodeAttributeDefinitons {

    private static final int MAX_RELAY_INDEX = 4;

    public static final String LOGICAL_DEVICE = "SWDeviceGenericIO";

    /**
     * Property of Logical Node for relays, Pos.
     */
    public static final String PROPERTY_POSITION = ".Pos";

    /**
     * Property of Logical Node, for relays, SwitchType
     */
    public static final String PROPERTY_SWITCH_TYPE = ".SwType";

    /**
     * Property of CSLC Node, software configuration.
     */
    public static final String PROPERTY_SOFTWARE_CONFIGURATION = ".SWCf";

    // SWCf

    /**
     * Attribute of Property Pos, used to read the status of the relay.
     */
    public static final String PROPERTY_POSITION_ATTRIBUTE_STATE = "stVal";
    /**
     * Attribute of Property Pos, used to switch the relay on or off.
     */
    public static final String PROPERTY_POSITION_ATTRIBUTE_CONTROL = ".Oper.ctlVal";

    public static final String PROPERTY_SWITCH_TYPE_ATTRIBUTE_STATE = "SwType";

    /*
     * Property of CSLC Node.
     */
    public static final String PROPERTY_NODE_CSLC_PREFIX = "/CSLC";

    /*
     * Prefix of the relays' Logical Node.
     */
    private static final String LOGICAL_NODE_RELAY_PREFIX = "/XSWC";

    private LogicalNodeAttributeDefinitons() {
        // Private constructor to prevent instantiation of this class.
    }

    /**
     * Returns the value of the Relay's logical node for the given index
     */
    public static final String getNodeNameForRelayIndex(final int index) {

        if (index < 1 || index > MAX_RELAY_INDEX) {
            throw new IllegalArgumentException("Invalid index value : " + index);
        }

        return LogicalNodeAttributeDefinitons.LOGICAL_NODE_RELAY_PREFIX + index;
    }
}

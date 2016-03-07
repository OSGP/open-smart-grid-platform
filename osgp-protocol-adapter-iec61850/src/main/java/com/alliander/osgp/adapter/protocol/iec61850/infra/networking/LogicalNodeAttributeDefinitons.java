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
public class LogicalNodeAttributeDefinitons {

    public static final String LOGICAL_DEVICE = "SWDeviceGenericIO";

    /**
     * Logical Node for relay 1, XSWC1.
     */
    public static final String LOGICAL_NODE_XSWC1 = "/XSWI1";
    /**
     * Logical Node for relay 2, XSWC2.
     */
    public static final String LOGICAL_NODE_XSWC2 = "/XSWI2";
    /**
     * Logical Node for relay 3, XSWC2.
     */
    public static final String LOGICAL_NODE_XSWC3 = "/XSWI3";
    /**
     * Logical Node for relay 4, XSWC1.
     */
    public static final String LOGICAL_NODE_XSWC4 = "/XSWI4";

    /**
     * Property of Logical Node for relays, Pos.
     */
    public static final String PROPERTY_POSITION = ".Pos";
    /**
     * Attribute of Property Pos, used to read the status of the relay.
     */
    public static final String PROPERTY_POSITION_ATTRIBUTE_STATE = "stVal";
    /**
     * Attribute of Property Pos, used to switch the relay on or off.
     */
    public static final String PROPERTY_POSITION_ATTRIBUTE_CONTROL = ".Oper.ctlVal";

    public static final String PROPERTY_SWITCH_TYPE_ATTRIBUTE_STATE = "SwType.stVal";

    private LogicalNodeAttributeDefinitons() {
        // Private constructor to prevent instantiation of this class.
    }
}

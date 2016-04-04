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

    /**
     * The name of the Logical Device.
     */
    public static final String LOGICAL_DEVICE = "SWDeviceGenericIO";

    /**
     * Property of Logical Node for relays, Pos.
     */
    public static final String PROPERTY_POSITION = ".Pos";

    /**
     * Property of CSLC Node, clock
     * */
    public static final String PROPERTY_CLOCK = ".Clock";

    /**
     * Property of Logical Node, for relays, SwitchType
     */
    public static final String PROPERTY_SWITCH_TYPE = ".SwType";

    /**
     * Property of CSLC Node, software configuration.
     */
    public static final String PROPERTY_SOFTWARE_CONFIGURATION = ".SWCf";

    /**
     * Property of CSLC Node, IP configuration.
     */
    public static final String PROPERTY_IP_CONFIGURATION = ".IPCf";

    /**
     * Property of CSLC Node, Reg[ister] configuration.
     */
    public static final String PROPERTY_REG_CONFIGURATION = ".Reg";

    /**
     * Attribute of Property Reg, used to read or set the IP address of the
     * platform.
     */
    public static final String PROPERTY_REG_ATTRIBUTE_OSGP_IP_ADDRESS = "svrAddr";

    /**
     * Attribute of Property Reg, used to read or set the port number of the
     * platform.
     */
    public static final String PROPERTY_REG_ATTRIBUTE_SERVER_PORT = "svrPort";

    // SWCf

    /**
     * Attribute of Property Pos, used to read the status of the relay.
     */
    public static final String PROPERTY_POSITION_ATTRIBUTE_STATE = "stVal";

    /**
     * Attribute of Property SWCf, used to read the value of the offset from
     * astronomic sunrise.
     */
    public static final String PROPERTY_POSITION_OFFSET_SUNRISE = "osRise";

    /**
     * Attribute of Property Clock, used to read the value of time sync period.
     */
    public static final String PROPERTY_POSITION_SYNC_PERIOD = "syncPer";

    /**
     * Attribute of Property Clock, used to enable daylights savings.
     */
    public static final String PROPERTY_POSITION_DAYLIGHT_SAVING_ENABLED = "enbDst";

    /**
     * Attribute of Property SWCf, used to read the value of the offset from
     * astronomic sunset.
     */
    public static final String PROPERTY_POSITION_OFFSET_SUNSET = "osSet";

    /**
     * Attribute of Property SWCf, used to read the value of fixed ip address
     */
    public static final String PROPERTY_POSITION_FIXED_IP = "ipAddr";

    /**
     * Attribute of Property SWCf, used to enable dhcp
     */
    public static final String PROPERTY_POSITION_DHCP_ENABLED = "enbDHCP";

    /**
     * Attribute of Property Pos, used to switch the relay on or off.
     */
    public static final String PROPERTY_POSITION_ATTRIBUTE_CONTROL = ".Oper.ctlVal";

    /**
     * Property of CSLC Node, reboot.
     */
    public static final String PROPERTY_RB_OPER = ".RbOper";

    /**
     * Attribute of Property RbOper, used to reboot the device.
     */
    public static final String PROPERTY_RB_OPER_ATTRIBUTE_OPER = "Oper";

    /**
     * Attribute of Property Oper, used to reboot the device.
     */
    public static final String PROPERTY_RB_OPER_ATTRIBUTE_CONTROL = "ctlVal";

    /*
     * CSLC, configuration Logical Node.
     */
    public static final String LOGICAL_NODE_CSLC = "/CSLC";

    /*
     * XSWC, prefix of the relays' Logical Node.
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

/**
 * Copyright 2014-2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.protocol.iec61850.infra.networking;

import com.alliander.osgp.adapter.protocol.iec61850.domain.valueobjects.ScheduleWeekday;
import com.alliander.osgp.adapter.protocol.iec61850.domain.valueobjects.TriggerType;
import com.alliander.osgp.dto.valueobjects.LightType;

/**
 * Definitions of Logical Device, Logical Nodes and Attributes.
 */
public final class LogicalNodeAttributeDefinitons {

    private static final int MAX_RELAY_INDEX = 4;

    private static final int MAX_SCHEDULE_INDEX = 64;

    /**
     * The name of the Logical Device.
     */
    public static final String LOGICAL_DEVICE = "SWDeviceGenericIO";

    /**
     * CSLC, configuration Logical Node.
     */
    public static final String LOGICAL_NODE_CSLC = "/CSLC";

    /*
     * XSWC, prefix of the relays' Logical Node.
     */
    private static final String LOGICAL_NODE_RELAY_PREFIX = "/XSWC";

    /**
     * Property of XSWC Node, CfSt, configuration state of a relay which
     * determines if the relay can be operated.
     */
    public static final String PROPERTY_MASTER_CONTROL = ".CfSt";

    /**
     * Property of XSWC Node, schedule
     * */
    public static final String PROPERTY_SCHEDULE = ".Sche";

    /**
     * Property of XSWC Node, schedule
     * */
    public static final String PROPERTY_SCHEDULE_PREFIX = "sche";

    /**
     * Schedule enabled.
     *
     * Property of sche node, which is a node of the schedule node of the XSWC
     * Node
     * */
    public static final String PROPERTY_SCHEDULE_ENABLE = "enable";

    /**
     * Schedule day of the week. see {@link ScheduleWeekday}
     *
     * Property of sche node, which is a node of the schedule node of the XSWC
     * Node
     * */
    public static final String PROPERTY_SCHEDULE_DAY = "day";

    /**
     * Time on
     *
     * Property of sche node, which is a node of the schedule node of the XSWC
     * Node
     * */
    public static final String PROPERTY_SCHEDULE_TIME_ON = "tOn";

    /**
     * Time on type. see {@link TriggerType}
     *
     * Property of sche node, which is a node of the schedule node of the XSWC
     * Node
     * */
    public static final String PROPERTY_SCHEDULE_TIME_ON_TYPE = "tOnT";

    /**
     * Time off
     *
     * Property of sche node, which is a node of the schedule node of the XSWC
     * Node
     * */
    public static final String PROPERTY_SCHEDULE_TIME_OFF = "tOff";

    /**
     * Time off type. see {@link TriggerType}
     *
     * Property of sche node, which is a node of the schedule node of the XSWC
     * Node
     * */
    public static final String PROPERTY_SCHEDULE_TIME_OFF_TYPE = "tOffT";

    /**
     * Attribute of Property CfSt, enbOper, enables the operation of a relay.
     */
    public static final String PROPERTY_MASTER_CONTROL_ATTRIBUTE_ENABLE_OPERATION = "enbOper";

    /**
     * Property of XSWC Node, Pos.
     */
    public static final String PROPERTY_POSITION = ".Pos";

    /**
     * Attribute of Property Pos, Oper, operates a relay.
     */
    public static final String PROPERTY_POSITION_ATTRIBUTE_OPER = "Oper";

    /**
     * Attribute of Attribute Oper, ctlVal, operates a relay.
     */
    public static final String PROPERTY_POSITION_ATTRIBUTE_OPER_CONTROL_VALUE = "ctlVal";

    /**
     * Property of CSLC Node, clock
     * */
    public static final String PROPERTY_CLOCK = ".Clock";

    /**
     * Property of XSWC Node, SwitchType
     */
    public static final String PROPERTY_SWITCH_TYPE = ".SwType";

    /**
     * Property of CSLC Node, software configuration.
     */
    public static final String PROPERTY_SOFTWARE_CONFIGURATION = ".SWCf";

    /**
     * Property of CSLC Node, Functional firmware configuration.
     */
    public static final String PROPERTY_FUNCTIONAL_FIRMWARE_CONFIGURATION = ".FuncFwDw";

    /**
     * Property of CSLC Node, security firmware configuration.
     */
    public static final String PROPERTY_SECURITY_FIRMWARE_CONFIGURATION = ".ScyFwDw";

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
     * Attribute of Property SWCf, used to read the value of the
     * {@link LightType}
     */
    public static final String PROPERTY_SOFTWARE_CONFIG_LIGHT_TYPE = "LT";

    /**
     * Attribute of both firmware configuration nodes, used to read the value of
     * the current firmware version
     */
    public static final String PROPERTY_FIRMWARE_CONFIG_CURRENT_VERSION = "curVer";

    /**
     * Attribute of Property SWCf, used to read the value of fixed ip address
     */
    public static final String PROPERTY_POSITION_FIXED_IP = "ipAddr";

    /**
     * Attribute of Property SWCf, used to enable dhcp
     */
    public static final String PROPERTY_POSITION_DHCP_ENABLED = "enbDHCP";

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

    /**
     * Returns the value of the Relay's logical node for the given index
     */
    public static final String getSchedulePropertyNameForRelayIndex(final int index) {

        if (index < 1 || index > MAX_SCHEDULE_INDEX) {
            throw new IllegalArgumentException("Invalid index value : " + index);
        }

        return LogicalNodeAttributeDefinitons.PROPERTY_SCHEDULE_PREFIX + index;
    }
}

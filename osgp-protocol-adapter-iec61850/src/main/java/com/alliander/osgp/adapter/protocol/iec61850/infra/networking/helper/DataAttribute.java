/**
 * Copyright 2014-2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.protocol.iec61850.infra.networking.helper;

/**
 * Contains a list of Data attributes of the IEC61850 Device.
 */
public enum DataAttribute {
    /**
     * /** Property of CSLC Node, astronomical time based on GPS location.
     */
    ASTRONOMICAL("Atnm"),
    /**
     * Property of CSLC Node, Certificate authority replacement.
     */
    CERTIFICATE_AUTHORITY_REPLACE("CARepl"),
    /**
     * Property of CSLC Node, clock
     */
    CLOCK("Clock"),
    /**
     * Property of CSLC Node, Event Buffer.
     */
    EVENT_BUFFER("EvnBuf"),
    /**
     * Property of CSLC Node, Functional firmware configuration.
     */
    FUNCTIONAL_FIRMWARE("FuncFwDw"),
    /**
     * Property of CSLC Node, IP configuration.
     */
    IP_CONFIGURATION("IPCf"),
    /**
     * Property of XSWC Node, CfSt, configuration state of a relay which
     * determines if the relay can be operated.
     */
    MASTER_CONTROL("CfSt"),
    /**
     *
     */
    NAME_PLATE("NamPlt"),
    /**
     * Property of XSWC Node, Pos.
     */
    POSITION("Pos"),
    /**
     * Property of CSLC Node, reboot.
     */
    REBOOT_OPERATION("RbOper"),
    /**
     * Property of CSLC Node, Reg[ister] configuration.
     */
    REGISTRATION("Reg"),
    /**
     * Property of LLN0 Node, evn_rpn01, contains the reporting information.
     */
    REPORTING("evn_rpn01"),
    /**
     * Property of XSWC Node, schedule.
     */
    SCHEDULE("Sche"),
    /**
     * Property of CSLC Node, security firmware configuration.
     */
    SECURITY_FIRMWARE("ScyFwDw"),
    /**
     * Property of CSLC Node, Sensor.
     */
    SENSOR("Sensor"),
    /**
     * Property of CSLC Node, software configuration.
     */
    SOFTWARE_CONFIGURATION("SWCf"),
    /**
     * Property of CSLC Node, TLS configuration.
     */
    TLS_CONFIGURATION("TlsCf"),
    /**
     * Property of XSWC Node, SwitchType.
     */
    SWITCH_TYPE("SwType"),
    /**
     * Property of XSWC Node, On Interval Buffer.
     */
    SWITCH_ON_INTERVAL_BUFFER("OnItvB"),
    /**
     * Property of ZGEN Node, generator speed measurement.
     */
    GENERATOR_SPEED("GnSpd"),
    /**
     * Property of ZGEN Node, demanded power setpoint.
     */
    DEMAND_POWER("DmdPwr"),
    /**
     * Property of ZGEN Node, power rating setpoint.
     */
    POWER_RATING("PwrRtg"),
    /**
     * Generic health data attribute
     */
    HEALTH("Health"),
    /**
     * Generic behavior data attribute
     */
    BEHAVIOR("Beh"),
    /**
     * Generic mode data attribute
     */
    MODE("Mod"),
    /**
     * Generic state data attribute
     */
    STATE("GnOpSt"),
    /**
     * Generic operation time data attribute
     */
    OPERATIONAL_HOURS("OpTmsRs"),
    /**
     * Generic operation time data attribute
     */
    OPERATION_TIME("OpTmh"),
    /**
     * Generic maximum power limit data attribute
     */
    MAXIMUM_POWER_LIMIT("MaxWLim"),
    /**
     * Generic actual power limit
     */
    ACTUAL_POWER_LIMIT("OutWSet"),
    /**
     * Generic (mandatory) physical name data attribute
     */
    PHYSICAL_NAME("PhyNam"),
    /**
     * Actual Power
     */
    ACTUAL_POWER("TotW"),
    /**
     * Maximum Actual Power
     */
    MAX_ACTUAL_POWER("MaxWPhs"),
    /**
     * Minimum Actual Power
     */
    MIN_ACTUAL_POWER("MinWPhs"),
    /**
     * Total Energy
     */
    TOTAL_ENERGY("TotWh"),
    /**
     * State of Charge
     */
    STATE_OF_CHARGE("TotPF"),
    /**
     * Property of LLN0 Node, ReportStatus01, contains the reporting
     * information.
     */
    REPORT_STATUS_ONE("ReportStatus01"),
    /**
     * Property of LLN0 Node, ReportMeasurements01, contains the reporting
     * information.
     */
    REPORT_MEASUREMENTS_ONE("ReportMeasurements01"),
    /**
     * Alarm 1
     */
    ALARM_ONE("Alm1"),
    /**
     * Alarm 2
     */
    ALARM_TWO("Alm2"),
    /**
     * Alarm 3
     */
    ALARM_THREE("Alm3"),
    /**
     * Alarm 4
     */
    ALARM_FOUR("Alm4"),
    /**
     * Other Alarms
     */
    ALARM_OTHER("IntIn1"),
    /**
     * Warning 1
     */
    WARNING_ONE("Wrn1"),
    /**
     * Warning 2
     */
    WARNING_TWO("Wrn2"),
    /**
     * Warning 3
     */
    WARNING_THREE("Wrn3"),
    /**
     * Warning 4
     */
    WARNING_FOUR("Wrn4"),
    /**
     * Other warnings
     */
    WARNING_OTHER("IntIn2"),
    /**
     * Schedule ID
     */
    SCHEDULE_ID("SchdId"),
    /**
     * Schedule Type
     */
    SCHEDULE_TYPE("SchdTyp"),
    /**
     * Schedule Category
     */
    SCHEDULE_CAT("SchdCat"),
    /**
     * Schedule Category WAGO RTU
     */
    SCHEDULE_CAT_RTU("SchCat"),
    /**
     * Absolute time schedule entries
     */
    SCHEDULE_ABS_TIME("SchdAbsTm"),
    /**
     * Volume Heat Buffer
     */
    VLMCAP("VlmCap"),
    /**
     * Absolute time schedule entries
     */
    TEMPERATURE("TmpSv"),
    /**
     * Absolute time schedule entries
     */
    MATERIAL_STATUS("MatStat"),
    /**
     * Absolute time schedule entries
     */
    MATERIAL_TYPE("MatTyp"),
    /**
     * Absolute time schedule entries
     */
    MATERIAL_FLOW("FlwRte");

    private String description;

    DataAttribute(final String description) {
        this.description = description;
    }

    public String getDescription() {
        return this.description;
    }

    public static DataAttribute fromString(final String description) {

        if (description != null) {
            for (final DataAttribute da : DataAttribute.values()) {
                if (description.equalsIgnoreCase(da.description)) {
                    return da;
                }
            }
        }
        throw new IllegalArgumentException("No DataAttribute constant with description " + description + " found.");
    }
}

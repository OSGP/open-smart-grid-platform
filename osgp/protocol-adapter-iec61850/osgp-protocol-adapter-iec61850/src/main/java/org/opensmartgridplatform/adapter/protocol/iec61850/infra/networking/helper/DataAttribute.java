// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.helper;

/** Contains a list of Data attributes of the IEC61850 Device. */
public enum DataAttribute {
  /** /** Property of CSLC Node, astronomical time based on GPS location. */
  ASTRONOMICAL("Atnm"),
  /** Property of CSLC Node, Certificate authority replacement. */
  CERTIFICATE_AUTHORITY_REPLACE("CARepl"),
  /** Property of CSLC Node, clock */
  CLOCK("Clock"),
  /** Property of CSLC Node, Event Buffer. */
  EVENT_BUFFER("EvnBuf"),
  /** Property of CSLC Node, Event Report. */
  EVENT_RPN("EvnRpn"),
  /** Property of CSLC Node, Functional firmware configuration. */
  FUNCTIONAL_FIRMWARE("FuncFwDw"),

  IND("Ind"),
  /** Property of CSLC Node, IP configuration. */
  IP_CONFIGURATION("IPCf"),
  /**
   * Property of XSWC Node, CfSt, configuration state of a relay which determines if the relay can
   * be operated.
   */
  MASTER_CONTROL("CfSt"),
  /** */
  NAME_PLATE("NamPlt"),
  /** Property of XSWC Node, Pos. */
  POSITION("Pos"),
  /** Property of LLN0 Node, rcb_A, contains the buffered report information. */
  RCB_A("rcb_A"),
  /** Property of LLN0 Node, rcb_B, contains the unbuffered report information. */
  RCB_B("rcb_B"),
  /** Property of CSLC Node, reboot. */
  REBOOT_OPERATION("RbOper"),
  /** Property of CSLC Node, Reg[ister] configuration. */
  REGISTRATION("Reg"),
  /** Property of LLN0 Node, evn_rpn01, contains the reporting information. */
  REPORTING("evn_rpn01"),
  /** Property of XSWC Node, schedule. */
  SCHEDULE("Sche"),
  /** Property of CSLC Node, security firmware configuration. */
  SECURITY_FIRMWARE("ScyFwDw"),
  /** Property of CSLC Node, Sensor. */
  SENSOR("Sensor"),
  /** Property of CSLC Node, software configuration. */
  SOFTWARE_CONFIGURATION("SWCf"),
  /** Property of CSLC Node, TLS configuration. */
  TLS_CONFIGURATION("TlsCf"),
  /** Property of XSWC Node, SwitchType. */
  SWITCH_TYPE("SwType"),
  /** Property of XSWC Node, On Interval Buffer. */
  SWITCH_ON_INTERVAL_BUFFER("OnItvB"),
  /** Property of ZGEN Node, generator speed measurement. */
  GENERATOR_SPEED("GnSpd"),
  /** Property of ZGEN Node, demanded power setpoint. */
  DEMAND_POWER("DmdPwr"),
  /** Property of ZGEN Node, power rating setpoint. */
  POWER_RATING("PwrRtg"),
  /** Generic health data attribute */
  HEALTH("Health"),
  /** Generic behavior data attribute */
  BEHAVIOR("Beh"),
  /** Generic mode data attribute */
  MODE("Mod"),
  /** Generic state data attribute */
  STATE("GnOpSt"),
  /** Generic operation time data attribute */
  OPERATIONAL_HOURS("OpTmsRs"),
  /** Generic operation time data attribute */
  OPERATION_TIME("OpTmh"),
  /** Generic maximum power limit data attribute */
  MAXIMUM_POWER_LIMIT("MaxWLim"),
  /** Generic actual power limit */
  ACTUAL_POWER_LIMIT("OutWSet"),
  /** Generic (mandatory) physical name data attribute */
  PHYSICAL_NAME("PhyNam"),
  /** Actual Power */
  ACTUAL_POWER("TotW"),
  /** Maximum Actual Power */
  MAX_ACTUAL_POWER("MaxWPhs"),
  /** Minimum Actual Power */
  MIN_ACTUAL_POWER("MinWPhs"),
  /** Total Energy */
  TOTAL_ENERGY("TotWh"),
  /** Battery: State of Charge, Heat Pump: Coefficient of Performance, Wind: Cosinus Phi */
  AVERAGE_POWER_FACTOR("TotPF"),
  /** Property of LLN0 Node, ReportStatus01, contains the reporting information. */
  REPORT_STATUS_ONE("ReportStatus01"),
  /** Property of LLN0 Node, ReportMeasurements01, contains the reporting information. */
  REPORT_MEASUREMENTS_ONE("ReportMeasurements01"),
  /** Property of LLN0 Node, ReportHeartbeat01, contains the reporting information. */
  REPORT_HEARTBEAT_ONE("ReportHeartbeat01"),
  /** Alarm 1 */
  ALARM_ONE("Alm1"),
  /** Alarm 2 */
  ALARM_TWO("Alm2"),
  /** Alarm 3 */
  ALARM_THREE("Alm3"),
  /** Alarm 4 */
  ALARM_FOUR("Alm4"),
  /** Other Alarms */
  ALARM_OTHER("IntIn1"),
  /** Warning 1 */
  WARNING_ONE("Wrn1"),
  /** Warning 2 */
  WARNING_TWO("Wrn2"),
  /** Warning 3 */
  WARNING_THREE("Wrn3"),
  /** Warning 4 */
  WARNING_FOUR("Wrn4"),
  /** Other warnings */
  WARNING_OTHER("IntIn2"),
  /** Schedule ID */
  SCHEDULE_ID("SchdId"),
  /** Schedule Type */
  SCHEDULE_TYPE("SchdTyp"),
  /** Schedule Category */
  SCHEDULE_CAT("SchdCat"),
  /** Schedule Category WAGO RTU */
  SCHEDULE_CAT_RTU("SchCat"),
  /** Absolute time schedule entries */
  SCHEDULE_ABS_TIME("SchdAbsTm"),
  /** Volume Heat Buffer */
  VLMCAP("VlmCap"),
  /** Absolute time schedule entries */
  TEMPERATURE("TmpSv"),
  /** Absolute time schedule entries */
  MATERIAL_STATUS("MatStat"),
  /** Absolute time schedule entries */
  MATERIAL_TYPE("MatTyp"),
  /** Absolute time schedule entries */
  MATERIAL_FLOW("FlwRte"),
  /** Physical Health */
  PHYSICAL_HEALTH("PhyHealth"),
  /** Active Power */
  ACTIVE_POWER("W"),
  /** Active Power Phase A */
  ACTIVE_POWER_PHASE_A("W.phsA"),
  /** Active Power Phase B */
  ACTIVE_POWER_PHASE_B("W.phsB"),
  /** Active Power Phase C */
  ACTIVE_POWER_PHASE_C("W.phsC"),
  /** Frequency */
  FREQUENCY("Hz"),
  /** Phase To Neutral Voltage */
  PHASE_TO_NEUTRAL_VOLTAGE("PNV"),
  /** Phase To Neutral Voltage Phase A */
  PHASE_TO_NEUTRAL_VOLTAGE_PHASE_A("PNV.phsA"),
  /** Phase To Neutral Voltage Phase B */
  PHASE_TO_NEUTRAL_VOLTAGE_PHASE_B("PNV.phsB"),
  /** Phase To Neutral Voltage Phase C */
  PHASE_TO_NEUTRAL_VOLTAGE_PHASE_C("PNV.phsC"),
  /** Power Factor */
  POWER_FACTOR("PF"),
  /** Power Factor Phase A */
  POWER_FACTOR_PHASE_A("PF.phsA"),
  /** Power Factor Phase B */
  POWER_FACTOR_PHASE_B("PF.phsB"),
  /** Power Factor Phase C */
  POWER_FACTOR_PHASE_C("PF.phsC"),
  /** Impedance */
  IMPEDANCE("Z"),
  /** Impedance Phase A */
  IMPEDANCE_PHASE_A("Z.phsA"),
  /** Impedance Phase B */
  IMPEDANCE_PHASE_B("Z.phsB"),
  /** Impedance Phase C */
  IMPEDANCE_PHASE_C("Z.phsC"),
  /** Voltage Dips */
  VOLTAGE_DIPS("OpCntRs");

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
    throw new IllegalArgumentException(
        "No DataAttribute constant with description " + description + " found.");
  }
}

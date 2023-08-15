// Copyright 2012-20 Fraunhofer ISE
// Copyright 2020 Alliander N.V.
// SPDX-FileCopyrightText: Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.dlms.interfaceclass;

/** This enumeration contains interface classes defined in IEC 62056-62. */
public enum InterfaceClass {
  DATA(1, 0),
  REGISTER(3, 0),
  EXTENDED_REGISTER(4, 0),
  DEMAND_REGISTER(5, 0),
  REGISTER_ACTIVATION(6, 0),
  PROFILE_GENERIC(7, 1),
  CLOCK(8, 0),
  SCRIPT_TABLE(9, 0),
  SCHEDULE(10, 0),
  SPECIAL_DAYS_TABLE(11, 0),
  ASSOCIATION_SN(12, 2),
  ASSOCIATION_LN(15, 1),
  SAP_ASSIGNMENT(17, 0),
  IMAGE_TRANSFER(18, 0),
  ACTIVITY_CALENDAR(20, 0),
  REGISTER_MONITOR(21, 0),
  UTILITY_TABLES(26, 0),
  SINGLE_ACTION_SCHEDULE(22, 0),
  REGISTER_TABLE(61, 0),
  STATUS_MAPPING(63, 0),
  DISCONNECT_CONTROL(70, 0),
  MBUS_CLIENT(72, 1),
  MBUS_DIAGNOSTIC(77, 0),

  // Protocol related interface classes
  IEC_LOCAL_PORT_SETUP(19, 1),
  MODEM_CONFIGURATION(27, 1),
  AUTO_ANSWER(28, 0),
  AUTO_CONNECT(29, 1),
  IEC_HDLC_SETUP_CLASS(23, 1),
  IEC_TWISTED_PAIR(24, 0),
  PUSH_SETUP(40, 0),
  TCP_UDP_SETUP(41, 0),
  IP_V4_SETUP(42, 0),
  ETHERNET_SETUP(43, 0),
  PPP_SETUP(44, 0),
  GPRS_MODEM_SETUP(45, 0),
  SMTP_SETUP(46, 0),
  GSM_DIAGNOSTIC(47, 2),
  SECURITY_SETUP(64, 0),

  UNKNOWN(-1, -1);

  private final int id;
  private final int version;

  private static final InterfaceClass[] values = InterfaceClass.values();

  private InterfaceClass(final int id, final int version) {
    this.id = id;
    this.version = version;
  }

  public static InterfaceClass interfaceClassFor(final int id) {
    for (final InterfaceClass interfaceClass : values) {
      if (interfaceClass.id == id) {
        return interfaceClass;
      }
    }

    return UNKNOWN;
  }

  public int id() {
    return this.id;
  }

  public int version() {
    return this.version;
  }
}

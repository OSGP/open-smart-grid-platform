// Copyright 2012-20 Fraunhofer ISE
// Copyright 2020 Alliander N.V.
// SPDX-FileCopyrightText: Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.dlms.interfaceclass;

import java.util.Arrays;
import java.util.Optional;
import org.opensmartgridplatform.dlms.interfaceclass.attribute.ActivityCalendarAttribute;
import org.opensmartgridplatform.dlms.interfaceclass.attribute.AssociationLnAttribute;
import org.opensmartgridplatform.dlms.interfaceclass.attribute.AssociationSnAttribute;
import org.opensmartgridplatform.dlms.interfaceclass.attribute.AttributeClass;
import org.opensmartgridplatform.dlms.interfaceclass.attribute.AttributeType;
import org.opensmartgridplatform.dlms.interfaceclass.attribute.AutoAnswerAttribute;
import org.opensmartgridplatform.dlms.interfaceclass.attribute.AutoConnectAttribute;
import org.opensmartgridplatform.dlms.interfaceclass.attribute.ClockAttribute;
import org.opensmartgridplatform.dlms.interfaceclass.attribute.DataAttribute;
import org.opensmartgridplatform.dlms.interfaceclass.attribute.DemandRegisterAttribute;
import org.opensmartgridplatform.dlms.interfaceclass.attribute.DisconnectControlAttribute;
import org.opensmartgridplatform.dlms.interfaceclass.attribute.EthernetSetupAttribute;
import org.opensmartgridplatform.dlms.interfaceclass.attribute.ExtendedRegisterAttribute;
import org.opensmartgridplatform.dlms.interfaceclass.attribute.GprsModemSetupAttribute;
import org.opensmartgridplatform.dlms.interfaceclass.attribute.GsmDiagnosticAttribute;
import org.opensmartgridplatform.dlms.interfaceclass.attribute.IecHdlcSetupClassAttribute;
import org.opensmartgridplatform.dlms.interfaceclass.attribute.IecLocalPortSetupAttribute;
import org.opensmartgridplatform.dlms.interfaceclass.attribute.IecTwistedPairAttribute;
import org.opensmartgridplatform.dlms.interfaceclass.attribute.ImageTransferAttribute;
import org.opensmartgridplatform.dlms.interfaceclass.attribute.Ipv4SetupAttribute;
import org.opensmartgridplatform.dlms.interfaceclass.attribute.MbusClientAttribute;
import org.opensmartgridplatform.dlms.interfaceclass.attribute.MbusDiagnosticAttribute;
import org.opensmartgridplatform.dlms.interfaceclass.attribute.ModemConfigurationAttribute;
import org.opensmartgridplatform.dlms.interfaceclass.attribute.PppSetupAttribute;
import org.opensmartgridplatform.dlms.interfaceclass.attribute.ProfileGenericAttribute;
import org.opensmartgridplatform.dlms.interfaceclass.attribute.PushSetupAttribute;
import org.opensmartgridplatform.dlms.interfaceclass.attribute.RegisterActivationAttribute;
import org.opensmartgridplatform.dlms.interfaceclass.attribute.RegisterAttribute;
import org.opensmartgridplatform.dlms.interfaceclass.attribute.RegisterMonitorAttribute;
import org.opensmartgridplatform.dlms.interfaceclass.attribute.RegisterTableAttribute;
import org.opensmartgridplatform.dlms.interfaceclass.attribute.SapAssignmentAttribute;
import org.opensmartgridplatform.dlms.interfaceclass.attribute.ScheduleAttribute;
import org.opensmartgridplatform.dlms.interfaceclass.attribute.ScriptTableAttribute;
import org.opensmartgridplatform.dlms.interfaceclass.attribute.SecuritySetupAttribute;
import org.opensmartgridplatform.dlms.interfaceclass.attribute.SingleActionScheduleAttribute;
import org.opensmartgridplatform.dlms.interfaceclass.attribute.SmtpSetupAttribute;
import org.opensmartgridplatform.dlms.interfaceclass.attribute.SpecialDaysTableAttribute;
import org.opensmartgridplatform.dlms.interfaceclass.attribute.StatusMappingAttribute;
import org.opensmartgridplatform.dlms.interfaceclass.attribute.TcpUdpSetupAttribute;
import org.opensmartgridplatform.dlms.interfaceclass.attribute.UtilityTablesAttribute;

/** This enumeration contains interface classes defined in IEC 62056-62. */
public enum InterfaceClass {
  DATA(1, 0, DataAttribute.class),
  REGISTER(3, 0, RegisterAttribute.class),
  EXTENDED_REGISTER(4, 0, ExtendedRegisterAttribute.class),
  DEMAND_REGISTER(5, 0, DemandRegisterAttribute.class),
  REGISTER_ACTIVATION(6, 0, RegisterActivationAttribute.class),
  PROFILE_GENERIC(7, 1, ProfileGenericAttribute.class),
  CLOCK(8, 0, ClockAttribute.class),
  SCRIPT_TABLE(9, 0, ScriptTableAttribute.class),
  SCHEDULE(10, 0, ScheduleAttribute.class),
  SPECIAL_DAYS_TABLE(11, 0, SpecialDaysTableAttribute.class),
  ASSOCIATION_SN(12, 2, AssociationSnAttribute.class),
  ASSOCIATION_LN(15, 1, AssociationLnAttribute.class),
  SAP_ASSIGNMENT(17, 0, SapAssignmentAttribute.class),
  IMAGE_TRANSFER(18, 0, ImageTransferAttribute.class),
  ACTIVITY_CALENDAR(20, 0, ActivityCalendarAttribute.class),
  REGISTER_MONITOR(21, 0, RegisterMonitorAttribute.class),
  UTILITY_TABLES(26, 0, UtilityTablesAttribute.class),
  SINGLE_ACTION_SCHEDULE(22, 0, SingleActionScheduleAttribute.class),
  REGISTER_TABLE(61, 0, RegisterTableAttribute.class),
  STATUS_MAPPING(63, 0, StatusMappingAttribute.class),
  DISCONNECT_CONTROL(70, 0, DisconnectControlAttribute.class),
  MBUS_CLIENT(72, 1, MbusClientAttribute.class),
  MBUS_DIAGNOSTIC(77, 0, MbusDiagnosticAttribute.class),

  // Protocol related interface classes
  IEC_LOCAL_PORT_SETUP(19, 1, IecLocalPortSetupAttribute.class),
  MODEM_CONFIGURATION(27, 1, ModemConfigurationAttribute.class),
  AUTO_ANSWER(28, 0, AutoAnswerAttribute.class),
  AUTO_CONNECT(29, 1, AutoConnectAttribute.class),
  IEC_HDLC_SETUP_CLASS(23, 1, IecHdlcSetupClassAttribute.class),
  IEC_TWISTED_PAIR(24, 0, IecTwistedPairAttribute.class),
  PUSH_SETUP(40, 0, PushSetupAttribute.class),
  TCP_UDP_SETUP(41, 0, TcpUdpSetupAttribute.class),
  IP_V4_SETUP(42, 0, Ipv4SetupAttribute.class),
  ETHERNET_SETUP(43, 0, EthernetSetupAttribute.class),
  PPP_SETUP(44, 0, PppSetupAttribute.class),
  GPRS_MODEM_SETUP(45, 0, GprsModemSetupAttribute.class),
  SMTP_SETUP(46, 0, SmtpSetupAttribute.class),
  GSM_DIAGNOSTIC(47, 2, GsmDiagnosticAttribute.class),
  SECURITY_SETUP(64, 0, SecuritySetupAttribute.class),

  UNKNOWN(-1, -1, null);

  private final int id;
  private final int version;
  private final Class<? extends Enum<?>> attributeClass;

  private static final InterfaceClass[] values = InterfaceClass.values();

  private InterfaceClass(
      final int id, final int version, final Class<? extends Enum<?>> attributeClass) {
    this.id = id;
    this.version = version;
    this.attributeClass = attributeClass;
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

  public AttributeClass[] getAttributeEnumValues() {
    return (AttributeClass[]) this.attributeClass.getEnumConstants();
  }

  public AttributeType getAttributeType(final int attributeId) {
    final AttributeClass[] attributesForClass = this.getAttributeEnumValues();

    final Optional<AttributeClass> optionalAttributeClass =
        Arrays.stream(attributesForClass)
            .filter(attribute -> attribute.attributeId() == attributeId)
            .findFirst();

    AttributeType attributeType = AttributeType.UNKNOWN;
    if (optionalAttributeClass.isPresent()) {
      attributeType = optionalAttributeClass.get().attributeType();
    }

    return attributeType;
  }
}

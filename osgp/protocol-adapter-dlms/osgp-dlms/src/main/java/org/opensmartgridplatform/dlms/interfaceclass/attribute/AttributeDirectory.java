//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.dlms.interfaceclass.attribute;

import java.util.EnumMap;
import java.util.Map;
import org.opensmartgridplatform.dlms.interfaceclass.InterfaceClass;

/** This class maps AttributeClass to an InterfaceClass. */
public class AttributeDirectory {

  private static final Map<InterfaceClass, Class<? extends AttributeClass>> classes =
      new EnumMap<>(InterfaceClass.class);

  private AttributeDirectory() {
    throw new AssertionError("Utility class");
  }

  static {
    classes.put(DataAttribute.INTERFACE_CLASS, DataAttribute.class);
    classes.put(RegisterAttribute.INTERFACE_CLASS, RegisterAttribute.class);
    classes.put(ExtendedRegisterAttribute.INTERFACE_CLASS, ExtendedRegisterAttribute.class);
    classes.put(DemandRegisterAttribute.INTERFACE_CLASS, DemandRegisterAttribute.class);
    classes.put(RegisterActivationAttribute.INTERFACE_CLASS, RegisterActivationAttribute.class);
    classes.put(ProfileGenericAttribute.INTERFACE_CLASS, ProfileGenericAttribute.class);
    classes.put(ClockAttribute.INTERFACE_CLASS, ClockAttribute.class);
    classes.put(ScriptTableAttribute.INTERFACE_CLASS, ScriptTableAttribute.class);
    classes.put(ScheduleAttribute.INTERFACE_CLASS, ScheduleAttribute.class);
    classes.put(SpecialDaysTableAttribute.INTERFACE_CLASS, SpecialDaysTableAttribute.class);
    classes.put(ActivityCalendarAttribute.INTERFACE_CLASS, ActivityCalendarAttribute.class);
    classes.put(AssociationLnAttribute.INTERFACE_CLASS, AssociationLnAttribute.class);
    classes.put(AssociationSnAttribute.INTERFACE_CLASS, AssociationSnAttribute.class);
    classes.put(SapAssignmentAttribute.INTERFACE_CLASS, SapAssignmentAttribute.class);
    classes.put(ImageTransferAttribute.INTERFACE_CLASS, ImageTransferAttribute.class);
    classes.put(RegisterMonitorAttribute.INTERFACE_CLASS, RegisterMonitorAttribute.class);
    classes.put(UtilityTablesAttribute.INTERFACE_CLASS, UtilityTablesAttribute.class);
    classes.put(SingleActionScheduleAttribute.INTERFACE_CLASS, SingleActionScheduleAttribute.class);
    classes.put(RegisterTableAttribute.INTERFACE_CLASS, RegisterTableAttribute.class);
    classes.put(StatusMappingAttribute.INTERFACE_CLASS, StatusMappingAttribute.class);
    classes.put(DisconnectControlAttribute.INTERFACE_CLASS, DisconnectControlAttribute.class);
    classes.put(MbusClientAttribute.INTERFACE_CLASS, MbusClientAttribute.class);

    // Protocol related interface classes
    classes.put(IecLocalPortSetupAttribute.INTERFACE_CLASS, IecLocalPortSetupAttribute.class);
    classes.put(ModemConfigurationAttribute.INTERFACE_CLASS, ModemConfigurationAttribute.class);
    classes.put(AutoAnswerAttribute.INTERFACE_CLASS, AutoAnswerAttribute.class);
    classes.put(AutoConnectAttribute.INTERFACE_CLASS, AutoConnectAttribute.class);
    classes.put(IecHdlcSetupClassAttribute.INTERFACE_CLASS, IecHdlcSetupClassAttribute.class);
    classes.put(IecTwistedPairAttribute.INTERFACE_CLASS, IecTwistedPairAttribute.class);
    classes.put(TcpUdpSetupAttribute.INTERFACE_CLASS, TcpUdpSetupAttribute.class);
    classes.put(Ipv4SetupAttribute.INTERFACE_CLASS, Ipv4SetupAttribute.class);
    classes.put(EthernetSetupAttribute.INTERFACE_CLASS, EthernetSetupAttribute.class);
    classes.put(PppSetupAttribute.INTERFACE_CLASS, PppSetupAttribute.class);
    classes.put(GprsModemSetupAttribute.INTERFACE_CLASS, GprsModemSetupAttribute.class);
    classes.put(SmtpSetupAttribute.INTERFACE_CLASS, SmtpSetupAttribute.class);
    classes.put(SecuritySetupAttribute.INTERFACE_CLASS, SecuritySetupAttribute.class);
  }

  public static AttributeClass attributeClassFor(
      final InterfaceClass interfaceClass, final int attributeId)
      throws AttributeNotFoundException {

    if (interfaceClass == InterfaceClass.UNKNOWN) {
      throw new AttributeNotFoundException("Interfaceclass is UNKNOWN");
    }

    final Class<? extends AttributeClass> attributeClassClass = classes.get(interfaceClass);

    for (final AttributeClass attributeClass : attributeClassClass.getEnumConstants()) {
      if (attributeClass.attributeId() == attributeId) {
        return attributeClass;
      }
    }

    throw new AttributeNotFoundException(
        String.format(
            "Attribute with ID %d not found in interfaceclass %s.",
            attributeId, interfaceClass.name()));
  }

  public static class AttributeNotFoundException extends Exception {

    private static final long serialVersionUID = 5973793893150244347L;

    public AttributeNotFoundException(final String message) {
      super(message);
    }
  }
}

//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.dlms.interfaceclass.method;

import java.util.EnumMap;
import java.util.Map;
import org.opensmartgridplatform.dlms.interfaceclass.InterfaceClass;

/** This class maps MethodClass to an InterfaceClass. */
public class MethodDirectory {

  private static final Map<InterfaceClass, Class<? extends MethodClass>> classes =
      new EnumMap<>(InterfaceClass.class);

  private MethodDirectory() {
    throw new AssertionError("Utility class");
  }

  static {
    classes.put(RegisterMethod.INTERFACE_CLASS, RegisterMethod.class);
    classes.put(ExtendedRegisterMethod.INTERFACE_CLASS, ExtendedRegisterMethod.class);
    classes.put(DemandRegisterMethod.INTERFACE_CLASS, DemandRegisterMethod.class);
    classes.put(RegisterActivationMethod.INTERFACE_CLASS, RegisterActivationMethod.class);
    classes.put(ProfileGenericMethod.INTERFACE_CLASS, ProfileGenericMethod.class);
    classes.put(ClockMethod.INTERFACE_CLASS, ClockMethod.class);
    classes.put(ScriptTableMethod.INTERFACE_CLASS, ScriptTableMethod.class);
    classes.put(ScheduleMethod.INTERFACE_CLASS, ScheduleMethod.class);
    classes.put(SpecialDaysTableMethod.INTERFACE_CLASS, SpecialDaysTableMethod.class);
    classes.put(ActivityCalendarMethod.INTERFACE_CLASS, ActivityCalendarMethod.class);
    classes.put(AssociationLnMethod.INTERFACE_CLASS, AssociationLnMethod.class);
    classes.put(AssociationSnMethod.INTERFACE_CLASS, AssociationSnMethod.class);
    classes.put(SapAssignmentMethod.INTERFACE_CLASS, SapAssignmentMethod.class);
    classes.put(ImageTransferMethod.INTERFACE_CLASS, ImageTransferMethod.class);
    classes.put(RegisterTableMethod.INTERFACE_CLASS, RegisterTableMethod.class);
    classes.put(SecuritySetupMethod.INTERFACE_CLASS, SecuritySetupMethod.class);
    classes.put(DisconnectControlMethod.INTERFACE_CLASS, DisconnectControlMethod.class);
    classes.put(MBusClientMethod.INTERFACE_CLASS, MBusClientMethod.class);

    // Protocol related interface classes
    classes.put(Ipv4SetupMethod.INTERFACE_CLASS, Ipv4SetupMethod.class);
  }

  public static MethodClass methodClassFor(final InterfaceClass interfaceClass, final int methodId)
      throws MethodNotFoundException {

    if (interfaceClass == InterfaceClass.UNKNOWN) {
      throw new MethodNotFoundException("Interfaceclass is UNKNOWN");
    }

    final Class<? extends MethodClass> methodClassClass = classes.get(interfaceClass);

    for (final MethodClass methodClass : methodClassClass.getEnumConstants()) {
      if (methodClass.getMethodId() == methodId) {
        return methodClass;
      }
    }

    throw new MethodNotFoundException(
        String.format(
            "Method with ID %d not found in interfaceclass %s.", methodId, interfaceClass.name()));
  }

  public static class MethodNotFoundException extends Exception {

    private static final long serialVersionUID = 9201709400284915614L;

    public MethodNotFoundException(final String message) {
      super(message);
    }
  }
}

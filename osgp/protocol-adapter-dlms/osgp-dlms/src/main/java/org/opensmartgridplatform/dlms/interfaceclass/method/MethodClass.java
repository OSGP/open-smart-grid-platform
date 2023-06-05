// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.dlms.interfaceclass.method;

import org.opensmartgridplatform.dlms.interfaceclass.InterfaceClass;

/** This is the main interface for all enumerations in this package. */
public interface MethodClass {

  int getMethodId();

  InterfaceClass getInterfaceClass();

  boolean isMandatory();

  String getMethodName();
}

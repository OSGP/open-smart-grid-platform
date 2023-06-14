// Copyright 2012-20 Fraunhofer ISE
// Copyright 2020 Alliander N.V.
// SPDX-FileCopyrightText: Contributors to the GXF project
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

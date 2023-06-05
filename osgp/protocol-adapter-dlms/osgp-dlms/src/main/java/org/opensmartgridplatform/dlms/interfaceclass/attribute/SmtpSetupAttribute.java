// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.dlms.interfaceclass.attribute;

import org.opensmartgridplatform.dlms.interfaceclass.InterfaceClass;

/** This class contains the attributes defined for IC SmtpSetup. */
public enum SmtpSetupAttribute implements AttributeClass {
  LOGICAL_NAME(1),
  SERVER_PORT(2),
  USER_NAME(3),
  LOGIN_PASSWORD(4),
  SERVER_ADDRESS(5),
  SENDER_ADDRESS(6);

  static final InterfaceClass INTERFACE_CLASS = InterfaceClass.SMTP_SETUP;

  private final int attributeId;

  private SmtpSetupAttribute(final int attributeId) {
    this.attributeId = attributeId;
  }

  @Override
  public int attributeId() {
    return this.attributeId;
  }

  @Override
  public String attributeName() {
    return this.name();
  }

  @Override
  public InterfaceClass interfaceClass() {
    return INTERFACE_CLASS;
  }
}

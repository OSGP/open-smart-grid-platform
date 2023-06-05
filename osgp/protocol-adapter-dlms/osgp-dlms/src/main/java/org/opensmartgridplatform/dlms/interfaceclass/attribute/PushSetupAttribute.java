/*
 * Copyright 2023 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.dlms.interfaceclass.attribute;

import org.opensmartgridplatform.dlms.interfaceclass.InterfaceClass;

/** This class contains the attributes defined for IC PushSetup. */
public enum PushSetupAttribute implements AttributeClass {
  LOGICAL_NAME(1),
  PUSH_OBJECT_LIST(2),
  SEND_DESTINATION_AND_METHOD(3),
  COMMUNICATION_WINDOW(4),
  RANDOMISATION_START_INTERVAL(5),
  NUMBER_OF_RETRIES(6),
  REPETITION_DELAY(7);

  static final InterfaceClass INTERFACE_CLASS = InterfaceClass.PUSH_SETUP;

  private final int attributeId;

  private PushSetupAttribute(final int attributeId) {
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

// Copyright 2012-20 Fraunhofer ISE
// Copyright 2020 Alliander N.V.
// SPDX-FileCopyrightText: Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.dlms.interfaceclass.attribute;

import static org.opensmartgridplatform.dlms.interfaceclass.attribute.AttributeType.CAPTURE_OBJECT_DEFINITION;
import static org.opensmartgridplatform.dlms.interfaceclass.attribute.AttributeType.CAPTURE_OBJECT_DEFINITION_LIST;

import org.opensmartgridplatform.dlms.interfaceclass.InterfaceClass;

/** This class contains the attributes defined for IC ProfileGeneric. */
public enum ProfileGenericAttribute implements AttributeClass {
  LOGICAL_NAME(1),
  BUFFER(2),
  CAPTURE_OBJECTS(3, CAPTURE_OBJECT_DEFINITION_LIST),
  CAPTURE_PERIOD(4),
  SORT_METHOD(5, org.opensmartgridplatform.dlms.interfaceclass.attribute.AttributeType.SORT_METHOD),
  SORT_OBJECT(6, CAPTURE_OBJECT_DEFINITION),
  ENTRIES_IN_USE(7),
  PROFILE_ENTRIES(8);

  static final InterfaceClass INTERFACE_CLASS = InterfaceClass.PROFILE_GENERIC;

  private final int attributeId;

  private final AttributeType attributeType;

  private ProfileGenericAttribute(final int attributeId) {
    this.attributeId = attributeId;
    this.attributeType = AttributeType.UNKNOWN;
  }

  private ProfileGenericAttribute(final int attributeId, final AttributeType attributeType) {
    this.attributeId = attributeId;
    this.attributeType = attributeType;
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

  @Override
  public AttributeType attributeType() {
    return this.attributeType;
  }
}

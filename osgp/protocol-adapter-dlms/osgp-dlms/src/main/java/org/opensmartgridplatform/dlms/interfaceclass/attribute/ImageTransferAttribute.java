/*
 * Copyright 2020 Alliander N.V.
 * Copyright 2012-20 Fraunhofer ISE
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * This file was originally part of jDLMS, where it was part of a group of classes residing in
 * packages org.openmuc.jdlms.interfaceclass, org.openmuc.jdlms.interfaceclass.attribute and
 * org.openmuc.jdlms.interfaceclass.method that have been deprecated for jDLMS since version 1.5.1.
 *
 * It has been copied to the GXF code base under the Apache License, Version 2.0 with the
 * permission of Fraunhofer ISE. For more information about jDLMS visit
 *
 * http://www.openmuc.org
 */
package org.opensmartgridplatform.dlms.interfaceclass.attribute;

import org.opensmartgridplatform.dlms.interfaceclass.InterfaceClass;

/** This class contains the attributes defined for IC ImageTransfer. */
public enum ImageTransferAttribute implements AttributeClass {
  LOGICAL_NAME(1),
  IMAGE_BLOCK_SIZE(2),
  IMAGE_TRANSFERRED_BLOCKS_STATUS(3),
  IMAGE_FIRST_NOT_TRANSFERRED_BLOCK_NUMBER(4),
  IMAGE_TRANSFER_ENABLED(5),
  IMAGE_TRANSFER_STATUS(6),
  IMAGE_TO_ACTIVATE_INFO(7);

  static final InterfaceClass INTERFACE_CLASS = InterfaceClass.IMAGE_TRANSFER;

  private final int attributeId;

  private ImageTransferAttribute(final int attributeId) {
    this.attributeId = attributeId;
  }

  @Override
  public InterfaceClass interfaceClass() {
    return INTERFACE_CLASS;
  }

  @Override
  public int attributeId() {
    return this.attributeId;
  }

  @Override
  public String attributeName() {
    return this.name();
  }
}

// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

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

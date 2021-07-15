/*
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.mbus;

import org.apache.commons.lang3.StringUtils;
import org.openmuc.jdlms.datatypes.DataObject;
import org.opensmartgridplatform.dlms.interfaceclass.attribute.MbusClientAttribute;

/**
 * Represents the M-Bus Client Setup identification number.
 *
 * <p>The IdentificationNumber in its textual form consists of the last 8 digits of the
 * identification number.<br>
 * The long value is the base-10 value calculated from the textual representation interpreted as
 * hexadecimal (base-16). This value is the one that is used for the indentification_number
 * (attribute 6) of the DLMS M-Bus client (class ID 72).
 *
 * @see MbusClientAttribute#IDENTIFICATION_NUMBER
 */
public class IdentificationNumber {

  private static final int HEX_RADIX = 16;

  private final String last8Digits;

  IdentificationNumber(final String last8Digits) {
    this.last8Digits = last8Digits;
  }

  /** @return a DataObject with the double-long-unsigned value of the identification number */
  public DataObject asDataObject() {
    if (StringUtils.isBlank(this.last8Digits)) {
      return DataObject.newNullData();
    }
    return DataObject.newUInteger32Data(this.getIdentificationNumber());
  }

  Long getIdentificationNumber() {
    if (StringUtils.isBlank(this.last8Digits)) {
      return null;
    }
    return Long.parseLong(this.last8Digits, HEX_RADIX);
  }

  String getLast8Digits() {
    return this.last8Digits;
  }

  @Override
  public String toString() {
    return String.format(
        "IdentificationNumber[%s(%d)]", this.last8Digits, this.getIdentificationNumber());
  }
}

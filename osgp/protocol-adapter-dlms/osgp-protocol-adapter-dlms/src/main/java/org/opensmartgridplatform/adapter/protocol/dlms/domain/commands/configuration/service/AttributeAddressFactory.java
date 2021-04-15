/*
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.configuration.service;

import org.openmuc.jdlms.AttributeAddress;
import org.openmuc.jdlms.ObisCode;

public class AttributeAddressFactory {

  private static final int CLASS_ID = 1;
  private static final ObisCode OBIS_CODE = new ObisCode("0.1.94.31.3.255");
  private static final int ATTRIBUTE_ID = 2;

  private AttributeAddressFactory() {
    // do not instantiate utility class
  }

  public static AttributeAddress getConfigurationObjectAddress() {
    return new AttributeAddress(CLASS_ID, OBIS_CODE, ATTRIBUTE_ID);
  }
}

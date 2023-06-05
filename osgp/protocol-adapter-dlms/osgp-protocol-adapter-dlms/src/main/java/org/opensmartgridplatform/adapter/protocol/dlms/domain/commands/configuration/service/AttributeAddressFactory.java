// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

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

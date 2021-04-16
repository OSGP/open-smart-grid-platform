/*
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.adhoc;

import java.util.Map;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.common.ObisCodeValues;
import org.opensmartgridplatform.cucumber.platform.smartmetering.PlatformSmartmeteringKeys;

public class ObisCodeValuesFactory {

  private ObisCodeValuesFactory() {
    // Private constructor for utility class
  }

  public static ObisCodeValues fromParameterMap(final Map<String, String> parameters) {
    final ObisCodeValues obisCode = new ObisCodeValues();
    obisCode.setA(Short.parseShort(parameters.get(PlatformSmartmeteringKeys.OBIS_CODE_A)));
    obisCode.setB(Short.parseShort(parameters.get(PlatformSmartmeteringKeys.OBIS_CODE_B)));
    obisCode.setC(Short.parseShort(parameters.get(PlatformSmartmeteringKeys.OBIS_CODE_C)));
    obisCode.setD(Short.parseShort(parameters.get(PlatformSmartmeteringKeys.OBIS_CODE_D)));
    obisCode.setE(Short.parseShort(parameters.get(PlatformSmartmeteringKeys.OBIS_CODE_E)));
    obisCode.setF(Short.parseShort(parameters.get(PlatformSmartmeteringKeys.OBIS_CODE_F)));
    return obisCode;
  }
}

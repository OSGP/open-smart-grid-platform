//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.monitoring;

import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getShort;

import java.util.Map;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.common.ObisCodeValues;
import org.opensmartgridplatform.cucumber.platform.smartmetering.PlatformSmartmeteringKeys;

public class ObisCodeValuesFactory {

  private ObisCodeValuesFactory() {
    // Private constructor for utility class
  }

  public static ObisCodeValues fromParameterMap(final Map<String, String> parameterMap) {

    final ObisCodeValues result = new ObisCodeValues();
    result.setA(getShort(parameterMap, PlatformSmartmeteringKeys.OBIS_CODE_A, (short) 0));
    result.setB(getShort(parameterMap, PlatformSmartmeteringKeys.OBIS_CODE_B, (short) 0));
    result.setC(getShort(parameterMap, PlatformSmartmeteringKeys.OBIS_CODE_C, (short) 0));
    result.setD(getShort(parameterMap, PlatformSmartmeteringKeys.OBIS_CODE_D, (short) 0));
    result.setE(getShort(parameterMap, PlatformSmartmeteringKeys.OBIS_CODE_E, (short) 0));
    result.setF(getShort(parameterMap, PlatformSmartmeteringKeys.OBIS_CODE_F, (short) 0));
    return result;
  }
}

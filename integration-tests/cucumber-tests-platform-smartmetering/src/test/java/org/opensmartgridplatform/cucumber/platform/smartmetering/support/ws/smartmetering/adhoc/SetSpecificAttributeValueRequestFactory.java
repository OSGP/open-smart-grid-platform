// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.adhoc;

import java.math.BigInteger;
import java.util.Map;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.adhoc.AllowedObjectType;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.adhoc.SetSpecificAttributeValueAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.adhoc.SetSpecificAttributeValueRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.adhoc.ValueToSet;
import org.opensmartgridplatform.cucumber.platform.smartmetering.PlatformSmartmeteringKeys;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.RequestFactoryHelper;

public class SetSpecificAttributeValueRequestFactory {

  private SetSpecificAttributeValueRequestFactory() {
    // Private constructor for utility class
  }

  public static SetSpecificAttributeValueRequest fromParameterMap(
      final Map<String, String> parameters) {
    final SetSpecificAttributeValueRequest request = new SetSpecificAttributeValueRequest();
    request.setDeviceIdentification(
        parameters.get(PlatformSmartmeteringKeys.DEVICE_IDENTIFICATION));
    final ValueToSet valueToSet = new ValueToSet();
    valueToSet.setObjectType(
        AllowedObjectType.valueOf(parameters.get(PlatformSmartmeteringKeys.OBJECT_TYPE)));
    valueToSet.setAttribute(new BigInteger(parameters.get(PlatformSmartmeteringKeys.ATTRIBUTE)));
    valueToSet.setIntValue(new BigInteger(parameters.get(PlatformSmartmeteringKeys.INT_VALUE)));
    request.getValuesToSet().add(valueToSet);
    if (parameters.containsKey(PlatformSmartmeteringKeys.OBJECT_TYPE + "_2")) {
      final ValueToSet valueToSet2 = new ValueToSet();
      valueToSet2.setObjectType(
          AllowedObjectType.valueOf(parameters.get(PlatformSmartmeteringKeys.OBJECT_TYPE + "_2")));
      valueToSet2.setAttribute(
          new BigInteger(parameters.get(PlatformSmartmeteringKeys.ATTRIBUTE + "_2")));
      valueToSet2.setIntValue(
          new BigInteger(parameters.get(PlatformSmartmeteringKeys.INT_VALUE + "_2")));
      request.getValuesToSet().add(valueToSet2);
    }
    return request;
  }

  public static SetSpecificAttributeValueAsyncRequest fromScenarioContext() {
    final SetSpecificAttributeValueAsyncRequest asyncRequest =
        new SetSpecificAttributeValueAsyncRequest();
    asyncRequest.setCorrelationUid(RequestFactoryHelper.getCorrelationUidFromScenarioContext());
    asyncRequest.setDeviceIdentification(
        RequestFactoryHelper.getDeviceIdentificationFromScenarioContext());
    return asyncRequest;
  }
}

// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering;

import java.util.Map;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.opensmartgridplatform.cucumber.core.ScenarioContext;
import org.opensmartgridplatform.cucumber.platform.PlatformKeys;

public class RequestFactoryHelper {

  private RequestFactoryHelper() {
    // Private constructor for utility class.
  }

  public static String getCorrelationUidFromScenarioContext() {
    final String correlationUid =
        (String) ScenarioContext.current().get(PlatformKeys.KEY_CORRELATION_UID);
    if (correlationUid == null) {
      throw new AssertionError(
          "ScenarioContext must contain the correlation UID for key \""
              + PlatformKeys.KEY_CORRELATION_UID
              + "\" before creating an async request.");
    }
    return correlationUid;
  }

  public static String getDeviceIdentificationFromScenarioContext() {
    final String deviceIdentification =
        (String) ScenarioContext.current().get(PlatformKeys.KEY_DEVICE_IDENTIFICATION);
    if (deviceIdentification == null) {
      throw new AssertionError(
          "ScenarioContext must contain the device identification for key \""
              + PlatformKeys.KEY_DEVICE_IDENTIFICATION
              + "\" before creating a request.");
    }
    return deviceIdentification;
  }

  public static String getDeviceIdentificationFromStepData(
      final Map<String, String> requestParameters) {
    final String deviceIdentification =
        requestParameters.get(PlatformKeys.KEY_DEVICE_IDENTIFICATION);
    if (deviceIdentification == null) {
      throw new AssertionError(
          "The Step DataTable must contain the device identification for key \""
              + PlatformKeys.KEY_DEVICE_IDENTIFICATION
              + "\" when creating a request.");
    }
    return deviceIdentification;
  }

  public static byte[] hexDecodeDeviceKey(final String hexString, final String keyType) {
    if (hexString == null) {
      return null;
    }
    try {
      return Hex.decodeHex(hexString.toCharArray());
    } catch (final DecoderException e) {
      throw new AssertionError(
          "Key value \"" + hexString + "\" for \"" + keyType + "\" is not hex binary", e);
    }
  }
}

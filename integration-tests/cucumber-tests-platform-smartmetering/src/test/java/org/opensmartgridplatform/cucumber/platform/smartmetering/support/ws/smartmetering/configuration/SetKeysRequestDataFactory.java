// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.configuration;

import java.util.Map;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.SetKeysRequestData;
import org.opensmartgridplatform.cucumber.platform.PlatformKeys;
import org.opensmartgridplatform.cucumber.platform.smartmetering.SecurityKey;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.RequestFactoryHelper;

public class SetKeysRequestDataFactory {

  private SetKeysRequestDataFactory() {
    // Private constructor for utility class.
  }

  public static SetKeysRequestData fromParameterMap(final Map<String, String> requestParameters) {
    final SetKeysRequestData setKeysRequestData = new SetKeysRequestData();
    setKeysRequestData.setAuthenticationKey(
        RequestFactoryHelper.hexDecodeDeviceKey(
            getSoapKey(requestParameters, PlatformKeys.KEY_DEVICE_AUTHENTICATIONKEY),
            PlatformKeys.KEY_DEVICE_AUTHENTICATIONKEY));
    setKeysRequestData.setEncryptionKey(
        RequestFactoryHelper.hexDecodeDeviceKey(
            getSoapKey(requestParameters, PlatformKeys.KEY_DEVICE_ENCRYPTIONKEY),
            PlatformKeys.KEY_DEVICE_ENCRYPTIONKEY));
    return setKeysRequestData;
  }

  private static String getSoapKey(final Map<String, String> requestParameters, final String key) {
    final String keyName = requestParameters.get(key);
    if (keyName == null) {
      return null;
    }
    return SecurityKey.valueOf(keyName).getSoapRequestKey();
  }
}

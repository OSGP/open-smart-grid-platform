// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.management;

import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getBoolean;
import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getInteger;

import java.math.BigInteger;
import java.util.Map;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.management.SetDeviceCommunicationSettingsData;
import org.opensmartgridplatform.cucumber.platform.smartmetering.PlatformSmartmeteringDefaults;
import org.opensmartgridplatform.cucumber.platform.smartmetering.PlatformSmartmeteringKeys;

public class SetDeviceCommunicationSettingsRequestDataFactory {

  public static SetDeviceCommunicationSettingsData fromParameterMap(
      final Map<String, String> parameters) {
    final SetDeviceCommunicationSettingsData setDeviceCommunicationSettingsData =
        new SetDeviceCommunicationSettingsData();

    setDeviceCommunicationSettingsData.setChallengeLength(
        BigInteger.valueOf(getInteger(parameters, PlatformSmartmeteringKeys.CHALLENGE_LENGTH)));
    setDeviceCommunicationSettingsData.setWithListMax(
        BigInteger.valueOf(
            getInteger(
                parameters,
                PlatformSmartmeteringKeys.WITH_LIST_MAX,
                PlatformSmartmeteringDefaults.WITH_LIST_MAX)));
    setDeviceCommunicationSettingsData.setSelectiveAccessSupported(
        getBoolean(
            parameters,
            PlatformSmartmeteringKeys.SELECTIVE_ACCESS_SUPPORTED,
            PlatformSmartmeteringDefaults.SELECTIVE_ACCESS_SUPPORTED));
    setDeviceCommunicationSettingsData.setIpAddressIsStatic(
        getBoolean(
            parameters,
            PlatformSmartmeteringKeys.IP_ADDRESS_IS_STATIC,
            PlatformSmartmeteringDefaults.IP_ADDRESS_IS_STATIC));
    setDeviceCommunicationSettingsData.setUseSn(
        getBoolean(
            parameters, PlatformSmartmeteringKeys.USE_SN, PlatformSmartmeteringDefaults.USE_SN));
    setDeviceCommunicationSettingsData.setUseHdlc(
        getBoolean(
            parameters,
            PlatformSmartmeteringKeys.USE_HDLC,
            PlatformSmartmeteringDefaults.USE_HDLC));
    setDeviceCommunicationSettingsData.setPolyphase(
        getBoolean(
            parameters,
            PlatformSmartmeteringKeys.POLYPHASE,
            PlatformSmartmeteringDefaults.POLYPHASE));

    return setDeviceCommunicationSettingsData;
  }
}

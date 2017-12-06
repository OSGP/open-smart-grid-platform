/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.platform.smartmetering.support.ws.smartmetering.management;

import static com.alliander.osgp.cucumber.core.Helpers.getBoolean;

import java.math.BigInteger;
import java.util.Map;

import com.alliander.osgp.adapter.ws.schema.smartmetering.management.SetDeviceCommunicationSettingsData;
import com.alliander.osgp.cucumber.platform.smartmetering.Helpers;
import com.alliander.osgp.cucumber.platform.smartmetering.PlatformSmartmeteringDefaults;
import com.alliander.osgp.cucumber.platform.smartmetering.PlatformSmartmeteringKeys;

public class SetDeviceCommunicationSettingsRequestDataFactory {

    public static SetDeviceCommunicationSettingsData fromParameterMap(final Map<String, String> parameters) {
        final SetDeviceCommunicationSettingsData setDeviceCommunicationSettingsData = new SetDeviceCommunicationSettingsData();

        setDeviceCommunicationSettingsData.setChallengeLength(
                BigInteger.valueOf(Helpers.getInteger(parameters, PlatformSmartmeteringKeys.CHALLENGE_LENGTH)));
        setDeviceCommunicationSettingsData.setWithListSupported(getBoolean(parameters,
                PlatformSmartmeteringKeys.WITH_LIST_SUPPORTED, PlatformSmartmeteringDefaults.WITH_LIST_SUPPORTED));
        setDeviceCommunicationSettingsData.setSelectiveAccessSupported(
                getBoolean(parameters, PlatformSmartmeteringKeys.SELECTIVE_ACCESS_SUPPORTED,
                        PlatformSmartmeteringDefaults.SELECTIVE_ACCESS_SUPPORTED));
        setDeviceCommunicationSettingsData.setIpAddressIsStatic(getBoolean(parameters,
                PlatformSmartmeteringKeys.IP_ADDRESS_IS_STATIC, PlatformSmartmeteringDefaults.IP_ADDRESS_IS_STATIC));
        setDeviceCommunicationSettingsData.setUseSn(
                getBoolean(parameters, PlatformSmartmeteringKeys.USE_SN, PlatformSmartmeteringDefaults.USE_SN));
        setDeviceCommunicationSettingsData.setUseHdlc(
                getBoolean(parameters, PlatformSmartmeteringKeys.USE_HDLC, PlatformSmartmeteringDefaults.USE_HDLC));

        return setDeviceCommunicationSettingsData;
    }
}

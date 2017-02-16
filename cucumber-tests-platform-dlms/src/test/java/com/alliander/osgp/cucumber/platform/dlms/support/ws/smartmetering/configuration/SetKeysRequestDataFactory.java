/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.platform.dlms.support.ws.smartmetering.configuration;

import java.util.Map;

import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.SetKeysRequestData;
import com.alliander.osgp.cucumber.platform.Keys;
import com.alliander.osgp.cucumber.platform.dlms.support.ws.smartmetering.RequestFactoryHelper;

public class SetKeysRequestDataFactory {

    private SetKeysRequestDataFactory() {
        // Private constructor for utility class.
    }

    public static SetKeysRequestData fromParameterMap(final Map<String, String> requestParameters) {
        final SetKeysRequestData setKeysRequestData = new SetKeysRequestData();
        setKeysRequestData.setAuthenticationKey(RequestFactoryHelper.hexDecodeDeviceKey(
                requestParameters.get(Keys.KEY_DEVICE_AUTHENTICATIONKEY), Keys.KEY_DEVICE_AUTHENTICATIONKEY));
        setKeysRequestData.setEncryptionKey(RequestFactoryHelper.hexDecodeDeviceKey(
                requestParameters.get(Keys.KEY_DEVICE_ENCRYPTIONKEY), Keys.KEY_DEVICE_ENCRYPTIONKEY));
        return setKeysRequestData;
    }
}

/*
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.installation;

import java.util.Map;

import org.opensmartgridplatform.adapter.ws.schema.smartmetering.installation.DeCoupleMbusDeviceByChannelAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.installation.DeCoupleMbusDeviceByChannelRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.installation.DeCoupleMbusDeviceByChannelRequestData;
import org.opensmartgridplatform.cucumber.platform.PlatformKeys;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.RequestFactoryHelper;

public class DeCoupleMbusDeviceByChannelRequestFactory {

    private DeCoupleMbusDeviceByChannelRequestFactory() {
        // Private constructor for utility class.
    }

    public static DeCoupleMbusDeviceByChannelRequest forGatewayAndChannel(final String gatewayDeviceIdentification,
            final String channel) {
        final DeCoupleMbusDeviceByChannelRequest request = new DeCoupleMbusDeviceByChannelRequest();
        request.setDeviceIdentification(gatewayDeviceIdentification);
        final DeCoupleMbusDeviceByChannelRequestData requestData = new DeCoupleMbusDeviceByChannelRequestData();
        requestData.setChannel(Short.parseShort(channel));
        request.setDeCoupleMbusDeviceByChannelRequestData(requestData);
        return request;
    }

    public static DeCoupleMbusDeviceByChannelRequest fromSettings(final Map<String, String> settings) {
        final DeCoupleMbusDeviceByChannelRequest request = new DeCoupleMbusDeviceByChannelRequest();
        final DeCoupleMbusDeviceByChannelRequestData requestData = new DeCoupleMbusDeviceByChannelRequestData();
        requestData.setChannel(Short.valueOf(settings.get(PlatformKeys.KEY_CHANNEL)));
        request.setDeCoupleMbusDeviceByChannelRequestData(requestData);
        request.setDeviceIdentification(settings.get(PlatformKeys.KEY_DEVICE_IDENTIFICATION));
        return request;
    }

    public static DeCoupleMbusDeviceByChannelAsyncRequest fromScenarioContext() {
        final String correlationUid = RequestFactoryHelper.getCorrelationUidFromScenarioContext();
        final String deviceIdentification = RequestFactoryHelper.getDeviceIdentificationFromScenarioContext();
        final DeCoupleMbusDeviceByChannelAsyncRequest asyncRequest = new DeCoupleMbusDeviceByChannelAsyncRequest();
        asyncRequest.setCorrelationUid(correlationUid);
        asyncRequest.setDeviceIdentification(deviceIdentification);
        return asyncRequest;
    }

}

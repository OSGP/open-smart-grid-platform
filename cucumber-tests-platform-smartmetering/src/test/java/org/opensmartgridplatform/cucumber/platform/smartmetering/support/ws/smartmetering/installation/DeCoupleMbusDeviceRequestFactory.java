/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.installation;

import org.opensmartgridplatform.adapter.ws.schema.smartmetering.installation.DeCoupleMbusDeviceAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.installation.DeCoupleMbusDeviceRequest;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.RequestFactoryHelper;

public class DeCoupleMbusDeviceRequestFactory {

    private DeCoupleMbusDeviceRequestFactory() {
        // Private constructor for utility class.
    }

    public static DeCoupleMbusDeviceRequest forGatewayAndMbusDevice(final String gatewayDeviceIdentification,
            final String mbusDeviceIdentification) {
        final DeCoupleMbusDeviceRequest deCoupleMbusDeviceRequest = new DeCoupleMbusDeviceRequest();
        deCoupleMbusDeviceRequest.setDeviceIdentification(gatewayDeviceIdentification);
        deCoupleMbusDeviceRequest.setMbusDeviceIdentification(mbusDeviceIdentification);
        return deCoupleMbusDeviceRequest;
    }

    public static DeCoupleMbusDeviceAsyncRequest fromScenarioContext() {
        final String correlationUid = RequestFactoryHelper.getCorrelationUidFromScenarioContext();
        final String deviceIdentification = RequestFactoryHelper.getDeviceIdentificationFromScenarioContext();
        final DeCoupleMbusDeviceAsyncRequest deCoupleMbusDeviceAsyncRequest = new DeCoupleMbusDeviceAsyncRequest();
        deCoupleMbusDeviceAsyncRequest.setCorrelationUid(correlationUid);
        deCoupleMbusDeviceAsyncRequest.setDeviceIdentification(deviceIdentification);
        return deCoupleMbusDeviceAsyncRequest;
    }
}

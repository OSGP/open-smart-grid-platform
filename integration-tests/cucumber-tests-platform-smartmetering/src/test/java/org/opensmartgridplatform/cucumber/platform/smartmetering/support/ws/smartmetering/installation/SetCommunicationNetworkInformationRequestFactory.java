/**
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.installation;

import java.util.Map;

import org.opensmartgridplatform.adapter.ws.schema.smartmetering.installation.SetCommunicationNetworkInformationAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.installation.SetCommunicationNetworkInformationRequest;
import org.opensmartgridplatform.cucumber.platform.PlatformKeys;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.RequestFactoryHelper;

public class SetCommunicationNetworkInformationRequestFactory {

    public static SetCommunicationNetworkInformationRequest fromParameters(final Map<String, String> inputSettings) {
        final SetCommunicationNetworkInformationRequest request = new SetCommunicationNetworkInformationRequest();
        request.setDeviceIdentification(inputSettings.get(PlatformKeys.KEY_DEVICE_IDENTIFICATION));
        request.setIpAddress(inputSettings.get(PlatformKeys.KEY_IP_ADDRESS));
        request.setBtsId(Integer.parseInt(inputSettings.get(PlatformKeys.KEY_BTS_ID)));
        request.setCellId(Integer.parseInt(inputSettings.get(PlatformKeys.KEY_CELL_ID)));

        return request;
    }

    public static SetCommunicationNetworkInformationAsyncRequest fromScenarioContext(
            final Map<String, String> responseSettings) {
        final String correlationUid = RequestFactoryHelper.getCorrelationUidFromScenarioContext();
        final String deviceIdentification = RequestFactoryHelper.getDeviceIdentificationFromStepData(responseSettings);
        final SetCommunicationNetworkInformationAsyncRequest setCommunicationNetworkInformationAsyncRequest =
                new SetCommunicationNetworkInformationAsyncRequest();
        setCommunicationNetworkInformationAsyncRequest.setCorrelationUid(correlationUid);
        setCommunicationNetworkInformationAsyncRequest.setDeviceIdentification(deviceIdentification);
        return setCommunicationNetworkInformationAsyncRequest;
    }
}

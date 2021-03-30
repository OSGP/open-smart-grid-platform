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

import org.opensmartgridplatform.adapter.ws.schema.smartmetering.installation.SetSubscriptionInformationAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.installation.SetSubscriptionInformationRequest;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.RequestFactoryHelper;

public class SetSubscriptionInformationRequestFactory {

    public static SetSubscriptionInformationRequest fromParameters(final Map<String, String> inputSettings) {
        final SetSubscriptionInformationRequest request = new SetSubscriptionInformationRequest();
        request.setDeviceIdentification(inputSettings.get("DeviceIdentification"));
        request.setIpAddress(inputSettings.get("IpAddress"));
        request.setBtsId(Integer.parseInt(inputSettings.get("BtsId")));
        request.setCellId(Integer.parseInt(inputSettings.get("CellId")));

        return request;
    }

    public static SetSubscriptionInformationAsyncRequest fromScenarioContext(
            final Map<String, String> responseSettings) {
        final String correlationUid = RequestFactoryHelper.getCorrelationUidFromScenarioContext();
        final String deviceIdentification = RequestFactoryHelper.getDeviceIdentificationFromStepData(responseSettings);
        final SetSubscriptionInformationAsyncRequest setSubscriptionInformationAsyncRequest =
                new SetSubscriptionInformationAsyncRequest();
        setSubscriptionInformationAsyncRequest.setCorrelationUid(correlationUid);
        setSubscriptionInformationAsyncRequest.setDeviceIdentification(deviceIdentification);
        return setSubscriptionInformationAsyncRequest;
    }
}

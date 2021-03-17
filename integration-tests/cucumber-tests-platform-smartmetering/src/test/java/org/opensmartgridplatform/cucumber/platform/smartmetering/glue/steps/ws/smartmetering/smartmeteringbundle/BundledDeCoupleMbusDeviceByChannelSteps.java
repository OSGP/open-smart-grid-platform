/**
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy of
 * the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.platform.smartmetering.glue.steps.ws.smartmetering.smartmeteringbundle;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;

import org.opensmartgridplatform.adapter.ws.schema.smartmetering.bundle.DeCoupleMbusDeviceByChannelRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.bundle.DeCoupleMbusDeviceByChannelResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.common.OsgpResultType;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.common.Response;
import org.opensmartgridplatform.cucumber.platform.smartmetering.PlatformSmartmeteringKeys;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;

public class BundledDeCoupleMbusDeviceByChannelSteps extends BaseBundleSteps {

    @Given("^the bundle request contains a decouple mbus device by channel action$")
    public void theBundleRequestContainsADeCoupleMbusDeviceByChannelAction(final Map<String, String> parameters)
            throws Throwable {

        final DeCoupleMbusDeviceByChannelRequest action = new DeCoupleMbusDeviceByChannelRequest();
        action.setChannel(Short.valueOf(parameters.get(PlatformSmartmeteringKeys.CHANNEL)));
        this.addActionToBundleRequest(action);
    }

    @Then("^the decouple mbus device by channel bundle response is \"([^\"]*)\" with Mbus Device \"([^\"]*)\"$")
    public void theDecoupleMbusDeviceByChannelBundleResponseIsWithMbusDevice(final String result,
            final String mbusDeviceIdentification) throws Throwable {

        final Response response = this.getNextBundleResponse();

        assertThat(response).as("Not a valid response").isInstanceOf(DeCoupleMbusDeviceByChannelResponse.class);

        assertThat(((DeCoupleMbusDeviceByChannelResponse) response).getMbusDeviceIdentification())
                .as("MbusDeviceIdentification")
                .isEqualTo(mbusDeviceIdentification);
        assertThat(response.getResult()).as("Result").isEqualTo(OsgpResultType.valueOf(result));

    }

    @Then("^the decouple mbus device by channel bundle response is \"([^\"]*)\" without Mbus Device$")
    public void theDecoupleMbusDeviceByChannelBundleResponseIsWithoutMbusDevice(final String result) throws Throwable {

        final Response response = this.getNextBundleResponse();

        assertThat(response).as("Not a valid response").isInstanceOf(DeCoupleMbusDeviceByChannelResponse.class);

        assertThat(((DeCoupleMbusDeviceByChannelResponse) response).getMbusDeviceIdentification())
                .as("MbusDeviceIdentification")
                .isNull();
        assertThat(response.getResult()).as("Result").isEqualTo(OsgpResultType.valueOf(result));

    }

}

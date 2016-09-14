/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */

package org.osgp.adapter.protocol.dlms.simulator.trigger;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.Response;

import org.apache.cxf.jaxrs.client.WebClient;
import org.osgp.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alliander.osgp.shared.usermanagement.AbstractClient;
import com.alliander.osgp.shared.usermanagement.ResponseException;
import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;

public class SimulatorTriggerClient extends AbstractClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(SimulatorTriggerClient.class);

    private static final String CONSTRUCTION_FAILED = "SimulatorTriggerClient construction failed";
    private final String triggerPath = "/trigger";

    /**
     * Construct a SimulatorTriggerClient instance.
     *
     * @param baseAddress
     *            The base address or URL for the SimulatorTriggerClient.
     * @throws SimulatorTriggerClientException
     *             In case the construction fails, a
     *             SimulatorTriggerClientException will be thrown.
     */
    public SimulatorTriggerClient(final String baseAddress) {

        try {

            // Create Apache CXF WebClient with JSON provider.
            final List<Object> providers = new ArrayList<Object>();
            providers.add(new JacksonJaxbJsonProvider());

            this.webClient = WebClient.create(baseAddress, providers);
            if (this.webClient == null) {
                throw new SimulatorTriggerClientException("webclient is null");
            }

        } catch (final Exception e) {
            LOGGER.error(CONSTRUCTION_FAILED, e);
        }

    }

    public void sendTrigger(final DlmsDevice simulatedDlmsDevice) throws SimulatorTriggerClientException {

        final Response response = this.getWebClientInstance().path(this.triggerPath)
                .query("port", simulatedDlmsDevice.getPort()).query("logicalId", simulatedDlmsDevice.getLogicalId())
                .get();

        try {
            this.checkResponseStatus(response);
        } catch (final ResponseException e) {
            throw new SimulatorTriggerClientException("sendTrigger response exception", e);
        }

    }

    private void checkResponseStatus(final Response response) throws ResponseException {

        if (response == null) {
            throw new ResponseException(RESPONSE_IS_NULL);
        }

        final int httpStatusCode = response.getStatus();
        if (httpStatusCode != 200) {
            throw new ResponseException(HTTP_STATUS_IS_NOT_200 + httpStatusCode);
        }

    }
}

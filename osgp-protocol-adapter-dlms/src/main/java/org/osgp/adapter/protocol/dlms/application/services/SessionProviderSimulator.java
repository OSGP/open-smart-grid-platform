/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */

package org.osgp.adapter.protocol.dlms.application.services;

import java.io.IOException;

import javax.annotation.PostConstruct;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.osgp.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.osgp.adapter.protocol.jasper.sessionproviders.SessionProvider;
import org.osgp.adapter.protocol.jasper.sessionproviders.SessionProviderEnum;
import org.osgp.adapter.protocol.jasper.sessionproviders.exceptions.SessionProviderException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import com.alliander.osgp.shared.exceptionhandling.FunctionalException;

@Component
@PropertySource("file:${osp/osgpAdapterProtocolDlms/config}")
public class SessionProviderSimulator extends SessionProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(SessionProviderSimulator.class);

    @Value("${triggered.simulator.url}")
    private String baseUrl;
    @Value("${triggered.simulator.ipaddress}")
    private String ipAddress;

    @Autowired
    private DomainHelperService domainHelperService;

    private HttpClient httpClient;
    private HttpGet trigger;
    private HttpResponse response;

    /**
     * Initialization function executed after dependency injection has finished.
     * The SessionProvider Singleton is added to the HashMap of
     * SessionProviderMap.
     */
    @PostConstruct
    public void init() {
        this.sessionProviderMap.addProvider(SessionProviderEnum.SIMULATOR, this);
    }

    @Override
    public String getIpAddress(final String iccId) throws SessionProviderException {

        final DlmsDevice dlmsDevice;

        try {
            dlmsDevice = this.domainHelperService.findDlmsDevice(iccId);
            // create HTTP Client
            this.httpClient = HttpClientBuilder.create().build();
            // create new getRequest
            final String url = this.configureUrl(dlmsDevice);
            this.trigger = new HttpGet(url);
            // Add additional header to getRequest which accepts application/xml
            // data
            this.trigger.addHeader("accept", "application/json");
        } catch (final FunctionalException e) {
            LOGGER.warn("No device known with deviceId: " + iccId);
            throw new SessionProviderException("" + e);
        }

        this.processResponse();

        return this.ipAddress;
    }

    private void processResponse() throws SessionProviderException {

        // Execute request and catch response
        try {
            this.response = this.httpClient.execute(this.trigger);
        } catch (final ClientProtocolException e) {
            throw new SessionProviderException("Error processing response from the simulator", e);
        } catch (final IOException e) {
            LOGGER.warn("A problem occured during IO or the connection was aborted");
            throw new SessionProviderException("" + e);
        }

        // Check for HTTP response code: 200 = success
        if (this.response.getStatusLine().getStatusCode() != 200) {
            throw new SessionProviderException("Failed: Unable to successfully start a simulator. ");
        }

    }

    private String configureUrl(final DlmsDevice dlmsDevice) {

        final int port = dlmsDevice.getPort().intValue();
        final Long logicalId = dlmsDevice.getLogicalId();

        return this.baseUrl + "?port=" + port + "&logicalId=" + logicalId;
    }

}

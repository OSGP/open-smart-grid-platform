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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import com.alliander.osgp.shared.exceptionhandling.FunctionalException;

/**
 *
 * This class fakes being a SessionProvider. Instead, it sends a webrequest to
 * start an instance of a device simulator on-demand, returning the ip-address
 * of the location where this device simulator is started.
 *
 * To work properly, an implementation of a device simulator needs to be
 * present, and deployed. This device simulator is not included in the source
 * code of Protocol-Adapter-DLMS.
 *
 * Besides the implementation of a device simulator, the url and ip-address of
 * the location of the web service should be provided in the config file.
 *
 */
@Component
@PropertySource("file:${osp/osgpAdapterProtocolDlms/config}")
public class SessionProviderSimulator extends SessionProvider {

    @Value("${triggered.simulator.url}")
    private String baseUrl;
    @Value("${triggered.simulator.ipaddress}")
    private String ipAddress;

    @Autowired
    private DomainHelperService domainHelperService;

    /**
     * Initialization function executed after dependency injection has finished.
     * The SessionProvider Singleton is added to the HashMap of
     * SessionProviderMap.
     */
    @PostConstruct
    public void init() {
        this.sessionProviderMap.addProvider(SessionProviderEnum.SIMULATOR, this);
    }

    /**
     * This implementation depends on the iccId having the same value as the
     * device identification (in order to be able to look up some data with the
     * device for calling the simulator starting web service, like the port
     * number and logicalId of a simulated device).
     */
    @Override
    public String getIpAddress(final String iccId) throws SessionProviderException {

        final DlmsDevice dlmsDevice;
        final HttpClient httpClient;
        final HttpGet trigger;

        try {
            dlmsDevice = this.domainHelperService.findDlmsDevice(iccId);
            httpClient = HttpClientBuilder.create().build();
            final String url = this.configureUrl(dlmsDevice);
            trigger = new HttpGet(url);
            trigger.addHeader("accept", "application/json");
        } catch (final FunctionalException e) {
            throw new SessionProviderException("No device known with deviceId: " + iccId, e);
        }

        this.processResponse(httpClient, trigger);

        return this.ipAddress;
    }

    private void processResponse(final HttpClient httpClient, final HttpGet trigger) throws SessionProviderException {

        final HttpResponse response;
        try {
            response = httpClient.execute(trigger);
        } catch (final ClientProtocolException e) {
            throw new SessionProviderException("Error processing response from the simulator", e);
        } catch (final IOException e) {
            throw new SessionProviderException("A problem occured during IO or the connection was aborted", e);
        }

        // Check for HTTP response code: 200 = success
        if (response.getStatusLine().getStatusCode() != 200) {
            throw new SessionProviderException("Failed: Unable to successfully start a simulator. ");
        }

    }

    private String configureUrl(final DlmsDevice dlmsDevice) {

        final Long port = dlmsDevice.getPort();
        final Long logicalId = dlmsDevice.getLogicalId();

        return this.baseUrl + "?port=" + port + "&logicalId=" + logicalId;
    }

}

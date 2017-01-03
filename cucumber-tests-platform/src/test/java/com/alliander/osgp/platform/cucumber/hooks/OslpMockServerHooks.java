/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not 
 * use this file except in compliance with the License.  
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.platform.cucumber.hooks;

import org.springframework.beans.factory.annotation.Autowired;

import com.alliander.osgp.platform.cucumber.mocks.oslpdevice.MockOslpServer;

import cucumber.api.java.After;
import cucumber.api.java.Before;

public class OslpMockServerHooks {

    @Autowired
    private MockOslpServer mockServer;

    @Before("@OslpMockServer")
    public void startOslpMockServer() throws Throwable {
        if (this.mockServer != null) {
            this.mockServer.resetServer();

            this.mockServer.stop();
        }

        this.mockServer.start();
    }

    @After("@OslpMockServer")
    public void stopOslpMockServer() {
        this.mockServer.stop();
    }
}

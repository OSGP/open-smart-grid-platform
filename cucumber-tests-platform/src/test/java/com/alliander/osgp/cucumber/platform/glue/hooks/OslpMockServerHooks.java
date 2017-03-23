/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.platform.glue.hooks;

import org.springframework.beans.factory.annotation.Autowired;

import com.alliander.osgp.cucumber.platform.GlueBase;
import com.alliander.osgp.cucumber.platform.mocks.oslpdevice.MockOslpServer;

import cucumber.api.java.After;
import cucumber.api.java.Before;

public class OslpMockServerHooks extends GlueBase {

    @Autowired
    private MockOslpServer oslpMockServer;

    @Before("@OslpMockServer")
    public void startOslpMockServer() throws Throwable {
        if (this.oslpMockServer != null) {
            this.oslpMockServer.resetServer();

            this.oslpMockServer.stop();
        }

        this.oslpMockServer.start();
    }

    @After("@OslpMockServer")
    public void stopOslpMockServer() {
        this.oslpMockServer.stop();
    }
}

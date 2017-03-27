/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.platform.glue.hooks;

import org.springframework.beans.factory.annotation.Autowired;

import com.alliander.osgp.cucumber.platform.GlueBase;
import com.alliander.osgp.cucumber.platform.mocks.iec61850.Iec61850MockServerMarkerWadden;

import cucumber.api.java.After;
import cucumber.api.java.Before;

public class Iec61850MockServerMarkerWaddenHooks extends GlueBase {

    @Autowired
    private Iec61850MockServerMarkerWadden mockServer;

    @Before("@Iec61850MockServerMarkerWadden")
    public void startIec61850MockServerMarkerWadden() throws Throwable {
        this.mockServer.start();
    }

    @After("@Iec61850MockServerMarkerWadden")
    public void stopIec61850MockServerMarkerWadden() {
        this.mockServer.stop();
    }
}

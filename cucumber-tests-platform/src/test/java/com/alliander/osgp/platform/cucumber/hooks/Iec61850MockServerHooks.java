package com.alliander.osgp.platform.cucumber.hooks;

import org.springframework.beans.factory.annotation.Autowired;

import com.alliander.osgp.platform.cucumber.mocks.iec61850.Iec61850MockServer;

import cucumber.api.java.After;
import cucumber.api.java.Before;

public class Iec61850MockServerHooks {

    @Autowired
    private Iec61850MockServer mockServer;

    //    @Before("@Iec61850MockServer")
    @Before
    public void startIec61850MockServer() throws Throwable {
        this.mockServer.start();
    }

    // @After("@Iec61850MockServer")
    @After
    public void stopIec61850MockServer() {
        this.mockServer.stop();
    }
}

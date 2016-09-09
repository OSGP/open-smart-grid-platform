package com.alliander.osgp.platform.cucumber.hooks;

import org.springframework.beans.factory.annotation.Autowired;

import com.alliander.osgp.platform.cucumber.mocks.oslpdevice.OslpMockServer;

import cucumber.api.java.Before;

public class OslpMockServerHooks {

    @Autowired
    private OslpMockServer mockServer;

    @Before
    public void before() {
        this.mockServer.resetServer();
    }
}

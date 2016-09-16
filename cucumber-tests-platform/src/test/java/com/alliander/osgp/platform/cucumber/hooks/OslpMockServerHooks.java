package com.alliander.osgp.platform.cucumber.hooks;

import org.springframework.beans.factory.annotation.Autowired;

import com.alliander.osgp.platform.cucumber.mocks.oslpdevice.MockOslpServer;

import cucumber.api.java.After;
import cucumber.api.java.Before;

public class OslpMockServerHooks {

    @Autowired
    private MockOslpServer mockServer;

    @Before
    public void before() {
        this.mockServer.resetServer();
    }
    
    @Before("@OslpMockServer")
    public void startOslpMockServer() throws Throwable {
        this.mockServer.start();
    }
    
    @After("@OslpMockServer")
    public void stopOslpMockServer() {
        this.mockServer.stop();
    }
}

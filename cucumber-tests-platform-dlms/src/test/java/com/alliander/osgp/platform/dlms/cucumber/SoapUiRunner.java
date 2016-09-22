/**
 * Copyright 2016 Smart Society Services B.V.
 */
package com.alliander.osgp.platform.dlms.cucumber;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;

/**
 * Super class for SOAP UI runner implementations. Each Runner will be called
 * from a subclass.
 */
@Configuration
@PropertySources({ @PropertySource("classpath:osgp-cucumber-sss.properties"),
        @PropertySource(value = "file:${osgp/cucumber/platform}", ignoreResourceNotFound = true),
    	@PropertySource(value = "file:/etc/osp/cucumber-platform.properties", ignoreResourceNotFound = true)
})
public abstract class SoapUiRunner extends com.alliander.osgp.platform.cucumber.SoapUiRunner {

    /**
     * The url of the server to test. Default to localhost:443.
     */
    @Value("${dlmsDeviceSimulator}")
    private String dlmsDeviceSimulator;
    
    protected SoapUiRunner(final String soapUiProject){
        super(soapUiProject);
    }

}

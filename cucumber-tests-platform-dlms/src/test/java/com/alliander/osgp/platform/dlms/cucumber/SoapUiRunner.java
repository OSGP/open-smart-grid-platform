/**
 * Copyright 2016 Smart Society Services B.V.
 */
package com.alliander.osgp.platform.dlms.cucumber;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;

/**
 * Super class for SOAP UI runner implementations. Each Runner will be called
 * from a subclass.
 */
@Configuration
@PropertySources({ @PropertySource("classpath:cucumber-platform-dlms.properties"),
        @PropertySource(value = "file:${osgp/cucumber/platform/dlms}", ignoreResourceNotFound = true),
    	@PropertySource(value = "file:/etc/osp/cucumber-platform-dlms.properties", ignoreResourceNotFound = true)
})
public abstract class SoapUiRunner extends com.alliander.osgp.platform.cucumber.SoapUiRunner {

	/**
	 * Constructor.
	 * @param soapUiProject The soap ui project to use within the dlms related tests.
	 */
    protected SoapUiRunner(final String soapUiProject){
        super(soapUiProject);
    }

}

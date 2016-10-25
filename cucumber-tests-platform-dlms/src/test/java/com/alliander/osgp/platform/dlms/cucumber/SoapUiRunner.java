/**
 * Copyright 2016 Smart Society Services B.V.
 */
package com.alliander.osgp.platform.dlms.cucumber;

/**
 * Super class for SOAP UI runner implementations. Each Runner will be called
 * from a subclass.
 */
public abstract class SoapUiRunner extends com.alliander.osgp.platform.cucumber.SoapUiRunner {

	/**
	 * Constructor.
	 * @param soapUiProject The soap ui project to use within the dlms related tests.
	 */
    protected SoapUiRunner(final String soapUiProject){
        super(soapUiProject);
    }

}

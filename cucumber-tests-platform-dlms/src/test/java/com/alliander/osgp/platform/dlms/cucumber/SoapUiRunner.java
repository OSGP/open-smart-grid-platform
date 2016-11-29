/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
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

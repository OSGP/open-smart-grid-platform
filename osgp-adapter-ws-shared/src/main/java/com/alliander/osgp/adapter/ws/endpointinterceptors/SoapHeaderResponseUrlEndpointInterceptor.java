/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.ws.endpointinterceptors;


/**
 * Intercept a SOAP Header and put the message schedule time contents in the
 * MessageContext.
 */
public class SoapHeaderResponseUrlEndpointInterceptor extends AbstractSoapHeaderInterceptor {

    public SoapHeaderResponseUrlEndpointInterceptor(final String responseUrl) {
    	super(responseUrl, responseUrl);
    }

}

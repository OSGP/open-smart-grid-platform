/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.platform.cucumber.support.ws;

public class WebServiceException extends Exception {

    /**
     * Serial Version UID
     */
    private static final long serialVersionUID = -2721785457603104149L;

    public WebServiceException() {
    }

    public WebServiceException(final String message) {
        super(message);
    }

    public WebServiceException(final String message, final Throwable t) {
        super(message, t);
    }

}

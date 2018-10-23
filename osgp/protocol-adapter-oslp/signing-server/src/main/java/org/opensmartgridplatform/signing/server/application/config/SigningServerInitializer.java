/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.signing.server.application.config;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.springframework.web.WebApplicationInitializer;

import org.opensmartgridplatform.shared.application.config.AbstractApplicationInitializer;

/**
 * Web application Java configuration class.
 */
public class SigningServerInitializer extends AbstractApplicationInitializer implements WebApplicationInitializer {

    public SigningServerInitializer() {
        super(ApplicationContext.class, "java:comp/env/osgp/SigningServer/log-config");
    }
    
    /**
     * 
     */
    @Override
    public void onStartup(final ServletContext servletContext) throws ServletException {
        startUp(servletContext);
    }
}



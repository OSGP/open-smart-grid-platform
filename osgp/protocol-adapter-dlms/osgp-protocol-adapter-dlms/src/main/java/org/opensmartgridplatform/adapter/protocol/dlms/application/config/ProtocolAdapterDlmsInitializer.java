/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.application.config;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.jboss.netty.logging.InternalLoggerFactory;
import org.jboss.netty.logging.Slf4JLoggerFactory;
import org.opensmartgridplatform.shared.application.config.AbstractApplicationInitializer;
import org.springframework.web.WebApplicationInitializer;

/**
 * Web application Java configuration class.
 */
public class ProtocolAdapterDlmsInitializer extends AbstractApplicationInitializer
        implements WebApplicationInitializer {

    public ProtocolAdapterDlmsInitializer() {
        super(ApplicationContext.class, "java:comp/env/osgp/AdapterProtocolDlms/log-config");
        InternalLoggerFactory.setDefaultFactory(new Slf4JLoggerFactory());
    }

    /**
     * Handles the start of the spring boot.
     */
    @Override
    public void onStartup(final ServletContext servletContext) throws ServletException {
        this.startUp(servletContext);
    }
}

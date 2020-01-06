/**
 * Copyright 2014-2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.iec61850.application.config;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.opensmartgridplatform.shared.application.config.AbstractApplicationInitializer;
import org.springframework.web.WebApplicationInitializer;

import io.netty.util.internal.logging.InternalLoggerFactory;
import io.netty.util.internal.logging.Slf4JLoggerFactory;

/**
 * Web application Java configuration class.
 */
public class OsgpProtocolAdapterIec61850Initializer extends AbstractApplicationInitializer
        implements WebApplicationInitializer {

    public OsgpProtocolAdapterIec61850Initializer() {
        super(ApplicationContext.class, "java:comp/env/osgp/AdapterProtocolIec61850/log-config");
        InternalLoggerFactory.setDefaultFactory(Slf4JLoggerFactory.INSTANCE);
    }

    /**
     *
     */
    @Override
    public void onStartup(final ServletContext servletContext) throws ServletException {
        this.startUp(servletContext);
    }
}

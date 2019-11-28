/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.oslp.elster.application.config;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.jboss.netty.logging.InternalLoggerFactory;
import org.jboss.netty.logging.Slf4JLoggerFactory;
import org.opensmartgridplatform.shared.application.config.AbstractApplicationInitializer;
import org.springframework.web.WebApplicationInitializer;

/**
 * Web application Java configuration class.
 */
public class OsgpProtocolAdapterOslpInitializer extends AbstractApplicationInitializer
        implements WebApplicationInitializer {

    public OsgpProtocolAdapterOslpInitializer() {
        super(ApplicationContext.class, "java:comp/env/osgp/AdapterProtocolOslpElster/log-config");
        InternalLoggerFactory.setDefaultFactory(new Slf4JLoggerFactory());
    }

    @Override
    public void onStartup(final ServletContext servletContext) throws ServletException {
        this.startUp(servletContext);
    }
}

/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.webdevicesimulator.application.config;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;

import org.opensmartgridplatform.shared.application.config.AbstractApplicationInitializer;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.servlet.DispatcherServlet;

import io.netty.util.internal.logging.InternalLoggerFactory;
import io.netty.util.internal.logging.Slf4JLoggerFactory;

/**
 * Web application Java configuration class.
 */
public class WebDeviceSimulatorInitializer extends AbstractApplicationInitializer implements WebApplicationInitializer {

    private static final String DISPATCHER_SERVLET_NAME = "dispatcher";
    private static final String DISPATCHER_SERVLET_MAPPING = "/";

    /**
     * Default constructor.
     */
    public WebDeviceSimulatorInitializer() {
        super(ApplicationContext.class, "java:comp/env/osgp/WebDeviceSimulator/log-config");
    }

    @Override
    public void onStartup(final ServletContext servletContext) throws ServletException {
        this.startUp(servletContext);

        InternalLoggerFactory.setDefaultFactory(Slf4JLoggerFactory.INSTANCE);

        final ServletRegistration.Dynamic dispatcher = servletContext.addServlet(DISPATCHER_SERVLET_NAME,
                new DispatcherServlet(this.rootContext));
        dispatcher.setLoadOnStartup(1);
        dispatcher.addMapping(DISPATCHER_SERVLET_MAPPING);
    }
}

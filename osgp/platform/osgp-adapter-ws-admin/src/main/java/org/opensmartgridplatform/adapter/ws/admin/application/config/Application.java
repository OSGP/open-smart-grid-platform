package org.opensmartgridplatform.adapter.ws.admin.application.config;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.TimeZone;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.opensmartgridplatform.logging.domain.config.ReadOnlyLoggingConfig;
import org.opensmartgridplatform.ws.admin.config.AdminWebServiceConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.quartz.QuartzAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.PropertySource;
import org.springframework.ws.transport.http.MessageDispatcherServlet;

import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.ext.spring.LogbackConfigurer;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootApplication(exclude = { SecurityAutoConfiguration.class, DataSourceAutoConfiguration.class,
        QuartzAutoConfiguration.class, FlywayAutoConfiguration.class })
@ComponentScan(
        basePackages = { "org.opensmartgridplatform.shared.domain.services", "org.opensmartgridplatform.domain.core",
                "org.opensmartgridplatform.adapter.ws.admin", "org.opensmartgridplatform.logging.domain" })
@ImportResource("classpath:applicationContext.xml")
@Import({ MessagingConfig.class, PersistenceConfig.class, WebServiceConfig.class, ReadOnlyLoggingConfig.class,
        AdminWebServiceConfig.class })
@PropertySource("classpath:osgp-adapter-ws-admin.properties")
@PropertySource(value = "file:${osgp/Global/config}", ignoreResourceNotFound = true)
@PropertySource(value = "file:${osgp/AdapterWsAdmin/config}", ignoreResourceNotFound = true)
public class Application extends SpringBootServletInitializer {
    private static final String DISPATCHER_SERVLET_NAME = "spring-ws";
    private static final String DISPATCHER_SERVLET_MAPPING = "/ws/*";
    private static final String LOG_CONFIG = "java:comp/env/osgp/AdapterWsAdmin/log-config";

    public static void main(final String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Override
    public void onStartup(final ServletContext servletContext) throws ServletException {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        this.initializeLogging(LOG_CONFIG);
        super.onStartup(servletContext);
    }

    @Override
    protected SpringApplicationBuilder configure(final SpringApplicationBuilder builder) {
        return builder.sources(Application.class);
    }

    @Bean
    public ServletRegistrationBean registerMessageDispatcherServlet(final ApplicationContext applicationContext) {
        final MessageDispatcherServlet servlet = new MessageDispatcherServlet();
        servlet.setApplicationContext(applicationContext);
        servlet.setTransformWsdlLocations(true);

        final ServletRegistrationBean registrationBean = new ServletRegistrationBean(servlet,
                DISPATCHER_SERVLET_MAPPING);
        registrationBean.setName(DISPATCHER_SERVLET_NAME);
        registrationBean.setLoadOnStartup(1);
        return registrationBean;
    }

    private void initializeLogging(final String logConfig) throws ServletException {
        Context initialContext;
        try {
            initialContext = new InitialContext();
            final String logLocation = (String) initialContext.lookup(logConfig);
            if (new File(logLocation).exists()) {
                LogbackConfigurer.initLogging(logLocation);
                log.info("Initialized logging using {}", logConfig);
            }
        } catch (final NamingException | FileNotFoundException | JoranException e) {
            log.info("Failed to initialize logging using {}", logConfig, e);
            throw new ServletException(e);
        }
    }
}

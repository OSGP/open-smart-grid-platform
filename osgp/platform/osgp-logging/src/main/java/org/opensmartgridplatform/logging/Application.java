package org.opensmartgridplatform.logging;

import lombok.extern.slf4j.Slf4j;
import org.opensmartgridplatform.logging.application.config.PersistenceConfig;
import org.opensmartgridplatform.logging.application.config.messaging.InboundLoggingRequestsMessagingConfig;
import org.opensmartgridplatform.logging.infra.jms.LoggingMessageListener;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.quartz.QuartzAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.ws.transport.http.MessageDispatcherServlet;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import java.util.Properties;
import java.util.TimeZone;

@Slf4j
@SpringBootApplication(
        exclude = {UserDetailsServiceAutoConfiguration.class, DataSourceAutoConfiguration.class, QuartzAutoConfiguration.class})
@ComponentScan(basePackageClasses = {PersistenceConfig.class, LoggingMessageListener.class,
        InboundLoggingRequestsMessagingConfig.class})
@PropertySource("classpath:osgp-logging.properties")
@PropertySource(value = "file:${osgp/Global/config}", ignoreResourceNotFound = true)
@PropertySource(value = "file:${osgp/Logging/config}", ignoreResourceNotFound = true)
public class Application extends SpringBootServletInitializer {

    private static final String DISPATCHER_SERVLET_NAME = "spring-ws";
    private static final String DISPATCHER_SERVLET_MAPPING = "/ws/*";
    private static final String LOG_CONFIG = "java:comp/env/osgp/Logging/log-config";

    /**
     * Run with embedded Tomcat container.
     *
     * @param args
     */
    public static void main(final String[] args) {
        SpringApplication.run(Application.class, args);
    }

    /**
     * Run by a servlet container.
     *
     * @param servletContext
     * @return
     */
    @Override
    public void onStartup(final ServletContext servletContext) throws ServletException {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        super.onStartup(servletContext);
    }

    @Override
    protected SpringApplicationBuilder configure(final SpringApplicationBuilder builder) {
        final String logPropertiesLocation = this.getLogbackConfigurationLocation();

        log.info("Location for properties: {}", logPropertiesLocation);

        final Properties props = new Properties();
        props.setProperty("logging.config", logPropertiesLocation);
        builder.application().setDefaultProperties(props);

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

    private String getLogbackConfigurationLocation() {
        try {
            final Context initialContext = new InitialContext();

            return (String) initialContext.lookup(LOG_CONFIG);
        } catch (final NamingException | RuntimeException e) {
            throw new IllegalStateException("Getting the location of the logback configuration file failed", e);
        }
    }
}

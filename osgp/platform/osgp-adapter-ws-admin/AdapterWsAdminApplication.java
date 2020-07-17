package org.opensmartgridplatform.adapter.ws.admin.application.config;

import java.io.File;
import java.util.Optional;
import java.util.Properties;
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
public class AdapterWsAdminApplication extends SpringBootServletInitializer {
    private static final String DISPATCHER_SERVLET_NAME = "spring-ws";
    private static final String DISPATCHER_SERVLET_MAPPING = "/ws/*";
    private static final String LOG_CONFIG = "java:comp/env/osgp/AdapterWsAdmin/log-config";

    public static void main(final String[] args) {
        SpringApplication.run(AdapterWsAdminApplication.class, args);
    }

    @Override
    public void onStartup(final ServletContext servletContext) throws ServletException {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        super.onStartup(servletContext);
    }

    @Override
    protected SpringApplicationBuilder configure(final SpringApplicationBuilder builder) {
        final Optional<String> logPropertiesLocation = this.getLogbackConfigurationLocation();

        if (logPropertiesLocation.isPresent()) {
            log.info("Location for properties: {}", logPropertiesLocation.get());

            final Properties props = new Properties();
            props.setProperty("logging.config", logPropertiesLocation.get());
            builder.application().setDefaultProperties(props);
        }

        return builder.sources(AdapterWsAdminApplication.class);
    }

    private Optional<String> getLogbackConfigurationLocation() {
        try {
            final Context initialContext = new InitialContext();
            final String location = (String) initialContext.lookup(LOG_CONFIG);
            final File logConfig = new File(location);

            if (logConfig.exists()) {
                return Optional.of(location);
            }

            log.error("File {} does not exist.", location);
        } catch (final NamingException | RuntimeException e) {
            log.error("Getting the location of the logback configuration file failed. Reason: {}", e.getMessage());
        }

        return Optional.empty();
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
}

package org.opensmartgridplatform.adapter.ws.core.application.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.core.io.FileSystemResource;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.soap.saaj.SaajSoapMessageFactory;
import org.springframework.ws.soap.security.support.KeyStoreFactoryBean;

import org.opensmartgridplatform.adapter.ws.clients.SendNotificationServiceClient;
import org.opensmartgridplatform.adapter.ws.core.application.services.NotificationServiceWsCore;
import org.opensmartgridplatform.adapter.ws.shared.services.NotificationService;
import org.opensmartgridplatform.adapter.ws.shared.services.NotificationServiceBlackHole;
import org.opensmartgridplatform.shared.application.config.AbstractConfig;
import org.opensmartgridplatform.shared.infra.ws.DefaultWebServiceTemplateFactory;

@Configuration
@PropertySources({ @PropertySource("classpath:osgp-adapter-ws-core.properties"),
        @PropertySource(value = "file:${osgp/Global/config}", ignoreResourceNotFound = true),
        @PropertySource(value = "file:${osgp/AdapterWsCore/config}", ignoreResourceNotFound = true), })
public class NotificationClientConfig extends AbstractConfig {

    // Notification web service global properties.

    @Value("${web.service.notification.enabled:true}")
    private boolean webserviceNotificationEnabled;

    @Value("${web.service.notification.security.enabled:false}")
    private boolean webserviceNotificationSecurityEnabled;

    @Value("${web.service.notification.url:http://localhost:8080/web-api-net-management/soap/osgp/notificationService/}")
    private String webserviceNotificationUrl;

    @Value("${jaxb2.marshaller.context.path.notification:org.opensmartgridplatform.adapter.ws.schema.core.notification}")
    private String marshallerContextPathNotification;

    @Value("${apache.client.max.connections.per.route:20}")
    private int maxConnectionsPerRoute;

    @Value("${apache.client.max.connections.total:400}")
    private int maxConnectionsTotal;

    // Notification web service application name, user name and organization
    // properties.

    @Value("${web.service.notification.application.name:WS_CORE}")
    private String applicationName;

    @Value("${web.service.notification.username:OSGP}")
    private String webserviceNotificationUsername;

    @Value("${web.service.notification.organisation:OSGP}")
    private String webserviceNotificationOrganisation;

    // Notification web service security properties.

    @Value("${web.service.truststore.location:/etc/ssl/certs/trust.jks}")
    private String webserviceTruststoreLocation;

    @Value("${web.service.truststore.password:123456}")
    private String webserviceTruststorePassword;

    @Value("${web.service.truststore.type:jks}")
    private String webserviceTruststoreType;

    @Value("${web.service.keystore.location:/etc/ssl/certs}")
    private String webserviceKeystoreLocation;

    @Value("${web.service.keystore.password:1234}")
    private String webserviceKeystorePassword;

    @Value("${web.service.keystore.type:pkcs12}")
    private String webserviceKeystoreType;

    @Bean
    public SaajSoapMessageFactory messageFactory() {
        return new SaajSoapMessageFactory();
    }

    @Bean
    public KeyStoreFactoryBean webServiceTrustStoreFactory() {
        final KeyStoreFactoryBean factory = new KeyStoreFactoryBean();
        factory.setType(this.webserviceTruststoreType);
        factory.setLocation(new FileSystemResource(this.webserviceTruststoreLocation));
        factory.setPassword(this.webserviceTruststorePassword);
        return factory;
    }

    public Jaxb2Marshaller notificationSenderMarshaller() {
        final Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setContextPath(this.marshallerContextPathNotification);
        return marshaller;
    }

    @Bean
    public SendNotificationServiceClient sendNotificationServiceClient() {
        return new SendNotificationServiceClient(
                this.createWebServiceTemplateFactory(this.notificationSenderMarshaller()),
                this.webserviceNotificationOrganisation, this.webserviceNotificationUsername);
    }

    @Bean
    public DefaultWebServiceTemplateFactory createWebServiceTemplateFactory(final Jaxb2Marshaller marshaller) {
        final DefaultWebServiceTemplateFactory.Builder builder = new DefaultWebServiceTemplateFactory.Builder();
        builder.setMarshaller(marshaller).setMessageFactory(this.messageFactory())
                .setTargetUri(this.webserviceNotificationUrl).setMaxConnectionsPerRoute(this.maxConnectionsPerRoute)
                .setMaxConnectionsTotal(this.maxConnectionsTotal).setApplicationName(this.applicationName)
                .setSecurityEnabled(this.webserviceNotificationSecurityEnabled);
        if (this.webserviceNotificationSecurityEnabled) {
            builder.setKeyStoreType(this.webserviceKeystoreType).setKeyStoreLocation(this.webserviceKeystoreLocation)
                    .setKeyStorePassword(this.webserviceKeystorePassword)
                    .setTrustStoreFactory(this.webServiceTrustStoreFactory());
        }

        return builder.build();
    }

    @Bean
    public NotificationService notificationService() {
        if (this.webserviceNotificationEnabled) {
            return new NotificationServiceWsCore();
        } else {
            return new NotificationServiceBlackHole();
        }
    }
}

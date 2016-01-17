/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.acceptancetests.config;

import static org.mockito.Mockito.mock;

import javax.annotation.Resource;

import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.DateTimeZone;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;

import com.alliander.osgp.acceptancetests.config.messaging.MessagingConfig;
import com.alliander.osgp.adapter.ws.core.application.mapping.DeviceInstallationMapper;
import com.alliander.osgp.adapter.ws.core.application.mapping.DeviceManagementMapper;
import com.alliander.osgp.adapter.ws.endpointinterceptors.CertificateAndSoapHeaderAuthorizationEndpointInterceptor;
import com.alliander.osgp.adapter.ws.endpointinterceptors.SoapHeaderEndpointInterceptor;
import com.alliander.osgp.adapter.ws.endpointinterceptors.X509CertificateRdnAttributeValueEndpointInterceptor;
import com.alliander.osgp.domain.core.services.SecurityService;
import com.alliander.osgp.shared.application.config.PagingSettings;

@Configuration
@ComponentScan(basePackages = { "com.alliander.osgp" }, excludeFilters = { @Filter(type = FilterType.ANNOTATION, value = Configuration.class) })
// @Import({ OslpConfig.class, PersistenceConfig.class, SchedulingConfig.class,
// MessagingConfig.class })
@Import({ OslpConfig.class, PersistenceConfig.class, MessagingConfig.class })
public class TestApplicationContext {

    protected static final String LOCAL_TIME_ZONE_IDENTIFIER = "Europe/Paris";
    protected static final DateTimeZone LOCAL_TIME_ZONE = DateTimeZone.forID(LOCAL_TIME_ZONE_IDENTIFIER);
    protected static final int TIME_ZONE_OFFSET_MINUTES = LOCAL_TIME_ZONE.getStandardOffset(new DateTime().getMillis())
            / DateTimeConstants.MILLIS_PER_MINUTE;

    private static final int PAGING_MAXIMUM_PAGE_SIZE = 30;
    private static final int PAGING_DEFAULT_PAGE_SIZE = 15;

    private static final String ORGANISATION_IDENTIFICATION_HEADER = "OrganisationIdentification";
    private static final String ORGANISATION_IDENTIFICATION_CONTEXT = "OrganisationIdentification";
    private static final String X509_RDN_ATTRIBUTE_ID = "cn";
    private static final String X509_RDN_ATTRIBUTE_VALUE_CONTEXT_PROPERTY_NAME = "CommonNameSet";

    private static final String DEFAULT_PROTOCOL = "OSLP";
    private static final String DEFAULT_PROTOCOL_VERSION = "1.0";
    private static final Integer RECENT_DEVICES_PERIOD = 3;
    private static final int MAX_RETRY_COUNT = 3;
    private static final String NET_MANAGEMENT_ORGANISATION = "LianderNetManagement";

    @Resource
    Environment environment;

    @Bean
    public LocalValidatorFactoryBean validator() {
        final LocalValidatorFactoryBean localValidatorFactoryBean = new LocalValidatorFactoryBean();
        final org.springframework.core.io.Resource[] resources = { new ClassPathResource("constraint-mappings.xml") };
        localValidatorFactoryBean.setMappingLocations(resources);
        return localValidatorFactoryBean;
    }

    @Bean
    public MethodValidationPostProcessor methodValidationPostProcessor() {
        final MethodValidationPostProcessor m = new MethodValidationPostProcessor();
        m.setValidatorFactory(this.validator());
        return m;
    }

    @Bean
    public SecurityService securityServiceMock() {
        return mock(SecurityService.class);
    }

    @Bean
    public PagingSettings pagingSettings() {
        final PagingSettings settings = new PagingSettings(PAGING_MAXIMUM_PAGE_SIZE, PAGING_DEFAULT_PAGE_SIZE);

        return settings;
    }

    @Bean
    public SoapHeaderEndpointInterceptor organisationIdentificationInterceptor() {
        return new SoapHeaderEndpointInterceptor(ORGANISATION_IDENTIFICATION_HEADER,
                ORGANISATION_IDENTIFICATION_CONTEXT);
    }

    @Bean
    public X509CertificateRdnAttributeValueEndpointInterceptor x509CertificateSubjectCnEndpointInterceptor() {
        return new X509CertificateRdnAttributeValueEndpointInterceptor(X509_RDN_ATTRIBUTE_ID,
                X509_RDN_ATTRIBUTE_VALUE_CONTEXT_PROPERTY_NAME);
    }

    @Bean
    public CertificateAndSoapHeaderAuthorizationEndpointInterceptor organisationIdentificationInCertificateCnEndpointInterceptor() {
        return new CertificateAndSoapHeaderAuthorizationEndpointInterceptor(
                X509_RDN_ATTRIBUTE_VALUE_CONTEXT_PROPERTY_NAME, ORGANISATION_IDENTIFICATION_CONTEXT);
    }

    // === Time zone config ===

    @Bean
    public String localTimeZoneIdentifier() {
        return LOCAL_TIME_ZONE_IDENTIFIER;
    }

    @Bean
    public DateTimeZone localTimeZone() {
        return LOCAL_TIME_ZONE;
    }

    @Bean
    public Integer timeZoneOffsetMinutes() {
        return TIME_ZONE_OFFSET_MINUTES;
    }

    @Bean
    public String defaultProtocol() {
        return DEFAULT_PROTOCOL;
    }

    @Bean
    public String defaultProtocolVersion() {
        return DEFAULT_PROTOCOL_VERSION;
    }

    @Bean
    public Integer recentDevicesPeriod() {
        return RECENT_DEVICES_PERIOD;
    }

    @Bean
    public int getMaxRetryCount() {
        return MAX_RETRY_COUNT;
    }

    @Bean
    @Qualifier("wsCoreDeviceManagementNetManagementOrganisation")
    public String netManagementOrganisation() {
        return NET_MANAGEMENT_ORGANISATION;
    }

    @Bean
    @Qualifier("coreDeviceInstallationMapper")
    public DeviceInstallationMapper deviceInstallationMapper() {
        return new DeviceInstallationMapper();
    }

    @Bean
    @Qualifier("coreDeviceManagementMapper")
    public DeviceManagementMapper deviceManagementMapper() {
        return new DeviceManagementMapper();
    }
}

/**
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging;

import java.util.Arrays;
import java.util.List;

import org.mockito.Mockito;
import org.opensmartgridplatform.adapter.protocol.dlms.application.config.messaging.OutboundLogItemRequestsMessagingConfig;
import org.opensmartgridplatform.adapter.protocol.dlms.application.config.messaging.OutboundOsgpCoreResponsesMessagingConfig;
import org.opensmartgridplatform.adapter.protocol.dlms.application.services.DomainHelperService;
import org.opensmartgridplatform.adapter.protocol.dlms.application.services.MonitoringService;
import org.opensmartgridplatform.adapter.protocol.dlms.application.services.SecretManagementService;
import org.opensmartgridplatform.adapter.protocol.dlms.application.services.ThrottlingService;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.DlmsHelper;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionFactory;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionHelper;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.InvocationCounterManager;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.repositories.DlmsDeviceRepository;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.OsgpExceptionConverter;
import org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging.processors.GetPowerQualityProfileRequestMessageProcessor;
import org.opensmartgridplatform.shared.application.config.AbstractConfig;
import org.opensmartgridplatform.shared.application.config.messaging.DefaultJmsConfiguration;
import org.opensmartgridplatform.shared.infra.jms.BaseMessageProcessorMap;
import org.opensmartgridplatform.shared.infra.jms.MessageProcessorMap;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.type.ClassMetadata;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.filter.TypeFilter;
import stub.DlmsConnectionFactoryStub;
import stub.DlmsPersistenceConfigStub;

/**
 * Test Configuration for JMS Listener triggered tests.
 */
@Configuration
@ComponentScan(basePackages = {}, excludeFilters = @ComponentScan.Filter(type = FilterType.CUSTOM, classes =
        MessagingTestConfiguration.ExcludeFilter.class))
@PropertySource("classpath:osgp-adapter-protocol-dlms.properties")
@Import({ DlmsPersistenceConfigStub.class, OutboundLogItemRequestsMessagingConfig.class,
        OutboundOsgpCoreResponsesMessagingConfig.class })
public class MessagingTestConfiguration extends AbstractConfig {

    // JMS

    @Bean
    public DefaultJmsConfiguration defaultJmsConfiguration() {
        return new DefaultJmsConfiguration();
    }

    @Bean("protocolDlmsInboundOsgpCoreRequestsMessageListener")
    public DeviceRequestMessageListener deviceRequestMessageListener() {
        return new DeviceRequestMessageListener();
    }

    @Bean("protocolDlmsInboundOsgpCoreRequestsMessageProcessorMap")
    public MessageProcessorMap messageProcessorMap() {
        return new BaseMessageProcessorMap("InboundOsgpCoreRequestsMessageProcessorMap");
    }

    @Bean("protocolDlmsOutboundOsgpCoreResponsesMessageSender")
    public DeviceResponseMessageSender deviceResponseMessageSender() {
        return Mockito.mock(DeviceResponseMessageSender.class);
    }

    // Beans, Mocks and Stubs

    @Bean
    public DlmsHelper dlmsHelper() {
        return new DlmsHelper();
    }

    @Bean
    public DlmsConnectionFactory dlmsConnectionFactory() {
        return new DlmsConnectionFactoryStub();
    }

    @Bean
    public InvocationCounterManager invocationCounterManager(DlmsDeviceRepository dlmsDeviceRepository) {
        return new InvocationCounterManager(this.dlmsConnectionFactory(), this.dlmsHelper(), dlmsDeviceRepository);
    }

    @Bean
    public DlmsConnectionHelper dlmsConnectionHelper(DlmsDeviceRepository dlmsDeviceRepository) {
        return new DlmsConnectionHelper(this.invocationCounterManager(dlmsDeviceRepository), this.dlmsConnectionFactory());
    }

    @Bean
    public DlmsLogItemRequestMessageSender dlmsLogItemRequestMessageSender() {
        return new DlmsLogItemRequestMessageSender();
    }

    @Bean
    public OsgpExceptionConverter osgpExceptionConverter() {
        return new OsgpExceptionConverter();
    }

    @Bean
    public ThrottlingService throttlingService() {
        return new ThrottlingService();
    }

    @Bean
    public GetPowerQualityProfileRequestMessageProcessor getPowerQualityProfileRequestMessageProcessor() {
        return new GetPowerQualityProfileRequestMessageProcessor();
    }

    @Bean
    public RetryHeaderFactory retryHeaderFactory() {
        return new RetryHeaderFactory();
    }

    @Bean
    public DomainHelperService domainHelperService() {
        return Mockito.mock(DomainHelperService.class);
    }

    @Bean
    public MonitoringService monitoringService() {
        return Mockito.mock(MonitoringService.class);
    }

    @Bean
    public SecretManagementService secretManagementService() {
        return Mockito.mock(SecretManagementService.class);
    }

    public static class ExcludeFilter implements TypeFilter {

        @Override
        public boolean match(MetadataReader metadataReader, MetadataReaderFactory metadataReaderFactory) {
            ClassMetadata classMetadata = metadataReader.getClassMetadata();
            String fullyQualifiedName = classMetadata.getClassName();

            boolean match = this.classesNeeded.stream().anyMatch(fullyQualifiedName::contains);

            return match || !fullyQualifiedName.contains("GetPowerQualityProfileRequestMessageProcessor");
        }

        private final List<String> classesNeeded = Arrays
                .asList("RequestMessageProcessor", "ResponseMessageProcessor", "BundleMessageProcessor",
                        "SetRandomisationSettingsMessageProcessor", "RequestMessageSender",
                        "OsgpResponseMessageListener");
    }

}



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

import javax.jms.ConnectionFactory;
import javax.net.ssl.SSLException;

import org.opensmartgridplatform.adapter.protocol.dlms.application.services.DomainHelperService;
import org.opensmartgridplatform.adapter.protocol.dlms.application.services.MonitoringService;
import org.opensmartgridplatform.adapter.protocol.dlms.application.services.SecurityKeyService;
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
import org.opensmartgridplatform.shared.application.config.messaging.JmsConfigurationFactory;
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
import org.springframework.jms.core.JmsTemplate;
import util.DeviceResponseMessageSenderStub;
import util.MockDlmsConnectionFactory;
import util.MockDomainHelperService;
import util.MockMonitoringService;
import util.SecurityKeyServiceStub;

@Configuration
@ComponentScan(basePackages = {}, includeFilters = @ComponentScan.Filter(type = FilterType.CUSTOM, classes = {
        MessagingTestConfiguration.IncludeFilter.class }), excludeFilters = @ComponentScan.Filter(type =
        FilterType.CUSTOM, classes = MessagingTestConfiguration.ExcludeFilter.class))
@PropertySource("classpath:osgp-adapter-protocol-dlms.properties")
@Import({ MockDlmsPersistenceConfig.class })
public class MessagingTestConfiguration extends AbstractConfig {

    @Bean
    public DefaultJmsConfiguration defaultJmsConfiguration() {
        return new DefaultJmsConfiguration();
    }

    @Bean
    public DlmsHelper dlmsHelper() {
        return new DlmsHelper();
    }

    @Bean
    public DlmsConnectionFactory dlmsConnectionFactory() {
        return new MockDlmsConnectionFactory();
    }

    @Bean
    public InvocationCounterManager invocationCounterManager(DlmsDeviceRepository dlmsDeviceRepository) {
        return new InvocationCounterManager(dlmsConnectionFactory(), dlmsHelper(), dlmsDeviceRepository);
    }

    @Bean
    public DlmsConnectionHelper dlmsConnectionHelper(DlmsDeviceRepository dlmsDeviceRepository) {
        return new DlmsConnectionHelper(invocationCounterManager(dlmsDeviceRepository), dlmsConnectionFactory());
    }

    @Bean
    public DlmsLogItemRequestMessageSender dlmsLogItemRequestMessageSender() {
        return new DlmsLogItemRequestMessageSender();
    }

    @Bean
    public JmsConfigurationFactory jmsConfigurationFactory() throws SSLException {
        return new JmsConfigurationFactory(environment, defaultJmsConfiguration(), "jms.dlms.log.item.requests");
    }

    @Bean(destroyMethod = "stop", name = "protocolDlmsOutboundLogItemRequestsConnectionFactory")
    public ConnectionFactory connectionFactory() throws SSLException {
        return this.jmsConfigurationFactory().getPooledConnectionFactory();
    }

    @Bean(name = "protocolDlmsOutboundLogItemRequestsJmsTemplate")
    public JmsTemplate jmsTemplate() throws SSLException {

        return this.jmsConfigurationFactory().initJmsTemplate();
    }

    @Bean("protocolDlmsInboundOsgpCoreRequestsMessageListener")
    public DeviceRequestMessageListener deviceRequestMessageListener() {
        return new DeviceRequestMessageListener();
    }

    @Bean("protocolDlmsInboundOsgpCoreRequestsMessageProcessorMap")
    public MessageProcessorMap messageProcessorMap() {
        return new BaseMessageProcessorMap("InboundOsgpCoreRequestsMessageProcessorMap");
    }

    //// OUTBOUND /////

    @Bean("protocolDlmsOutboundOsgpCoreResponsesMessageSender")
    public DeviceResponseMessageSender deviceResponseMessageSender() {
        return new DeviceResponseMessageSenderStub();
    }

    @Bean(destroyMethod = "stop", name = "protocolDlmsOutboundOsgpCoreResponsesConnectionFactory")
    public ConnectionFactory connectionFactory2() throws SSLException {

        return this.jmsConfigurationFactory().getPooledConnectionFactory();
    }

    @Bean(name = "protocolDlmsOutboundOsgpCoreResponsesJmsTemplate")
    public JmsTemplate jmsTemplate2() throws SSLException {
        return this.jmsConfigurationFactory().initJmsTemplate();
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
        return new MockDomainHelperService();
    }

    @Bean
    public MonitoringService monitoringService() {
        return new MockMonitoringService();
    }

    @Bean
    public SecurityKeyService securityKeyService() {
        return new SecurityKeyServiceStub();
    }

    public static class IncludeFilter implements TypeFilter {

        @Override
        public boolean match(MetadataReader metadataReader, MetadataReaderFactory metadataReaderFactory) {
            ClassMetadata classMetadata = metadataReader.getClassMetadata();
            String fullyQualifiedName = classMetadata.getClassName();

            return classesNeeded.stream().anyMatch(fullyQualifiedName::contains);

        }

        //@formatter:off
        private final List<String> classesNeeded = Arrays
                .asList("DlmsConnectionHelper",
                        "DlmsConnectionManager",
                        "InvocationCounterManager",
                        "DlmsDeviceRepository",
                        "OsgpExceptionConverter",
                        "DlmsConnectionFactory",
                        "DlmsHelper",
                        "DlmsDeviceRepository",
                        "DeviceRequestMessageListener",
                        "CommandExecutorMap",
                        "MessageProcessorMap",
                        "DefaultJmsConfiguration",
                        "JmsConfiguration",
                        "ActualMeterReadsRequestMessageProcessor",
                        "GetPowerQualityProfileRequestMessageProcessor",
                        "DeviceResponseMessageSender",
                        "RetryHeaderFactory",
                        "ThrottlingService",
                        "MonitoringService");
        //@formatter:on
    }

    public static class ExcludeFilter implements TypeFilter {

        @Override
        public boolean match(MetadataReader metadataReader, MetadataReaderFactory metadataReaderFactory) {
            ClassMetadata classMetadata = metadataReader.getClassMetadata();
            String fullyQualifiedName = classMetadata.getClassName();

            boolean match = classesNeeded.stream().anyMatch(fullyQualifiedName::contains);

           /* return match || (!fullyQualifiedName.contains("ActualMeterReadsRequestMessageProcessor")
                    && !fullyQualifiedName.contains("GetPowerQualityProfileRequestMessageProcessor"));
*/
            return match || !fullyQualifiedName.contains("GetPowerQualityProfileRequestMessageProcessor");

            //return false;
        }

        private final List<String> classesNeeded = Arrays
                .asList("RequestMessageProcessor", "ResponseMessageProcessor", "BundleMessageProcessor",
                        "SetRandomisationSettingsMessageProcessor", "RequestMessageSender",
                        "OsgpResponseMessageListener");
    }

}



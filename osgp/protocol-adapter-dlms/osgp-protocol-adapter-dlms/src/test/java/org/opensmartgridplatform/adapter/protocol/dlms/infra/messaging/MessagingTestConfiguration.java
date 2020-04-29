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

import org.opensmartgridplatform.shared.application.config.AbstractConfig;
import org.opensmartgridplatform.shared.application.config.messaging.DefaultJmsConfiguration;
import org.opensmartgridplatform.shared.infra.jms.BaseMessageProcessorMap;
import org.opensmartgridplatform.shared.infra.jms.MessageProcessorMap;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.type.ClassMetadata;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.filter.TypeFilter;

@Configuration
@ComponentScan(basePackages = {}, includeFilters = @ComponentScan.Filter(type = FilterType.CUSTOM, classes =
        MessagingTestConfiguration.IncludeFilter.class), excludeFilters = @ComponentScan.Filter(type =
        FilterType.CUSTOM, classes = MessagingTestConfiguration.ExcludeFilter.class))
@PropertySource("classpath:osgp-adapter-protocol-dlms.properties")
public class MessagingTestConfiguration extends AbstractConfig {

    @Bean
    public DefaultJmsConfiguration defaultJmsConfiguration() {
        return new DefaultJmsConfiguration();
    }

    @Bean("protocolDlmsInboundOsgpCoreRequestsMessageProcessorMap")
    public MessageProcessorMap messageProcessorMap() {
        return new BaseMessageProcessorMap("InboundOsgpCoreRequestsMessageProcessorMap");
    }

    public static class IncludeFilter implements TypeFilter {

        @Override
        public boolean match(MetadataReader metadataReader, MetadataReaderFactory metadataReaderFactory) {
            ClassMetadata classMetadata = metadataReader.getClassMetadata();
            String fullyQualifiedName = classMetadata.getClassName();

            return classesNeeded.stream().anyMatch(fullyQualifiedName::contains);

        }

        private List<String> classesNeeded = Arrays
                .asList("DlmsConnectionHelper", "DeviceRequestMessageListener", "CommandExecutorMap",
                        "MessageProcessorMap", "DefaultJmsConfiguration", "JmsConfiguration");
    }

    public static class ExcludeFilter implements TypeFilter {

        @Override
        public boolean match(MetadataReader metadataReader, MetadataReaderFactory metadataReaderFactory) {
            ClassMetadata classMetadata = metadataReader.getClassMetadata();
            String fullyQualifiedName = classMetadata.getClassName();

            return classesNeeded.stream().anyMatch(fullyQualifiedName::contains);

        }

        private List<String> classesNeeded = Arrays
                .asList("RequestMessageProcessor", "ResponseMessageProcessor", "BundleMessageProcessor",
                        "SetRandomisationSettingsMessageProcessor", "ResponseMessageSender", "RequestMessageSender",
                        "OsgpResponseMessageListener");
    }

}



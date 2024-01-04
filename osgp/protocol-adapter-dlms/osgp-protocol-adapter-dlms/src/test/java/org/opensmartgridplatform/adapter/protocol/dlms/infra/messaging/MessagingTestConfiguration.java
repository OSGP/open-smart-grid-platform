// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import javax.jms.Destination;
import org.apache.activemq.command.ActiveMQDestination;
import org.mockito.Mockito;
import org.opensmartgridplatform.adapter.protocol.dlms.application.config.DevicePingConfig;
import org.opensmartgridplatform.adapter.protocol.dlms.application.config.ThrottlingConfig;
import org.opensmartgridplatform.adapter.protocol.dlms.application.config.messaging.OutboundLogItemRequestsMessagingConfig;
import org.opensmartgridplatform.adapter.protocol.dlms.application.config.messaging.OutboundOsgpCoreResponsesMessagingConfig;
import org.opensmartgridplatform.adapter.protocol.dlms.application.services.DomainHelperService;
import org.opensmartgridplatform.adapter.protocol.dlms.application.services.MonitoringService;
import org.opensmartgridplatform.adapter.protocol.dlms.application.services.SecretManagementService;
import org.opensmartgridplatform.adapter.protocol.dlms.application.services.SystemEventService;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.DlmsHelper;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionFactory;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionHelper;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.InvocationCounterManager;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.repositories.DlmsDeviceRepository;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.OsgpExceptionConverter;
import org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging.processors.GetPowerQualityProfileRequestMessageProcessor;
import org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging.requests.to.core.OsgpRequestMessageSender;
import org.opensmartgridplatform.shared.application.config.AbstractConfig;
import org.opensmartgridplatform.shared.application.config.messaging.DefaultJmsConfiguration;
import org.opensmartgridplatform.shared.application.config.messaging.JmsBrokerType;
import org.opensmartgridplatform.shared.domain.services.CorrelationIdProviderService;
import org.opensmartgridplatform.shared.domain.services.CorrelationIdProviderUUIDService;
import org.opensmartgridplatform.shared.infra.jms.BaseMessageProcessorMap;
import org.opensmartgridplatform.shared.infra.jms.JmsMessageCreator;
import org.opensmartgridplatform.shared.infra.jms.MessageProcessorMap;
import org.opensmartgridplatform.shared.infra.networking.ping.Pinger;
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
import stub.DlmsConnectionFactoryStub;
import stub.DlmsPersistenceConfigStub;

/** Test Configuration for JMS Listener triggered tests. */
@Configuration
@ComponentScan(
    excludeFilters =
        @ComponentScan.Filter(
            type = FilterType.CUSTOM,
            classes = MessagingTestConfiguration.ExcludeFilter.class))
@PropertySource("classpath:osgp-adapter-protocol-dlms.properties")
@Import({
  DlmsPersistenceConfigStub.class,
  OutboundLogItemRequestsMessagingConfig.class,
  OutboundOsgpCoreResponsesMessagingConfig.class
})
public class MessagingTestConfiguration extends AbstractConfig {

  private final long invocationCounterEventThreshold = 10;

  // JMS

  @Bean
  public DefaultJmsConfiguration defaultJmsConfiguration() {
    return new DefaultJmsConfiguration();
  }

  @Bean
  public JmsMessageCreator jmsMessageCreator() {
    return new JmsMessageCreator(JmsBrokerType.ACTIVE_MQ);
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

  @Bean
  public JmsTemplate protocolDlmsDeviceRequestMessageSenderJmsTemplate() {
    return Mockito.mock(JmsTemplate.class);
  }

  @Bean
  public JmsTemplate protocolDlmsOutboundOsgpCoreRequestsJmsTemplate() {
    return Mockito.mock(JmsTemplate.class);
  }

  // Beans, Mocks and Stubs

  @Bean
  public DlmsHelper dlmsHelper() {
    return new DlmsHelper();
  }

  @Bean
  public DeviceRequestMessageSender deviceRequestMessageSender() {
    return new DeviceRequestMessageSender();
  }

  @Bean("protocolDlmsReplyToQueue")
  public Destination replyToQueue() {
    return Mockito.mock(ActiveMQDestination.class);
  }

  @Bean
  public OsgpRequestMessageSender osgpRequestMessageSender() {
    return new OsgpRequestMessageSender();
  }

  @Bean
  public CorrelationIdProviderService correlationIdProviderService() {
    return new CorrelationIdProviderUUIDService();
  }

  @Bean
  public SystemEventService systemEventService(
      final OsgpRequestMessageSender osgpRequestMessageSender,
      final CorrelationIdProviderService correlationIdProviderService) {

    return new SystemEventService(
        osgpRequestMessageSender,
        correlationIdProviderService,
        this.invocationCounterEventThreshold);
  }

  @Bean
  public DlmsConnectionFactory dlmsConnectionFactory() {
    return new DlmsConnectionFactoryStub();
  }

  @Bean
  public InvocationCounterManager invocationCounterManager(
      final DlmsDeviceRepository deviceRepository,
      final DlmsLogItemRequestMessageSender dlmsLogItemRequestMessageSender) {
    return new InvocationCounterManager(
        this.dlmsConnectionFactory(),
        this.dlmsHelper(),
        deviceRepository,
        dlmsLogItemRequestMessageSender);
  }

  @Bean
  public DlmsConnectionHelper dlmsConnectionHelper(
      final DlmsDeviceRepository deviceRepository,
      final DlmsLogItemRequestMessageSender dlmsLogItemRequestMessageSender) {
    final DevicePingConfig devicePingConfig =
        new DevicePingConfig() {
          @Override
          public boolean pingingEnabled() {
            return false;
          }

          @Override
          public Pinger pinger() {
            return new Pinger(1, 0, Duration.ofSeconds(1), false);
          }
        };
    return new DlmsConnectionHelper(
        this.invocationCounterManager(deviceRepository, dlmsLogItemRequestMessageSender),
        this.dlmsConnectionFactory(),
        devicePingConfig,
        0,
        this.domainHelperService());
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
  public ThrottlingConfig throttlingConfig() {
    return new ThrottlingConfig();
  }

  @Bean
  public GetPowerQualityProfileRequestMessageProcessor
      getPowerQualityProfileRequestMessageProcessor() {
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
    public boolean match(
        final MetadataReader metadataReader, final MetadataReaderFactory metadataReaderFactory) {
      final ClassMetadata classMetadata = metadataReader.getClassMetadata();
      final String fullyQualifiedName = classMetadata.getClassName();

      final boolean match = this.classesNeeded.stream().anyMatch(fullyQualifiedName::contains);

      return match || !fullyQualifiedName.contains("GetPowerQualityProfileRequestMessageProcessor");
    }

    private final List<String> classesNeeded =
        Arrays.asList(
            "RequestMessageProcessor",
            "ResponseMessageProcessor",
            "BundleMessageProcessor",
            "SetRandomisationSettingsMessageProcessor",
            "RequestMessageSender",
            "OsgpResponseMessageListener");
  }
}

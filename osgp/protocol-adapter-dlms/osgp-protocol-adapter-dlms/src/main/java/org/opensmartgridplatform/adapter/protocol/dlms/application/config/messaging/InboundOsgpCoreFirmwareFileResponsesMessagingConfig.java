// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.application.config.messaging;

import jakarta.jms.ConnectionFactory;
import jakarta.jms.Destination;
import java.util.UUID;
import javax.net.ssl.SSLException;
import org.opensmartgridplatform.shared.application.config.messaging.DefaultJmsConfiguration;
import org.opensmartgridplatform.shared.application.config.messaging.JmsConfigurationFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

/** Configuration class for inbound responses from OSGP Core on firmware file requests . */
@Configuration
public class InboundOsgpCoreFirmwareFileResponsesMessagingConfig {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(InboundOsgpCoreFirmwareFileResponsesMessagingConfig.class);

  private static final String PROPERTY_NAME_FIRMWARE_FILE_RESPONSES_QUEUE =
      "jms.osgp.firmwarefile.responses.queue";

  private final JmsConfigurationFactory jmsConfigurationFactory;
  private final Environment environment;

  public InboundOsgpCoreFirmwareFileResponsesMessagingConfig(
      final Environment environment, final DefaultJmsConfiguration defaultJmsConfiguration)
      throws SSLException {
    this.environment = environment;
    this.jmsConfigurationFactory =
        new JmsConfigurationFactory(
            environment, defaultJmsConfiguration, "jms.osgp.firmwarefile.responses");
  }

  @Bean(
      destroyMethod = "stop",
      name = "protocolDlmsInboundOsgpCoreFirmwareFileResponsesConnectionFactory")
  public ConnectionFactory connectionFactory() {
    LOGGER.info(
        "Initializing protocolDlmsInboundOsgpCoreFirmwareFileResponsesConnectionFactory bean.");
    return this.jmsConfigurationFactory.getPooledConnectionFactory();
  }

  /**
   * Instead of a fixed name for the responses queue, a 'reply-to' responses queue is used. This
   * 'reply-to' responses queue is communicated to OSGP core by this protocol adapter instance when
   * a request message is sent to OSGP core. OSGP core will send response messages to the 'reply-to'
   * queue. This ensures that the response messages for this protocol adapter instance are sent back
   * to this instance.
   */
  @Bean(name = "protocolDlmsReplyToQueue")
  public Destination replyToQueue() {
    final String queueName =
        this.createUniqueQueueName(PROPERTY_NAME_FIRMWARE_FILE_RESPONSES_QUEUE);
    LOGGER.info("------> replyToQueue: {}", queueName);
    return this.jmsConfigurationFactory.getQueue(queueName);
  }

  private String createUniqueQueueName(final String responseQueuePropertyName) {
    final String responsesQueuePrefix =
        this.environment.getRequiredProperty(responseQueuePropertyName);
    return responsesQueuePrefix + "-" + UUID.randomUUID().toString();
  }
}

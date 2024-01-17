// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.shared.application.config.messaging;

import jakarta.jms.ConnectionFactory;
import jakarta.jms.Destination;
import javax.net.ssl.SSLException;
import org.apache.activemq.RedeliveryPolicy;

public interface JmsBroker {

  JmsBrokerType getBrokerType();

  Destination getQueue(String queueName);

  ConnectionFactory initConnectionFactory() throws SSLException;

  RedeliveryPolicy getRedeliveryPolicy();
}

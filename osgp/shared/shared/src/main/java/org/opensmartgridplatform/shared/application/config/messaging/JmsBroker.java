// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.shared.application.config.messaging;

import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.net.ssl.SSLException;
import org.apache.activemq.RedeliveryPolicy;

public interface JmsBroker {

  JmsBrokerType getBrokerType();

  Destination getQueue(String queueName);

  ConnectionFactory initConnectionFactory() throws SSLException;

  RedeliveryPolicy getRedeliveryPolicy();
}

/*
 * Copyright 2023 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.shared.application.config.messaging;

import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.net.ssl.SSLException;
import org.apache.activemq.RedeliveryPolicy;

public interface JmsBroker {

  JmsBrokerType getBrokerType();

  Destination getQueue();

  ConnectionFactory initConnectionFactory() throws SSLException;

  RedeliveryPolicy getRedeliveryPolicy();
}

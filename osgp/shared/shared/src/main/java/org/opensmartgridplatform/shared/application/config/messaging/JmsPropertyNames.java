// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.shared.application.config.messaging;

/** Common property names for JMS configuration classes. */
public final class JmsPropertyNames {

  public static final String PROPERTY_NAME_BROKER_TYPE = "broker.type";
  public static final String PROPERTY_NAME_BROKER_URL = "broker.url";
  public static final String PROPERTY_NAME_QUEUE = "queue";

  // PROPERTIES FOR CONNECTION POOL
  public static final String PROPERTY_NAME_CONNECTION_POOL_SIZE = "connection.pool.size";
  public static final String PROPERTY_NAME_CONNECTION_POOL_MAX_ACTIVE_SESSIONS =
      "connection.pool.max.active.sessions";
  public static final String PROPERTY_NAME_CONNECTION_POOL_BLOCK_IF_SESSION_POOL_IS_FULL =
      "connection.pool.block.if.session.pool.is.full";
  public static final String PROPERTY_NAME_CONNECTION_POOL_BLOCK_IF_SESSION_POOL_IS_FULL_TIMEOUT =
      "connection.pool.block.if.session.pool.is.full.timeout";
  public static final String PROPERTY_NAME_CONNECTION_POOL_EXPIRY_TIMEOUT =
      "connection.pool.expiry.timeout";
  public static final String PROPERTY_NAME_CONNECTION_POOL_TIME_BETWEEN_EXPIRATION_CHECK_MILLIS =
      "connection.pool.time.between.expiration.check.millis";
  public static final String PROPERTY_NAME_CONNECTION_POOL_IDLE_TIMEOUT =
      "connection.pool.idle.timeout";
  public static final String PROPERTY_NAME_CONNECTION_QUEUE_CONSUMER_WINDOW_SIZE =
      "connection.queue.consumer.window.size";
  public static final String PROPERTY_NAME_CONNECTION_QUEUE_PREFETCH = "connection.queue.prefetch";
  public static final String PROPERTY_NAME_CONNECTION_MESSAGE_PRIORITY_SUPPORTED =
      "connection.message.priority.supported";
  public static final String PROPERTY_NAME_CONNECTION_SEND_TIMEOUT = "connection.send.timeout";

  public static final String PROPERTY_NAME_MAX_THREAD_POOL_SIZE = "max.thread.pool.size";
  public static final String PROPERTY_NAME_TRUST_ALL_PACKAGES = "trust.all.packages";
  public static final String PROPERTY_NAME_TRUSTED_PACKAGES = "trusted.packages";
  public static final String PROPERTY_NAME_BROKER_CLIENT_KEY_STORE = "broker.client.key.store";
  public static final String PROPERTY_NAME_BROKER_CLIENT_KEY_STORE_SECRET =
      "broker.client.key.store.pwd";
  public static final String PROPERTY_NAME_BROKER_CLIENT_TRUST_STORE = "broker.client.trust.store";
  public static final String PROPERTY_NAME_BROKER_CLIENT_TRUST_STORE_SECRET =
      "broker.client.trust.store.pwd";
  public static final String PROPERTY_NAME_BROKER_USERNAME = "broker.username";
  public static final String PROPERTY_NAME_BROKER_SECRET = "broker.password";

  // PROPERTIES FOR MESSAGE LISTENERS
  public static final String PROPERTY_NAME_MAX_CONCURRENT_CONSUMERS = "max.concurrent.consumers";
  public static final String PROPERTY_NAME_CONCURRENT_CONSUMERS = "concurrent.consumers";

  // PROPERTIES FOR REDELIVERY POLICY
  public static final String PROPERTY_NAME_USE_EXPONENTIAL_BACK_OFF = "use.exponential.back.off";
  public static final String PROPERTY_NAME_BACK_OFF_MULTIPLIER = "back.off.multiplier";
  public static final String PROPERTY_NAME_MAXIMUM_REDELIVERY_DELAY = "maximum.redelivery.delay";
  public static final String PROPERTY_NAME_INITIAL_REDELIVERY_DELAY = "initial.redelivery.delay";
  public static final String PROPERTY_NAME_MAXIMUM_REDELIVERIES = "maximum.redeliveries";
  public static final String PROPERTY_NAME_REDELIVERY_DELAY = "redelivery.delay";

  // PROPERTIES FOR MESSAGE SENDERS
  public static final String PROPERTY_NAME_DELIVERY_PERSISTENT = "delivery.persistent";
  public static final String PROPERTY_NAME_TIME_TO_LIVE = "time.to.live";
  public static final String PROPERTY_NAME_EXPLICIT_QOS_ENABLED = "explicit.qos.enabled";

  private JmsPropertyNames() {
    // Don't instantiate utility class.
  }
}

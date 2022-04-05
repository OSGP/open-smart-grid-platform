/*
 * Copyright 2020 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.simulator.protocol.mqtt;

import static java.nio.charset.StandardCharsets.UTF_8;

import io.moquette.BrokerConstants;
import io.moquette.broker.ClientDescriptor;
import io.moquette.broker.Server;
import io.moquette.broker.config.IConfig;
import io.moquette.interception.AbstractInterceptHandler;
import io.moquette.interception.messages.InterceptConnectMessage;
import io.moquette.interception.messages.InterceptDisconnectMessage;
import io.moquette.interception.messages.InterceptPublishMessage;
import io.netty.buffer.ByteBuf;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Broker {

  private static final Logger LOG = LoggerFactory.getLogger(Broker.class);
  private static final String LISTENER_ID = "MoquetteBroker";

  private boolean running;
  private final IConfig config;

  private Server server;

  public Broker(final IConfig config) {
    this.config = config;
  }

  public boolean isRunning() {
    return this.running;
  }

  public void start() throws IOException {
    if (this.isRunning()) {
      LOG.warn("Broker already runnning, exiting");
      return;
    }
    this.server = new Server();
    this.startServer(this.server, this.config);
    this.logConnectedClients("after start");
    this.handleShutdown(this);
  }

  public void stop() {
    if (!this.isRunning()) {
      LOG.warn("Broker not running, nothing to stop");
      return;
    }
    this.logConnectedClients("before stop");

    LOG.info("Stopping broker");
    this.server.stopServer();
    LOG.info("Broker stopped");

    this.running = false;
  }

  public Collection<ClientDescriptor> getConnectedClients() {
    return this.server.listConnectedClients();
  }

  private void logConnectedClients(final String context) {
    final String clients =
        this.server.listConnectedClients().stream()
            .map(ClientDescriptor::toString)
            .collect(Collectors.joining("\n"));
    LOG.info("Connected clients {}:\n{}", context, clients);
  }

  private void startServer(final Server server, final IConfig config) throws IOException {
    final String host = config.getProperty(BrokerConstants.HOST_PROPERTY_NAME);
    final String port = config.getProperty(BrokerConstants.PORT_PROPERTY_NAME);
    final String sslPort = config.getProperty(BrokerConstants.SSL_PORT_PROPERTY_NAME);
    final boolean sslConfigured = sslPort != null;
    final String sslHostAndPort = sslConfigured ? (" and on " + host + ":" + sslPort) : "";
    LOG.info(
        "About to start Server for MQTT broker listening on {}:{}{}", host, port, sslHostAndPort);
    server.startServer(
        config,
        Collections.singletonList(
            new AbstractInterceptHandler() {
              // The protocol adapter is not considered a publishing client and ignored in client
              // detection
              private static final String PROTOCOL_ADAPTER_CLIENT = "gxf-mqtt-client";
              private final Set<String> otherClients = new HashSet<>();

              @Override
              public String getID() {
                return LISTENER_ID;
              }

              /**
               * Client detection: only one testing client is considered active. If more than one
               * testing clients are active, warnings are logged. Connecting adds client to the
               * client list.
               *
               * @param msg interceptor message
               */
              @Override
              public void onConnect(final InterceptConnectMessage msg) {
                LOG.info("Connect by client {}", msg.getClientID());
                if (!PROTOCOL_ADAPTER_CLIENT.equals(msg.getClientID())) {
                  if (!this.otherClients.isEmpty()) {
                    LOG.warn(
                        "MQTT client {} is connecting with other clients active: {}",
                        msg.getClientID(),
                        String.join(", ", this.otherClients));
                  }
                  this.otherClients.add(msg.getClientID());
                }
              }

              /**
               * Client detection: only one testing client is considered active. Disconnecting
               * removes client from the client list.
               *
               * @param msg interceptor message
               */
              @Override
              public void onDisconnect(final InterceptDisconnectMessage msg) {
                LOG.info("Disconnect by client {}", msg.getClientID());
                this.otherClients.remove(msg.getClientID());
              }

              @Override
              public void onPublish(final InterceptPublishMessage msg) {
                LOG.info(
                    "Broker received on topic: {} content: {}",
                    msg.getTopicName(),
                    new String(getBytes(msg), UTF_8));
              }
            }));
    this.running = true;
    LOG.info("Broker started press [CTRL+C] to stop");
  }

  private static byte[] getBytes(final InterceptPublishMessage msg) {
    final ByteBuf buf = msg.getPayload();
    final byte[] bytes = new byte[buf.readableBytes()];
    final int readerIndex = buf.readerIndex();
    buf.getBytes(readerIndex, bytes);
    return bytes;
  }

  private void handleShutdown(final Broker broker) {
    Runtime.getRuntime().addShutdownHook(new Thread(broker::stop));
  }
}

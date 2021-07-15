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

import io.moquette.broker.Server;
import io.moquette.broker.config.IConfig;
import io.moquette.interception.AbstractInterceptHandler;
import io.moquette.interception.messages.InterceptPublishMessage;
import io.netty.buffer.ByteBuf;
import java.io.IOException;
import java.util.Collections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Broker {

  private static final Logger LOG = LoggerFactory.getLogger(Broker.class);
  private static final String LISTENER_ID = "MoquetteBroker";

  private static volatile boolean running;
  private final IConfig config;

  public Broker(final IConfig config) {
    this.config = config;
  }

  public static boolean isRunning() {
    return running;
  }

  public void start() throws IOException {
    if (isRunning()) {
      LOG.warn("Broker already runnning, exiting");
      return;
    }
    final Server server = new Server();
    startServer(server, this.config);
    handleShutdown(server);
  }

  private static void startServer(final Server server, final IConfig config) throws IOException {
    server.startServer(
        config,
        Collections.singletonList(
            new AbstractInterceptHandler() {
              @Override
              public String getID() {
                return LISTENER_ID;
              }

              @Override
              public void onPublish(final InterceptPublishMessage msg) {
                LOG.info(
                    String.format(
                        "Broker received on topic: %s content: %s%n",
                        msg.getTopicName(), new String(getBytes(msg), UTF_8)));
              }
            }));
    running = true;
    LOG.info("Broker started press [CTRL+C] to stop");
  }

  private static byte[] getBytes(final InterceptPublishMessage msg) {
    final ByteBuf buf = msg.getPayload();
    final byte[] bytes = new byte[buf.readableBytes()];
    final int readerIndex = buf.readerIndex();
    buf.getBytes(readerIndex, bytes);
    return bytes;
  }

  private static void handleShutdown(final Server server) {
    Runtime.getRuntime()
        .addShutdownHook(
            new Thread(
                () -> {
                  LOG.info("Stopping broker");
                  server.stopServer();
                  running = false;
                  LOG.info("Broker stopped");
                }));
  }
}

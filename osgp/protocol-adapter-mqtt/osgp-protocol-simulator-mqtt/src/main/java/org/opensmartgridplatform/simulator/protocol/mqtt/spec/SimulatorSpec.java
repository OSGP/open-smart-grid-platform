/*
 * Copyright 2020 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.simulator.protocol.mqtt.spec;

public class SimulatorSpec {

  private String brokerHost;
  private int brokerPort;
  private int startupPauseMillis;
  private Message[] messages;

  public SimulatorSpec() {
    // when instantiated from JSON
  }

  public SimulatorSpec(final String brokerHost, final int brokerPort) {
    this.brokerHost = brokerHost;
    this.brokerPort = brokerPort;
  }

  public void setStartupPauseMillis(final int startupPauseMillis) {
    this.startupPauseMillis = startupPauseMillis;
  }

  public void setMessages(final Message[] messages) {
    this.messages = messages;
  }

  public String getBrokerHost() {
    return this.brokerHost;
  }

  public int getBrokerPort() {
    return this.brokerPort;
  }

  public int getStartupPauseMillis() {
    return this.startupPauseMillis;
  }

  public Message[] getMessages() {
    return this.messages;
  }
}

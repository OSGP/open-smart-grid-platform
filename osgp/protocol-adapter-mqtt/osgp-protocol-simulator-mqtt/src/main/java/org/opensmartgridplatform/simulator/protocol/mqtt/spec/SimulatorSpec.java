// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.simulator.protocol.mqtt.spec;

public class SimulatorSpec {

  private String brokerHost;
  private int brokerPort;
  private int startupPauseMillis;
  private Message[] messages;
  private boolean keepReplayingMessages = true;

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

  public void processMessagesOnlyOnce() {
    this.keepReplayingMessages = false;
  }

  public boolean keepReplayingMessages() {
    return this.keepReplayingMessages;
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

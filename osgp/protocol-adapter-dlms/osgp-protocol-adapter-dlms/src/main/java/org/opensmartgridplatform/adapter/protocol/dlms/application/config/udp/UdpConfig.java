/*
 * Copyright 2022 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */

package org.opensmartgridplatform.adapter.protocol.dlms.application.config.udp;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.ip.udp.UnicastReceivingChannelAdapter;
import org.springframework.integration.ip.udp.UnicastSendingMessageHandler;
import org.springframework.messaging.MessageChannel;

@Configuration
@PropertySource("classpath:osgp-adapter-protocol-dlms.properties")
@PropertySource(value = "file:${osgp/Global/config}", ignoreResourceNotFound = true)
@PropertySource(value = "file:${osgp/AdapterProtocolDlms/config}", ignoreResourceNotFound = true)
public class UdpConfig {

  @Value("${udp.channel}")
  private String channel;

  @Value("${udp.port}")
  private Integer port;

  @Bean
  public MessageChannel inboundChannel() {
    return new DirectChannel();
  }

  @Bean(name = "udpReceivingAdapter")
  public UnicastReceivingChannelAdapter udpReceivingAdapter() {
    final UnicastReceivingChannelAdapter adapter = new UnicastReceivingChannelAdapter(this.port);
    adapter.setOutputChannel(this.inboundChannel());
    adapter.setOutputChannelName(this.channel);
    return adapter;
  }

  @Bean
  public UnicastSendingMessageHandler udpSendingAdapter() {
    return new UnicastSendingMessageHandler("localhost", this.port);
  }
}

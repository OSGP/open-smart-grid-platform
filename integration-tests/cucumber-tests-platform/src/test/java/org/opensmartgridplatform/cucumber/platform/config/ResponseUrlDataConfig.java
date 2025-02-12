package org.opensmartgridplatform.cucumber.platform.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ResponseUrlDataConfig {

  @Value("${response.url.notification.context}")
  private String responseContextPath;

  @Value("${response.url.notification.port}")
  private int responsePort;

  @Value("${response.url.notification.address}")
  private String responseAddress;

  @Bean("responseUrl")
  public String notificationTargetUri() {
    return "http://" + this.responseAddress + ":" + this.responsePort + this.responseContextPath;
  }
}

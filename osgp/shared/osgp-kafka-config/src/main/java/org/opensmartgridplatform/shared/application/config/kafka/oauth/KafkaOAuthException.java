/*
 * Copyright 2023 Alliander N.V.
 */

package org.opensmartgridplatform.shared.application.config.kafka.oauth;

public class KafkaOAuthException extends RuntimeException {
  public KafkaOAuthException(String s, Throwable throwable) {
    super(s, throwable);
  }
}

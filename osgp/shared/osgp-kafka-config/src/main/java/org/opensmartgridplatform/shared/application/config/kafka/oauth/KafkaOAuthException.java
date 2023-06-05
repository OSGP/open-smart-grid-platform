// Copyright 2023 Alliander N.V.
// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.shared.application.config.kafka.oauth;

public class KafkaOAuthException extends RuntimeException {
  public KafkaOAuthException(String s, Throwable throwable) {
    super(s, throwable);
  }
}

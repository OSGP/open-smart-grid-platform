/*
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.kafka.da.application.config;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import org.opensmartgridplatform.adapter.kafka.da.signature.MessageSigner;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.util.StreamUtils;

@Configuration
public class MessageSignerConfig {

  @Value("${distributionautomation.kafka.message.signing.enabled}")
  private boolean signingEnabled;

  @Value("${distributionautomation.kafka.message.signing.strip.headers}")
  private boolean stripHeaders;

  @Value("${distributionautomation.kafka.message.signature.algorithm:SHA256withRSA}")
  private String signatureAlgorithm;

  @Value("${distributionautomation.kafka.message.signature.provider:SunRsaSign}")
  private String signatureProvider;

  @Value("${distributionautomation.kafka.message.signature.key.algorithm:RSA}")
  private String signatureKeyAlgorithm;

  @Value("${distributionautomation.kafka.message.signature.key.size:2048}")
  private int signatureKeySize;

  @Value("${distributionautomation.kafka.message.signature.key.private:#{null}}")
  private Resource signingKeyResource;

  @Value("${distributionautomation.kafka.message.signature.key.public:#{null}}")
  private Resource verificationKeyResource;

  @Bean
  public MessageSigner messageSigner() {
    return MessageSigner.newBuilder()
        .signingEnabled(this.signingEnabled)
        .stripHeaders(this.stripHeaders)
        .signatureAlgorithm(this.signatureAlgorithm)
        .signatureProvider(this.signatureProvider)
        .signatureKeyAlgorithm(this.signatureKeyAlgorithm)
        .signatureKeySize(this.signatureKeySize)
        .signingKey(this.readKeyFromPemResource(this.signingKeyResource, "private signing key"))
        .verificationKey(
            this.readKeyFromPemResource(this.verificationKeyResource, "public verification key"))
        .build();
  }

  private String readKeyFromPemResource(final Resource keyResource, final String name) {
    if (keyResource == null) {
      return null;
    }
    try {
      return StreamUtils.copyToString(keyResource.getInputStream(), StandardCharsets.ISO_8859_1);
    } catch (final IOException e) {
      throw new UncheckedIOException("Unable to read " + name + " as ISO-LATIN-1 PEM text", e);
    }
  }
}

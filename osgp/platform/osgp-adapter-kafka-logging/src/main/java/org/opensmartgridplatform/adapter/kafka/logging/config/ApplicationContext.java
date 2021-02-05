/**
 * Copyright 2020 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.kafka.logging.config;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;

import org.opensmartgridplatform.adapter.kafka.da.signature.MessageSigner;
import org.opensmartgridplatform.kafka.logging.KafkaLogger;
import org.opensmartgridplatform.kafka.logging.Slf4jKafkaLogger;
import org.opensmartgridplatform.shared.application.config.AbstractConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.Resource;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.util.StreamUtils;

@Configuration
@EnableKafka
@ComponentScan(basePackages = { "org.opensmartgridplatform.adapter.kafka.logging",
        "org.opensmartgridplatform.shared.application.config" })
@PropertySource("classpath:osgp-adapter-kafka-logging.properties")
@PropertySource(value = "file:${osgp/Global/config}", ignoreResourceNotFound = true)
@PropertySource(value = "file:${osgp/AdapterKafkaLogging/config}", ignoreResourceNotFound = true)
public class ApplicationContext extends AbstractConfig {

    @Bean
    public KafkaLogger kafkaLogger() {
        return new Slf4jKafkaLogger();
    }

    @Bean
    public MessageSigner messageSigner(
            @Value("${low.voltage.kafka.message.signature.algorithm:SHA256withRSA}") final String signatureAlgorithm,
            @Value("${low.voltage.kafka.message.signature.provider:SunRsaSign}") final String signatureProvider,
            @Value("${low.voltage.kafka.message.signature.key.algorithm:RSA}") final String signatureKeyAlgorithm,
            @Value("${low.voltage.kafka.message.signature.key.size:2048}") final int signatureKeySize,
            @Value("${low.voltage.kafka.message.signature.key.private:#{null}}") final Resource signingKeyResource,
            @Value("${low.voltage.kafka.message.signature.key.public}") final Resource verificationKeyResource) {

        return MessageSigner.newBuilder()
                .signatureAlgorithm(signatureAlgorithm)
                .signatureProvider(signatureProvider)
                .signatureKeyAlgorithm(signatureKeyAlgorithm)
                .signatureKeySize(signatureKeySize)
                .signingKey(this.readKeyFromPemResource(signingKeyResource, "private signing key"))
                .verificationKey(this.readKeyFromPemResource(verificationKeyResource, "public verification key"))
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

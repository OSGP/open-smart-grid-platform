/**
 * Copyright 2020 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.kafka.da.application.config;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;

import org.opensmartgridplatform.adapter.kafka.da.signature.MessageSigner;
import org.opensmartgridplatform.shared.application.config.kafka.AbstractKafkaProducerConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.util.StreamUtils;

import com.alliander.data.scadameasurementpublishedevent.Message;

@Configuration
public class LowVoltageMessageProducerConfig extends AbstractKafkaProducerConfig<String, Message> {

    @Value("${distributionautomation.kafka.message.signing.enabled}")
    boolean isSigningEnabled;

    @Value("${distributionautomation.kafka.message.signing.strip.headers}")
    boolean isStripHeaders;

    @Value("${distributionautomation.kafka.message.signature.algorithm:SHA256withRSA}")
    String signatureAlgorithm;

    @Value("${distributionautomation.kafka.message.signature.provider:SunRsaSign}")
    String signatureProvider;

    @Value("${distributionautomation.kafka.message.signature.key.algorithm:RSA}")
    String signatureKeyAlgorithm;

    @Value("${distributionautomation.kafka.message.signature.key.size:2048}")
    int signatureKeySize;

    @Value("${distributionautomation.kafka.message.signature.key.private:#{null}}")
    Resource signingKeyResource;

    @Value("${distributionautomation.kafka.message.signature.key.public:#{null}}")
    Resource verificationKeyResource;

    @Autowired
    public LowVoltageMessageProducerConfig(final Environment environment,
            @Value("${distributionautomation.kafka.common.properties.prefix}") final String propertiesPrefix,
            @Value("${distributionautomation.kafka.topic}") final String topic) {
        super(environment, propertiesPrefix, topic);
    }

    @Bean("distributionAutomationKafkaTemplate")
    @Override
    public KafkaTemplate<String, Message> kafkaTemplate() {
        return this.getKafkaTemplate();
    }

    @Bean
    public MessageSigner messageSigner(
    // @Value("${distributionautomation.kafka.message.signing.enabled}") final
    // boolean signingEnabled,
    // @Value("${distributionautomation.kafka.message.signing.strip.headers}")
    // final boolean stripHeaders,
    // @Value("${distributionautomation.kafka.message.signature.algorithm:SHA256withRSA}")
    // final String signatureAlgorithm,
    // @Value("${distributionautomation.kafka.message.signature.provider:SunRsaSign}")
    // final String signatureProvider,
    // @Value("${distributionautomation.kafka.message.signature.key.algorithm:RSA}")
    // final String signatureKeyAlgorithm,
    // @Value("${distributionautomation.kafka.message.signature.key.size:2048}")
    // final int signatureKeySize,
    // @Value("${distributionautomation.kafka.message.signature.key.private:#{null}}")
    // final Resource signingKeyResource,
    // @Value("${distributionautomation.kafka.message.signature.key.public:#{null}}")
    // final Resource verificationKeyResource
    ) {

        return MessageSigner.newBuilder()
                .signingEnabled(this.isSigningEnabled)
                .stripHeaders(this.isStripHeaders)
                .signatureAlgorithm(this.signatureAlgorithm)
                .signatureProvider(this.signatureProvider)
                .signatureKeyAlgorithm(this.signatureKeyAlgorithm)
                .signatureKeySize(this.signatureKeySize)
                .signingKey(this.readKeyFromPemResource(this.signingKeyResource, "private signing key"))
                .verificationKey(this.readKeyFromPemResource(this.verificationKeyResource, "public verification key"))
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

/**
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.oslp.elster.infra.networking;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doThrow;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.spec.ECGenParameterSpec;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.commons.codec.binary.Base64;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.opensmartgridplatform.adapter.protocol.oslp.elster.application.services.DeviceRegistrationService;
import org.opensmartgridplatform.adapter.protocol.oslp.elster.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.oslp.Oslp;
import org.opensmartgridplatform.oslp.Oslp.GetStatusResponse;
import org.opensmartgridplatform.oslp.Oslp.GetStatusResponse.Builder;
import org.opensmartgridplatform.oslp.Oslp.LightType;
import org.opensmartgridplatform.oslp.Oslp.LinkType;
import org.opensmartgridplatform.oslp.Oslp.Message;
import org.opensmartgridplatform.oslp.OslpEnvelope;
import org.springframework.test.util.ReflectionTestUtils;

import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Metrics;
import io.micrometer.jmx.JmxConfig;
import io.micrometer.jmx.JmxMeterRegistry;
import io.netty.channel.embedded.EmbeddedChannel;

@ExtendWith(MockitoExtension.class)
class MetricsCounterTest {

    private static final String MESSAGES_SUCCESSFUL = "messages.successful";
    private static final String MESSAGES_FAILED = "messages.failed";

    @Mock
    private DeviceRegistrationService deviceRegistrationService;

    @InjectMocks
    private OslpChannelHandlerClient handler;

    @BeforeEach
    void setup() {
        final JmxConfig jmxConfig = key -> null;
        Metrics.addRegistry(new JmxMeterRegistry(jmxConfig, Clock.SYSTEM));
        ReflectionTestUtils.setField(this.handler, "successfulMessagesMetric", MESSAGES_SUCCESSFUL);
        ReflectionTestUtils.setField(this.handler, "failedMessagesMetric", MESSAGES_FAILED);
        final ConcurrentMap<String, OslpCallbackHandler> callbackHandlers = new ConcurrentHashMap<>();
        callbackHandlers.put("embedded", new OslpCallbackHandler(this.responseHandler()));
        ReflectionTestUtils.setField(this.handler, "callbackHandlers", callbackHandlers);
    }

    @Test
    void countSuccess() throws Exception {

        final EmbeddedChannel channel = new EmbeddedChannel(this.handler);

        channel.writeInbound(this.oslpEnvelope());

        final Counter counter = Metrics.globalRegistry.find(MESSAGES_SUCCESSFUL).counter();
        assertThat(counter.count()).isEqualTo(1.0);

    }

    @Test
    void countFailure() throws Exception {

        doThrow(new ProtocolAdapterException("")).when(this.deviceRegistrationService)
                .checkSequenceNumber(any(byte[].class), anyInt());
        final EmbeddedChannel channel = new EmbeddedChannel(this.handler);

        channel.writeInbound(this.oslpEnvelope());

        final Counter counter = Metrics.globalRegistry.find(MESSAGES_FAILED).counter();
        assertThat(counter.count()).isEqualTo(1.0);

    }

    private OslpResponseHandler responseHandler() {
        return new OslpResponseHandler() {

            @Override
            public void handleResponse(final OslpEnvelope oslpResponse) {
            }

            @Override
            public void handleException(final Throwable t) {
            }
        };
    }

    private OslpEnvelope oslpEnvelope() throws Exception {
        final ECGenParameterSpec parameterSpec = new ECGenParameterSpec("secp256r1");
        final KeyPairGenerator generator = KeyPairGenerator.getInstance("EC");
        generator.initialize(parameterSpec, new SecureRandom());
        final KeyPair keyPair = generator.generateKeyPair();

        final OslpEnvelope oslpEnvelope = this.envelopeBuilder(keyPair.getPrivate()).build();
        oslpEnvelope.validate(keyPair.getPublic());

        return oslpEnvelope;
    }

    public OslpEnvelope.Builder envelopeBuilder(final PrivateKey privateKey) {
        final Integer sequenceNumber = 1;
        final byte[] sequenceNumberBytes = new byte[2];
        sequenceNumberBytes[0] = (byte) (sequenceNumber >>> 8);
        sequenceNumberBytes[1] = sequenceNumber.byteValue();

        final Builder response = GetStatusResponse.newBuilder()
                .setPreferredLinktype(LinkType.GPRS)
                .setActualLinktype(LinkType.GPRS)
                .setLightType(LightType.DALI)
                .setEventNotificationMask(1)
                .setStatus(Oslp.Status.OK);

        return new OslpEnvelope.Builder().withSignature("SHA256withECDSA")
                .withProvider("SunEC")
                .withPrimaryKey(privateKey)
                .withSecurityKey(null)
                .withDeviceId(Base64.decodeBase64("TST-123456789012"))
                .withSequenceNumber(sequenceNumberBytes)
                .withPayloadMessage(Message.newBuilder().setGetStatusResponse(response).build());
    }
}

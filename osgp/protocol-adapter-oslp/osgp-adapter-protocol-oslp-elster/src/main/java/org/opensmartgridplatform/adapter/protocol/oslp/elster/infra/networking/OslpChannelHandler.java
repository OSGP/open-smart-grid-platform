/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.oslp.elster.infra.networking;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Semaphore;

import javax.annotation.Resource;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.SimpleChannelHandler;
import org.opensmartgridplatform.adapter.protocol.oslp.elster.domain.entities.OslpDevice;
import org.opensmartgridplatform.adapter.protocol.oslp.elster.domain.repositories.OslpDeviceRepository;
import org.opensmartgridplatform.adapter.protocol.oslp.elster.infra.messaging.OslpLogItemRequestMessage;
import org.opensmartgridplatform.adapter.protocol.oslp.elster.infra.messaging.OslpLogItemRequestMessageSender;
import org.opensmartgridplatform.oslp.Oslp;
import org.opensmartgridplatform.oslp.OslpEnvelope;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class OslpChannelHandler extends SimpleChannelHandler {

    private static Semaphore availableConnections;
    private static boolean limitConnections;

    private final Logger logger;

    @Resource
    protected String oslpSignatureProvider;

    @Resource
    protected String oslpSignature;

    @Resource
    protected int connectionTimeout;

    @Autowired
    private OslpDeviceRepository oslpDeviceRepository;

    @Autowired
    private OslpLogItemRequestMessageSender oslpLogItemRequestMessageSender;

    protected final ConcurrentMap<Integer, OslpCallbackHandler> callbackHandlers = new ConcurrentHashMap<>();

    protected OslpChannelHandler(final Logger logger) {
        this.logger = logger;
        OslpChannelHandler.limitConnections = false;
    }

    protected OslpChannelHandler(final Logger logger, final int maxConcurrentIncomingMessages) {
        OslpChannelHandler.availableConnections = new Semaphore(maxConcurrentIncomingMessages);
        OslpChannelHandler.limitConnections = true;
        this.logger = logger;
    }

    public void setProvider(final String provider) {
        this.oslpSignatureProvider = provider;
    }

    public void setSignature(final String signature) {
        this.oslpSignature = signature;
    }

    private void acquireConnection() throws InterruptedException {
        if (OslpChannelHandler.limitConnections) {
            final Instant acquireStart = Instant.now();
            this.logger.info("Connection requested. Available connections: " + availableConnections.availablePermits());

            availableConnections.acquire();

            final Duration acquireDuration = Duration.between(acquireStart, Instant.now());
            this.logger.info("Connection granted. Available connections: {}, acquireDuration (ms) {}",
                    availableConnections.availablePermits(), acquireDuration.toMillis());
        }
    }

    private void releaseConnection() {
        if (OslpChannelHandler.limitConnections) {
            this.logger.info("Connection released. Available connections: " + availableConnections.availablePermits());
            availableConnections.release();
        }
    }

    @Override
    public void channelOpen(final ChannelHandlerContext ctx, final ChannelStateEvent e) throws Exception {
        this.acquireConnection();

        this.logger.info("{} Channel opened", e.getChannel().getId());
        super.channelOpen(ctx, e);
    }

    @Override
    public void channelDisconnected(final ChannelHandlerContext ctx, final ChannelStateEvent e) throws Exception {
        this.logger.info("{} Channel disconnected", e.getChannel().getId());
        super.channelDisconnected(ctx, e);
    }

    @Override
    public void channelClosed(final ChannelHandlerContext ctx, final ChannelStateEvent e) throws Exception {
        this.logger.info("{} Channel closed", e.getChannel().getId());
        this.releaseConnection();

        super.channelClosed(ctx, e);
    }

    @Override
    public void channelUnbound(final ChannelHandlerContext ctx, final ChannelStateEvent e) throws Exception {
        this.logger.info("{} Channel unbound", e.getChannel().getId());
        super.channelUnbound(ctx, e);
    }

    @Override
    public void exceptionCaught(final ChannelHandlerContext ctx, final ExceptionEvent e) {
        final int channelId = e.getChannel().getId();

        if (this.isConnectionReset(e.getCause())) {
            this.logger.info("{} Connection was (as expected) reset by the device.", channelId);
        } else {
            this.logger.warn("{} Unexpected exception from downstream. {}", channelId, e.getCause());
            this.callbackHandlers.get(channelId).getDeviceResponseHandler().handleException(e.getCause());
            this.callbackHandlers.remove(channelId);
        }
        e.getChannel().close();
    }

    protected void logMessage(final OslpEnvelope message, final boolean incoming) {

        final String deviceUid = Base64.encodeBase64String(message.getDeviceId());
        String deviceIdentification = this.getDeviceIdentificationFromMessage(message.getPayloadMessage());

        // Assume outgoing messages always valid.
        final boolean isValid = !incoming || message.isValid();

        if (StringUtils.isEmpty(deviceIdentification)) {
            // Getting the deviceIdentification from the oslpDevice instance
            final OslpDevice oslpDevice = this.oslpDeviceRepository.findByDeviceUid(deviceUid);
            if (oslpDevice != null) {
                deviceIdentification = oslpDevice.getDeviceIdentification();
            }
        }

        final OslpLogItemRequestMessage oslpLogItemRequestMessage = new OslpLogItemRequestMessage(null, deviceUid,
                deviceIdentification, incoming, isValid, message.getPayloadMessage(), message.getSize());

        this.oslpLogItemRequestMessageSender.send(oslpLogItemRequestMessage);
    }

    private boolean isConnectionReset(final Throwable e) {
        return e != null && e instanceof IOException && e.getMessage() != null
                && e.getMessage().contains("Connection reset by peer");
    }

    protected boolean isOslpResponse(final OslpEnvelope envelope) {
        return envelope.getPayloadMessage().hasRegisterDeviceResponse()
                || envelope.getPayloadMessage().hasConfirmRegisterDeviceResponse()
                || envelope.getPayloadMessage().hasStartSelfTestResponse()
                || envelope.getPayloadMessage().hasStopSelfTestResponse()
                || envelope.getPayloadMessage().hasUpdateFirmwareResponse()
                || envelope.getPayloadMessage().hasSetLightResponse()
                || envelope.getPayloadMessage().hasSetEventNotificationsResponse()
                || envelope.getPayloadMessage().hasEventNotificationResponse()
                || envelope.getPayloadMessage().hasSetScheduleResponse()
                || envelope.getPayloadMessage().hasGetFirmwareVersionResponse()
                || envelope.getPayloadMessage().hasGetStatusResponse()
                || envelope.getPayloadMessage().hasResumeScheduleResponse()
                || envelope.getPayloadMessage().hasSetRebootResponse()
                || envelope.getPayloadMessage().hasSetTransitionResponse()
                || envelope.getPayloadMessage().hasSetConfigurationResponse()
                || envelope.getPayloadMessage().hasGetConfigurationResponse()
                || envelope.getPayloadMessage().hasSwitchConfigurationResponse()
                || envelope.getPayloadMessage().hasGetActualPowerUsageResponse()
                || envelope.getPayloadMessage().hasGetPowerUsageHistoryResponse()
                || envelope.getPayloadMessage().hasSwitchFirmwareResponse()
                || envelope.getPayloadMessage().hasUpdateDeviceSslCertificationResponse()
                || envelope.getPayloadMessage().hasSetDeviceVerificationKeyResponse();
    }

    private String getDeviceIdentificationFromMessage(final Oslp.Message message) {
        String deviceIdentification = "";

        if (message.hasRegisterDeviceRequest()) {
            deviceIdentification = message.getRegisterDeviceRequest().getDeviceIdentification();
        }

        return deviceIdentification;
    }
}

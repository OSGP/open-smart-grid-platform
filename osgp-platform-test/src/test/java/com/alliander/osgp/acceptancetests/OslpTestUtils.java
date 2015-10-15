/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.acceptancetests;

import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.DateTimeZone;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import com.alliander.osgp.adapter.protocol.oslp.application.services.DeviceManagementService;
import com.alliander.osgp.adapter.protocol.oslp.application.services.DeviceRegistrationService;
import com.alliander.osgp.adapter.protocol.oslp.infra.networking.OslpChannelHandler;
import com.alliander.osgp.adapter.protocol.oslp.infra.networking.OslpChannelHandlerClient;
import com.alliander.osgp.adapter.protocol.oslp.infra.networking.OslpChannelHandlerServer;
import com.alliander.osgp.adapter.protocol.oslp.infra.networking.OslpDeviceService;
import com.alliander.osgp.adapter.protocol.oslp.infra.networking.SequenceNumberUtils;
import com.alliander.osgp.oslp.OslpEnvelope;
import com.alliander.osgp.shared.security.CertificateHelper;

public class OslpTestUtils {
    // Private DER SIM key, base64 encoded using
    // http://www.motobit.com/util/base64-decoder-encoder.asp
    public static final String PRIVATE_KEY_BASE_64 = "MIGHAgEAMBMGByqGSM49AgEGCCqGSM49AwEHBG0wawIBAQQg8ydsIOMoTlBPn6rJezELYFLUUuQe"
            + "3GvrhI3TDJj1yNyhRANCAAQ0UmJgxWImQ5wgepQ65nlsK0lvYb/GW6nx4ngLgncDZmWH3Pck8eC1"
            + "xsKg1goWpvl7P1um4cIjKyBwfqf8FxZa";

    // Public DER SIM key, base64 encoded using
    // http://www.motobit.com/util/base64-decoder-encoder.asp
    public static final String PUBLIC_KEY_BASE_64 = "MFkwEwYHKoZIzj0CAQYIKoZIzj0DAQcDQgAENFJiYMViJkOcIHqUOuZ5bCtJb2G/xlup8eJ4C4J3"
            + "A2Zlh9z3JPHgtcbCoNYKFqb5ez9bpuHCIysgcH6n/BcWWg==";

    public static final String KEY_TYPE = "EC";
    public static final String SIGNATURE = "SHA256withECDSA";
    public static final Integer OSLP_SEQUENCE_NUMBER_WINDOW = 6;
    public static final Integer OSLP_SEQUENCE_NUMBER_MAXIMUM = 65535;
    private static final String PROVIDER_ORACLE = "SunEC";
    private static final String PROVIDER_OPENJDK = "SunPKCS11-NSS";
    public static final String LOCAL_TIME_ZONE_IDENTIFIER = "Europe/Paris";
    public static final DateTimeZone LOCAL_TIME_ZONE = DateTimeZone.forID(LOCAL_TIME_ZONE_IDENTIFIER);
    public static final int TIME_ZONE_OFFSET_MINUTES = LOCAL_TIME_ZONE.getStandardOffset(new DateTime().getMillis())
            / DateTimeConstants.MILLIS_PER_MINUTE;

    private static final String OPENJDK = "OPENJDK";

    private static final Integer NEXT_SEQUENCE_NUMBER = 2;

    private static int CLIENT_PORT = 12121;

    private static int LOCAL_CLIENT_PORT = 12123;

    public static OslpChannelHandlerClient createOslpChannelHandlerClient() {
        final OslpChannelHandlerClient oslpChannelHandler = new OslpChannelHandlerClient();

        configureOslpChannelHandler(oslpChannelHandler);

        final ClientBootstrap clientBootstrap = Mockito.mock(ClientBootstrap.class);
        oslpChannelHandler.setBootstrap(clientBootstrap);

        return oslpChannelHandler;
    }

    public static OslpChannelHandlerServer createOslpChannelHandlerServer() {
        final OslpChannelHandlerServer oslpChannelHandler = new OslpChannelHandlerServer();

        configureOslpChannelHandler(oslpChannelHandler);

        final DeviceRegistrationService deviceRegistrationService = Mockito.mock(DeviceRegistrationService.class);
        oslpChannelHandler.setDeviceRegistrationService(deviceRegistrationService);

        final DeviceManagementService deviceManagementService = Mockito.mock(DeviceManagementService.class);
        oslpChannelHandler.setDeviceManagementService(deviceManagementService);

        return oslpChannelHandler;
    }

    public static OslpChannelHandlerClient createOslpChannelHandlerWithResponse(final OslpEnvelope response,
            final Channel channel, final InetAddress networkAddress) {
        final OslpChannelHandlerClient oslpChannelHandler = OslpTestUtils.createOslpChannelHandlerClient();

        final ChannelFuture channelFuture = Mockito.mock(ChannelFuture.class);

        when(oslpChannelHandler.getBootstrap().connect(Mockito.any(SocketAddress.class))).thenReturn(channelFuture);
        when(channelFuture.getChannel()).thenReturn(channel);
        when(channelFuture.isSuccess()).thenReturn(true);
        when(channel.isConnected()).thenReturn(true);

        // Hardcoded to single channel id
        when(channel.getId()).thenReturn(1);

        // Manual trigger validation based on public key, OslpSecurityHandler is
        // skipped
        response.validate(getDefaultPublicKey());
        when(channel.write(anyObject())).thenAnswer(new OslpResponseAnswer(oslpChannelHandler, response, channel));

        final ArgumentCaptor<ChannelFutureListener> argument = ArgumentCaptor.forClass(ChannelFutureListener.class);
        Mockito.doAnswer(new ChannelFutureListenerOperationCompleteAnswer(channelFuture, argument)).when(channelFuture)
                .addListener(argument.capture());

        return oslpChannelHandler;
    }

    public static OslpEnvelope.Builder createOslpEnvelopeBuilder() {
        // Include both private and public key. Public key is needed to validate
        // response messages
        // private key to actually create the signature key. The OslpEnvelope
        // can serve a dual purpose like this (both request creation or response
        // creation).

        try {
            return new OslpEnvelope.Builder()
                    .withSignature(SIGNATURE)
                    .withSequenceNumber(SequenceNumberUtils.convertIntegerToByteArray(NEXT_SEQUENCE_NUMBER))
                    .withProvider(provider())
                    .withPrimaryKey(
                            CertificateHelper.createPrivateKeyFromBase64(PRIVATE_KEY_BASE_64, KEY_TYPE, provider()));
        } catch (NoSuchAlgorithmException | InvalidKeySpecException | IOException | NoSuchProviderException e) {
            throw new IllegalStateException("Could not create OslpEnvelope.", e);
        }
    }

    public static InetSocketAddress createInetSocketAddress(final InetAddress address) {
        if (address.isLoopbackAddress()) {
            return new InetSocketAddress(address, LOCAL_CLIENT_PORT);
        }

        return new InetSocketAddress(address, CLIENT_PORT);
    }

    //
    // TODO: HAD TO HACK THIS TO REMOVE PRIVATE KEY FROM PROTOCOL-ADAPTER-OSLP
    // [KS 2015-10-04]
    //
    public static OslpDeviceService configureDeviceServiceForOslp(final OslpDeviceService oslpDeviceService) {
        // try {
        // oslpDeviceService.setSignature(SIGNATURE);
        // oslpDeviceService.setProvider(provider());
        oslpDeviceService.setOslpPortClient(CLIENT_PORT);
        oslpDeviceService.setOslpPortClientLocal(LOCAL_CLIENT_PORT);
        // oslpDeviceService.setPrivateKey(CertificateHelper.createPrivateKeyFromBase64(PRIVATE_KEY_BASE_64,
        // KEY_TYPE,
        // provider()));

        return oslpDeviceService;
        // }
        // catch (InvalidKeySpecException | NoSuchAlgorithmException |
        // IOException | NoSuchProviderException e) { throw new
        // IllegalStateException("Could not configure OslpDeviceService", e); }

    }

    //
    // TODO: HAD TO HACK THIS TO REMOVE PRIVATE KEY FROM PROTOCOL-ADAPTER-OSLP
    // [KS 2015-10-04]
    //
    public static void configureOslpChannelHandler(final OslpChannelHandler oslpChannelHandler) {
        // try {
        oslpChannelHandler.setSignature(SIGNATURE);
        oslpChannelHandler.setProvider(provider());
        // oslpChannelHandler.setPrivateKey(CertificateHelper.createPrivateKeyFromBase64(PRIVATE_KEY_BASE_64,
        // KEY_TYPE, provider()));
        // } catch (NoSuchAlgorithmException | InvalidKeySpecException |
        // IOException | NoSuchProviderException e) {
        // throw new
        // IllegalStateException("Could not create OslpChannelHandler.", e);
        // }
    }

    public static String provider() {
        if (System.getProperty("java.runtime.name").toUpperCase().contains(OPENJDK)) {
            return PROVIDER_OPENJDK;
        } else {
            return PROVIDER_ORACLE;
        }
    }

    public static PublicKey getDefaultPublicKey() {
        try {
            return CertificateHelper.createPublicKeyFromBase64(PUBLIC_KEY_BASE_64, KEY_TYPE, provider());
        } catch (NoSuchAlgorithmException | InvalidKeySpecException | NoSuchProviderException | IOException e) {
            throw new IllegalStateException("Could not read private key.", e);
        }
    }

    public static void onMessageReceivedWrapper(final OslpEnvelope message, final OslpChannelHandler channelHandler,
            final ChannelHandlerContext channelHandlerContext, final MessageEvent messageEvent) throws Exception {
        // Manually perform validation
        message.validate(OslpTestUtils.getDefaultPublicKey());

        channelHandler.messageReceived(channelHandlerContext, messageEvent);
    }
}
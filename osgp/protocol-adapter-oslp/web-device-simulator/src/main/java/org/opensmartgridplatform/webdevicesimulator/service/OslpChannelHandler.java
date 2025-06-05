package org.opensmartgridplatform.webdevicesimulator.service;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.opensmartgridplatform.oslp.Envelope.OslpEnvelope;
import org.opensmartgridplatform.oslp.LegacyOslpEnvelope;
import org.opensmartgridplatform.webdevicesimulator.exceptions.DeviceSimulatorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Sharable
public class OslpChannelHandler extends SimpleChannelInboundHandler<OslpEnvelope> {

    private static final Logger LOGGER = LoggerFactory.getLogger(OslpChannelHandler.class);

    private final Lock lock = new ReentrantLock();
    private final ConcurrentMap<String, Callback> callbacks = new ConcurrentHashMap<>();

    @Autowired
    private Bootstrap bootstrap;
    @Autowired private int connectionTimeout;


    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, OslpEnvelope oslpEnvelope) throws Exception {
        LOGGER.info("RECEIVED OSLP ENVELOPE: {}", oslpEnvelope);
        channelRead0OslpReponse(channelHandlerContext, oslpEnvelope);
    }

    private void channelRead0OslpReponse(
            final ChannelHandlerContext ctx, final OslpEnvelope message) {
        LOGGER.info("Received OSLP Response (before callback): {}", message.getPayload());

        // Lookup correct callback and call handle method
        final String channelId = ctx.channel().id().asLongText();
        final Callback callback = this.callbacks.remove(channelId);
        if (callback == null) {
            LOGGER.warn("Callback for channel {} does not longer exist, dropping response.", channelId);
            return;
        }

        callback.handle(message);
    }

    public OslpEnvelope send(
            final InetSocketAddress address,
            final LegacyOslpEnvelope request,
            final String deviceIdentification)
            throws IOException, DeviceSimulatorException {
        LOGGER.info("Sending OSLP request: {}", request.getPayloadMessage());

        final Callback callback = new Callback(this.connectionTimeout);

        this.lock.lock();

        // Open connection and send message
        ChannelFuture channelFuture;
        try {
            channelFuture = this.bootstrap.connect(address);
            channelFuture.awaitUninterruptibly(this.connectionTimeout, TimeUnit.MILLISECONDS);
            if (channelFuture.channel() != null && channelFuture.channel().isActive()) {
                LOGGER.info("Connection established to: {}", address);
            } else {
                LOGGER.info(
                        "The connnection to OSGP from device {} is not successful", deviceIdentification);
                LOGGER.warn("Unable to connect to: {}", address);
                throw new IOException("Unable to connect");
            }

            this.callbacks.put(channelFuture.channel().id().asLongText(), callback);
            channelFuture.channel().writeAndFlush(request);
        } finally {
            this.lock.unlock();
        }

        // wait for response and close connection
        try {
            final OslpEnvelope response = callback.get(deviceIdentification);
            LOGGER.info("Received OSLP response (after callback): {}", response.getPayload());

            /*
             * Devices expect the channel to be closed if - and only if - the
             * platform initiated the conversation. If the device initiated the
             * conversation it needs to close the channel itself.
             */
            channelFuture.channel().close();

            return response;
        } catch (final IOException | DeviceSimulatorException e) {
            // Remove callback when exception has occurred
            this.callbacks.remove(channelFuture.channel().id().asLongText());
            throw e;
        }
    }

    private static class Callback {

        private final CountDownLatch latch = new CountDownLatch(1);
        private final int connectionTimeout;
        private OslpEnvelope response;

        Callback(final int connectionTimeout) {
            this.connectionTimeout = connectionTimeout;
        }

        OslpEnvelope get(final String deviceIdentification)
                throws IOException, DeviceSimulatorException {
            try {
                if (!this.latch.await(this.connectionTimeout, TimeUnit.MILLISECONDS)) {
                    LOGGER.warn(
                            "Failed to receive response from device {} within timelimit {} ms",
                            deviceIdentification,
                            this.connectionTimeout);
                    throw new IOException(
                            "Failed to receive response within timelimit " + this.connectionTimeout + " ms");
                }

                LOGGER.info("Response received within {} ms", this.connectionTimeout);
            } catch (final InterruptedException e) {
                throw new DeviceSimulatorException("InterruptedException", e);
            }
            return this.response;
        }

        void handle(final OslpEnvelope response) {
            this.response = response;
            this.latch.countDown();
        }
    }
}

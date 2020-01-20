/**
 * Copyright 2020 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.oslp.elster.application.config;

import static io.netty.buffer.ByteBufUtil.appendPrettyHexDump;
import static io.netty.util.internal.StringUtil.NEWLINE;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufHolder;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

/**
 * This class contains some functions copied from {@link LoggingHandler} of
 * Netty version 4.1.44.Final. The Netty implementation always print a HEX dump.
 * This class can be used to disable HEX dump printing.
 *
 * For more information see: https://github.com/netty/netty/issues/8172
 */
public class OslpLoggingHandler extends LoggingHandler {

    private boolean hexDump;

    public OslpLoggingHandler(final LogLevel logLevel, final boolean hexDump) {
        super(logLevel);
        this.hexDump = hexDump;
    }

    /**
     * Formats an event and returns the formatted message.
     *
     * @param eventName
     *            the name of the event
     */
    @Override
    protected String format(final ChannelHandlerContext ctx, final String eventName) {
        final String chStr = ctx.channel().toString();
        return new StringBuilder(chStr.length() + 1 + eventName.length()).append(chStr).append(' ').append(eventName)
                .toString();
    }

    /**
     * Formats an event and returns the formatted message.
     *
     * @param eventName
     *            the name of the event
     * @param arg
     *            the argument of the event
     */
    @Override
    protected String format(final ChannelHandlerContext ctx, final String eventName, final Object arg) {
        if (arg instanceof ByteBuf) {
            return this.formatByteBuf(ctx, eventName, (ByteBuf) arg);
        } else if (arg instanceof ByteBufHolder) {
            return this.formatByteBufHolder(ctx, eventName, (ByteBufHolder) arg);
        } else {
            return formatSimple(ctx, eventName, arg);
        }
    }

    /**
     * Generates the default log message of the specified event whose argument
     * is a {@link ByteBuf}.
     */
    private String formatByteBuf(final ChannelHandlerContext ctx, final String eventName, final ByteBuf msg) {
        final String chStr = ctx.channel().toString();
        final int length = msg.readableBytes();
        if (length == 0) {
            final StringBuilder buf = new StringBuilder(chStr.length() + 1 + eventName.length() + 4);
            buf.append(chStr).append(' ').append(eventName).append(": 0B");
            return buf.toString();
        } else {
            final int rows = length / 16 + (length % 15 == 0 ? 0 : 1) + 4;
            final StringBuilder buf = new StringBuilder(
                    chStr.length() + 1 + eventName.length() + 2 + 10 + 1 + 2 + rows * 80);

            buf.append(chStr).append(' ').append(eventName).append(": ").append(length).append('B').append(NEWLINE);
            if (this.hexDump) {
                appendPrettyHexDump(buf, msg);
            }

            return buf.toString();
        }
    }

    /**
     * Generates the default log message of the specified event whose argument
     * is a {@link ByteBufHolder}.
     */
    private String formatByteBufHolder(final ChannelHandlerContext ctx, final String eventName,
            final ByteBufHolder msg) {
        final String chStr = ctx.channel().toString();
        final String msgStr = msg.toString();
        final ByteBuf content = msg.content();
        final int length = content.readableBytes();
        if (length == 0) {
            final StringBuilder buf = new StringBuilder(
                    chStr.length() + 1 + eventName.length() + 2 + msgStr.length() + 4);
            buf.append(chStr).append(' ').append(eventName).append(", ").append(msgStr).append(", 0B");
            return buf.toString();
        } else {
            final int rows = length / 16 + (length % 15 == 0 ? 0 : 1) + 4;
            final StringBuilder buf = new StringBuilder(
                    chStr.length() + 1 + eventName.length() + 2 + msgStr.length() + 2 + 10 + 1 + 2 + rows * 80);

            buf.append(chStr).append(' ').append(eventName).append(": ").append(msgStr).append(", ").append(length)
                    .append('B').append(NEWLINE);
            if (this.hexDump) {
                appendPrettyHexDump(buf, content);
            }

            return buf.toString();
        }
    }

    /**
     * Generates the default log message of the specified event whose argument
     * is an arbitrary object.
     */
    private static String formatSimple(final ChannelHandlerContext ctx, final String eventName, final Object msg) {
        final String chStr = ctx.channel().toString();
        final String msgStr = String.valueOf(msg);
        final StringBuilder buf = new StringBuilder(chStr.length() + 1 + eventName.length() + 2 + msgStr.length());
        return buf.append(chStr).append(' ').append(eventName).append(": ").append(msgStr).toString();
    }
}

//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.adapter.protocol.oslp.elster.infra.networking;

import io.netty.channel.AbstractChannel;
import io.netty.channel.Channel;
import io.netty.channel.ChannelConfig;
import io.netty.channel.ChannelId;
import io.netty.channel.ChannelMetadata;
import io.netty.channel.ChannelOutboundBuffer;
import io.netty.channel.DefaultChannelId;
import io.netty.channel.EventLoop;
import java.net.SocketAddress;
import java.util.Objects;

public class TestableChannel extends AbstractChannel {

  public TestableChannel() {
    this(null, DefaultChannelId.newInstance());
  }

  public TestableChannel(final Channel parent, final ChannelId id) {
    super(parent, Objects.requireNonNull(id));
  }

  public static ChannelId id(final String shortText, final String longText) {
    Objects.requireNonNull(shortText);
    Objects.requireNonNull(longText);
    return new ChannelId() {

      private static final long serialVersionUID = 1L;

      @Override
      public String toString() {
        return String.format("ChannelId[%s]", this.asLongText());
      }

      @Override
      public boolean equals(final Object obj) {
        if (this == obj) {
          return true;
        }
        if (!(obj instanceof ChannelId)) {
          return false;
        }
        return this.asLongText().equals(((ChannelId) obj).asLongText());
      }

      @Override
      public int hashCode() {
        return this.asLongText().hashCode();
      }

      @Override
      public int compareTo(final ChannelId o) {
        return this.asLongText().compareTo(o.asLongText());
      }

      @Override
      public String asShortText() {
        return shortText;
      }

      @Override
      public String asLongText() {
        return longText;
      }
    };
  }

  @Override
  public String toString() {
    return String.format("Channel[%s]", this.id().asLongText());
  }

  @Override
  public ChannelConfig config() {
    // Not needed for existing tests
    return null;
  }

  @Override
  public boolean isOpen() {
    return true;
  }

  @Override
  public boolean isActive() {
    return true;
  }

  @Override
  public ChannelMetadata metadata() {
    return null;
  }

  @Override
  protected AbstractUnsafe newUnsafe() {
    return null;
  }

  @Override
  protected boolean isCompatible(final EventLoop loop) {
    return true;
  }

  @Override
  protected SocketAddress localAddress0() {
    return null;
  }

  @Override
  protected SocketAddress remoteAddress0() {
    return null;
  }

  @Override
  protected void doBind(final SocketAddress localAddress) throws Exception {}

  @Override
  protected void doDisconnect() throws Exception {}

  @Override
  protected void doClose() throws Exception {}

  @Override
  protected void doBeginRead() throws Exception {}

  @Override
  protected void doWrite(final ChannelOutboundBuffer in) throws Exception {}
}

// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.shared.infra.networking.ping;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CommandBuilder {

  public static final int MIN_SIZE = 0;
  public static final int MAX_SIZE = 65500;

  private PingFlavor pingFlavor = PingFlavor.forCurrentOs();

  private int count = 1;

  private int size = -1;

  private Duration timeout = null;

  private Boolean lookupNamesForHostAddresses = null;

  private String destination = "127.0.0.1";

  public CommandBuilder withPingFlavor(final PingFlavor pingFlavor) {
    this.pingFlavor = pingFlavor;
    return this;
  }

  public CommandBuilder withCount(final int count) {
    if (count < 0) {
      throw new IllegalArgumentException();
    }
    this.count = count;
    return this;
  }

  public CommandBuilder withDefaultCount() {
    this.count = 0;
    return this;
  }

  public CommandBuilder withSize(final int size) {
    if (size != -1 && (size < MIN_SIZE || size > MAX_SIZE)) {
      throw new IllegalArgumentException(
          String.format("size out of range '%d <= value <= %d': %d", MIN_SIZE, MAX_SIZE, size));
    }
    this.size = size;
    return this;
  }

  public CommandBuilder withDefaultSize() {
    this.size = -1;
    return this;
  }

  public CommandBuilder withTimeout(final Duration timeout) {
    if (timeout != null && (timeout.isZero() || timeout.isNegative())) {
      throw new IllegalArgumentException("timeout must have a positive duration: " + timeout);
    }
    this.timeout = timeout;
    return this;
  }

  public CommandBuilder withDefaultTimeout() {
    this.timeout = null;
    return this;
  }

  public CommandBuilder withLookupNamesForHostAddresses(final Boolean lookupNamesForHostAddresses) {
    this.lookupNamesForHostAddresses = lookupNamesForHostAddresses;
    return this;
  }

  public CommandBuilder withDefaultLookupNamesForHostAddresses() {
    this.lookupNamesForHostAddresses = null;
    return this;
  }

  public CommandBuilder withDestination(final String destination) {
    this.destination = Objects.requireNonNull(destination, "destination");
    return this;
  }

  public List<String> commands() {
    final List<String> commands = new ArrayList<>();
    commands.add(this.pingFlavor.pingCommand());
    if (this.count > 0) {
      commands.add(this.pingFlavor.countFlag(this.count));
    }
    if (this.size > -1) {
      commands.add(this.pingFlavor.sizeFlag(this.size));
    }
    if (this.timeout != null && !this.timeout.isNegative() && !this.timeout.isZero()) {
      commands.add(this.pingFlavor.timeoutFlag(this.timeout));
    }
    if (this.lookupNamesForHostAddresses != null) {
      final String flag =
          this.pingFlavor.lookupNamesForHostAddressesFlag(this.lookupNamesForHostAddresses);
      if (!flag.isEmpty()) {
        commands.add(flag);
      }
    }
    commands.add(this.destination);
    return commands;
  }

  @Override
  public String toString() {
    return String.format(
        "CommandBuilder[flavor=%s, count=%d, size=%d, timeout=%s, lookupNames=%s]",
        this.pingFlavor, this.count, this.size, this.timeout, this.lookupNamesForHostAddresses);
  }
}

// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.shared.infra.networking.ping;

import java.time.Duration;
import java.util.Locale;
import java.util.function.Function;
import java.util.function.ToLongFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public enum PingFlavor {
  LINUX(
      "ping",
      "-c %d",
      "-s %d",
      "-W %d",
      Duration::getSeconds,
      lookupNames -> Boolean.FALSE.equals(lookupNames) ? "-n" : ""),
  MAC_OS(
      "ping",
      "-c %d",
      "-s %d",
      "-t %d",
      Duration::getSeconds,
      lookupNames -> Boolean.FALSE.equals(lookupNames) ? "-n" : ""),
  WINDOWS(
      "ping",
      "-n %d",
      "-l %d",
      "-w %d",
      Duration::toMillis,
      lookupNames -> Boolean.TRUE.equals(lookupNames) ? "-a" : "");

  private static final Logger LOGGER = LoggerFactory.getLogger(PingFlavor.class);

  private final String pingCommand;
  private final String countFlagFormat;
  private final String sizeFlagFormat;
  private final String timeoutFlagFormat;
  private final ToLongFunction<Duration> timeoutValue;
  private final Function<Boolean, String> lookupNamesForHostAddressesFlag;

  PingFlavor(
      final String pingCommand,
      final String countFlagFormat,
      final String sizeFlagFormat,
      final String timeoutFlagFormat,
      final ToLongFunction<Duration> timeoutValue,
      final Function<Boolean, String> lookupNamesForHostAddressesFlag) {

    this.pingCommand = pingCommand;
    this.countFlagFormat = countFlagFormat;
    this.sizeFlagFormat = sizeFlagFormat;
    this.timeoutFlagFormat = timeoutFlagFormat;
    this.timeoutValue = timeoutValue;
    this.lookupNamesForHostAddressesFlag = lookupNamesForHostAddressesFlag;
  }

  public static PingFlavor forCurrentOs() {
    final String osName = System.getProperty("os.name", "");
    final String osNameLowerCase = osName.toLowerCase(Locale.UK);
    if (osNameLowerCase.contains("windows")) {
      return WINDOWS;
    } else if (osNameLowerCase.contains("mac os")) {
      return MAC_OS;
    } else {
      if (!osNameLowerCase.contains("linux")) {
        LOGGER.warn("OS name not anticipated, assuming Linux-like ping flavor: '{}'", osName);
      }
      return LINUX;
    }
  }

  public String pingCommand() {
    return this.pingCommand;
  }

  public String countFlagFormat() {
    return this.countFlagFormat;
  }

  public String sizeFlagFormat() {
    return this.sizeFlagFormat;
  }

  public String timeoutFlagFormat() {
    return this.timeoutFlagFormat;
  }

  public String countFlag(final int count) {
    if (this.countFlagFormat == null || count < 1) {
      return "";
    }
    return String.format(this.countFlagFormat, count);
  }

  public String sizeFlag(final int size) {
    if (this.sizeFlagFormat == null || size < 0) {
      return "";
    }
    return String.format(this.sizeFlagFormat, size);
  }

  public String timeoutFlag(final Duration timeout) {
    if (this.timeoutFlagFormat == null
        || timeout == null
        || this.timeoutValue.applyAsLong(timeout) < 1) {
      return "";
    }
    return String.format(this.timeoutFlagFormat, this.timeoutValue.applyAsLong(timeout));
  }

  public String lookupNamesForHostAddressesFlag(final Boolean lookupNamesForHostAddresses) {
    return this.lookupNamesForHostAddressesFlag.apply(lookupNamesForHostAddresses);
  }
}

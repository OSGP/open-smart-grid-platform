// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.shared.infra.networking.ping;

import static org.assertj.core.api.Assertions.assertThat;

import java.security.SecureRandom;
import java.time.Duration;
import java.util.Random;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;

class PingFlavorTest {

  private final Random random = new SecureRandom();

  @ParameterizedTest
  @CsvSource({"Linux, LINUX", "Mac OS X, MAC_OS", "Windows 10, WINDOWS"})
  void pingFlavorGetsPickedByOsName(final String osName, final String flavorName) {
    this.testWithOsName(
        osName,
        () -> {
          final PingFlavor pingFlavorForCurrentOs = PingFlavor.forCurrentOs();
          assertThat(pingFlavorForCurrentOs).isSameAs(PingFlavor.valueOf(flavorName));
        });
  }

  private void testWithOsName(final String osName, final Runnable test) {
    this.testWithSystemProperty("os.name", osName, test);
  }

  private void testWithSystemProperty(final String key, final String value, final Runnable test) {
    final String originalValue = System.getProperty(key);
    try {
      System.setProperty(key, value);
      test.run();
    } finally {
      if (originalValue != null) {
        System.setProperty(key, originalValue);
      }
    }
  }

  @ParameterizedTest
  @CsvSource({"LINUX, ping", "MAC_OS, ping", "WINDOWS, ping"})
  void pingFlavorGivesCorrectPingCommand(final PingFlavor pingFlavor, final String pingCommand) {
    assertThat(pingFlavor.pingCommand()).isEqualTo(pingCommand);
  }

  @ParameterizedTest
  @CsvSource({"LINUX, 2, -c 2", "MAC_OS, 1, -c 1", "WINDOWS, 3, -n 3"})
  void pingFlavorGivesCorrectCountFlag(
      final PingFlavor pingFlavor, final int count, final String countFlag) {
    assertThat(pingFlavor.countFlag(count)).isEqualTo(countFlag);
  }

  @ParameterizedTest
  @EnumSource(PingFlavor.class)
  void pingFlavorGivesEmptyFlagForZeroCountFlag(final PingFlavor pingFlavor) {
    assertThat(pingFlavor.countFlag(0)).as("%s.countFlag(0)", pingFlavor.name()).isEmpty();
  }

  @ParameterizedTest
  @EnumSource(PingFlavor.class)
  void pingFlavorGivesEmptyFlagForNonPositiveCountFlag(final PingFlavor pingFlavor) {
    final int nonPositive = this.nonPositiveInt();
    assertThat(pingFlavor.countFlag(nonPositive))
        .as("%s.countFlag(%d)", pingFlavor.name(), nonPositive)
        .isEmpty();
  }

  @ParameterizedTest
  @CsvSource({
    "LINUX, 58, -s 58",
    "LINUX, 0, -s 0",
    "MAC_OS, 27, -s 27",
    "MAC_OS, 0, -s 0",
    "WINDOWS, 64, -l 64",
    "WINDOWS, 0, -l 0"
  })
  void pingFlavorGivesCorrectSizeFlag(
      final PingFlavor pingFlavor, final int size, final String sizeFlag) {
    assertThat(pingFlavor.sizeFlag(size)).isEqualTo(sizeFlag);
  }

  @ParameterizedTest
  @EnumSource(PingFlavor.class)
  void pingFlavorGivesEmptyFlagForDefaultSizeFlag(final PingFlavor pingFlavor) {
    assertThat(pingFlavor.sizeFlag(-1)).as("%s.sizeFlag(-1)", pingFlavor.name()).isEmpty();
  }

  @ParameterizedTest
  @EnumSource(PingFlavor.class)
  void pingFlavorGivesEmptyFlagForNegativeSizeFlag(final PingFlavor pingFlavor) {
    final int negative = this.negativeInt();
    assertThat(pingFlavor.sizeFlag(negative))
        .as("%s.sizeFlag(%d)", pingFlavor.name(), negative)
        .isEmpty();
  }

  @ParameterizedTest
  @CsvSource({"LINUX, PT5S, -W 5", "MAC_OS, PT6S, -t 6", "WINDOWS, PT0.35S, -w 350"})
  void pingFlavorGivesCorrectTimeoutFlag(
      final PingFlavor pingFlavor, final Duration timeout, final String timeoutFlag) {

    assertThat(pingFlavor.timeoutFlag(timeout)).isEqualTo(timeoutFlag);
  }

  @ParameterizedTest
  @EnumSource(PingFlavor.class)
  void pingFlavorGivesEmptyFlagForNullTimeoutFlag(final PingFlavor pingFlavor) {
    assertThat(pingFlavor.timeoutFlag(null))
        .as("%s.timeoutFlag(null)", pingFlavor.name())
        .isEmpty();
  }

  @ParameterizedTest
  @EnumSource(PingFlavor.class)
  void pingFlavorGivesEmptyFlagForZeroTimeoutFlag(final PingFlavor pingFlavor) {
    final Duration zeroTimeout = Duration.ofSeconds(0);
    assertThat(pingFlavor.timeoutFlag(zeroTimeout))
        .as("%s.timeoutFlag(%s)", pingFlavor.name(), zeroTimeout)
        .isEmpty();
  }

  @ParameterizedTest
  @EnumSource(PingFlavor.class)
  void pingFlavorGivesEmptyFlagForNonPositiveTimeoutFlag(final PingFlavor pingFlavor) {
    final Duration nonPositive = Duration.ofMillis(this.nonPositiveLong());
    assertThat(pingFlavor.timeoutFlag(nonPositive))
        .as("%s.timeoutFlag(%s)", pingFlavor.name(), nonPositive)
        .isEmpty();
  }

  private int negativeInt() {
    final int result = this.nonPositiveInt();
    return result == 0 ? -1 : result;
  }

  private int nonPositiveInt() {
    final int randomInt = this.random.nextInt();
    return randomInt > 0 ? -randomInt : randomInt;
  }

  private long nonPositiveLong() {
    final long randomLong = this.random.nextLong();
    return randomLong > 0 ? -randomLong : randomLong;
  }

  @ParameterizedTest
  @EnumSource(PingFlavor.class)
  void pingFlavorGivesEmptyFlagForDefaultLookupNamesForHostAddressesFlag(
      final PingFlavor pingFlavor) {
    assertThat(pingFlavor.lookupNamesForHostAddressesFlag(null))
        .as("%s.lookupNamesForHostAddressesFlag(null)", pingFlavor.name())
        .isEmpty();
  }

  @ParameterizedTest
  @CsvSource({
    "LINUX, true, ''",
    "LINUX, false, -n",
    "MAC_OS, true, ''",
    "MAC_OS, false, -n",
    "WINDOWS, true, -a",
    "WINDOWS, false, ''"
  })
  void pingFlavorGivesCorrectLookupNamesForHostAddressesFlag(
      final PingFlavor pingFlavor,
      final Boolean lookupNames,
      final String lookupNamesForHostAddressesFlag) {

    assertThat(pingFlavor.lookupNamesForHostAddressesFlag(lookupNames))
        .isEqualTo(lookupNamesForHostAddressesFlag);
  }
}

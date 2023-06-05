// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.shared.infra.networking.ping;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class CommandBuilderTest {

  static Stream<Arguments> commandBuilderArgumentsProvider() {
    return Stream.of(
        arguments(
            PingFlavor.LINUX,
            2,
            56,
            Duration.ofSeconds(2),
            false,
            "lfenergy.org",
            Arrays.asList("ping", "-c 2", "-s 56", "-W 2", "-n", "lfenergy.org")),
        arguments(
            PingFlavor.LINUX,
            2,
            -1,
            null,
            true,
            "192.168.12.27",
            Arrays.asList("ping", "-c 2", "192.168.12.27")),
        arguments(
            PingFlavor.LINUX,
            0,
            0,
            Duration.ofMillis(2718),
            null,
            "lfenergy.org",
            Arrays.asList("ping", "-s 0", "-W 2", "lfenergy.org")),
        arguments(
            PingFlavor.MAC_OS,
            5,
            0,
            null,
            null,
            "192.128.71.129",
            Arrays.asList("ping", "-c 5", "-s 0", "192.128.71.129")),
        arguments(
            PingFlavor.MAC_OS,
            0,
            -1,
            Duration.ofSeconds(37),
            true,
            "127.0.0.1",
            Arrays.asList("ping", "-t 37", "127.0.0.1")),
        arguments(
            PingFlavor.MAC_OS,
            5,
            24,
            Duration.ofSeconds(37),
            false,
            "127.0.0.1",
            Arrays.asList("ping", "-c 5", "-s 24", "-t 37", "-n", "127.0.0.1")),
        arguments(
            PingFlavor.WINDOWS,
            3,
            0,
            Duration.ofMillis(3141),
            true,
            "opensmartgridplatform.org",
            Arrays.asList("ping", "-n 3", "-l 0", "-w 3141", "-a", "opensmartgridplatform.org")),
        arguments(
            PingFlavor.WINDOWS,
            3,
            0,
            null,
            false,
            "opensmartgridplatform.org",
            Arrays.asList("ping", "-n 3", "-l 0", "opensmartgridplatform.org")),
        arguments(
            PingFlavor.WINDOWS,
            0,
            -1,
            Duration.ofMillis(1618),
            null,
            "192.168.1.81",
            Arrays.asList("ping", "-w 1618", "192.168.1.81")));
  }

  @ParameterizedTest
  @MethodSource("commandBuilderArgumentsProvider")
  void commandBuilderGivesCorrectCommands(
      final PingFlavor pingFlavor,
      final int count,
      final int size,
      final Duration timeout,
      final Boolean lookupNamesForHostAddresses,
      final String destination,
      final List<String> expectedCommands) {

    final List<String> actualCommands =
        new CommandBuilder()
            .withPingFlavor(pingFlavor)
            .withCount(count)
            .withSize(size)
            .withTimeout(timeout)
            .withLookupNamesForHostAddresses(lookupNamesForHostAddresses)
            .withDestination(destination)
            .commands();

    assertThat(actualCommands).isEqualTo(expectedCommands);
  }
}

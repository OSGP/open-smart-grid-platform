// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.shared.infra.networking.ping;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.opensmartgridplatform.shared.infra.networking.ping.Pinger.CommandExecutor;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class PingerTest {

  @Mock private CommandExecutor commandExecutor;

  @Test
  void pingReturnsTrueWhenReceivingPackages() throws Exception {
    this.whenPingCommandIsExecutedReceivingPackages();
    final Pinger pinger = this.newPinger(1, 0, Duration.ofSeconds(1), false);

    final boolean pingResult = pinger.ping("localhost");

    assertThat(pingResult).isTrue();
  }

  @Test
  void pingReturnsFalseWhenNotReceivingPackages() throws Exception {
    this.whenPingCommandIsExecutedWithoutReceivingPackages();
    final Pinger pinger = this.newPinger(100, 24, Duration.ofSeconds(15), true);

    final boolean pingResult = pinger.ping("192.168.5.3");

    assertThat(pingResult).isFalse();
  }

  @Test
  void pingReturnsFalseOnExceptionExecutingPing() throws Exception {
    this.whenAnExceptionIsThrownExecutingThePingCommand();
    final Pinger pinger = this.newPinger(0, -1, Duration.ofSeconds(7), null);

    final boolean pingResult = pinger.ping("192.168.1.1");

    assertThat(pingResult).isFalse();
  }

  @Test
  void pingReturnsFalseOnExceptionProcessingPingInputBeforeTimeout() throws Exception {
    this.whenAnExceptionIsThrownProcessingInputFromThePingCommandBeforeTimeout();
    final Pinger pinger = this.newPinger(2, 100, Duration.ofSeconds(8), false);

    final boolean pingResult = pinger.ping("192.168.1.1");

    assertThat(pingResult).isFalse();
  }

  @Test
  void pingReturnsFalseWhenPingResultIsNotAvailableBeforeTimeout() throws Exception {
    this.whenThePingCommandResultsAreNotObtainedBeforeTheTimeout();
    final Pinger pinger = this.newPinger(5, 56, Duration.ofSeconds(12), null);

    final boolean pingResult = pinger.ping("192.168.2.3");

    assertThat(pingResult).isFalse();
  }

  private Pinger newPinger(
      final int count,
      final int size,
      final Duration timeout,
      final Boolean lookupNamesForHostAddresses) {
    final Pinger pinger = new Pinger(count, size, timeout, lookupNamesForHostAddresses);
    ReflectionTestUtils.setField(pinger, "commandExecutor", this.commandExecutor);
    return pinger;
  }

  private void whenPingCommandIsExecutedReceivingPackages() throws Exception {
    when(this.commandExecutor.execute(anyList(), any(Duration.class)))
        .thenReturn(
            Arrays.asList(
                "PING 127.0.0.1 (127.0.0.1) 56(84) bytes of data.",
                "64 bytes from 127.0.0.1: icmp_seq=1 ttl=64 time=0.026 ms",
                "",
                "--- 127.0.0.1 ping statistics ---",
                "1 packets transmitted, 1 received, 0% packet loss, time 0ms",
                "rtt min/avg/max/mdev = 0.026/0.026/0.026/0.000 ms"));
  }

  private void whenPingCommandIsExecutedWithoutReceivingPackages() throws Exception {
    when(this.commandExecutor.execute(anyList(), any(Duration.class)))
        .thenReturn(
            Arrays.asList(
                "PING 192.168.5.3 (192.168.5.3) 56(84) bytes of data.",
                "From 219.83.2.211 icmp_seq=1 Destination Net Unreachable",
                "",
                "--- 192.168.5.3 ping statistics ---",
                "1 packets transmitted, 0 received, +1 errors, 100% packet loss, time 0ms",
                ""));
  }

  private void whenAnExceptionIsThrownExecutingThePingCommand() throws Exception {
    when(this.commandExecutor.execute(anyList(), any(Duration.class))).thenThrow(IOException.class);
  }

  private void whenAnExceptionIsThrownProcessingInputFromThePingCommandBeforeTimeout()
      throws Exception {
    when(this.commandExecutor.execute(anyList(), any(Duration.class)))
        .thenThrow(ExecutionException.class);
  }

  private void whenThePingCommandResultsAreNotObtainedBeforeTheTimeout() throws Exception {
    when(this.commandExecutor.execute(anyList(), any(Duration.class)))
        .thenThrow(TimeoutException.class);
  }

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
        new Pinger.CommandBuilder()
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

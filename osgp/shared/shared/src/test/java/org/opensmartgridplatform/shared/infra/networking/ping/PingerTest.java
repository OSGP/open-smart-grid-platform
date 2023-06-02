//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.shared.infra.networking.ping;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.time.Duration;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PingerTest {

  @Mock private CommandExecutor commandExecutor;

  @Test
  void pingReturnsTrueWhenReceivingPackages() throws Exception {
    this.whenPingCommandIsExecutedReceivingPackages();
    final Pinger pinger = new Pinger(1, 0, Duration.ofSeconds(1), false, this.commandExecutor);

    final boolean pingResult = pinger.ping("localhost");

    assertThat(pingResult).isTrue();
  }

  @Test
  void pingReturnsFalseWhenNotReceivingPackages() throws Exception {
    this.whenPingCommandIsExecutedWithoutReceivingPackages();
    final Pinger pinger = new Pinger(100, 24, Duration.ofSeconds(15), true, this.commandExecutor);

    final boolean pingResult = pinger.ping("192.168.5.3");

    assertThat(pingResult).isFalse();
  }

  @Test
  void pingReturnsFalseOnExceptionExecutingPing() throws Exception {
    this.whenAnExceptionIsThrownExecutingThePingCommand();
    final Pinger pinger = new Pinger(0, -1, Duration.ofSeconds(7), null, this.commandExecutor);

    final boolean pingResult = pinger.ping("192.168.1.1");

    assertThat(pingResult).isFalse();
  }

  @Test
  void pingReturnsFalseOnExceptionProcessingPingInputBeforeTimeout() throws Exception {
    this.whenAnExceptionIsThrownProcessingInputFromThePingCommandBeforeTimeout();
    final Pinger pinger = new Pinger(2, 100, Duration.ofSeconds(8), false, this.commandExecutor);

    final boolean pingResult = pinger.ping("192.168.1.1");

    assertThat(pingResult).isFalse();
  }

  @Test
  void pingReturnsFalseWhenPingResultIsNotAvailableBeforeTimeout() throws Exception {
    this.whenThePingCommandResultsAreNotObtainedBeforeTheTimeout();
    final Pinger pinger = new Pinger(5, 56, Duration.ofSeconds(12), null, this.commandExecutor);

    final boolean pingResult = pinger.ping("192.168.2.3");

    assertThat(pingResult).isFalse();
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
}

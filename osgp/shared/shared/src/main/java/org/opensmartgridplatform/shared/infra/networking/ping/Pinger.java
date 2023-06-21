// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.shared.infra.networking.ping;

import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.function.Predicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Pinger {

  private static final Logger LOGGER = LoggerFactory.getLogger(Pinger.class);

  private final CommandExecutor commandExecutor;
  private final int count;
  private final int size;
  private final Duration timeout;

  private final Boolean lookupNamesForHostAddresses;

  public Pinger(
      final int count,
      final int size,
      final Duration timeout,
      final Boolean lookupNamesForHostAddresses) {
    this(count, size, timeout, lookupNamesForHostAddresses, new CommandExecutor());
  }

  Pinger(
      final int count,
      final int size,
      final Duration timeout,
      final Boolean lookupNamesForHostAddresses,
      final CommandExecutor commandExecutor) {
    this.count = count;
    this.size = size;
    this.timeout = timeout;
    this.lookupNamesForHostAddresses = lookupNamesForHostAddresses;
    this.commandExecutor = commandExecutor;
  }

  public boolean ping(final String destination) {
    final CommandBuilder commandBuilder =
        new CommandBuilder()
            .withCount(this.count)
            .withSize(this.size)
            .withTimeout(this.timeout)
            .withLookupNamesForHostAddresses(this.lookupNamesForHostAddresses)
            .withDestination(destination);
    LOGGER.debug("About to ping based on {}", commandBuilder);
    final List<String> commands = commandBuilder.commands();
    try {
      final List<String> inputLines;
      if (this.timeout == null || this.timeout.isNegative() || this.timeout.isZero()) {
        inputLines = this.commandExecutor.execute(commands);
      } else {
        inputLines = this.commandExecutor.execute(commands, this.timeout);
      }
      final Predicate<String> probableEchoReplyWithTtl =
          s -> s.toLowerCase(Locale.UK).contains("ttl=");
      final boolean pingInputLikelyHasEchoReplies =
          inputLines.stream().anyMatch(probableEchoReplyWithTtl);
      LOGGER.debug("Done pinging, found input with TTL values: {}", pingInputLikelyHasEchoReplies);
      return pingInputLikelyHasEchoReplies;
    } catch (final IOException e) {
      LOGGER.error("Exception occurred while executing ping command", e);
    } catch (final TimeoutException e) {
      LOGGER.info("Timeout executing ping command");
    } catch (final RejectedExecutionException e) {
      LOGGER.error("Exception occurred preventing processing the output of the ping command", e);
    } catch (final ExecutionException e) {
      LOGGER.error("Exception occurred while processing input from the executed ping command", e);
    }
    return false;
  }
}

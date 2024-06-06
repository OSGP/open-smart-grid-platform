// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.shared.infra.networking.ping;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.SequenceInputStream;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Predicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Pinger {

  private static final Logger LOGGER = LoggerFactory.getLogger(Pinger.class);

  private final CommandExecutor commandExecutor = new CommandExecutor();
  private final int count;
  private final int size;
  private final Duration timeout;

  private final Boolean lookupNamesForHostAddresses;

  public Pinger(
      final int count,
      final int size,
      final Duration timeout,
      final Boolean lookupNamesForHostAddresses) {
    this.count = count;
    this.size = size;
    this.timeout = timeout;
    this.lookupNamesForHostAddresses = lookupNamesForHostAddresses;
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

  /*
   * An external SIG analysis, recommended to make this not a public class.
   * This class could be vulnerable to arbitrary command execution.
   * The reason is that this class executes a command on the operating system.
   */
  class CommandExecutor {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommandExecutor.class);

    private static final long AWAIT_TERMINATION_IN_SEC = 5;

    public List<String> execute(final List<String> commands, final Duration timeout)
        throws IOException, TimeoutException, ExecutionException {

      final String commandLine = this.commandLine(commands);
      final Process process = this.start(commands);

      final ExecutorService executorService = Executors.newSingleThreadExecutor();
      final Future<List<String>> inputLines =
          executorService.submit(() -> this.readLinesFromInput(process));
      try {
        return inputLines.get(timeout.toMillis(), TimeUnit.MILLISECONDS);
      } catch (final TimeoutException e) {
        inputLines.cancel(true);
        throw e;
      } catch (final InterruptedException e) {
        LOGGER.warn(
            "Reading input lines from executed process was interrupted: \"{}\"", commandLine, e);
      } finally {
        this.shutdownAndAwaitTermination(executorService);
        this.destroyProcess(process, commands);
      }
      return Collections.emptyList();
    }

    private void destroyProcess(final Process process, final List<String> commands) {
      process.destroy();
      if (process.isAlive()) {
        LOGGER.debug("Destroy the process running \"{}\"", commands);
        process.destroyForcibly();
      } else {
        LOGGER.debug(
            "Process running \"{}\" ended with exit value: {}", commands, process.exitValue());
      }
    }

    void shutdownAndAwaitTermination(final ExecutorService executorService) {
      executorService.shutdown();
      try {
        if (!executorService.awaitTermination(AWAIT_TERMINATION_IN_SEC, TimeUnit.SECONDS)) {
          executorService.shutdownNow();
          if (!executorService.awaitTermination(AWAIT_TERMINATION_IN_SEC, TimeUnit.SECONDS)) {
            LOGGER.error("Pool did not terminate");
          }
        }
      } catch (final InterruptedException ex) {
        executorService.shutdownNow();
        Thread.currentThread().interrupt();
      }
    }

    private String commandLine(final List<String> commands) {
      return String.join(" ", commands);
    }

    public List<String> execute(final List<String> commands) throws IOException {
      final Process process = this.start(commands);
      try {
        return this.readLinesFromInput(process);
      } finally {
        if (process.isAlive()) {
          final String commandLine = this.commandLine(commands);
          LOGGER.debug("Destroy the process running \"{}\"", commandLine);
          process.destroyForcibly();
        } else {
          LOGGER.debug(
              "Process running \"{}\" ended with exit value: {}", commands, process.exitValue());
        }
      }
    }

    private Process start(final List<String> commands) throws IOException {
      if (LOGGER.isDebugEnabled()) {
        LOGGER.debug("About to execute: \"{}\"", this.commandLine(commands));
      }
      return new ProcessBuilder(commands).start();
    }

    public List<String> readLinesFromInput(final Process process) throws IOException {
      final List<String> lines = new ArrayList<>();
      try (final BufferedReader reader =
          new BufferedReader(
              new InputStreamReader(
                  new SequenceInputStream(process.getInputStream(), process.getErrorStream())))) {
        String line = reader.readLine();
        while (line != null) {
          LOGGER.debug("Input line: \"{}\"", line);
          lines.add(line);
          line = reader.readLine();
        }
      }
      return lines;
    }
  }

  static class CommandBuilder {

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

    public CommandBuilder withLookupNamesForHostAddresses(
        final Boolean lookupNamesForHostAddresses) {
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
}

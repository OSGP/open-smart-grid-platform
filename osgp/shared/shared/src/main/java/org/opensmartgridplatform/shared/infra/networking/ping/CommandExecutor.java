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
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CommandExecutor {

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

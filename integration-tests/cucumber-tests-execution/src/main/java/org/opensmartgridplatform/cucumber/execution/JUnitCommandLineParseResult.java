// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.cucumber.execution;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.internal.Classes;
import org.junit.runner.Computer;
import org.junit.runner.FilterFactory.FilterNotCreatedException;
import org.junit.runner.Request;
import org.junit.runner.manipulation.Filter;
import org.junit.runners.model.InitializationError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JUnitCommandLineParseResult {

  private static final Logger LOGGER = LoggerFactory.getLogger(JUnitCommandLineParseResult.class);

  private static final String DASH_DASH = "--";
  private static final String DASH_DASH_FILTER = "--filter";

  private final List<String> filterSpecs = new ArrayList<>();
  private final List<Class<?>> classes = new ArrayList<>();
  private final List<Throwable> parserErrors = new ArrayList<>();

  /** Do not use. Testing purposes only. */
  JUnitCommandLineParseResult() {}

  /** Returns filter specs parsed from command line. */
  public List<String> getFilterSpecs() {
    return Collections.unmodifiableList(this.filterSpecs);
  }

  /** Returns test classes parsed from command line. */
  public List<Class<?>> getClasses() {
    return Collections.unmodifiableList(this.classes);
  }

  /**
   * Parses the arguments.
   *
   * @param args Arguments
   */
  public static JUnitCommandLineParseResult parse(final String[] args) {
    final JUnitCommandLineParseResult result = new JUnitCommandLineParseResult();

    result.parseArgs(args);

    return result;
  }

  private void parseArgs(final String[] args) {
    this.parseParameters(this.parseOptions(args));
  }

  String[] parseOptions(final String... args) {
    for (int i = 0; i != args.length; i += 2) {
      final String arg = args[i];
      final int valueIndex = i + 1;

      LOGGER.info(" argument {}:{}", i, arg);

      if (DASH_DASH.equals(arg)) {
        return Arrays.copyOfRange(args, valueIndex, args.length);
      } else if (arg.startsWith(DASH_DASH)) {
        if (!this.parseFilterSpecs(arg, valueIndex, args)) {
          break;
        }
      } else {
        return Arrays.copyOfRange(args, i, args.length);
      }
    }

    return new String[] {};
  }

  private boolean parseFilterSpecs(final String arg, final int valueIndex, final String... args) {
    if (arg.startsWith(DASH_DASH_FILTER)) {
      String filterSpec = "";
      if (DASH_DASH_FILTER.equals(arg)) {
        if (valueIndex < args.length) {
          filterSpec = args[valueIndex];
        } else {
          this.parserErrors.add(new CommandLineParserError(arg + " value not specified"));
          return false;
        }
      } else {
        filterSpec = arg.substring(arg.indexOf('=') + 1);
      }
      this.filterSpecs.add(filterSpec);
    } else {
      this.parserErrors.add(
          new CommandLineParserError("JUnit knows nothing about the " + arg + " option"));
    }

    return true;
  }

  void parseParameters(final String[] args) {
    for (final String arg : args) {
      try {
        this.classes.add(Classes.getClass(arg));
      } catch (final ClassNotFoundException e) {
        this.parserErrors.add(
            new IllegalArgumentException("Could not find class [" + arg + "]", e));
      }
    }
  }

  private Request errorReport(final Throwable cause) {
    return Request.errorReport(JUnitCommandLineParseResult.class, cause);
  }

  /**
   * Creates a {@link Request}.
   *
   * @param computer {@link Computer} to be used.
   */
  public Request createRequest(final Computer computer) {
    if (this.parserErrors.isEmpty()) {
      final Request request =
          Request.classes(computer, this.classes.toArray(new Class<?>[this.classes.size()]));
      return this.applyFilterSpecs(request);
    } else {
      return this.errorReport(new InitializationError(this.parserErrors));
    }
  }

  private Request applyFilterSpecs(Request request) {
    try {
      for (final String filterSpec : this.filterSpecs) {
        final Filter filter = FilterFactories.createFilterFromFilterSpec(request, filterSpec);
        request = request.filterWith(filter);
      }
      return request;
    } catch (final FilterNotCreatedException e) {
      return this.errorReport(e);
    }
  }

  /** Exception used if there's a problem parsing the command line. */
  public static class CommandLineParserError extends Exception {
    private static final long serialVersionUID = 1L;

    public CommandLineParserError(final String message) {
      super(message);
    }
  }
}

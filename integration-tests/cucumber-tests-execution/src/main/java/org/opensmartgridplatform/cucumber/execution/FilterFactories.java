// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.cucumber.execution;

import org.junit.internal.Classes;
import org.junit.runner.Description;
import org.junit.runner.FilterFactory;
import org.junit.runner.FilterFactory.FilterNotCreatedException;
import org.junit.runner.FilterFactoryParams;
import org.junit.runner.Request;
import org.junit.runner.manipulation.Filter;

/** Utility class whose methods create a {@link FilterFactory}. */
public final class FilterFactories {

  private FilterFactories() {
    // Private constructor to prevent instantiation of this class.
  }

  /**
   * Creates a {@link Filter}.
   *
   * <p>A filter specification is of the form "package.of.FilterFactory=args-to-filter-factory" or
   * "package.of.FilterFactory".
   *
   * @param request the request that will be filtered
   * @param filterSpec the filter specification
   * @throws org.junit.runner.FilterFactory.FilterNotCreatedException
   */
  public static Filter createFilterFromFilterSpec(final Request request, final String filterSpec)
      throws FilterFactory.FilterNotCreatedException {
    final Description topLevelDescription = request.getRunner().getDescription();
    String[] tuple;

    if (filterSpec.contains("=")) {
      tuple = filterSpec.split("=", 2);
    } else {
      tuple = new String[] {filterSpec, ""};
    }

    return createFilter(tuple[0], new FilterFactoryParams(topLevelDescription, tuple[1]));
  }

  /**
   * Creates a {@link Filter}.
   *
   * @param filterFactoryFqcn The fully qualified class name of the {@link FilterFactory}
   * @param params The arguments to the {@link FilterFactory}
   */
  public static Filter createFilter(
      final String filterFactoryFqcn, final FilterFactoryParams params)
      throws FilterFactory.FilterNotCreatedException {
    final FilterFactory filterFactory = createFilterFactory(filterFactoryFqcn);

    return filterFactory.createFilter(params);
  }

  /**
   * Creates a {@link Filter}.
   *
   * @param filterFactoryClass The class of the {@link FilterFactory}
   * @param params The arguments to the {@link FilterFactory}
   */
  public static Filter createFilter(
      final Class<? extends FilterFactory> filterFactoryClass, final FilterFactoryParams params)
      throws FilterFactory.FilterNotCreatedException {
    final FilterFactory filterFactory = createFilterFactory(filterFactoryClass);

    return filterFactory.createFilter(params);
  }

  static FilterFactory createFilterFactory(final String filterFactoryFqcn)
      throws FilterNotCreatedException {
    Class<? extends FilterFactory> filterFactoryClass;

    try {
      filterFactoryClass = Classes.getClass(filterFactoryFqcn).asSubclass(FilterFactory.class);
    } catch (final Exception e) {
      throw new FilterNotCreatedException(e);
    }

    return createFilterFactory(filterFactoryClass);
  }

  static FilterFactory createFilterFactory(final Class<? extends FilterFactory> filterFactoryClass)
      throws FilterNotCreatedException {
    try {
      return filterFactoryClass.getConstructor().newInstance();
    } catch (final Exception e) {
      throw new FilterNotCreatedException(e);
    }
  }
}

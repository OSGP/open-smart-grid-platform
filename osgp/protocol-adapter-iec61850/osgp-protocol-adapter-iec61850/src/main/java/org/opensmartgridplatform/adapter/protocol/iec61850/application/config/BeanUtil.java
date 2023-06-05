// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.iec61850.application.config;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

@Service
public class BeanUtil implements ApplicationContextAware {

  private static ApplicationContext context;

  public static <T> T getBean(final Class<T> beanClass) {
    return context.getBean(beanClass);
  }

  public static <T> T getBeanByName(final String beanName, final Class<T> beanClass) {
    return context.getBean(beanName, beanClass);
  }

  @SuppressWarnings(
      "squid:S2696") // setApplicationContext is an overriding method that cannot be made static and
  // context has to be a static variable to be able to use it statically in the
  // getBean and getBeanByName methods
  @Override
  public void setApplicationContext(final ApplicationContext applicationContext) {
    context = applicationContext;
  }
}

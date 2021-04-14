/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * <p>Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * <p>http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.iec61850.application.config;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

@Service
public class BeanUtil implements ApplicationContextAware {

  private static ApplicationContext context;

  @Override
  public void setApplicationContext(final ApplicationContext applicationContext) {
    context = applicationContext;
  }

  public static <T> T getBean(final Class<T> beanClass) {
    return context.getBean(beanClass);
  }

  public static <T> T getBeanByName(final String beanName, final Class<T> beanClass) {
    return context.getBean(beanName, beanClass);
  }
}

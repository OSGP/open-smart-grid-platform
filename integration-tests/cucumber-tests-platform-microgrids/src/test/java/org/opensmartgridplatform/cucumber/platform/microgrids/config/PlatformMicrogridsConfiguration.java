/*
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.platform.microgrids.config;

import org.opensmartgridplatform.cucumber.platform.config.AbstractPlatformApplicationConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:cucumber-tests-platform-microgrids.properties")
@PropertySource(
    value = "file:/etc/osp/test/global-cucumber.properties",
    ignoreResourceNotFound = true)
@PropertySource(
    value = "file:/etc/osp/test/cucumber-tests-platform-microgrids.properties",
    ignoreResourceNotFound = true)
public class PlatformMicrogridsConfiguration extends AbstractPlatformApplicationConfiguration {}

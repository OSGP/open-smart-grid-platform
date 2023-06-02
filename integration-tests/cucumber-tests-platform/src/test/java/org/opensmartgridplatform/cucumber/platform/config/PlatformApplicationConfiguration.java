//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.cucumber.platform.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/** Base class for the application configuration. */
@Configuration
@PropertySource("classpath:cucumber-tests-platform.properties")
@PropertySource(
    value = "file:/etc/osp/test/global-cucumber.properties",
    ignoreResourceNotFound = true)
@PropertySource(
    value = "file:/etc/osp/test/cucumber-tests-platform.properties",
    ignoreResourceNotFound = true)
public class PlatformApplicationConfiguration extends AbstractPlatformApplicationConfiguration {}

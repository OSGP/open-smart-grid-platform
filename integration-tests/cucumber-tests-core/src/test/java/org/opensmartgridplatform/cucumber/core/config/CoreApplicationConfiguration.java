// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.cucumber.core.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/** Base class for the application configuration. */
@Configuration
@PropertySource("classpath:cucumber-tests-core.properties")
@PropertySource(
    value = "file:/etc/osp/test/global-cucumber.properties",
    ignoreResourceNotFound = true)
@PropertySource(
    value = "file:/etc/osp/test/cucumber-tests-core.properties",
    ignoreResourceNotFound = true)
public class CoreApplicationConfiguration extends BaseApplicationConfiguration {}

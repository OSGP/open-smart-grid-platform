//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.cucumber.platform.publiclighting.config;

import org.opensmartgridplatform.cucumber.platform.config.AbstractPlatformApplicationConfiguration;
import org.opensmartgridplatform.cucumber.protocol.iec60870.config.Iec60870Config;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;

@Configuration
@Import(Iec60870Config.class)
@PropertySource("classpath:cucumber-tests-platform-publiclighting.properties")
@PropertySource(
    value = "file:/etc/osp/test/global-cucumber.properties",
    ignoreResourceNotFound = true)
@PropertySource(
    value = "file:/etc/osp/test/cucumber-tests-platform-publiclighting.properties",
    ignoreResourceNotFound = true)
public class PublicLightingConfiguration extends AbstractPlatformApplicationConfiguration {}

/*
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.protocol.iec60870.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;

@Configuration
@Import({Iec60870MockServerConfig.class, Iec60870PersistenceConfig.class})
@PropertySource("classpath:cucumber-tests-protocol-iec60870.properties")
@PropertySource(
    value = "file:/etc/osp/test/global-cucumber.properties",
    ignoreResourceNotFound = true)
@PropertySource(
    value = "file:/etc/osp/test/cucumber-tests-protocol-iec60870.properties",
    ignoreResourceNotFound = true)
public class Iec60870Config {}

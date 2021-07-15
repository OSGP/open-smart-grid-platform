/*
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.platform.distributionautomation;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import io.cucumber.junit.CucumberOptions.SnippetType;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
    features = {
      "classpath:features/osgp-adapter-ws-distributionautomation",
      "classpath:features/low-voltage"
    },
    tags = {"not @Skip", "not @NightlyBuildOnly"},
    glue = {
      "classpath:org.opensmartgridplatform.cucumber.platform.glue",
      "classpath:org.opensmartgridplatform.cucumber.platform.common.glue",
      "classpath:org.opensmartgridplatform.cucumber.platform.distributionautomation.glue",
      "classpath:org.opensmartgridplatform.cucumber.protocol.iec60870.glue"
    },
    plugin = {
      "pretty",
      "html:target/output/Cucumber-report",
      "html:target/output/Cucumber-html-report.html",
      "json:target/output/cucumber.json"
    },
    snippets = SnippetType.CAMELCASE,
    strict = true,
    dryRun = false)
public class AcceptanceTests {}

// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.cucumber.platform.microgrids;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import io.cucumber.junit.CucumberOptions.SnippetType;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
    features = {
      "classpath:features/osgp-adapter-ws-microgrids",
      "classpath:features/osgp-adapter-ws-core"
    },
    tags = {"not @Skip", "not @NightlyBuildOnly"},
    glue = {
      "classpath:org.opensmartgridplatform.cucumber.platform.glue",
      "classpath:org.opensmartgridplatform.cucumber.platform.common.glue",
      "classpath:org.opensmartgridplatform.cucumber.platform.microgrids.glue"
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

// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.cucumber.platform.publiclighting;

import io.cucumber.core.options.Constants;
import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("org/opensmartgridplatform/cucumber/platform/publiclighting/glue/steps")
@ConfigurationParameter(
    key = Constants.FEATURES_PROPERTY_NAME,
    value = "classpath:features/publiclighting")
@ConfigurationParameter(
    key = Constants.FILTER_TAGS_PROPERTY_NAME,
    value = "not @Skip or not @NightlyBuildOnly")
@ConfigurationParameter(
    key = Constants.GLUE_PROPERTY_NAME,
    value =
        "classpath:org.opensmartgridplatform.cucumber.platform.glue, classpath:org.opensmartgridplatform.cucumber.platform.common.glue, classpath:org.opensmartgridplatform.cucumber.platform.publiclighting.glue, classpath:org.opensmartgridplatform.cucumber.protocol.iec60870.glue")
@ConfigurationParameter(
    key = Constants.PLUGIN_PROPERTY_NAME,
    value =
        "pretty, html:target/output/osgp-cucumber-tests-platform-publiclighting/Cucumber-report, html:target/output/osgp-cucumber-tests-platform-publiclighting/Cucumber-html-report.html, json:target/output/osgp-cucumber-tests-platform-publiclighting/cucumber.json")
@ConfigurationParameter(key = Constants.SNIPPET_TYPE_PROPERTY_NAME, value = "CAMELCASE")
public class AcceptanceTests {}

// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.cucumber.platform.common;

import io.cucumber.core.options.Constants;
import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("org/opensmartgridplatform/cucumber/platform/common/glue/steps")
@ConfigurationParameter(key = Constants.FEATURES_PROPERTY_NAME, value = "classpath:features/common")
@ConfigurationParameter(
    key = Constants.FILTER_TAGS_PROPERTY_NAME,
    value = "@SetDeviceLifecycleStatus or @ResendNotifications")
@ConfigurationParameter(
    key = Constants.GLUE_PROPERTY_NAME,
    value =
        "classpath:org.opensmartgridplatform.cucumber.platform.glue, classpath:org.opensmartgridplatform.cucumber.platform.common.glue")
@ConfigurationParameter(
    key = Constants.PLUGIN_PROPERTY_NAME,
    value =
        "pretty, html:target/output/osgp-cucumber-tests-platform-common/Cucumber-report, html:target/output/osgp-cucumber-tests-platform-common/Cucumber-html-report.html, json:target/output/osgp-cucumber-tests-platform-common/cucumber.json")
@ConfigurationParameter(key = Constants.SNIPPET_TYPE_PROPERTY_NAME, value = "CAMELCASE")
public class AcceptanceTests {}

/**
 * Copyright 2016 Smart Society Services B.V.
 */
package com.alliander.osgp.platform.dlms.cucumber;

import org.junit.runner.RunWith;

import cucumber.api.CucumberOptions;
import cucumber.api.SnippetType;
import cucumber.api.junit.Cucumber;

@RunWith(Cucumber.class)
@CucumberOptions(
		features = "src/test/resources/features",
		tags = { "@TEST"}, // "~@SKIP" },
		plugin = {
				"pretty",
				"html:target/output/Cucumber-report",
				"html:target/output/Cucumber-html-report.html" },
		snippets = SnippetType.CAMELCASE)
public class AcceptanceTests {

}

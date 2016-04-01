package com.alliander.osgp.platform.cucumber;

import org.junit.runner.RunWith;

import cucumber.api.CucumberOptions;
import cucumber.api.SnippetType;
import cucumber.api.junit.Cucumber;

@RunWith(Cucumber.class)
@CucumberOptions(features = "src/test/resources/", tags = {}, plugin = { "pretty", "html:output/Cucumber-report",
        "html:output/Cucumber-html-report.html" }, snippets = SnippetType.CAMELCASE)
public class RunCukesTest {

}
package com.alliander.osgp.cucumber.platform.dlms.glue.steps.ws.smartmetering.smartmeteringbundle;

import org.junit.runner.RunWith;

import cucumber.api.CucumberOptions;
import cucumber.api.SnippetType;
import cucumber.api.junit.Cucumber;

@RunWith(Cucumber.class)
@CucumberOptions(features = "src/test/resources/features/osgp-adapter-ws-smartmetering/SmartMeteringBundle.feature", tags = {
        "~@Skip" }, glue = { "classpath:com.alliander.osgp.cucumber.platform.glue",
                "classpath:com.alliander.osgp.cucumber.platform.dlms.glue" }, plugin = { "pretty",
                        "html:target/output/Cucumber-report", "html:target/output/Cucumber-html-report.html",
                        "json:target/output/cucumber.json" }, snippets = SnippetType.CAMELCASE, dryRun = false)
public class BundleTest {

}

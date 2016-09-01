package com.alliander.osgp.platform.cucumber.steps.webapps;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

import cucumber.api.java.en.Given;

public class webappSteps {
	WebDriver driver = null;
	
	@Given("^I navigated to Google$")
	public void GivenINavigatedToGoogle() {
		driver = new FirefoxDriver();
		driver.navigate().to("http://localhost/html/");
	}
}

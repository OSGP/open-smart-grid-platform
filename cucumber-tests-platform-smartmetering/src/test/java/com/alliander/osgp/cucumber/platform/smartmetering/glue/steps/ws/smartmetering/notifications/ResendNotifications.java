package com.alliander.osgp.cucumber.platform.smartmetering.glue.steps.ws.smartmetering.notifications;

import cucumber.api.DataTable;
import cucumber.api.PendingException;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class ResendNotifications {

	@When("^OSGP checks for which response data a notification has to be resend$")
	public void osgpChecksForWhichResponseDataANotificationHasToBeResend() throws Throwable {
		// Do nothing - scheduled task runs automatically
	}

	@Then("^a notification is sent$")
	public void aNotificationIsSent(DataTable arg1) throws Throwable {
	    // Write code here that turns the phrase above into concrete actions
	    // For automatic transformation, change DataTable to one of
	    // List<YourType>, List<List<E>>, List<Map<K,V>> or Map<K,V>.
	    // E,K,V must be a scalar (String, Integer, Date, enum etc)
	    throw new PendingException();
	}

	@Then("^a record in the response_data table of the database has values$")
	public void recordInTheResponseDataTableOfTheAdapterDatabaseHasValues(DataTable arg1) throws Throwable {
	    // Write code here that turns the phrase above into concrete actions
	    // For automatic transformation, change DataTable to one of
	    // List<YourType>, List<List<E>>, List<Map<K,V>> or Map<K,V>.
	    // E,K,V must be a scalar (String, Integer, Date, enum etc)
	    throw new PendingException();
	}

	@Then("^no notification is sent$")
	public void noNotificationIsSent() throws Throwable {
	    // Write code here that turns the phrase above into concrete actions
	    throw new PendingException();
	}
}

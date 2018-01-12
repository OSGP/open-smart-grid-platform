package com.alliander.osgp.cucumber.platform.microgrids.glue.steps.ws.microgrids.notifications;

import static org.junit.Assert.assertEquals;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.alliander.osgp.adapter.ws.domain.entities.ResponseData;
import com.alliander.osgp.adapter.ws.domain.repositories.ResponseDataRepository;
import com.alliander.osgp.cucumber.platform.PlatformKeys;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class ResendNotifications {
	
	@Autowired
	private ResponseDataRepository responseDataRespository;

	@When("^the missed notification is resend$")
	public void theMissedNotificationIsResend(final Map<String, String> settings) throws Throwable {
		// Do nothing - scheduled task runs automatically
	}
	
	@When("^no notification is resend$")
	public void noNotificationIsResend() throws Throwable {
		// Do nothing - scheduled task runs automatically
	}
	
	@Then("^a record in the response_data table of the database has values$")
	public void recordInTheResponseDataTableOfTheAdapterDatabaseHasValues(final Map<String, String> settings)
			throws Throwable {
		String correlationUid = settings.get(PlatformKeys.KEY_CORRELATION_UID);
		ResponseData responseData = this.responseDataRespository.findByCorrelationUid(correlationUid);

		int maxtime = 120000;
		int timeout = 500;
		int initial_timeout = 60000; //needed to make sure the ResendNotificationJob has at least runned once
		Thread.sleep(initial_timeout);
		for (int delayedtime = 0; delayedtime < maxtime; delayedtime += timeout) {
			Thread.sleep(timeout);
			responseData = this.responseDataRespository.findByCorrelationUid(correlationUid);
			if (settings.get(PlatformKeys.KEY_NUMBER_OF_NOTIFICATIONS_SEND)
					.equals(responseData.getNumberOfNotificationsSend().toString())) {
				break;
			}
		}
		assertEquals("NumberOfNotificationsSend is not as expected",
				settings.get(PlatformKeys.KEY_NUMBER_OF_NOTIFICATIONS_SEND),
				responseData.getNumberOfNotificationsSend().toString());
		assertEquals("MessageType is not as expected", settings.get(PlatformKeys.KEY_MESSAGE_TYPE),
				responseData.getMessageType());
	}
}

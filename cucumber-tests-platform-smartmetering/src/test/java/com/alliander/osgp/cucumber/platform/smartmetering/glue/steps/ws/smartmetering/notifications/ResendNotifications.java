/**
 * Copyright 2018 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.platform.smartmetering.glue.steps.ws.smartmetering.notifications;

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
		final String correlationUid = settings.get(PlatformKeys.KEY_CORRELATION_UID);
		ResponseData responseData = this.responseDataRespository.findByCorrelationUid(correlationUid);

		final int maxtime = 120000;
		final int timeout = 500;
		final int initial_timeout = 60000; // needed to make sure the ResendNotificationJob has at least runned once
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

	@Then("^no notification is sent$")
	public void noNotificationIsSent(final Map<String, String> settings) throws Throwable {
		final String correlationUid = settings.get(PlatformKeys.KEY_CORRELATION_UID);
		final ResponseData responseData = this.responseDataRespository.findByCorrelationUid(correlationUid);
		assertEquals("NumberOfNotificationsSend is not as expected",
				settings.get(PlatformKeys.KEY_NUMBER_OF_NOTIFICATIONS_SEND),
				responseData.getNumberOfNotificationsSend());
	}
}

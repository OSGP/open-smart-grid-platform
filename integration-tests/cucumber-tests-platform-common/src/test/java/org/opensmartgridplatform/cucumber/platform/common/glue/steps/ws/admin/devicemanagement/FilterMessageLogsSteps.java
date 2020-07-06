/**
 *  Copyright 2020 Smart Society Services B.V.
 */
package org.opensmartgridplatform.cucumber.platform.common.glue.steps.ws.admin.devicemanagement;

import static org.assertj.core.api.Assertions.assertThat;
import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getString;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.Map;

import org.opensmartgridplatform.adapter.ws.schema.admin.devicemanagement.FindMessageLogsRequest;
import org.opensmartgridplatform.adapter.ws.schema.admin.devicemanagement.FindMessageLogsResponse;
import org.opensmartgridplatform.adapter.ws.schema.admin.devicemanagement.MessageLog;
import org.opensmartgridplatform.cucumber.core.ScenarioContext;
import org.opensmartgridplatform.cucumber.platform.common.PlatformCommonDefaults;
import org.opensmartgridplatform.cucumber.platform.common.PlatformCommonKeys;
import org.opensmartgridplatform.cucumber.platform.common.support.ws.admin.AdminDeviceManagementClient;
import org.opensmartgridplatform.cucumber.platform.inputparsers.XmlGregorianCalendarInputParser;
import org.opensmartgridplatform.shared.exceptionhandling.WebServiceSecurityException;
import org.springframework.beans.factory.annotation.Autowired;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class FilterMessageLogsSteps {

    @Autowired
    private AdminDeviceManagementClient client;

    @When("^receiving a filter message log request$")
    public void getMessageLogDevID(final Map<String, String> requestParameters)
            throws WebServiceSecurityException, GeneralSecurityException, IOException {
        final FindMessageLogsRequest request = new FindMessageLogsRequest();

        if (requestParameters.containsKey(PlatformCommonKeys.KEY_DEVICE_IDENTIFICATION)) {
            request.setDeviceIdentification(getString(requestParameters, PlatformCommonKeys.KEY_DEVICE_IDENTIFICATION,
                    PlatformCommonDefaults.DEFAULT_DEVICE_IDENTIFICATION));
        }
        if (requestParameters.containsKey(PlatformCommonKeys.KEY_ORGANIZATION_IDENTIFICATION)) {
            request.setOrganisationIdentification(
                    getString(requestParameters, PlatformCommonKeys.KEY_ORGANIZATION_IDENTIFICATION,
                            PlatformCommonDefaults.DEFAULT_ORGANIZATION_IDENTIFICATION));
        }
        if (requestParameters.containsKey(PlatformCommonKeys.KEY_SETPOINT_START_TIME)) {
            request.setStartTime(XmlGregorianCalendarInputParser.parse(getString(requestParameters,
                    PlatformCommonKeys.KEY_SETPOINT_START_TIME, PlatformCommonDefaults.DEFAULT_BEGIN_DATE)));
        }
        if (requestParameters.containsKey(PlatformCommonKeys.KEY_SETPOINT_END_TIME)) {
            request.setStartTime(XmlGregorianCalendarInputParser.parse(getString(requestParameters,
                    PlatformCommonKeys.KEY_SETPOINT_END_TIME, PlatformCommonDefaults.DEFAULT_END_DATE)));
        }

        ScenarioContext.current().put(PlatformCommonKeys.RESPONSE, this.client.findMessageLogs(request));
    }

    @Then("the messages response contains {int} correct messages")
    public void theGetMessageLogsFilterSuccesful(final int amount, final Map<String, String> requestParameters)
            throws Throwable {
        List<MessageLog> messageLogs = getMessageLogs();
        assertThat(messageLogs.size() == amount);
        for (MessageLog log : messageLogs) {
            if (requestParameters.containsKey(PlatformCommonKeys.KEY_DEVICE_IDENTIFICATION)) {
                assertThat(log.getDeviceIdentification())
                        .isEqualTo(requestParameters.get(PlatformCommonKeys.KEY_DEVICE_IDENTIFICATION));
            }
            if (requestParameters.containsKey(PlatformCommonKeys.KEY_ORGANIZATION_IDENTIFICATION)) {
                assertThat(log.getDeviceIdentification())
                        .isEqualTo(requestParameters.get(PlatformCommonKeys.KEY_ORGANIZATION_IDENTIFICATION));
            }
        }
    }

    @Then("the messages response contains {int} correct messages with date filter")
    public void theGetMessageLogsDateFilterSuccessFul(final int amount) throws Throwable {
        List<MessageLog> messageLogs = getMessageLogs();
        assertThat(messageLogs.size() == amount);
    }

    private List<MessageLog> getMessageLogs() {
        Object response = ScenarioContext.current().get(PlatformCommonKeys.RESPONSE);
        assertThat(response instanceof FindMessageLogsResponse).isTrue();
        FindMessageLogsResponse messageResponse = (FindMessageLogsResponse) response;
        return messageResponse.getMessageLogPage().getMessageLogs();
    }

}

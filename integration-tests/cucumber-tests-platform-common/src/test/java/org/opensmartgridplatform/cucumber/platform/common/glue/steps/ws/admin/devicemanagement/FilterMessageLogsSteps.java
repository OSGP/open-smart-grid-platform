/**
 *  Copyright 2020 Smart Society Services B.V.
 */
package org.opensmartgridplatform.cucumber.platform.common.glue.steps.ws.admin.devicemanagement;

import static org.assertj.core.api.Assertions.assertThat;
import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getEnum;
import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getString;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.opensmartgridplatform.adapter.ws.schema.admin.devicemanagement.FindMessageLogsRequest;
import org.opensmartgridplatform.adapter.ws.schema.admin.devicemanagement.FindMessageLogsResponse;
import org.opensmartgridplatform.adapter.ws.schema.admin.devicemanagement.MessageLog;
import org.opensmartgridplatform.adapter.ws.schema.admin.devicemanagement.SortDirectionEnum;
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

    @When("receiving a message log request without a filter")
    public void getMessageLogNoFilter() throws WebServiceSecurityException, GeneralSecurityException, IOException {
        final FindMessageLogsRequest request = new FindMessageLogsRequest();
        ScenarioContext.current().put(PlatformCommonKeys.RESPONSE, this.client.findMessageLogs(request));
    }

    @When("^receiving a filter message log request$")
    public void getMessageLogFilter(final Map<String, String> requestParameters)
            throws IllegalArgumentException, WebServiceSecurityException {
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

        if (requestParameters.containsKey(PlatformCommonKeys.START_TIME)) {
            request.setStartTime(XmlGregorianCalendarInputParser.parse(getString(requestParameters,
                    PlatformCommonKeys.START_TIME, PlatformCommonDefaults.DEFAULT_BEGIN_DATE)));
        }

        if (requestParameters.containsKey(PlatformCommonKeys.END_TIME)) {
            request.setEndTime(XmlGregorianCalendarInputParser.parse(getString(requestParameters,
                    PlatformCommonKeys.END_TIME, PlatformCommonDefaults.DEFAULT_END_DATE)));
        }

        final SortDirectionEnum sortDirection = getEnum(requestParameters, PlatformCommonKeys.KEY_SORT_DIR,
                SortDirectionEnum.class);
        request.setSortDirection(sortDirection);

        if (requestParameters.containsKey(PlatformCommonKeys.KEY_SORTED_BY)) {
            request.setSortBy(requestParameters.get(PlatformCommonKeys.KEY_SORTED_BY));
        }

        ScenarioContext.current().put(PlatformCommonKeys.RESPONSE, this.client.findMessageLogs(request));
    }

    @Then("the messages response contains {int} correct messages")
    public void theGetMessageLogsFilterSuccesful(final int amount, final Map<String, String> requestParameters) {
        final List<MessageLog> messageLogs = this.getMessageLogs();
        assertThat(messageLogs.size()).isEqualTo(amount);
        for (final MessageLog log : messageLogs) {
            if (requestParameters.containsKey(PlatformCommonKeys.KEY_DEVICE_IDENTIFICATION)) {
                assertThat(log.getDeviceIdentification())
                        .isEqualTo(requestParameters.get(PlatformCommonKeys.KEY_DEVICE_IDENTIFICATION));
            }

            if (requestParameters.containsKey(PlatformCommonKeys.KEY_ORGANIZATION_IDENTIFICATION)) {
                assertThat(log.getOrganisationIdentification())
                        .isEqualTo(requestParameters.get(PlatformCommonKeys.KEY_ORGANIZATION_IDENTIFICATION));
            }
        }
    }

    @Then("the messages response contains {int} correct messages for devices")
    public void theGetMessageLogsDeviceWildcardFeatureSuccesful(final int amount, final List<String> ids) {
        final List<MessageLog> messageLogs = this.getMessageLogs();
        assertThat(messageLogs.size()).isEqualTo(amount);

        for (final MessageLog log : messageLogs) {
            assertThat(ids).contains(log.getDeviceIdentification());
        }
    }

    @Then("the messages response contains {int} correct messages with date filter or no filter")
    public void theGetMessageLogsDateFilterSuccessFul(final int amount) throws Throwable {
        final List<MessageLog> messageLogs = this.getMessageLogs();
        assertThat(messageLogs.size()).isEqualTo(amount);
    }

    @Then("the messages response contains {int} messages ordered descending by device identification")
    public void theGetMessageLogsInOrder(final int amount) {
        final List<MessageLog> messageLogs = this.getMessageLogs();
        assertThat(messageLogs.size()).isEqualTo(amount);

        final List<String> deviceIdentifications = messageLogs.stream()
                .map(MessageLog::getDeviceIdentification)
                .collect(Collectors.toList());
        Collections.reverse(deviceIdentifications);

        assertThat(deviceIdentifications).isSorted();
    }

    private List<MessageLog> getMessageLogs() {
        final Object response = ScenarioContext.current().get(PlatformCommonKeys.RESPONSE);
        assertThat(response instanceof FindMessageLogsResponse).isTrue();
        final FindMessageLogsResponse messageResponse = (FindMessageLogsResponse) response;
        return messageResponse.getMessageLogPage().getMessageLogs();
    }

}

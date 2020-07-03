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
import org.opensmartgridplatform.adapter.ws.schema.admin.devicemanagement.RemoveDeviceResponse;
import org.opensmartgridplatform.cucumber.core.ScenarioContext;
import org.opensmartgridplatform.cucumber.platform.common.PlatformCommonDefaults;
import org.opensmartgridplatform.cucumber.platform.common.PlatformCommonKeys;
import org.opensmartgridplatform.cucumber.platform.common.support.ws.admin.AdminDeviceManagementClient;
import org.opensmartgridplatform.shared.exceptionhandling.WebServiceSecurityException;
import org.springframework.beans.factory.annotation.Autowired;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class FilterMessageLogsSteps {
    
    @Autowired
    private AdminDeviceManagementClient client;
    
    @When("^receiving a filter message log on device identification request$")
    private void getMessageLogDevID(final Map<String, String> requestParameters)
            throws WebServiceSecurityException, GeneralSecurityException, IOException {
        final FindMessageLogsRequest request = new FindMessageLogsRequest();
        
        if(requestParameters.containsKey(PlatformCommonKeys.KEY_DEVICE_IDENTIFICATION)) {
            request.setDeviceIdentification(getString(requestParameters, PlatformCommonKeys.KEY_DEVICE_IDENTIFICATION,
                PlatformCommonDefaults.DEFAULT_DEVICE_IDENTIFICATION));
        }
        if(requestParameters.containsKey(PlatformCommonKeys.KEY_ORGANIZATION_IDENTIFICATION)) {
            request.setOrganisationIdentification(getString(requestParameters, PlatformCommonKeys.KEY_ORGANIZATION_IDENTIFICATION,
                PlatformCommonDefaults.DEFAULT_ORGANIZATION_IDENTIFICATION));
        }
        //TODO add time filters

        ScenarioContext.current()
                .put(PlatformCommonKeys.RESPONSE, this.client.findMessageLogs(request));
    }
    
    @Then("^the messages response contains (\\d+) correct messages $")
    public void theGetMessageLogDevIdIsSuccesfull(final int amount, final Map<String, String> requestParameters) throws Throwable {
        Object response = ScenarioContext.current().get(PlatformCommonKeys.RESPONSE);
        assertThat(response instanceof FindMessageLogsResponse).isTrue();
        FindMessageLogsResponse messageResponse = (FindMessageLogsResponse) response;
        List<MessageLog> messageLogs = messageResponse.getMessageLogPage().getMessageLogs();
        assertThat(messageLogs.size() == amount);
        for(MessageLog log : messageLogs) {
            if(requestParameters.containsKey(PlatformCommonKeys.KEY_DEVICE_IDENTIFICATION)) {
                assertThat(log.getDeviceIdentification()).isEqualTo(requestParameters.get(PlatformCommonKeys.KEY_DEVICE_IDENTIFICATION));
            }
            if(requestParameters.containsKey(PlatformCommonKeys.KEY_ORGANIZATION_IDENTIFICATION)) {
                assertThat(log.getDeviceIdentification()).isEqualTo(requestParameters.get(PlatformCommonKeys.KEY_ORGANIZATION_IDENTIFICATION));
            }
        }
    }

}

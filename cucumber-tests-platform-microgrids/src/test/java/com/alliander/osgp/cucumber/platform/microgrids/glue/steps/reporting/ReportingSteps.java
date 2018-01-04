package com.alliander.osgp.cucumber.platform.microgrids.glue.steps.reporting;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.alliander.osgp.adapter.protocol.iec61850.domain.repositories.Iec61850DeviceRepository;
import com.alliander.osgp.adapter.ws.schema.microgrids.adhocmanagement.GetDataAsyncRequest;
import com.alliander.osgp.adapter.ws.schema.microgrids.adhocmanagement.GetDataAsyncResponse;
import com.alliander.osgp.adapter.ws.schema.microgrids.adhocmanagement.GetDataRequest;
import com.alliander.osgp.adapter.ws.schema.microgrids.common.AsyncRequest;
import com.alliander.osgp.cucumber.core.GlueBase;
import com.alliander.osgp.cucumber.platform.microgrids.PlatformMicrogridsKeys;
import com.alliander.osgp.cucumber.platform.microgrids.mocks.iec61850.Iec61850MockServer;
import com.alliander.osgp.cucumber.platform.microgrids.support.ReportTriggerNodeMappingService;
import com.alliander.osgp.cucumber.platform.microgrids.support.ws.microgrids.NotificationService;
import com.alliander.osgp.cucumber.platform.microgrids.support.ws.microgrids.adhocmanagement.AdHocManagementClient;
import com.alliander.osgp.simulator.protocol.iec61850.server.QualityType;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class ReportingSteps extends GlueBase {

    @Autowired
    Iec61850DeviceRepository iec61850DeviceRepository;

    @Autowired
    private AdHocManagementClient adHocManagementClient;

    @Autowired
    private Iec61850MockServer iec61850MockServerPampus;

    @Autowired
    private Iec61850MockServer iec61850MockServerMarkerWadden;

    @Autowired
    private Iec61850MockServer iec61850MockServerSchoteroog;

    @Autowired
    private NotificationService mockNotificationService;

    @Autowired
    private ReportTriggerNodeMappingService reportTriggerNodeMappingService;

    @Given("^all reports are disabled on the rtu$")
    public void allReportsAreDisabled() {
        this.iec61850MockServerPampus.ensureReportsDisabled();
    }

    @Then("^all reports should not be enabled$")
    public void allReportsShouldNotBeEnabled() {
        this.iec61850MockServerPampus.assertReportsDisabled();
    }

    @Then("^all reports should be enabled$")
    public void allReportsShouldBeEnabled() throws Throwable {
        this.iec61850MockServerPampus.assertReportsEnabled();
    }

    @Given("^OSGP is connected to the Pampus RTU$")
    public void osgpIsConnectedToThePampusRTU(final Map<String, String> settings) throws Throwable {

        this.connectOsgpToRtuDevice(this.iec61850MockServerPampus, settings);
    }

    @Given("^OSGP is connected to the Marker Wadden RTU$")
    public void osgpIsConnectedToTheMarkerWaddenRTU(final Map<String, String> settings) throws Throwable {

        this.connectOsgpToRtuDevice(this.iec61850MockServerMarkerWadden, settings);
    }

    @Given("^OSGP is connected to the Schoteroog RTU$")
    public void osgpIsConnectedToTheSchoteroogRTU(final Map<String, String> settings) throws Throwable {

        this.connectOsgpToRtuDevice(this.iec61850MockServerSchoteroog, settings);
    }

    @When("^the Pampus RTU pushes a report$")
    public void thePampusRTUPushesAReport(final Map<String, String> settings) throws Throwable {

        this.pushAReport(this.iec61850MockServerPampus, settings);
    }

    @When("^the Marker Wadden RTU pushes a report$")
    public void theMarkerWaddenRTUPushesAReport(final Map<String, String> settings) throws Throwable {

        this.pushAReport(this.iec61850MockServerMarkerWadden, settings);
    }

    @When("^the Schoteroog RTU pushes a report$")
    public void theSchoteroogRTUPushesAReport(final Map<String, String> settings) throws Throwable {

        this.pushAReport(this.iec61850MockServerSchoteroog, settings);
    }

    private void connectOsgpToRtuDevice(final Iec61850MockServer iec61850MockServer, final Map<String, String> settings)
            throws Throwable {

        // Restart the simulator to avoid problems with cached connections.
        iec61850MockServer.stop();
        iec61850MockServer.start();

        // Do a GetDataRequest to get a connection with OSGP
        this.doGetDataRequest(settings);

        // Make sure the notifications queue is empty, so that when the
        // reportNotification arrives it's the only one in the queue.
        this.mockNotificationService.clearAllNotifications();
    }

    private void pushAReport(final Iec61850MockServer iec61850MockServer, final Map<String, String> settings)
            throws Throwable {

        // Change a quality attribute value to trigger sending of a report.
        final String triggerNode = this.reportTriggerNodeMappingService.getReportTriggerNode(settings);
        iec61850MockServer.mockValue(settings.get(PlatformMicrogridsKeys.LOGICAL_DEVICE), triggerNode,
                QualityType.OLD_DATA.name());
    }

    private void doGetDataRequest(final Map<String, String> settings) throws Throwable {

        final GetDataRequest getDataRequest = new GetDataRequest();
        getDataRequest.setDeviceIdentification(settings.get(PlatformMicrogridsKeys.KEY_DEVICE_IDENTIFICATION));

        GetDataAsyncResponse asyncResponse;
        asyncResponse = this.adHocManagementClient.getDataAsync(getDataRequest);

        final GetDataAsyncRequest getDataAsyncRequest = new GetDataAsyncRequest();
        final AsyncRequest value = new AsyncRequest();
        value.setCorrelationUid(asyncResponse.getAsyncResponse().getCorrelationUid());
        value.setDeviceId(asyncResponse.getAsyncResponse().getDeviceId());
        getDataAsyncRequest.setAsyncRequest(value);

        this.adHocManagementClient.getData(getDataAsyncRequest);
    }
}

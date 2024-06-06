// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.cucumber.platform.microgrids.glue.steps.reporting;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.util.Map;
import org.opensmartgridplatform.adapter.protocol.iec61850.domain.repositories.Iec61850DeviceRepository;
import org.opensmartgridplatform.adapter.ws.schema.microgrids.adhocmanagement.GetDataAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.microgrids.adhocmanagement.GetDataAsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.microgrids.adhocmanagement.GetDataRequest;
import org.opensmartgridplatform.adapter.ws.schema.microgrids.common.AsyncRequest;
import org.opensmartgridplatform.cucumber.platform.microgrids.PlatformMicrogridsKeys;
import org.opensmartgridplatform.cucumber.platform.microgrids.mocks.iec61850.Iec61850MockServer;
import org.opensmartgridplatform.cucumber.platform.microgrids.support.ws.microgrids.NotificationService;
import org.opensmartgridplatform.cucumber.platform.microgrids.support.ws.microgrids.adhocmanagement.AdHocManagementClient;
import org.opensmartgridplatform.simulator.protocol.iec61850.server.QualityType;
import org.springframework.beans.factory.annotation.Autowired;

public class ReportingSteps {

  @Autowired Iec61850DeviceRepository iec61850DeviceRepository;

  @Autowired private AdHocManagementClient adHocManagementClient;

  @Autowired private Iec61850MockServer iec61850MockServerPampus;

  @Autowired private Iec61850MockServer iec61850MockServerMarkerWadden;

  @Autowired private Iec61850MockServer iec61850MockServerSchoteroog;

  @Autowired private NotificationService mockNotificationService;

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
  public void osgpIsConnectedToTheMarkerWaddenRTU(final Map<String, String> settings)
      throws Throwable {

    this.connectOsgpToRtuDevice(this.iec61850MockServerMarkerWadden, settings);
  }

  @Given("^OSGP is connected to the Schoteroog RTU$")
  public void osgpIsConnectedToTheSchoteroogRTU(final Map<String, String> settings)
      throws Throwable {

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

  private void connectOsgpToRtuDevice(
      final Iec61850MockServer iec61850MockServer, final Map<String, String> settings)
      throws Throwable {

    // Restart the simulator to avoid problems with cached connections.
    iec61850MockServer.restart();

    // Do a GetDataRequest to ensure a connection with OSGP
    this.doGetDataRequest(settings);

    // Make sure the notifications queue is empty, so that when the
    // reportNotification arrives it's the only one in the queue.
    this.mockNotificationService.clearAllNotifications();
  }

  private void pushAReport(
      final Iec61850MockServer iec61850MockServer, final Map<String, String> settings)
      throws Throwable {
    // Change a quality attribute value to trigger sending of a report.
    final String triggerNode = settings.get(PlatformMicrogridsKeys.NODE);

    iec61850MockServer.mockValue(
        settings.get(PlatformMicrogridsKeys.LOGICAL_DEVICE),
        triggerNode,
        QualityType.OLD_DATA.name());
  }

  private void doGetDataRequest(final Map<String, String> settings) throws Throwable {

    final GetDataRequest getDataRequest = new GetDataRequest();
    getDataRequest.setDeviceIdentification(
        settings.get(PlatformMicrogridsKeys.KEY_DEVICE_IDENTIFICATION));

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

/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.platform.microgrids.glue.steps.ws.microgrids.adhocmanagement;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.alliander.osgp.adapter.protocol.iec61850.domain.entities.Iec61850Device;
import com.alliander.osgp.adapter.protocol.iec61850.domain.repositories.Iec61850DeviceRepository;
import com.alliander.osgp.adapter.ws.schema.microgrids.adhocmanagement.GetDataAsyncRequest;
import com.alliander.osgp.adapter.ws.schema.microgrids.adhocmanagement.GetDataAsyncResponse;
import com.alliander.osgp.adapter.ws.schema.microgrids.adhocmanagement.GetDataRequest;
import com.alliander.osgp.adapter.ws.schema.microgrids.common.AsyncRequest;
import com.alliander.osgp.cucumber.platform.microgrids.PlatformMicrogridsKeys;
import com.alliander.osgp.cucumber.platform.microgrids.mocks.iec61850.Iec61850MockServer;
import com.alliander.osgp.cucumber.platform.microgrids.support.ws.microgrids.NotificationService;
import com.alliander.osgp.cucumber.platform.microgrids.support.ws.microgrids.adhocmanagement.AdHocManagementClient;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.When;

public class PushServiceSteps {

    @Autowired
    Iec61850DeviceRepository iec61850DeviceRepository;

    @Autowired
    private AdHocManagementClient client;

    @Autowired
    private Iec61850MockServer iec61850MockServerPampus;

    @Autowired
    private NotificationService mockNotificationService;

    @Given("^OSGP is connected to the RTU device$")
    public void osgpIsConnectedToTheRTUDevice(final Map<String, String> settings) throws Throwable {

        // Restart the simulator to avoid problems with cached connections.
        this.iec61850MockServerPampus.stop();
        this.iec61850MockServerPampus.start();

        // Make sure all reports are enabled
        final Iec61850Device iec61850Device = this.iec61850DeviceRepository
                .findByDeviceIdentification(settings.get(PlatformMicrogridsKeys.KEY_DEVICE_IDENTIFICATION));
        iec61850Device.setEnableAllReportsOnConnect(true);
        this.iec61850DeviceRepository.save(iec61850Device);

        // Do a GetDataRequest to get a connection with OSGP
        this.doGetDataRequest(settings);

        // Make sure the notifications queue is empty, so that when the
        // reportNotification arrives it's the only one in the queue.
        this.mockNotificationService.clearAllNotifications();

    }

    @When("^the RTU pushes a report$")
    public void theRTUPushesAReport(final Map<String, String> settings) throws Throwable {

        // Change a value, that will trigger sending of a report.
        this.iec61850MockServerPampus.mockValue(settings.get("LogicalDevice"), settings.get("LogicalDeviceNode"),
                settings.get("Value"));
    }

    private void doGetDataRequest(final Map<String, String> settings) throws Throwable {

        final GetDataRequest getDataRequest = new GetDataRequest();
        getDataRequest.setDeviceIdentification(settings.get(PlatformMicrogridsKeys.KEY_DEVICE_IDENTIFICATION));
        GetDataAsyncResponse asyncResponse;
        asyncResponse = this.client.getDataAsync(getDataRequest);
        final GetDataAsyncRequest getDataAsyncRequest = new GetDataAsyncRequest();
        final AsyncRequest value = new AsyncRequest();
        value.setCorrelationUid(asyncResponse.getAsyncResponse().getCorrelationUid());
        value.setDeviceId(asyncResponse.getAsyncResponse().getDeviceId());
        getDataAsyncRequest.setAsyncRequest(value);
        this.client.getData(getDataAsyncRequest);
    }

}

/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.webdemoapp.web.controller;

import org.opensmartgridplatform.webdemoapp.application.services.OsgpPublicLightingClientSoapService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import org.opensmartgridplatform.adapter.ws.schema.publiclighting.adhocmanagement.GetStatusResponse;

@Controller
public class AsyncController {

    @Autowired
    OsgpPublicLightingClientSoapService soapService;

    @Autowired
    PublicLightingController publicLightingController;

    /**
     * This controller is called on a 2 second interval by the device-status.jsp
     * page.
     *
     * The controller retrieves the correlation uid from the URL and checks
     * (using the SoapService class) if a async request is processed by the
     * platform. If this is not the case, it re-directs back to the
     * device-status page. If the request is ready, it calls the deviceStatus
     * controller to display the response of the platform.
     */
    @RequestMapping(value = "/asyncStatus/{correlationId}", method = RequestMethod.GET)
    public ModelAndView asyncGetStatusRequest(@PathVariable final String correlationId) {

        final ModelAndView modelView = new ModelAndView();
        final GetStatusResponse responseStatus = this.soapService
                .getGetStatusResponse(this.splitCorrelationId(correlationId)[1], correlationId);

        switch (responseStatus.getResult().toString()) {
        case "OK":
            return this.publicLightingController.getStatusRequest(responseStatus.getDeviceStatus(),
                    this.splitCorrelationId(correlationId)[1]);
        default:
            System.out.println("Response not ready");
            modelView.addObject("correlationId", correlationId);
            modelView.setViewName("device-status");
        }
        return modelView;
    }

    /**
     * Splits a correlation Id into 3 seperate components. Correlation Id's
     * typically look like: <<org_id>>|||<<device_id>>|||<<uid>> The resulting
     * array will have org_id on pos 0, device_id on pos 1 en uid on pos 2.
     */
    private String[] splitCorrelationId(final String correlationId) {
        return correlationId.split("\\|\\|\\|");
    }

}

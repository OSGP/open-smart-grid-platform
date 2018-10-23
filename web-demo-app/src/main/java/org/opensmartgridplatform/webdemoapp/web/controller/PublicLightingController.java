/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.webdemoapp.web.controller;

import java.util.List;

import org.opensmartgridplatform.webdemoapp.application.services.OsgpPublicLightingClientSoapService;
import org.opensmartgridplatform.webdemoapp.domain.Device;
import org.opensmartgridplatform.webdemoapp.domain.DeviceLightStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.ws.soap.client.SoapFaultClientException;

import org.opensmartgridplatform.adapter.ws.schema.publiclighting.adhocmanagement.DeviceStatus;

/**
 * Handles requests for the public lighting domain
 */
@Controller
public class PublicLightingController {

    private static final Logger LOGGER = LoggerFactory.getLogger(PublicLightingController.class);

    @Autowired
    private OsgpPublicLightingClientSoapService osgpPublicLightingClientSoapService;

    /**
     * Redirects requests made to '/' to home.
     */
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String home() {
        return "home";
    }

    /**
     * Makes a SOAP request to the platform which returns a list of devices.
     * Adds the list to the Model of the View.
     *
     * The list.jsp page shows the list.
     *
     * @return ModelAndView
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public ModelAndView showDeviceList() {

        final List<Device> list = this.osgpPublicLightingClientSoapService.findAllDevicesRequest();

        final ModelAndView model = new ModelAndView("list");
        model.addObject("deviceList", list);

        return model;
    }

    /**
     * Process requests to view device details. This function will use the
     * SoapClient to make the request to the platform, this returns a
     * correlationId. The correlationId is passed back to the device-status
     * view, where it will poll the platform on a 2 second interval until the
     * status request is ready.
     */
    @RequestMapping(value = "/deviceDetails/{deviceId}", method = RequestMethod.GET)
    public ModelAndView devicesDetails(@PathVariable final String deviceId) {
        final ModelAndView modelView = new ModelAndView("device-status");
        try {
            String correlationId;
            correlationId = this.osgpPublicLightingClientSoapService.getDeviceStatus(deviceId);
            modelView.addObject("correlationId", correlationId);
        } catch (final SoapFaultClientException e) {
            LOGGER.error("Soap fault during deviceDetails()", e);
            return this.error(e.getFaultStringOrReason());
        } catch (final NullPointerException e) {
            LOGGER.error("Null pointer during deviceDetails()", e);
            return this.error("A response from the platform returned 'null'");
        }
        return modelView;
    }

    /**
     * Receives a Switch request for a light device. Sends the request to the
     * platform by using the SoapService. Some checks on the parameters of the
     * request: LightValue must be between 0 and 100. If light is turned off, do
     * not send a lightvalue to the platform.
     *
     * Returns a View with the request parameters in a device object.
     */
    @RequestMapping(value = "/doSwitchDevice", method = RequestMethod.POST)
    public ModelAndView switchDevice(@ModelAttribute("SpringWeb") final DeviceLightStatus deviceStatus) {
        final ModelAndView modelView = new ModelAndView("switch-result");
        try {
            if (deviceStatus.isLightOn()
                    && ((deviceStatus.getLightValue() > 0) && (deviceStatus.getLightValue() <= 100))) {
                this.osgpPublicLightingClientSoapService.setLightRequest(deviceStatus.getDeviceId(),
                        deviceStatus.getLightValue(), deviceStatus.isLightOn());
            } else if (!deviceStatus.isLightOn()) {
                this.osgpPublicLightingClientSoapService.switchLightRequest(deviceStatus.getDeviceId(),
                        deviceStatus.isLightOn());
            } else {
                return this.error("LightValue must be a number between 1 - 100");
            }

        } catch (final SoapFaultClientException e) {
            LOGGER.error("Soap fault during switchDevice()", e);
            return this.error(e.getFaultStringOrReason());
        }

        modelView.addObject("device", deviceStatus);

        return modelView;
    }

    /**
     * Creates a view to show the device details from a async GetStatus request.
     * This function is called from the async controller as soon as the
     * getStatus request is processed by the platform
     */
    public ModelAndView getStatusRequest(final DeviceStatus deviceStatus, final String deviceIdentification) {
        final ModelAndView modelView = new ModelAndView("device");
        final DeviceLightStatus deviceLightStatus = new DeviceLightStatus();

        deviceLightStatus.setDeviceId(deviceIdentification);
        deviceLightStatus.setLightValue(deviceStatus.getLightValues().get(0).getDimValue());
        deviceLightStatus.setLightOn(deviceStatus.getLightValues().get(0).isOn());

        modelView.addObject("command", new DeviceLightStatus());
        modelView.addObject("device", deviceLightStatus);

        return modelView;
    }

    /**
     * Displays an error message.
     */
    public ModelAndView error(final String error) {
        final ModelAndView modelView = new ModelAndView("error");

        modelView.addObject("error", error);

        return modelView;
    }

}

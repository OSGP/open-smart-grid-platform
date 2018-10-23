/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.webdemoapp.web.controller;

import org.opensmartgridplatform.webdemoapp.application.services.OsgpAdminClientSoapService;
import org.opensmartgridplatform.webdemoapp.domain.Device;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.ws.soap.client.SoapFaultClientException;

/**
 * Handles requests for the admin domain.
 */
@Controller
public class AdminController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AdminController.class);

    @Autowired
    OsgpAdminClientSoapService osgpAdminClientSoapService;

    @Autowired
    PublicLightingController publicLightingController;

    /**
     * Is called by the add-device page. This controller receives the device
     * from the JSP page and calls the 'updateKeyRequest' in the Soap Client
     * Service. This requests is sent to the Platform, and creates a device.
     */
    @RequestMapping(value = "/doAddDevice", method = RequestMethod.POST)
    public ModelAndView addDevice(@ModelAttribute("SpringWeb") final Device device) {

        final ModelAndView modelView = new ModelAndView("add-result");

        if (device.getDeviceIdentification() != null && device.getDeviceIdentification().length() > 2) {
            try {
                this.osgpAdminClientSoapService.updateKeyRequest(device);
            } catch (final SoapFaultClientException e) {
                LOGGER.error("Soap fault during addDevice()", e);
                return this.publicLightingController.error(e.getFaultStringOrReason());
            }
            modelView.addObject("deviceId", device.getDeviceIdentification());
            return modelView;
        } else {
            return this.publicLightingController.error("Device name cannot be empty");
        }
    }

    /**
     * Mapped to 'addDevice'. Returns a ModelAndView containing the name of the
     * JSP page which contains a form. Adds a Device object to the model. The
     * form fills the device object and the button on the jsp page links to the
     * 'doAddDevice' Controller.
     */
    @RequestMapping(value = "/addDevice", method = RequestMethod.GET)
    public ModelAndView devices() {
        return new ModelAndView("add-device", "command", new Device());
    }

}

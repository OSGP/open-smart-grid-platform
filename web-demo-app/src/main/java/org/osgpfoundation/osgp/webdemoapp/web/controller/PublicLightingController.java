/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.osgpfoundation.osgp.webdemoapp.web.controller;

import java.util.List;

import org.osgpfoundation.osgp.webdemoapp.application.services.OsgpPublicLightingClientSoapService;
import org.osgpfoundation.osgp.webdemoapp.domain.Device;
import org.osgpfoundation.osgp.webdemoapp.domain.DeviceLightStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.ws.soap.client.SoapFaultClientException;

/**
 * Handles requests for the application home page.
 */
@Controller
public class PublicLightingController {

	@Autowired
	OsgpPublicLightingClientSoapService osgpPublicLightingClientSoapService;

	/**
	 * Simply selects the home view to render by returning its name.
	 */
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String home() {
		return "redirect:/list";
	}

	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public ModelAndView showDeviceList() {

		List<Device> list = osgpPublicLightingClientSoapService
				.findAllDevicesRequest();

		ModelAndView model = new ModelAndView("list");
		model.addObject("deviceList", list);

		return model;
	}

	@RequestMapping(value = "/device", method = RequestMethod.GET)
	public ModelAndView deviceDetails() {

		ModelAndView model = new ModelAndView("device");

		return model;
	}

	@RequestMapping(value = "/deviceDetails/{deviceId}", method = RequestMethod.GET)
	public ModelAndView devicesDetails(@PathVariable String deviceId,
			Model model) {
		ModelAndView modelView = new ModelAndView("device");

		try {
			DeviceLightStatus deviceStatus = null;
			deviceStatus = osgpPublicLightingClientSoapService
					.getDeviceStatus(deviceId);
			modelView.addObject("device", deviceStatus);
		} catch (SoapFaultClientException e) {
			modelView.addObject("error", e.getFaultStringOrReason());
			modelView.addObject("errorMessage",
					"Is the device registered in the Platform?");
		} catch (NullPointerException e) {
			modelView.addObject("error", e.getCause());
			modelView.addObject("errorMessage",
					"The Soap Request returned null, is the platform running?");
		}

		modelView.addObject("command", new DeviceLightStatus());

		return modelView;
	}

	@RequestMapping(value = "/doSwitchDevice", method = RequestMethod.POST)
	public String addDevice(
			@ModelAttribute("SpringWeb") DeviceLightStatus deviceStatus,
			ModelMap model) {

		osgpPublicLightingClientSoapService.setLightRequest(
				deviceStatus.getDeviceId(), deviceStatus.getLightValue(),
				deviceStatus.isLightOn());

		model.addAttribute("device", deviceStatus);

		return "switch-result";
	}
}

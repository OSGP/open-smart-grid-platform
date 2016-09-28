package org.osgpfoundation.osgp.webdemoapp.web.controller;

import org.osgpfoundation.osgp.webdemoapp.application.services.OsgpAdminClientSoapService;
import org.osgpfoundation.osgp.webdemoapp.domain.Device;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.ws.soap.client.SoapFaultClientException;

/**
 * Handles requests for the application home page.
 */
@Controller
public class AdminController {

	@Autowired
	OsgpAdminClientSoapService osgpAdminClientSoapService;

	@Autowired
	PublicLightingController publicLightingController;
	
	@RequestMapping(value = "/doAddDevice", method = RequestMethod.POST)
	public ModelAndView addDevice(@ModelAttribute("SpringWeb") Device device) {
		
		ModelAndView modelView = new ModelAndView ("add-result");
		
		if (device.getDeviceIdentification() != null && device.getDeviceIdentification().length() > 2) {
			try {
			osgpAdminClientSoapService.updateKeyRequest(device);
			} catch (SoapFaultClientException e) {
				return publicLightingController
						.error(e.getFaultStringOrReason());
			}
			modelView.addObject("deviceId",device.getDeviceIdentification());
			return modelView;
		} else
			return publicLightingController
					.error("Device name cannot be empty");
	}

	@RequestMapping(value = "/addDevice", method = RequestMethod.GET)
	public ModelAndView devices() {
		return new ModelAndView("add-device", "command", new Device());
	}

}

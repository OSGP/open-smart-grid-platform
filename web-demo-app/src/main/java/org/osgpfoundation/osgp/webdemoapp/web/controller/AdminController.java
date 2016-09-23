package org.osgpfoundation.osgp.webdemoapp.web.controller;

import org.osgpfoundation.osgp.webdemoapp.application.services.OsgpAdminClientSoapService;
import org.osgpfoundation.osgp.webdemoapp.domain.Device;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

/**
 * Handles requests for the application home page.
 */
@Controller
public class AdminController {

	@Autowired
	OsgpAdminClientSoapService osgpAdminClientSoapService;

	@RequestMapping(value = "/doAddDevice", method = RequestMethod.POST)
	public String addDevice(@ModelAttribute("SpringWeb") Device device,
			ModelMap model) {

		osgpAdminClientSoapService.updateKeyRequest(device);

		return "list";
	}

	@RequestMapping(value = "/addDevice", method = RequestMethod.GET)
	public ModelAndView devices() {
		return new ModelAndView("add-device", "command", new Device());
	}
	
	
	
	

}

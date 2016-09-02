/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.osgpfoundation.osgp.webdemoapp.web.controller;

import java.util.ArrayList;
import java.util.List;

import org.osgpfoundation.osgp.webdemoapp.application.services.OsgpClientSoapService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.alliander.osgp.platform.ws.schema.common.deviceinstallation.Device;

/**
 * Handles requests for the application home page.
 */
@Controller
public class HomeController {
	
	private OsgpClientSoapService soapClientService = new OsgpClientSoapService();
    protected static final String DEVICES_URL = "/devices";
    protected static final String DEVICE_CREATE_URL = "/devices/create";
    protected static final String DEVICE_EDIT_URL = "/devices/edit/{id}";

    protected static final String DEVICES_VIEW = "devices/list";
    protected static final String DEVICE_CREATE_VIEW = "devices/create";
    protected static final String DEVICE_EDIT_VIEW = "devices/edit";
    protected static final String DEVICE_REGISTRATION_VIEW = "devices/deviceRegistrationCheck";
    
    protected static final String MODEL_ATTRIBUTE_DEVICES = "devices";

    /**
     * Simply selects the home view to render by returning its name.
     */
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String home() {
        return "redirect:/devices";
    }
    
//    @RequestMapping(value = DEVICES_URL, method = RequestMethod.GET)
//    public String showDevices(final Model model) {
//        model.addAttribute(MODEL_ATTRIBUTE_DEVICES);
//
//        final List<String> deviceTypes = new ArrayList<>();
//
//        return DEVICES_VIEW;
//    }
//    
//    @RequestMapping(value ="/add", method = RequestMethod.GET)
//    public String addDevice(Model model) {
//    	model.addAttribute("test","Test from Spring");
//    	return "devices/add";
//    }
//    
//    
//   @RequestMapping(value ="/add-device", method = RequestMethod.POST)
//    public String getAddDevice(@ModelAttribute("Springweb")Device device, Model model){
//	   
//	   return " ";
//   }
//   

   @RequestMapping(value = "/device", method = RequestMethod.GET)
   public ModelAndView student() {
      return new ModelAndView("device", "command", new Device());
   }
   
   @RequestMapping(value = "/addDevice", method = RequestMethod.POST)
   public String addDevice(@ModelAttribute("SpringWeb")Device device, 
   ModelMap model) {
	   System.out.println("Starting Soap Request");
	   System.out.println(device.getDeviceIdentification());
	   soapClientService.addDeviceRequest(device);
      
      return "add";
   }

}

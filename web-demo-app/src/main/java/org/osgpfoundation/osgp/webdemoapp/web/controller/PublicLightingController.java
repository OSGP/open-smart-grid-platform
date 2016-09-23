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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

/**
 * Handles requests for the application home page.
 */
@Controller
public class HomeController {

    
    @Autowired
    OsgpPublicLightingClientSoapService osgpPublicLightingClientSoapService;

    /**
     * Simply selects the home view to render by returning its name.
     */
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String home() {
        return "redirect:/list";
    }
    

   
   @RequestMapping(value = "/list", method= RequestMethod.GET )
   public ModelAndView showDeviceList () {
	   
	   List<Device> list = osgpPublicLightingClientSoapService.findAllDevicesRequest();
	   
	   ModelAndView model = new ModelAndView("list");
	   model.addObject("deviceList", list);
	   
	   return model;
   }

   
   @RequestMapping(value = "/device", method= RequestMethod.GET )
   public ModelAndView deviceDetails () {
	   	   
	   ModelAndView model = new ModelAndView("device");
	   
	   return model;
   }
   
}

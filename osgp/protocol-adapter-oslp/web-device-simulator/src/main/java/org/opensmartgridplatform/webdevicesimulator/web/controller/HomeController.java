/*
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.webdevicesimulator.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/** Handles requests for the application home page. */
@Controller
public class HomeController {

  /** Simply selects the home view to render by returning its name. */
  @RequestMapping(value = "/", method = RequestMethod.GET)
  public String home() {
    return "redirect:/devices";
  }
}

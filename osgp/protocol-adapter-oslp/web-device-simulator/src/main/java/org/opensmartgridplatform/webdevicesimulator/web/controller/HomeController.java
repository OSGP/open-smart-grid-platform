// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

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

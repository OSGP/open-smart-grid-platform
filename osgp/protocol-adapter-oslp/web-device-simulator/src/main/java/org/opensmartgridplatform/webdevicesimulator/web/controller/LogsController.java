//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.webdevicesimulator.web.controller;

import org.opensmartgridplatform.webdevicesimulator.domain.repositories.OslpLogItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/logs")
public class LogsController {

  @Autowired private OslpLogItemRepository repository;

  @RequestMapping(method = RequestMethod.GET)
  public String showLogs(final Model model) {
    model.addAttribute("logs", this.repository.findAllOrderByModificationTimeDesc());
    return "logs/list";
  }
}

package com.alliander.osgp.webdevicesimulator.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.alliander.osgp.webdevicesimulator.domain.repositories.OslpLogItemRepository;

@Controller
@RequestMapping("/logs")
public class LogsController {

    @Autowired
    private OslpLogItemRepository repository;

    @RequestMapping(method = RequestMethod.GET)
    public String showLogs(final Model model) {
        model.addAttribute("logs", this.repository.findAllOrderByModificationTimeDesc());
        return "logs/list";
    }
}

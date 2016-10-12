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
 * Handles requests for the admin domain.
 */
@Controller
public class AdminController {

    @Autowired
    OsgpAdminClientSoapService osgpAdminClientSoapService;

    @Autowired
    PublicLightingController publicLightingController;

    /**
     * Is called by the add-device page.
     * This controller receives the device from the JSP page and calls the 'updateKeyRequest' in the
     * Soap Client Service. This requests is sent to the Platform, and creates a device.
     *
     * @param device
     * @return
     */
    @RequestMapping(value = "/doAddDevice", method = RequestMethod.POST)
    public ModelAndView addDevice(@ModelAttribute("SpringWeb") final Device device) {

        final ModelAndView modelView = new ModelAndView("add-result");

        if (device.getDeviceIdentification() != null && device.getDeviceIdentification().length() > 2) {
            try {
                this.osgpAdminClientSoapService.updateKeyRequest(device);
            } catch (final SoapFaultClientException e) {
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
     *
     * @return
     */
    @RequestMapping(value = "/addDevice", method = RequestMethod.GET)
    public ModelAndView devices() {
        return new ModelAndView("add-device", "command", new Device());
    }

}

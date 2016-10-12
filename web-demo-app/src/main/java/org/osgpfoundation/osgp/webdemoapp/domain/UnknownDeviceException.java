package org.osgpfoundation.osgp.webdemoapp.domain;

/**
 * Custom exception to handle unknown devices.
 *
 */
public class UnknownDeviceException extends Throwable {

    private static final long serialVersionUID = 1L;

    private String soapFaultMessage;

    public UnknownDeviceException(final String faultStringOrReason) {
        this.soapFaultMessage = faultStringOrReason;
    }

    public String getSoapFaultMessage() {
        return this.soapFaultMessage;
    }

    public void setSoapFaultMessage(final String soapFaultMessage) {
        this.soapFaultMessage = soapFaultMessage;
    }

}

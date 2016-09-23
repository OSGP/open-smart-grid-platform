package org.osgpfoundation.osgp.webdemoapp.domain;

public class UnknownDeviceException extends Throwable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String soapFaultMessage;

	public UnknownDeviceException(String faultStringOrReason) {
		this.soapFaultMessage = faultStringOrReason;
	}

	public String getSoapFaultMessage() {
		return soapFaultMessage;
	}

	public void setSoapFaultMessage(String soapFaultMessage) {
		this.soapFaultMessage = soapFaultMessage;
	}
	
	

}

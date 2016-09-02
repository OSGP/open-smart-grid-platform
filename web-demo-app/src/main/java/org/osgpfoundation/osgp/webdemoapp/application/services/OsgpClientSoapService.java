package org.osgpfoundation.osgp.webdemoapp.application.services;

import org.springframework.ws.client.core.WebServiceTemplate;

import com.alliander.osgp.platform.ws.schema.common.deviceinstallation.AddDeviceRequest;
import com.alliander.osgp.platform.ws.schema.common.deviceinstallation.Device;

public class OsgpClientSoapService {
	
	private SoapRequestHelper soapRequestHelper;
	
	public OsgpClientSoapService () {
		this.soapRequestHelper = new SoapRequestHelper();
	}
	
	public void addDeviceRequest (Device device) {
		AddDeviceRequest request = new AddDeviceRequest ();
		
		request.setDevice(device);
		
		System.out.println("Check before call");
		
		WebServiceTemplate template = this.soapRequestHelper.createAddDeviceRequest();
		
		System.out.println("Check after call");
		
		template.marshalSendAndReceive(request);
		
	}
	
}

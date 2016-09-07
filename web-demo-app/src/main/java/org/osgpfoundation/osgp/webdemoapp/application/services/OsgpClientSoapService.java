package org.osgpfoundation.osgp.webdemoapp.application.services;

import org.springframework.ws.client.core.WebServiceTemplate;

import com.alliander.osgp.platform.ws.schema.admin.devicemanagement.UpdateKeyRequest;
import com.alliander.osgp.platform.ws.schema.common.deviceinstallation.AddDeviceRequest;
import com.alliander.osgp.platform.ws.schema.common.deviceinstallation.Device;
//import com.alliander.osgp.platform.ws.schema.common.

public class OsgpClientSoapService {
	
	private SoapRequestHelper soapRequestHelper;
	
	public OsgpClientSoapService () {
		this.soapRequestHelper = new SoapRequestHelper();
	}
	
	public void addDeviceRequest (Device device) {
		AddDeviceRequest request = new AddDeviceRequest ();
		
		request.setDevice(device);
				
		WebServiceTemplate template = this.soapRequestHelper.createAddDeviceRequest();
		
		
		template.marshalSendAndReceive(request);
		
	}
	
	public void addUpdateKeyRequest (Device device) {
		
		UpdateKeyRequest keyRequest = new UpdateKeyRequest ();
		
		keyRequest.setDeviceIdentification("SSLD_000-00-02");
		keyRequest.setPublicKey("MFkwEwYHKoZIzj0CAQYIKoZIzj0DAQcDQgAEFhUImXFJdqmputquVAc2CPdnn9Ju00M3m/Ice7wABNN+oAYKQbw/OceqvZmFF1+r4nO/vCm/f1JO5nEorE2jNQ==");
		keyRequest.setProtocolInfoId(1);
		
				
		WebServiceTemplate template = this.soapRequestHelper.createUpdateKeyRequest();
		
		
		template.marshalSendAndReceive(keyRequest);
		
	}
	
}

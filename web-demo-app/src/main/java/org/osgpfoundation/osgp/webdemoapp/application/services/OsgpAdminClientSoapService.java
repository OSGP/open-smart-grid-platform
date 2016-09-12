package org.osgpfoundation.osgp.webdemoapp.application.services;

import ma.glasnost.orika.MapperFacade;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.client.core.WebServiceTemplate;

import com.alliander.osgp.platform.ws.schema.admin.devicemanagement.UpdateKeyRequest;

public class OsgpAdminClientSoapService {
	
	@Autowired
	SoapRequestHelper soapRequestHelper;
	
//	@Autowired
//	@Qualifier("adminAdHocMapperFacade")
	private MapperFacade adminAdHocMapperFacade;
	
	public OsgpAdminClientSoapService (MapperFacade mapper) {
		adminAdHocMapperFacade = mapper;
	}
	
	
	public void updateKeyRequest(org.osgpfoundation.osgp.webdemoapp.domain.Device device) {

		UpdateKeyRequest keyRequest = new UpdateKeyRequest();

		keyRequest.setDeviceIdentification(device.getDeviceIdentification());
		keyRequest
				.setPublicKey("MFkwEwYHKoZIzj0CAQYIKoZIzj0DAQcDQgAEFhUImXFJdqmputquVAc2CPdnn9Ju00M3m/Ice7wABNN+oAYKQbw/OceqvZmFF1+r4nO/vCm/f1JO5nEorE2jNQ==");
		keyRequest.setProtocolInfoId(1);

		WebServiceTemplate template = this.soapRequestHelper
				.createUpdateKeyRequest();

		template.marshalSendAndReceive(keyRequest);

	}

}

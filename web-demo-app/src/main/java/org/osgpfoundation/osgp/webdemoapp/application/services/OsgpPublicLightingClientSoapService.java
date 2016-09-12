package org.osgpfoundation.osgp.webdemoapp.application.services;

import java.util.List;

import ma.glasnost.orika.MapperFacade;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.client.core.WebServiceTemplate;

import com.alliander.osgp.platform.ws.schema.publiclighting.adhocmanagement.FindAllDevicesRequest;
import com.alliander.osgp.platform.ws.schema.publiclighting.adhocmanagement.FindAllDevicesResponse;

public class OsgpPublicLightingClientSoapService {
	
	@Autowired
	private SoapRequestHelper soapRequestHelper;

	private MapperFacade publicLightingAdHocMapperFacade;

	public OsgpPublicLightingClientSoapService(MapperFacade mapper) {
		this.publicLightingAdHocMapperFacade = mapper;
		
	}

	public List<org.osgpfoundation.osgp.webdemoapp.domain.Device> findAllDevicesRequest() {
		FindAllDevicesRequest findAllDevicesRequest = new FindAllDevicesRequest();

		WebServiceTemplate template = this.soapRequestHelper
				.createFindAllDevicesRequest();

		FindAllDevicesResponse response = (FindAllDevicesResponse) template
				.marshalSendAndReceive(findAllDevicesRequest);

		List<org.osgpfoundation.osgp.webdemoapp.domain.Device> result = publicLightingAdHocMapperFacade.mapAsList(response.getDevicePage()
				.getDevices(),
				org.osgpfoundation.osgp.webdemoapp.domain.Device.class);
		
		return result;
	}

}

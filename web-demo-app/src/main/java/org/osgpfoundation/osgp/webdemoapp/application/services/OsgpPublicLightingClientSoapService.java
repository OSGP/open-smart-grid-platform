package org.osgpfoundation.osgp.webdemoapp.application.services;

import java.util.List;

import ma.glasnost.orika.MapperFacade;

import org.osgpfoundation.osgp.webdemoapp.domain.DeviceLightStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.client.core.WebServiceTemplate;

import com.alliander.osgp.platform.ws.schema.publiclighting.adhocmanagement.AsyncRequest;
import com.alliander.osgp.platform.ws.schema.publiclighting.adhocmanagement.FindAllDevicesRequest;
import com.alliander.osgp.platform.ws.schema.publiclighting.adhocmanagement.FindAllDevicesResponse;
import com.alliander.osgp.platform.ws.schema.publiclighting.adhocmanagement.GetStatusAsyncRequest;
import com.alliander.osgp.platform.ws.schema.publiclighting.adhocmanagement.GetStatusAsyncResponse;
import com.alliander.osgp.platform.ws.schema.publiclighting.adhocmanagement.GetStatusRequest;
import com.alliander.osgp.platform.ws.schema.publiclighting.adhocmanagement.GetStatusResponse;
import com.alliander.osgp.platform.ws.schema.publiclighting.adhocmanagement.LightValue;
import com.alliander.osgp.platform.ws.schema.publiclighting.adhocmanagement.OsgpResultType;
import com.alliander.osgp.platform.ws.schema.publiclighting.adhocmanagement.SetLightAsyncResponse;
import com.alliander.osgp.platform.ws.schema.publiclighting.adhocmanagement.SetLightRequest;

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
				.createPublicLightingRequest();

		FindAllDevicesResponse response = (FindAllDevicesResponse) template
				.marshalSendAndReceive(findAllDevicesRequest);

		List<org.osgpfoundation.osgp.webdemoapp.domain.Device> result = publicLightingAdHocMapperFacade
				.mapAsList(response.getDevicePage().getDevices(),
						org.osgpfoundation.osgp.webdemoapp.domain.Device.class);

		return result;
	}

	public String setLightRequest(String deviceId, int dimValue, boolean lightOn) {
		SetLightRequest request = new SetLightRequest();
		LightValue lightValue = new LightValue();
		lightValue.setDimValue(dimValue);
		lightValue.setOn(lightOn);

		WebServiceTemplate template = this.soapRequestHelper
				.createPublicLightingRequest();

		request.setDeviceIdentification(deviceId);
		request.getLightValue().add(lightValue);

		SetLightAsyncResponse response = (SetLightAsyncResponse) template
				.marshalSendAndReceive(request);

		return response.getAsyncResponse().getCorrelationUid();
	}
	
	public String switchLightRequest(String deviceId, boolean lightOn) {
		SetLightRequest request = new SetLightRequest();
		LightValue lightValue = new LightValue();
		lightValue.setOn(lightOn);

		WebServiceTemplate template = this.soapRequestHelper
				.createPublicLightingRequest();

		request.setDeviceIdentification(deviceId);
		request.getLightValue().add(lightValue);

		SetLightAsyncResponse response = (SetLightAsyncResponse) template
				.marshalSendAndReceive(request);

		return response.getAsyncResponse().getCorrelationUid();
	}

	public DeviceLightStatus getDeviceStatus(String deviceId) {
		WebServiceTemplate requestTemplate = this.soapRequestHelper
				.createPublicLightingRequest();

		GetStatusRequest request = new GetStatusRequest();
		request.setDeviceIdentification(deviceId);
		GetStatusAsyncResponse asyncStatusResponse = null;

		asyncStatusResponse = (GetStatusAsyncResponse) requestTemplate
				.marshalSendAndReceive(request);

		WebServiceTemplate responseTemplate = this.soapRequestHelper
				.createPublicLightingRequest();

		GetStatusAsyncRequest asyncStatusRequest = new GetStatusAsyncRequest();

		AsyncRequest asyncRequest = new AsyncRequest();

		asyncRequest.setCorrelationUid(asyncStatusResponse.getAsyncResponse()
				.getCorrelationUid());
		asyncRequest.setDeviceId(asyncStatusResponse.getAsyncResponse()
				.getDeviceId());

		asyncStatusRequest.setAsyncRequest(asyncRequest);

		DeviceLightStatus deviceStatus = null;

		boolean responseReceived = false;
		int i = 0;

		while (!responseReceived || i < 10) {
			GetStatusResponse response = (GetStatusResponse) responseTemplate
					.marshalSendAndReceive(asyncStatusRequest);
			OsgpResultType result = response.getResult();
			if (result.value().equals("OK")) {
				responseReceived = true;
				deviceStatus = processStatusResponse(response.getDeviceStatus()
						.getLightValues().get(0), asyncStatusResponse
						.getAsyncResponse().getDeviceId());
				break;
			} else {
				// Wait a while for response
				try {
					Thread.sleep(1000);
				} catch (InterruptedException ie) {
					ie.printStackTrace();
				}
				i++;
			}
		}
		return deviceStatus;
	}

	private DeviceLightStatus processStatusResponse(LightValue lightValue,
			String deviceId) {
		DeviceLightStatus deviceStatus = new DeviceLightStatus();

		deviceStatus.setLightValue(lightValue.getDimValue());
		deviceStatus.setLightOn(lightValue.isOn());
		deviceStatus.setDeviceId(deviceId);

		return deviceStatus;
	}

}

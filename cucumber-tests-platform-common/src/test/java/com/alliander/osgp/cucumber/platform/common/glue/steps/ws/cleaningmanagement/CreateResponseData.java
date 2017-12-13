package com.alliander.osgp.cucumber.platform.common.glue.steps.ws.cleaningmanagement;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.alliander.osgp.adapter.ws.smartmetering.domain.repositories.MeterResponseDataRepository;

import cucumber.api.java.en.Given;

public class CreateResponseData {
	
    @Autowired
    private MeterResponseDataRepository meterResponseDataRepository;
    
    
	@Given("^a record in the meter_response_data of the osgp_adapter_ws_smartmetering database$")
	public void aRecordInTheMeter_response_dataOfTheOsgp_adapter_ws_smartmeteringDatabase(final Map<String, String> settings) throws Throwable {
        
        this.meterResponseDataRepository.save(meterResponseData);
        
	}

	@Given("^a record in the rtu_response_data of the osgp_adapter_ws_microgrids database$")
	public void aRecordInTheRtu_response_dataOfTheOsgp_adapter_ws_microgridsDatabase(final Map<String, String> settings) throws Throwable {

	}

	@Given("^a record in the rtu_response_data of the osgp_adapter_ws_distributionautomation database$")
	public void aRecordInTheRtu_response_dataOfTheOsgp_adapter_ws_distributionautomationDatabase(final Map<String, String> settings) throws Throwable {

	}
	
}

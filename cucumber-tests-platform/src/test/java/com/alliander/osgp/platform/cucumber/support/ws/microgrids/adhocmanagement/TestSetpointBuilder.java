package com.alliander.osgp.platform.cucumber.support.ws.microgrids.adhocmanagement;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.alliander.osgp.adapter.ws.schema.microgrids.adhocmanagement.SetDataRequest;
import com.alliander.osgp.adapter.ws.schema.microgrids.adhocmanagement.SetPoint;

public class TestSetpointBuilder {

    private Map<String, String> settings = new HashMap<>();
    
    @Test
    public void test() {
        settings.put("SetPointNode_1", "SchId");
        settings.put("SetPointNode_2", "SchType");

        SetPointBuilder builder = new SetPointBuilder().withSettings(settings);
        
        SetPoint setPoint = builder.build();
        
        System.out.println(setPoint);
    }

    @Test
    public void test2() {
        settings.put("DeviceIdentification", "TEST123");
        settings.put("SystemId_1" , "1");
        settings.put("SystemType_1", "RTU");
        settings.put("SetPointNode_1", "SchId");
        settings.put("SetPointValue_1", "1");
        settings.put("SetPointId_1", "1");
        settings.put("SetPointNode_2", "SchType");
        settings.put("SetPointValue_2", "1");
        settings.put("SetPointId_1", "1");
        SetDataRequest dataReq = SetDataRequestBuilder.fromParameterMap(settings);
        System.out.println(dataReq);
    }
}

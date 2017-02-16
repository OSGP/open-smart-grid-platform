/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.platform.support.ws.microgrids.adhocmanagement;

import java.util.List;
import java.util.Map;

import com.alliander.osgp.adapter.ws.schema.microgrids.adhocmanagement.GetDataAsyncRequest;
import com.alliander.osgp.adapter.ws.schema.microgrids.adhocmanagement.GetDataRequest;
import com.alliander.osgp.adapter.ws.schema.microgrids.adhocmanagement.MeasurementFilter;
import com.alliander.osgp.adapter.ws.schema.microgrids.adhocmanagement.SystemFilter;
import com.alliander.osgp.adapter.ws.schema.microgrids.common.AsyncRequest;
import com.alliander.osgp.cucumber.platform.Keys;
import com.alliander.osgp.cucumber.platform.core.ScenarioContext;

public class GetDataRequestBuilder {

    private GetDataRequestBuilder() {
        // Private constructor for utility class.
    }

    public static GetDataRequest fromParameterMap(final Map<String, String> requestParameters) {
        final GetDataRequest getDataRequest = new GetDataRequest();
        getDataRequest.setDeviceIdentification(requestParameters.get(Keys.KEY_DEVICE_IDENTIFICATION));
        addSystemFilters(requestParameters, getDataRequest);
        return getDataRequest;
    }

    public static GetDataAsyncRequest fromParameterMapAsync(final Map<String, String> requestParameters) {
        final String correlationUid = (String) ScenarioContext.Current().get(Keys.KEY_CORRELATION_UID);
        if (correlationUid == null) {
            throw new AssertionError("ScenarioContext must contain the correlation UID for key \""
                    + Keys.KEY_CORRELATION_UID + "\" before creating an async request.");
        }
        final String deviceIdentification = requestParameters.get(Keys.KEY_DEVICE_IDENTIFICATION);
        if (deviceIdentification == null) {
            throw new AssertionError("The Step DataTable must contain the device identification for key \""
                    + Keys.KEY_DEVICE_IDENTIFICATION + "\" when creating an async request.");
        }
        final GetDataAsyncRequest getDataAsyncRequest = new GetDataAsyncRequest();
        final AsyncRequest asyncRequest = new AsyncRequest();
        asyncRequest.setCorrelationUid(correlationUid);
        asyncRequest.setDeviceId(deviceIdentification);
        getDataAsyncRequest.setAsyncRequest(asyncRequest);
        return getDataAsyncRequest;
    }

    private static void addSystemFilters(final Map<String, String> requestParameters,
            final GetDataRequest getDataRequest) {

        if (!requestParameters.containsKey(Keys.KEY_NUMBER_OF_SYSTEMS)) {
            throw new AssertionError("The Step DataTable must contain the number of system filters for key \""
                    + Keys.KEY_NUMBER_OF_SYSTEMS + "\" when creating a get data request.");
        }
        final int numberOfSystems = Integer.parseInt(requestParameters.get(Keys.KEY_NUMBER_OF_SYSTEMS));

        final List<SystemFilter> systemFilters = getDataRequest.getSystem();
        for (int i = 0; i < numberOfSystems; i++) {
            addSystemFilter(requestParameters, systemFilters, i);
        }
    }

    private static void addSystemFilter(final Map<String, String> requestParameters,
            final List<SystemFilter> systemFilters, final int systemIndex) {

        final String indexPostfix = "_" + (systemIndex + 1);
        final SystemFilter systemFilter = new SystemFilter();
        systemFilter.setId(Integer.parseInt(requestParameters.get(Keys.KEY_SYSTEM_ID.concat(indexPostfix))));
        systemFilter.setType(requestParameters.get(Keys.KEY_SYSTEM_TYPE.concat(indexPostfix)));
        addMeasurementFilters(requestParameters, systemFilter, systemIndex, indexPostfix);
        systemFilters.add(systemFilter);
    }

    private static void addMeasurementFilters(final Map<String, String> requestParameters,
            final SystemFilter systemFilter, final int systemIndex, final String indexPostfix) {

        if (!requestParameters.containsKey(Keys.KEY_NUMBER_OF_MEASUREMENTS.concat(indexPostfix))) {
            return;
        }
        final int numberOfMeasurements = Integer.parseInt(requestParameters.get(Keys.KEY_NUMBER_OF_MEASUREMENTS
                .concat(indexPostfix)));

        final List<MeasurementFilter> measurementFilters = systemFilter.getMeasurementFilter();
        for (int i = 0; i < numberOfMeasurements; i++) {
            addMeasurementFilter(requestParameters, measurementFilters, systemIndex, i);
        }
    }

    private static void addMeasurementFilter(final Map<String, String> requestParameters,
            final List<MeasurementFilter> measurementFilters, final int systemIndex, final int measurementIndex) {

        final String indexPostfix = "_" + (systemIndex + 1) + "_" + (measurementIndex + 1);
        final MeasurementFilter measurementFilter = new MeasurementFilter();
        if (requestParameters.containsKey(Keys.KEY_MEASUREMENT_FILTER_ID.concat(indexPostfix))) {
            measurementFilter.setId(
                    Integer.parseInt(requestParameters.get(Keys.KEY_MEASUREMENT_FILTER_ID.concat(indexPostfix))));
        }
        measurementFilter.setNode(requestParameters.get(Keys.KEY_MEASUREMENT_FILTER_NODE.concat(indexPostfix)));
        measurementFilters.add(measurementFilter);
    }
}

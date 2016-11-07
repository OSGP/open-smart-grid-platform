/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.platform.cucumber.steps;

/**
 * Defaults within the database.
 */
public class Defaults {
	
	// Labels
	public static final String DEVICE_IDENTIFICATION_LABEL = "DeviceIdentification";
	
	// Values
	public static final String DEFAULT_ORGANIZATION_DESCRIPTION = "Test Organization";
	public static final String DEFAULT_ORGANIZATION_IDENTIFICATION = "test-org";
	public static final String DEFAULT_PREFIX = "MAA";
	public static final String DEFAULT_MANUFACTURER_ID = "Test";
	public static final String DEFAULT_MANUFACTURER_NAME = "Test Manufacturer";
	public static final Boolean DEFAULT_MANUFACTURER_USE_PREFIX = false;
	public static final String DEFAULT_DEVICE_MODEL_MODEL_CODE = "TestModel";
	public static final String DEFAULT_DEVICE_MODEL_DESCRIPTION = "Test Model";
    public static final String DEFAULT_DEVICE_IDENTIFICATION = "TD01"; // Test Device  01
	public static final Boolean DEFAULT_DEVICE_MODEL_METERED = true;

    // Expected values
    public static final String EXPECTED_RESULT_OK = "OK";

    public static final Boolean DEFAULT_HASSCHEDULE = false;

    public static final Boolean DEFAULT_PUBLICKEYPRESENT = false;
}

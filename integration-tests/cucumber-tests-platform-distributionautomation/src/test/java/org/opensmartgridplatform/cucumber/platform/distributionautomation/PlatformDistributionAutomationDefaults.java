/*
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.platform.distributionautomation;

import com.alliander.data.scadameasurementpublishedevent.UnitMultiplier;

public class PlatformDistributionAutomationDefaults {

  public static final String FEEDER_NAME = "feeder-1";
  public static final Integer FEEDER_NUMBER = 1;
  public static final String MQTT_HOST = "0.0.0.0";
  public static final Integer MQTT_PORT = 8883;
  public static final String MQTT_QOS = "EXACTLY_ONCE";
  public static final String PROFILE = "default_controlled_station";
  public static final String SUBSTATION_IDENTIFICATION = "sub-1";
  public static final String SUBSTATION_NAME = "substation-1";
  public static final UnitMultiplier UNIT_MULTIPLIER = UnitMultiplier.none;
}

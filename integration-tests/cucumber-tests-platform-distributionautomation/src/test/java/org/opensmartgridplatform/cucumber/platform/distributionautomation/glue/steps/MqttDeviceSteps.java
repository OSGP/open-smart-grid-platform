/**
 * Copyright 2020 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.platform.distributionautomation.glue.steps;

import java.io.IOException;
import java.util.Map;

import org.opensmartgridplatform.simulator.protocol.mqtt.LnaClient;
import org.opensmartgridplatform.simulator.protocol.mqtt.Simulator;
import org.opensmartgridplatform.simulator.protocol.mqtt.spec.Message;
import org.opensmartgridplatform.simulator.protocol.mqtt.spec.SimulatorSpec;

import io.cucumber.java.en.When;

public class MqttDeviceSteps {
    @When("the mqtt device sends a measurement report")
    public void theDeviceSendsAMeasurementReport(final Map<String, String> parameters) throws IOException {

        final String host = "0.0.0.0";
        final int port = 8883;
        final String topic = "topic";

        final SimulatorSpec spec = new SimulatorSpec(host, port);
        spec.setStartupPauseMillis(2000);
        final Message message = new Message(topic, parameters.get("payload"), 10000);
        final Message[] messages = { message };
        spec.setMessages(messages);
        final Simulator simulator = new Simulator();
        simulator.run(spec, false);
        final LnaClient lnaClient = new LnaClient(spec);
        lnaClient.run();

    }
}

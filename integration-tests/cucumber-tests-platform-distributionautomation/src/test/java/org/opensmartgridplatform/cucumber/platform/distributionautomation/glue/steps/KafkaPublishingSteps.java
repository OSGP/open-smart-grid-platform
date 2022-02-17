/*
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.platform.distributionautomation.glue.steps;

import io.cucumber.java.en.Then;
import java.util.Map;
import java.util.Objects;
import org.opensmartgridplatform.cucumber.platform.distributionautomation.PlatformDistributionAutomationKeys;
import org.opensmartgridplatform.cucumber.platform.distributionautomation.glue.kafka.in.StringMessageConsumer;
import org.springframework.beans.factory.annotation.Autowired;

public class KafkaPublishingSteps {

  @Autowired private StringMessageConsumer distributionAutomationMessageConsumer;

  @Then("a String message is published to Kafka")
  public void aStringMessageIsPublishedToKafka(final Map<String, String> parameters) {
    final String expectedMessage =
        Objects.requireNonNull(
            parameters.get(PlatformDistributionAutomationKeys.PAYLOAD),
            "expected message must be specified as: " + PlatformDistributionAutomationKeys.PAYLOAD);
    this.distributionAutomationMessageConsumer.checkKafkaOutput(expectedMessage);
  }
}

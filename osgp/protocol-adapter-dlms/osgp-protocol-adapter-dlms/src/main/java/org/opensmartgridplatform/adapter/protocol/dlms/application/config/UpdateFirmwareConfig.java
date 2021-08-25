/*
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.application.config;

import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.firmware.ImageTransfer;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.firmware.ImageTransfer.ImageTransferProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UpdateFirmwareConfig {

  @Value("${command.updatefirmware.verificationstatuscheck.interval}")
  private int verificationStatusCheckInterval;

  @Value("${command.updatefirmware.verificationstatuscheck.timeout}")
  private int verificationStatusCheckTimeout;

  @Value("${command.updatefirmware.initiationstatuscheck.interval}")
  private int initiationStatusCheckInterval;

  @Value("${command.updatefirmware.initiationstatuscheck.timeout}")
  private int initiationStatusCheckTimeout;

  @Bean
  public ImageTransferProperties imageTransferProperties() {
    final ImageTransferProperties imageTransferProperties =
        new ImageTransfer.ImageTransferProperties();
    imageTransferProperties.setVerificationStatusCheckInterval(
        this.verificationStatusCheckInterval);
    imageTransferProperties.setVerificationStatusCheckTimeout(this.verificationStatusCheckTimeout);
    imageTransferProperties.setInitiationStatusCheckInterval(this.initiationStatusCheckInterval);
    imageTransferProperties.setInitiationStatusCheckTimeout(this.initiationStatusCheckTimeout);
    return imageTransferProperties;
  }
}

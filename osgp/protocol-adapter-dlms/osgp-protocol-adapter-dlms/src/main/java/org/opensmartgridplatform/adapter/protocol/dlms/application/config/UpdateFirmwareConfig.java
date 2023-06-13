// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

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

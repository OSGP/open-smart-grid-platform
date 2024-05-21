// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.application.config;

import org.opensmartgridplatform.shared.application.config.AbstractConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:osgp-adapter-protocol-dlms.properties")
@PropertySource(value = "file:${osgp/Global/config}", ignoreResourceNotFound = true)
@PropertySource(value = "file:${osgp/AdapterProtocolDlms/config}", ignoreResourceNotFound = true)
public class FirmwareFileStoreConfig extends AbstractConfig {

  @Value("${smartmetering.firmware.path}")
  private String firmwarePath;

  @Value("${smartmetering.firmware.imageidentifier.extention}")
  private String firmwareImageIdentifierExtension;

  public String getFirmwarePath() {
    return this.firmwarePath;
  }

  public String getFirmwareImageIdentifierExtension() {
    return this.firmwareImageIdentifierExtension;
  }
}

//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.shared.infra.jms;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

public class ProtocolResponseMessageValidator {

  private ProtocolResponseMessageValidator() {
    // do not instantiate utility class
  }

  public static boolean isValid(final ProtocolResponseMessage msg, final Logger logger) {
    if (StringUtils.isBlank(msg.getOrganisationIdentification())) {
      logger.error("OrganisationIdentification is blank");
      return false;
    }
    if (StringUtils.isBlank(msg.getDeviceIdentification())) {
      logger.error("DeviceIdentification is blank");
      return false;
    }
    if (StringUtils.isBlank(msg.getCorrelationUid())) {
      logger.error("CorrelationUid is blank");
      return false;
    }
    if (msg.getResult() == null) {
      logger.error("Result is null");
      return false;
    }
    if (StringUtils.isBlank(msg.getDomain())) {
      logger.error("Domain is blank");
      return false;
    }
    if (StringUtils.isBlank(msg.getMessageType())) {
      logger.error("MessageType is blank");
      return false;
    }

    return true;
  }
}

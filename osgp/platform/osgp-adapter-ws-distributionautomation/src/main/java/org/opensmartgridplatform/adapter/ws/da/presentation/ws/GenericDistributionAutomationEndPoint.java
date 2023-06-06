// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.ws.da.presentation.ws;

import org.opensmartgridplatform.adapter.ws.da.application.mapping.DistributionAutomationMapper;
import org.opensmartgridplatform.adapter.ws.da.application.services.DistributionAutomationService;
import org.opensmartgridplatform.shared.exceptionhandling.ComponentType;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;
import org.opensmartgridplatform.shared.exceptionhandling.TechnicalException;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

public class GenericDistributionAutomationEndPoint {
  protected static final String NAMESPACE =
      "http://www.opensmartgridplatform.org/schemas/distributionautomation/defs/2017/04";

  @Autowired protected DistributionAutomationService service;

  @Autowired protected DistributionAutomationMapper mapper;

  protected void handleException(final Logger logger, final Exception e) throws OsgpException {
    // Rethrow exception if it already is a functional or technical
    // exception, otherwise throw new technical exception.
    logger.error("Exception occurred: ", e);
    if (e instanceof OsgpException) {
      throw (OsgpException) e;
    } else {
      throw new TechnicalException(ComponentType.WS_DISTRIBUTION_AUTOMATION, e);
    }
  }
}

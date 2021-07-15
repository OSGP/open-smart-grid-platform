/*
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.shared.application.config.messaging;

/** Names of JMS configurations. See {@link JmsConfigurationFactory}. */
public class JmsConfigurationNames {

  public static final String JMS_INCOMING_WS_REQUESTS = "jms.incoming.ws.requests";

  public static final String JMS_OUTGOING_WS_RESPONSES = "jms.outgoing.ws.responses";

  public static final String JMS_INCOMING_OSGP_CORE_RESPONSES = "jms.incoming.osgp.core.responses";

  public static final String JMS_OUTGOING_OSGP_CORE_REQUESTS = "jms.outgoing.osgp.core.requests";

  public static final String JMS_INCOMING_OSGP_CORE_REQUESTS = "jms.incoming.osgp.core.requests";

  public static final String JMS_OUTGOING_OSGP_CORE_RESPONSES = "jms.outgoing.osgp.core.responses";

  public static final String JMS_SIGNING_SERVER_REQUESTS = "jms.signing.server.requests";
  public static final String JMS_SIGNING_SERVER_RESPONSES = "jms.signing.server.responses";

  public static final String JMS_COMMON_WS_REQUESTS = "jms.common.ws.requests";

  public static final String JMS_COMMON_WS_RESPONSES = "jms.common.ws.responses";

  public static final String JMS_COMMON_DOMAIN_TO_WS_REQUESTS = "jms.common.domain.to.ws.requests";

  public static final String JMS_OSGP_CORE_REQUESTS = "jms.osgp.core.requests";

  public static final String JMS_OSGP_CORE_RESPONSES = "jms.osgp.core.responses";

  public static final String JMS_OSGP_CORE_REQUESTS_INCOMING = "jms.osgp.core.requests.incoming";

  public static final String JMS_OSGP_CORE_RESPONSES_INCOMING = "jms.osgp.core.responses.incoming";

  public static final String JMS_LOGGING_OUTGOING_LOG_ITEM_REQUESTS =
      "jms.logging.outgoing.log.item.requests";

  public static final String JMS_ADMIN_LOGGING = "jms.admin.logging";

  public static final String JMS_ADMIN_REQUESTS = "jms.admin.requests";

  public static final String JMS_ADMIN_RESPONSES = "jms.admin.responses";

  public static final String JMS_COMMON_LOGGING = "jms.common.logging";

  public static final String JMS_COMMON_REQUESTS = "jms.common.requests";

  public static final String JMS_COMMON_RESPONSES = "jms.common.responses";

  public static final String JMS_MICROGRIDS_REQUESTS = "jms.microgrids.requests";

  public static final String JMS_MICROGRIDS_RESPONSES = "jms.microgrids.responses";

  public static final String JMS_MICROGRIDS_LOGGING = "jms.microgrids.logging";

  public static final String JMS_PUBLICLIGHTING_REQUESTS = "jms.publiclighting.requests";

  public static final String JMS_PUBLICLIGHTING_RESPONSES = "jms.publiclighting.responses";

  public static final String JMS_PUBLICLIGHTING_LOGGING = "jms.publiclighting.logging";

  public static final String JMS_SMARTMETERING_REQUESTS = "jms.smartmetering.requests";

  public static final String JMS_SMARTMETERING_RESPONSES = "jms.smartmetering.responses";

  public static final String JMS_SMARTMETERING_LOGGING = "jms.smartmetering.logging";

  public static final String JMS_TARIFFSWITCHING_REQUESTS = "jms.tariffswitching.requests";

  public static final String JMS_TARIFFSWITCHING_RESPONSES = "jms.tariffswitching.responses";

  public static final String JMS_TARIFFSWITCHING_LOGGING = "jms.tariffswitching.logging";

  public static final String JMS_PROTOCOL_LOG_ITEM_REQUESTS = "jms.protocol.log.item.requests";

  private JmsConfigurationNames() {
    // Empty private constructor to prevent creating an instance of this
    // utility class.
  }
}

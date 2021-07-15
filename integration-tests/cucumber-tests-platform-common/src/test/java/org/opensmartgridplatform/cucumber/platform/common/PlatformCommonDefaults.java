/*
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.platform.common;

import org.opensmartgridplatform.adapter.ws.schema.admin.devicemanagement.PlatformFunctionGroup;
import org.opensmartgridplatform.adapter.ws.schema.core.configurationmanagement.LightType;
import org.opensmartgridplatform.adapter.ws.schema.core.configurationmanagement.LinkType;
import org.opensmartgridplatform.adapter.ws.schema.core.devicemanagement.DeviceFunctionGroup;

public class PlatformCommonDefaults
    extends org.opensmartgridplatform.cucumber.platform.PlatformDefaults {

  public static final LightType CONFIGURATION_LIGHTTYPE = null;
  public static final LinkType CONFIGURATION_PREFERRED_LINKTYPE = null;
  public static final LightType DEFAULT_CONFIGURATION_LIGHTTYPE = LightType.RELAY;
  public static final LinkType DEFAULT_CONFIGURATION_PREFERRED_LINKTYPE = LinkType.ETHERNET;
  public static final PlatformFunctionGroup DEFAULT_NEW_ORGANIZATION_PLATFORMFUNCTIONGROUP =
      PlatformFunctionGroup.ADMIN;
  public static final DeviceFunctionGroup DEVICE_FUNCTION_GROUP = DeviceFunctionGroup.OWNER;
  public static final boolean REVOKED = false;
}

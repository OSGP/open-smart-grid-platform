// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

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

// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.cucumber.platform.publiclighting;

import org.opensmartgridplatform.adapter.ws.schema.publiclighting.adhocmanagement.TransitionType;
import org.opensmartgridplatform.adapter.ws.schema.publiclighting.common.OsgpResultType;
import org.opensmartgridplatform.oslp.Oslp.LightType;
import org.opensmartgridplatform.oslp.Oslp.LinkType;
import org.opensmartgridplatform.oslp.Oslp.Status;

public class PlatformPubliclightingDefaults
    extends org.opensmartgridplatform.cucumber.platform.PlatformDefaults {
  public static final LinkType DEFAULT_ACTUAL_LINKTYPE = LinkType.LINK_NOT_SET;
  public static final Status DEFAULT_STATUS = Status.OK;
  public static final TransitionType DEFAULT_TRANSITION_TYPE = TransitionType.DAY_NIGHT;
  public static final OsgpResultType DEFAULT_PUBLICLIGHTING_STATUS = OsgpResultType.OK;
  public static final LinkType DEFAULT_PREFERRED_LINKTYPE = LinkType.LINK_NOT_SET;
  public static final LightType DEFAULT_LIGHTTYPE = LightType.LT_NOT_SET;
  public static final String FIRMARE_DOMAIN = "localhost";
  public static final String FIRMWARE_URL = "firmware/" + FIRMWARE_IDENTIFICATION;
  public static String DEFAULT_RELAY_CONFIGURATION = "";
  public static String DEFAULT_DC_MAP = "";
  public static Integer DEFAULT_OSLP_PORT = 12122;
}

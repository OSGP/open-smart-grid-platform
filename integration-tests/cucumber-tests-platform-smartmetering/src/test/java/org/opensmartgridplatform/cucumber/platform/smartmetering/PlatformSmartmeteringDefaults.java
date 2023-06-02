//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.cucumber.platform.smartmetering;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.opensmartgridplatform.cucumber.platform.PlatformDefaults;
import org.opensmartgridplatform.domain.core.entities.DeviceModel;
import org.opensmartgridplatform.domain.core.entities.ProtocolInfo;

/**
 * Defaults specific for the dlms related data. Note: Keep in mind that generic defaults should be
 * specified in the cucumber-tests-platform project.
 */
public class PlatformSmartmeteringDefaults
    extends org.opensmartgridplatform.cucumber.platform.PlatformDefaults {

  public static final String ALIAS = null;
  public static final Integer CHALLENGE_LENGTH = null;
  public static final Long CLIENT_ID = null;
  public static final String COMMUNICATION_METHOD = "GPRS";
  public static final String COMMUNICATION_PROVIDER = "KPN";
  public static final String CONTAINER_CITY = null;
  public static final String CONTAINER_MUNICIPALITY = null;
  public static final Integer CONTAINER_NUMBER = null;
  public static final String CONTAINER_NUMBER_ADDITION = null;
  public static final String CONTAINER_POSTAL_CODE = null;
  public static final String CONTAINER_STREET = null;
  public static final String DEVICE_IDENTIFICATION = null;
  public static final DeviceModel DEVICE_MODEL = null;
  public static final String DEVICE_TYPE = null;
  public static final String GATEWAY_DEVICE_IDENTIFICATION = null;
  public static final Float GPS_LATITUDE = null;
  public static final Float GPS_LONGITUDE = null;

  public static final boolean HLS3ACTIVE = false;
  public static final boolean HLS4ACTIVE = false;
  public static final boolean HLS5ACTIVE = true;
  public static final String ICC_ID = "iccid";
  public static final boolean LLS1_ACTIVE = false;
  public static final boolean USE_HDLC = false;
  public static final boolean USE_SN = false;
  public static final boolean POLYPHASE = false;

  public static final boolean IN_DEBUG_MODE = false;
  public static final boolean IN_MAINTENANCE = false;
  public static final boolean IP_ADDRESS_IS_STATIC = true;
  public static final boolean IS_ACTIVATED = true;
  public static final boolean IS_ACTIVE = true;
  public static final boolean CLOSE_OPTICAL_PORT = false;

  public static final Long LOGICAL_ID = 1L;
  public static final InetAddress NETWORK_ADDRESS;
  public static final Long PORT = 1024L;
  public static final String PROTOCOL = "DSMR";
  public static final String PROTOCOL_VERSION = "4.2.2";

  public static final Map<Long, ProtocolInfo> PORT_MAPPING = new HashMap<>();

  static {
    PORT_MAPPING.put(
        1024L,
        new ProtocolInfo.Builder().withProtocol("DSMR").withProtocolVersion("4.2.2").build());
    PORT_MAPPING.put(
        1026L, new ProtocolInfo.Builder().withProtocol("DSMR").withProtocolVersion("2.2").build());
    PORT_MAPPING.put(
        1027L, new ProtocolInfo.Builder().withProtocol("SMR").withProtocolVersion("5.0.0").build());
    PORT_MAPPING.put(
        1028L, new ProtocolInfo.Builder().withProtocol("SMR").withProtocolVersion("5.1").build());
    PORT_MAPPING.put(
        1029L, new ProtocolInfo.Builder().withProtocol("SMR").withProtocolVersion("5.2").build());
    PORT_MAPPING.put(
        1030L, new ProtocolInfo.Builder().withProtocol("SMR").withProtocolVersion("5.5").build());
    PORT_MAPPING.put(
        1031L, new ProtocolInfo.Builder().withProtocol("SMR").withProtocolVersion("4.3").build());
  }

  public static final Long INVOCATION_COUNTER = 12345L;

  public static final String SECURITY_KEY_TYPE_A = "E_METER_AUTHENTICATION";
  public static final String SECURITY_KEY_TYPE_E = "E_METER_ENCRYPTION";

  public static final String SECURITY_KEY_TYPE_M = "E_METER_MASTER";
  public static final boolean SELECTIVE_ACCESS_SUPPORTED = false;
  public static final String SMART_METER_E = "SMART_METER_E";
  public static final String SMART_METER_G = "SMART_METER_G";
  public static final Date TECHNICAL_INSTALLATION_DATE = new Date();
  public static final Date VALID_TO = null;
  public static final Long VERSION = 0L;
  public static final boolean WITH_LIST_SUPPORTED = false;

  public static final String DAYLIGHT_SAVINGS_BEGIN = "FFFF03FE0702000000FFC400";
  public static final String DAYLIGHT_SAVINGS_END = "FFFF0AFE0703000000FF8880";
  public static final byte DAYLIGHT_SAVINGS_DEVIATION = 60;
  public static final boolean DAYLIGHT_SAVINGS_ENABLED = true;
  public static final short TIME_ZONE_OFFSET = -60;
  public static final Boolean DAYLIGHT_SAVINGS_ACTIVE = false;
  public static final Byte DEVIATION = -60;

  static {
    InetAddress localhost;
    try {
      localhost = InetAddress.getByName(PlatformDefaults.LOCALHOST);
    } catch (final UnknownHostException e) {
      localhost = null;
    }
    NETWORK_ADDRESS = localhost;
  }
}

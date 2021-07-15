/*
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.platform.smartmetering;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;
import org.opensmartgridplatform.cucumber.platform.PlatformDefaults;
import org.opensmartgridplatform.domain.core.entities.DeviceModel;

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
  public static final String PASSWORD = "e7233dec0dfbf031960e21c149c3293e";

  public static final boolean IN_DEBUG_MODE = false;
  public static final boolean IN_MAINTENANCE = false;
  public static final boolean IP_ADDRESS_IS_STATIC = true;
  public static final boolean IS_ACTIVATED = true;
  public static final boolean IS_ACTIVE = true;

  public static final Long LOGICAL_ID = 1L;
  public static final InetAddress NETWORK_ADDRESS;
  public static final Long PORT = 1024L;
  public static final String PROTOCOL = "DSMR";
  public static final String PROTOCOL_VERSION = "4.2.2";
  public static final String SECURITY_KEY_A_DB =
      "c19fe80a22a0f6c5cdaad0826c4d204f23694ded08d811b66e9b845d9f2157d2";
  public static final String SECURITY_KEY_E_DB =
      "867424ac75b6d53c89276d304608321f0a1f6e401f453f84adf3477c7ee1623c";
  public static final String SECURITY_KEY_M_DB =
      "55dc88791e6c8f6aff4c8be7714fb8d2ae3d02693ec474593acd3523ee032638";
  public static final String SECURITY_KEY_A_XML =
      "9eab9df8169a9c22d694067435b584d573b1a57d62d491b58fd9058e994861666831fb9f5ddbf5aba9ef"
          + "169256cffc8e540c34b3f92246d062889eca13639fe317e92beec86b48b14d5ef4b74682497eed7d8ea3ae6ea3dfa1877045653cb989146f826b2d97a3294a2aa22f804b1f389d06"
          + "84482dde33e6cdfc51700156e3be94fc8d5b3a1302b3f3992564982e7cd7885c26fa96eeb7cab5a13d6d7fd341f665d61581dd71f652dc278823216ab75b5a430edc826021c4a2dc"
          + "9de95fbdfb0e79421e2662743650690bc6b69b0b91035e96cb6396626aa1c252cddf87046dc53b9da0c8d74b517c2845b2e8eaaf72e97d41df1c4ce232e7bb082c82154e9ae5";
  public static final String SECURITY_KEY_E_XML =
      "4e6fb5bd62d7a21f87438c04f518939cce7cfe8259ff40d9e3ff4a3a8c3befdad191eb066c8332d6d3066a2e"
          + "d866774616c2b893da4543998eb57fcf35323cd2b41960e857c1a99f5cb59405081712ab23da97353014f500046756eab2620d13a269b83cbefbdfb5e275862b34dd407fd745a1bc"
          + "a18f1b66cb114641212579c6da03e86be2973f8dd6988b15bb6e9ef0f5637827829fc2241891c050a95ef5fc787f740a40aa2d528c69f99c76ad380bba3725929fcbe11ab72cf61e"
          + "342ab95fc3b883372c110830f28144894aa2919a590822b1e594b807e86f49093982b871c658db0b6c08a90bae55c731efb3d40f245d8c0ad1478b55fa68cced3c1386a7";
  public static final String SECURITY_KEY_G_ENCRYPTION =
      "867424ac75b6d53c89276d304608321f0a1f6e401f453f84adf3477c7ee1623c";
  public static final String SECURITY_KEY_G_MASTER =
      "867424ac75b6d53c89276d304608321f0a1f6e401f453f84adf3477c7ee1623c";
  public static final String SECURITY_KEY_M_XML =
      "6fa7f5f19812391b2803a142f17c67aa0e3fc23b537ae6f9cd34a850d4fd5f4d60a3b2bdd6f8cb356e00e6c4e104"
          + "fb5ea521eeabd8cb69d8f7a5cbe2b20e010c089ee346aaa13c9abdc5e0c9ba0fcafff53d2dcd3c1b7a8ee3c3f76e0d00fcd043940586f055c5e19a0fa7eeff6a7894e128029eaf11"
          + "c1734565f3f5b614bfab9ea5ce24bf34d2e59878dc2401bd175333315ce197d4243dced9c4e28a23bc91dca432985debe81cf5912df7e99b28f596f335e80678d7b5d1edc93be8bf"
          + "22d77b2e172ccd7c6907454a983999840bf540343d281e8f9871386f005fe40065fcbe218bdc605be4e759cb1b8d5760eab7b8ceb95cfae2224c15045834962f9b6b";
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

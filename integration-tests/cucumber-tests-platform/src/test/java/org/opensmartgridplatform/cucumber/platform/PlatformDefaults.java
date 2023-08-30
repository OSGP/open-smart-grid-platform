// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.cucumber.platform;

import java.time.ZonedDateTime;
import org.joda.time.DateTime;
import org.opensmartgridplatform.domain.core.valueobjects.DeviceLifecycleStatus;
import org.opensmartgridplatform.domain.core.valueobjects.RelayType;

/** Defaults within the database. */
public class PlatformDefaults {

  public static final Boolean DEFAULT_ACTIVATED = true;
  public static final Boolean DEFAULT_ACTIVE = true;
  public static final DeviceLifecycleStatus DEFAULT_DEVICE_LIFECYCLE_STATUS =
      DeviceLifecycleStatus.IN_USE;
  public static final Integer DEFAULT_ACTUAL_CONSUMED_ENERGY = 96;
  public static final Integer DEFAULT_ACTUAL_CONSUMED_POWER = 48;
  public static final Integer DEFAULT_ACTUAL_CURRENT1 = 1;
  public static final Integer DEFAULT_ACTUAL_CURRENT2 = 2;
  public static final Integer DEFAULT_ACTUAL_CURRENT3 = 3;
  public static final Integer DEFAULT_ACTUAL_POWER1 = 1;
  public static final Integer DEFAULT_ACTUAL_POWER2 = 2;
  public static final Integer DEFAULT_ACTUAL_POWER3 = 3;
  public static final String DEFAULT_ALIAS = "";
  public static final Boolean DEFAULT_ALLOWED = false;

  public static final Integer DEFAULT_AVERAGE_POWER_FACTOR1 = 1;

  public static final Integer DEFAULT_AVERAGE_POWER_FACTOR2 = 2;
  public static final Integer DEFAULT_AVERAGE_POWER_FACTOR3 = 3;
  public static final String DEFAULT_BEGIN_DATE = "";

  public static final Short DEFAULT_CDMA_BATCH_NUMBER = 2;
  public static final String DEFAULT_CDMA_MAST_SEGMENT = "300/3";

  public static final Short DEFAULT_CHANNEL = (short) 1;
  public static final String DEFAULT_CONTAINER_CITY = "StandardCity";
  public static final String DEFAULT_CONTAINER_MUNICIPALITY = "GreaterStandardArea";
  public static final Integer DEFAULT_CONTAINER_NUMBER = 123;
  public static final String DEFAULT_CONTAINER_NUMBER_ADDITION = "A";
  public static final String DEFAULT_CONTAINER_POSTALCODE = "6546KM";
  public static final String DEFAULT_CONTAINER_STREET = "StandardStreet";
  public static final String DEFAULT_CORRELATION_UID = "correlation-uid-1";

  public static final String DEFAULT_DC_LIGHTS = "0";
  public static final Long DEFAULT_DEVICE_ID = new java.util.Random().nextLong();

  public static final String DEFAULT_DEVICE_IDENTIFICATION = "TD01";
  public static final String DEFAULT_DEVICE_MODEL_DESCRIPTION = "Test Model";
  public static final String DEFAULT_DEVICE_MODEL_MANUFACTURER = "Test";
  public static final String DEFAULT_DEVICE_MODEL_MODEL_CODE = "Test";
  public static final String DEFAULT_DEVICE_MODEL_NAME = "Test Model";
  public static final String DEFAULT_DEVICE_OUTPUT_SETTING_ALIAS = "Continues burner";
  public static final Integer DEFAULT_DEVICE_OUTPUT_SETTING_EXTERNALID = 1;
  public static final Integer DEFAULT_DEVICE_OUTPUT_SETTING_INTERNALID = 1;
  public static final String DEFAULT_SCHEDULE_CODE = "ScheduleCode";
  public static final String CODE = "Code";

  public static final String DEFAULT_DEVICE_TYPE = "SSLD";

  public static final Integer DEFAULT_DIMVALUE = 100;
  public static final String DEFAULT_DOMAINS = "COMMON;PUBLIC_LIGHTING;TARIFF_SWITCHING";

  public static final String DEFAULT_EAN_DESCRIPTION = "dummy EAN description";
  public static final String DEFAULT_END_DATE = "";
  public static final String DEFAULT_EVENT_DESCRIPTION = "Event description";
  public static final String DEFAULT_EVENTNOTIFICATIONS = "";
  public static final String DEFAULT_EVENTNOTIFICATIONTYPES = "";
  public static final Integer DEFAULT_EXTERNALID = 0;
  public static final Integer DEFAULT_FAILED_CONNECTION_COUNT = 0;
  public static final Boolean DEFAULT_FILESTORAGE = true;

  public static final String DEFAULT_FIRMWARE_IDENTIFICATION = "";
  public static final Boolean DEFAULT_HASSCHEDULE = false;
  public static final Boolean DEFAULT_INDEBUGMODE = false;
  public static final Integer DEFAULT_INDEX = 0;
  public static final Integer DEFAULT_INTERNALID = 0;

  public static final Boolean DEFAULT_IS_ACTIVATED = true;
  public static final Boolean DEFAULT_ISIMMEDIATE = false;
  public static final String DEFAULT_INTEGRATION_TYPE = "WEB_SERVICE";
  public static final Float DEFAULT_LATITUDE = null;
  public static final String DEFAULT_LATITUDE_STRING = null;
  public static final String DEFAULT_LIGHTVALUES = "";
  public static final String DEFAULT_TARIFFVALUES = "";

  public static final Float DEFAULT_LONGITUDE = null;
  public static final String DEFAULT_LONGITUDE_STRING = null;
  public static final String DEFAULT_MANUFACTURER_CODE = "Test";
  public static final String DEFAULT_MANUFACTURER_NAME = "Test";
  public static final Boolean DEFAULT_MANUFACTURER_USE_PREFIX = false;
  public static final String DEFAULT_NETWORK_ADDRESS = "127.0.0.1";
  public static final String DEFAULT_NEW_ORGANIZATION_IDENTIFICATION = "NewOrganization";
  public static final String DEFAULT_NEW_ORGANIZATION_NAME = "New Organization";
  public static final Boolean DEFAULT_ON = true;
  public static final String DEFAULT_ORGANIZATION_DESCRIPTION = "Test Organization";
  public static final String DEFAULT_ORGANIZATION_DOMAINS =
      "COMMON;PUBLIC_LIGHTING;TARIFF_SWITCHING";
  public static final Boolean DEFAULT_ORGANIZATION_ENABLED = true;
  public static final String DEFAULT_ORGANIZATION_IDENTIFICATION = "test-org";
  public static final String DEFAULT_ORGANIZATION_IDENTIFICATION_TO_FIND = "test-org";
  public static final String DEFAULT_DELEGATE_ORGANIZATION_IDENTIFICATION = "test-org";
  public static final String DEFAULT_ORGANIZATION_NAME = "Test organization";
  public static final String DEFAULT_ORGANIZATION_PREFIX = "cgi";

  public static final String DEFAULT_OWNER = "test-org";
  public static final Short DEFAULT_PAGE = 0;
  public static final Integer DEFAULT_PAGE_SIZE = 25;

  public static final String DEFAULT_PERIOD_TYPE = "INTERVAL";
  public static final String DEFAULT_PREFIX = "MAA";
  public static final String DEFAULT_PROTOCOL = "OSLP ELSTER";
  public static final String DEFAULT_PROTOCOL_VERSION = "1.0";
  public static final String DEFAULT_PROTOCOL_VARIANT = null;
  public static final String DEFAULT_PUBLIC_KEY = "123456abcdef";
  public static final Boolean DEFAULT_PUBLICKEYPRESENT = true;
  public static final String DEFAULT_PUBLICLIGHTING_DESCRIPTION = "";
  public static final String DEFAULT_RECORD_TIME = "";

  public static final String DEFAULT_SCHEDULED_TIME = "tomorrow at noon";
  public static final String DEFAULT_SMART_METER_DEVICE_IDENTIFICATION = "TEST1024000000001";
  public static final String DEFAULT_SMART_METER_GAS_DEVICE_IDENTIFICATION = "TESTG102400000001";
  public static final String DEFAULT_SUPPLIER = "Kaifa";
  public static final DateTime DEFAULT_TECHNICAL_INSTALLATION_DATE = DateTime.now().minusDays(1);
  public static final ZonedDateTime DEFAULT_TECHNICAL_INSTALLATION_DATE_ZONED =
      ZonedDateTime.now().minusDays(1);
  public static final Integer DEFAULT_TOTAL_LIGHTING_HOURS = 144;

  public static final Boolean DEFAULT_USE_PAGES = true;
  public static final String DEFAULT_USER_NAME = "Cucumber";
  public static final String DEVICE_MODEL_MODEL_CODE = "Test";
  public static final String DEVICE_MODEL_NAME = "Testmodel";
  public static final String DLMS_DEFAULT_COMMUNICATION_METHOD = "GPRS";
  public static final String DLMS_DEFAULT_DEVICE_TYPE = "SMART_METER_E";

  public static final Boolean DLMS_DEFAULT_LLS1_ACTIVE = false;
  public static final Boolean DLMS_DEFAULT_HSL3_ACTIVE = false;
  public static final Boolean DLMS_DEFAULT_HSL4_ACTIVE = false;
  public static final Boolean DLMS_DEFAULT_HSL5_ACTIVE = true;

  public static final Boolean DLMS_DEFAULT_IP_ADDRESS_IS_STATIC = true;
  public static final Long DLMS_DEFAULT_LOGICAL_ID = 1L;
  public static final Long DLMS_DEFAULT_PORT = 1024L;
  public static final String DLMS_DEFAULT_DEVICE_DELIVERY_DATE = "2016-05-11T00:00:00.000Z";
  public static final String DEFAULT_DEVICE_PROTOCOL_NAME = "DLMS";
  public static final String EMAIL = "someone@somewhere.nl";
  public static final Boolean EVENTS_NODELIST_EXPECTED = false;
  public static final String EXPECTED_RESULT_OK = "OK";
  public static final DateTime EXPIRYDATECONTRACT = DateTime.now().plusWeeks(1);
  public static final String PHONENUMBER = "+31 43 1234567";
  public static final org.opensmartgridplatform.domain.core.valueobjects.PlatformFunctionGroup
      PLATFORM_FUNCTION_GROUP =
          org.opensmartgridplatform.domain.core.valueobjects.PlatformFunctionGroup.ADMIN;
  public static final org.opensmartgridplatform.domain.core.valueobjects.DeviceFunctionGroup
      DEVICE_FUNCTION_GROUP =
          org.opensmartgridplatform.domain.core.valueobjects.DeviceFunctionGroup.OWNER;
  public static final String SMART_METER_E = "SMART_METER_E";
  public static final String SMART_METER_G = "SMART_METER_G";

  public static final boolean FIRMWARE_ACTIVE = true;
  public static final String FIRMWARE_IDENTIFICATION = "F01";
  public static final Boolean FIRMWARE_PUSH_TO_NEW_DEVICE = false;
  public static final String DC_LIGHTS = "";
  public static final int FIRMWARE_ID = 0;
  public static final String FIRMWARE_DESCRIPTION = "Test Firmware";
  public static final Boolean IN_MAINTENANCE = false;

  public static final String FIRMWARE_MODULE_VERSION_COMM = "1";
  public static final String FIRMWARE_MODULE_VERSION_FUNC = "2";
  public static final String FIRMWARE_MODULE_VERSION_MA = "3";
  public static final String FIRMWARE_MODULE_VERSION_MBUS = "4";
  public static final String FIRMWARE_MODULE_VERSION_SEC = "5";
  public static final String FIRMWARE_MODULE_VERSION_M_BUS_DRIVER_ACTIVE = "6";
  public static final String FIRMWARE_MODULE_VERSION_SIMPLE = "7";
  public static final String FIRMWARE_INSTALLED_BY = "Test User";
  public static final String FIRMWARE_INSTALLATION_DATE = "now";
  public static final Boolean FIRMWARE_FILE_EXISTS = true;
  public static final String FIRMWARE_FILENAME = "firmware.txt";

  public static final Boolean DEVICE_IN_MAINTENANCE = false;

  public static final String LOCALHOST = "127.0.0.1";
  public static final Integer OSLP_SERVER_PORT = 12121;
  public static final Integer RANDOM_DEVICE = 0;
  public static final Integer OSLP_ELSTER_SERVER_PORT = 12122;

  public static final Long DEFAULT_SCHEDULE_VERSION = 1L;
  public static final String SMSTYPE = "?";
  public static final String LIGHTMEASUREMENT_LASTMESSAGE = "";
  public static final Integer SMS_INDEX = 1;

  public static final boolean DEFAULT_BYPASS_RETRY = false;

  public static final int DEFAULT_KEY_RELAY = -1;
  public static final String DEFAULT_KEY_RELAYFUNCTION = "Relais 2";

  public static final String DEFAULT_TYPE_DAY = "Alle dagen";
  public static final String DEFAULT_TIME = "12:00";

  public static final String TIMESTAMP = "now";
  public static final Integer REQUESTED_PAGE = 0;

  public static final Integer RELAY_OVERVIEW_INDEX = 1;
  public static final Boolean LAST_SWITCHING_EVENT_STATE = false;
  public static final Boolean LAST_KNOWN_STATE = false;

  public static final DateTime DATE_NOW = DateTime.now();
  public static final String TODAY = "today";
  public static final String DEVICE_UID = "dGVzdDEyMzQ1Njc4";

  public static final RelayType DEFAULT_DEVICE_OUTPUT_SETTING_RELAY_TYPE = RelayType.LIGHT;
  public static final String DEVICE_OUTPUT_SETTINGS =
      DEFAULT_DEVICE_OUTPUT_SETTING_INTERNALID
          + ", "
          + DEFAULT_DEVICE_OUTPUT_SETTING_EXTERNALID
          + ", "
          + DEFAULT_DEVICE_OUTPUT_SETTING_RELAY_TYPE
          + ", "
          + DEFAULT_DEVICE_OUTPUT_SETTING_ALIAS;
  public static final String RELAY_STATUSES =
      DEFAULT_DEVICE_OUTPUT_SETTING_INTERNALID
          + ", "
          + DEFAULT_DEVICE_OUTPUT_SETTING_EXTERNALID
          + ", "
          + DEFAULT_DEVICE_OUTPUT_SETTING_RELAY_TYPE
          + ", "
          + DEFAULT_DEVICE_OUTPUT_SETTING_ALIAS;

  public static final Short DEFAULT_ASTRONOMICAL_SUNRISE_OFFSET = 0;
  public static final Short DEFAULT_ASTRONOMICAL_SUNSET_OFFSET = 0;
  public static final Long NON_EXISTENT_PROTOCOL_INFO_ID = -1L;

  public static final String DOMAIN = "DISTRIBUTION_AUTOMATION";
  public static final String DOMAIN_VERSION = "1.0";

  public static final String DEFAULT_LMD_DESCRIPTION = "LmdDescription";
  public static final String DEFAULT_LMD_CODE = "LmdCode";
  public static final String DEFAULT_LMD_COLOR = "LmdColor";
  public static final Short DEFAULT_LMD_DIGITAL_INPUT = 1;

  public static final Integer DEFAULT_BTS_ID = 10;
  public static final Integer DEFAULT_CELL_ID = 1;
}

/**
 * Copyright 2016-2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.platform.smartmetering;

/**
 * Keys specific for the dlms related data. Note: Keep in mind that generic keys
 * should be specified in the cucumber-tests-platform project.
 */
public class PlatformSmartmeteringKeys extends org.opensmartgridplatform.cucumber.platform.PlatformKeys {

    public static final String ALIAS = "Alias";
    public static final String CHALLENGE_LENGTH = "ChallengeLength";
    public static final String CHANNEL = "Channel";
    public static final String CLIENT_ID = "ClientId";
    public static final String COMMUNICATION_METHOD = "CommunicationMethod";
    public static final String COMMUNICATION_PROVIDER = "CommunicationProvider";
    public static final String CONTAINER_CITY = "ContainerCity";
    public static final String CONTAINER_MUNICIPALITY = "ContainerMunicipality";
    public static final String CONTAINER_NUMBER = "ContainerNumber";
    public static final String CONTAINER_POSTAL_CODE = "ContainerPostalCode";
    public static final String CONTAINER_STREET = "ContainerStreet";
    public static final String CORRELATION_UID = "CorrelationUid";
    public static final String SMART_METER_E = "SmartMeterE";
    // Default keys for dlms_device
    public static final String DEVICE_IDENTIFICATION = "DeviceIdentification";
    public static final String DEVICE_MODEL_CODE = "DeviceModelCode";

    // other
    public static final String DEVICE_TYPE = "DeviceType";
    public static final String GAS_DEVICE_IDENTIFICATION = "GasDeviceIdentification";

    /**
     * Use this device identification key in sending the request to OSGP
     */
    public static final String GATEWAY_DEVICE_IDENTIFICATION = "GatewayDeviceIdentification";
    public static final String MBUS_DEVICE_IDENTIFICATION = "MbusDeviceIdentification";

    public static final String GPS_LATITUDE = "GpsLatitude";
    public static final String GPS_LONGITUDE = "GpsLongitude";
    public static final String HLS3ACTIVE = "Hls3active";
    public static final String HLS4ACTIVE = "Hls4active";
    public static final String HLS5ACTIVE = "Hls5active";
    public static final String LLS1_ACTIVE = "Lls1active";
    public static final String USE_HDLC = "UseHdlc";
    public static final String USE_SN = "UseSn";
    public static final String ICC_ID = "IccId";
    public static final String IN_DEBUG_MODE = "InDebugMode";
    public static final String IN_MAINTENANCE = "InMaintenance";
    public static final String IP_ADDRESS_IS_STATIC = "IpAddressIsStatic";

    public static final String IS_ACTIVATED = "Activated";
    public static final String IS_ACTIVE = "Active";
    public static final String LOGICAL_ID = "LogicalId";
    public static final String MODULE_ACTIVE_FIRMWARE = "module_active_firmware";
    public static final String COMM_MODULE_ACTIVE_FIRMWARE = "communication_module_active_firmware";
    public static final String ACTIVE_FIRMWARE = "active_firmware";
    public static final String KEY_DEVICE_MASTERKEY = "Master_key";
    public static final String KEY_DEVICE_AUTHENTICATIONKEY = "Authentication_key";
    public static final String KEY_DEVICE_ENCRYPTIONKEY = "Encryption_key";
    public static final String KEY_DEVICE_ENCRYPTION_KEY_STATUS = "EncryptionKeyStatus";
    public static final String MBUS_DEFAULT_KEY = "MbusDefaultKey";
    public static final String MBUS_USER_KEY = "MbusUserKey";
    public static final String PASSWORD = "Password";

    public static final String OBIS_CODE_A = "ObisCodeA";
    public static final String OBIS_CODE_B = "ObisCodeB";
    public static final String OBIS_CODE_C = "ObisCodeC";
    public static final String OBIS_CODE_D = "ObisCodeD";
    public static final String OBIS_CODE_E = "ObisCodeE";
    public static final String OBIS_CODE_F = "ObisCodeF";

    // Default keys for security_key
    public static final String DLMS_DEVICE_ID = "DlmsDeviceId";
    public static final String INVOCATION_COUNTER = "InvocationCounter";
    public static final String NETWORK_ADDRESS = "NetworkAddress";
    public static final String PORT = "Port";
    public static final String PROTOCOL = "Protocol";
    public static final String PROTOCOL_VERSION = "ProtocolVersion";
    public static final String SELECTIVE_ACCESS_SUPPORTED = "SelectiveAccessSupported";
    public static final String SUPPLIER = "Supplier";
    public static final String TECHNICAL_INSTALLATION_DATE = "TechnicalInstallationDate";
    public static final String VALID_FROM = "ValidFrom";
    public static final String VALID_TO = "ValidTo";
    public static final String VERSION = "Version";
    public static final String WITH_LIST_SUPPORTED = "WithListSupported";
    public static final String MODULE_ACTIVE_FIRMWARE_VERSION = "ModuleActiveFirmwareVersion";

    public static final String DAYLIGHT_SAVINGS_BEGIN = "DaylightSavingsBegin";
    public static final String DAYLIGHT_SAVINGS_END = "DaylightSavingsEnd";
    public static final String DAYLIGHT_SAVINGS_DEVIATION = "DaylightSavingsDeviation";
    public static final String DAYLIGHT_SAVINGS_ENABLED = "DaylightSavingsEnabled";
    public static final String TIME_ZONE_OFFSET = "TimeZoneOffset";

    public static final String RESULT = "Result";
    public static final String DAYLIGHT_SAVINGS_ACTIVE = "DST";
    public static final String DEVIATION = "Deviation";

    public static final String CLASS_ID = "ClassId";

    public static final String ATTRIBUTE = "Attribute";

    public static final String RESPONSE_PART = "ResponsePart";

    public static final String BUNDLE_RESPONSE = "BundleResponse";
    public static final String BUNDLE_REQUEST = "BundleRequest";
    public static final String BUNDLE_ACTION_COUNT = "BundleActionCount";
    public static final String BUNDLE_RESPONSE_COUNT = "BundleResponseCount";

    public static final String MBUS_IDENTIFICATION_NUMBER = "MbusIdentificationNumber";
    public static final String MBUS_MANUFACTURER_IDENTIFICATION = "MbusManufacturerIdentification";
    public static final String MBUS_VERSION = "MbusVersion";
    public static final String MBUS_DEVICE_TYPE_IDENTIFICATION = "MbusDeviceTypeIdentification";
    public static final String MBUS_PRIMARY_ADDRESS = "MbusPrimaryAddress";

    public static final String ADMINISTRATIVE_STATUS_TYPE = "AdministrativeStatusType";
    public static final String ALARM_NOTIFICATION_COUNT = "AlarmNotificationCount";
    public static final String ALARM_TYPE = "AlarmType";
    public static final String ALARM_TYPE_ENABLED = "AlarmTypeEnabled";

    public static final String CONFIGURATION_FLAG_COUNT = "ConfigurationFlagCount";
    public static final String CONFIGURATION_FLAG_TYPE = "ConfigurationFlagType";
    public static final String CONFIGURATION_FLAG_ENABLED = "ConfigurationFlagEnabled";
    public static final String GPRS_OPERATION_MODE_TYPE = "GprsOperationModeType";

    public static final String SPECIAL_DAY_COUNT = "SpecialDayCount";
    public static final String SPECIAL_DAY_ID = "SpecialDayId";
    public static final String SPECIAL_DAY_DATE = "SpecialDayDate";

    public static final String ACTIVITY_CALENDAR = "ActivityCalendar";
    public static final String ACTIVITY_CALENDAR_NAME = "ActivityCalendarName";
    public static final String ACTIVITY_CALENDAR_ACTIVATE_PASSIVE_CALENDAR_TIME = "ActivatePassiveCalendarTime";
    public static final String SEASON_PROFILE_NAME = "SeasonProfileName";
    public static final String SEASON_PROFILE_START = "SeasonStart";
    public static final String SEASON_PROFILE_WEEK_NAME = "WeekName";
    public static final String WEEK_PROFILE_NAME = "WeekProfileName";
    public static final String WEEK_PROFILE_MONDAY_DAY_ID = "MondayDayId";
    public static final String WEEK_PROFILE_TUESDAY_DAY_ID = "TuesdayDayId";
    public static final String WEEK_PROFILE_WEDNESDAY_DAY_ID = "WednesdayDayId";
    public static final String WEEK_PROFILE_THURSDAY_DAY_ID = "ThursdayDayId";
    public static final String WEEK_PROFILE_FRIDAY_DAY_ID = "FridayDayId";
    public static final String WEEK_PROFILE_SATURDAY_DAY_ID = "SaturdayDayId";
    public static final String WEEK_PROFILE_SUNDAY_DAY_ID = "SundayDayId";
    public static final String DAY_PROFILE_DAY_ID = "DayId";
    public static final String DAY_PROFILE_ACTION_COUNT = "DayProfileActionCount";
    public static final String DAY_PROFILE_START_TIME = "StartTime";
    public static final String DAY_PROFILE_SCRIPT_SELECTOR = "ScriptSelector";

    public final static String NUMBER_OF_CAPTURE_OBJECTS = "NumberOfCaptureObjects";
    public final static String CAPTURE_OBJECT_CLASS_ID = "CaptureObject_ClassId";
    public final static String CAPTURE_OBJECT_LOGICAL_NAME = "CaptureObject_LogicalName";
    public final static String CAPTURE_OBJECT_ATTRIBUTE_INDEX = "CaptureObject_AttributeIndex";
    public final static String CAPTURE_OBJECT_DATA_INDEX = "CaptureObject_DataIndex";
    public final static String CAPTURE_PERIOD = "CapturePeriod";
}

/**
 * Copyright 2016-2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.platform.dlms;

/**
 * Keys specific for the dlms related data. Note: Keep in mind that generic keys
 * should be specified in the cucumber-tests-platform project.
 */
public class Keys extends com.alliander.osgp.cucumber.platform.Keys {

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
    public static final String DEVICE_MODEL = "DeviceModel";
    // other
    public static final String DEVICE_TYPE = "DeviceType";
    public static final String GAS_DEVICE_IDENTIFICATION = "GasDeviceIdentification";
    public static final String GATEWAY_DEVICE_IDENTIFICATION = "GatewayDeviceIdentification";

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

    // Default keys for security_key
    public static final String DLMS_DEVICE_ID = "DlmsDeviceId";
    public static final String SECURITY_KEY_A = "SecurityKeyAuthentication";
    public static final String SECURITY_KEY_E = "SecurityKeyEncryption";
    public static final String SECURITY_KEY_M = "SecurityKeyMaster";
    public static final String SECURITY_TYPE_A = "SecurityKeyTypeAuthentication";
    public static final String SECURITY_TYPE_E = "SecurityKeyTypeEncryption";
    public static final String SECURITY_TYPE_M = "SecurityKeyTypeMaster";
    public static final String NETWORK_ADDRESS = "NetworkAddress";
    public static final String PORT = "Port";
    public static final Object PROTOCOL = "Protocol";
    public static final Object PROTOCOL_VERSION = "ProtocolVersion";
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
}

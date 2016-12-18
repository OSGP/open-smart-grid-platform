/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.platform.cucumber.steps;

/**
 * This class contains a number of static String variables that are used to
 * put/get values from ScenarioContext or step settings.
 */
public class Keys {

    public static final String KEY_CORRELATION_UID = "CorrelationUid";
    public static final String KEY_DEVICE_IDENTIFICATION = "DeviceIdentification";
    public static final String KEY_DEVICE_TYPE = "DeviceType";
    public static final String KEY_PROTOCOL = "Protocol";
    public static final String KEY_PROTOCOL_VERSION = "ProtocolVersion";
    public static final String KEY_VERSION = "Version";
    public static final String KEY_ACTIVE = "Active";
    public static final String KEY_ORGANIZATION = "Organization";
    public static final String KEY_ALIAS = "alias";
    public static final String KEY_CITY = "containerCity";
    public static final String KEY_POSTCODE = "containerPostalCode";
    public static final String KEY_STREET = "containerStreet";
    public static final String KEY_NUMBER = "containerNumber";
    public static final String KEY_MUNICIPALITY = "containerMunicipality";
    public static final String KEY_LATITUDE = "gpsLatitude";
    public static final String KEY_LONGITUDE = "gpsLongitude";
    public static final String KEY_IS_ACTIVATED = "IsActivated";
    public static final String KEY_DEVICE_MODEL = "DeviceModel";
    public static final String KEY_TECH_INSTALL_DATE = "TechnicalInstallationDate";

    // TODO: It is called "Organisation" with an S in all webservices and
    // sourcecode, this should be consistent.
    public static final String KEY_ORGANIZATION_IDENTIFICATION = "OrganizationIdentification";
    public static final String KEY_USER_NAME = "UserName";
    public static final String KEY_DEVICE_FUNCTION_GRP = "DeviceFunctionGroup";
    public static final String KEY_COMM_METHOD = "CommunicationMethod";
    public static final String KEY_IP_ADDR_IS_STATIC = "IpAddressIsStatic";
    public static final String KEY_GATEWAY_DEVICE_ID = "GatewayDeviceIdentification";
    public static final String KEY_CHANNEL = "Channel";
    public static final String KEY_PERIOD_TYPE = "PeriodType";
    public static final String KEY_BEGIN_DATE = "BeginDate";
    public static final String KEY_END_DATE = "EndDate";
    public static final String KEY_INDEBUGMODE = "InDebugMode";
    public static final String KEY_DEVICE_COMMUNICATIONMETHOD = "CommunicationMethod";
    public static final String KEY_DEVICE_COMMUNICATIONPROVIDER = "CommunicationProvider";
    public static final String KEY_DEVICE_ICCID = "ICC_id";
    public static final String KEY_DEVICE_DSMRVERSION = "DSMR_version";
    public static final String KEY_DEVICE_SUPPLIER = "Supplier";
    public static final String KEY_DEVICE_HLS3ACTIVE = "HLS3_active";
    public static final String KEY_DEVICE_HLS4ACTIVE = "HLS4_active";
    public static final String KEY_DEVICE_HLS5ACTIVE = "HLS5_active";
    public static final String KEY_DEVICE_MASTERKEY = "Master_key";
    public static final String KEY_FIRMWARE_IDENTIFICATION = "FirmwareIdentification";

    public static final String KEY_DEVICE_IDENTIFICATION_E_LABEL = "DeviceIdentificationE";
    public static final String KEY_DEVICE_IDENTIFICATION_G_LABEL = "DeviceIdentificationG";

    public static final String KEY_EVENTS_NODELIST_EXPECTED = "event-nodelist-expected";
    public static final String KEY_DEVICE_UID = "DeviceUid";

    public static final String KEY_PAGE_SIZE = "PageSize";
    public static final String KEY_PAGE = "Page";

    public static final String KEY_SYSTEM_ID = "SystemId";
    public static final String KEY_SYSTEM_TYPE = "SystemType";
    public static final String KEY_MEASUREMENT_FILTER_NODE = "MeasurementFilterNode";
    public static final String KEY_MEASUREMENT_FILTER_ID = "MeasurementFilterId";
    public static final String KEY_NUMBER_OF_SYSTEMS = "NumberOfSystems";
    public static final String KEY_NUMBER_OF_MEASUREMENTS = "NumberOfMeasurements";
    public static final String KEY_NUMBER_OF_PROFILES = "NumberOfProfiles";
    public static final String KEY_MEASUREMENT_ID = "MeasurementId";
    public static final String KEY_MEASUREMENT_NODE = "MeasurementNode";
    public static final String KEY_MEASUREMENT_QUALIFIER = "MeasurementQualifier";
    public static final String KEY_MEASUREMENT_VALUE = "MeasurementValue";

    public static final String KEY_RESULT = "Result";
	public static final String KEY_MANUFACTURER = "Manufacturer";
	public static final String KEY_DEVICE_EXTERNAL_MANAGED = "DeviceExternalManaged";
	public static final String KEY_DEVICE_ACTIVATED = "DeviceActivated";
	public static final String KEY_DEVICE_MAINTENANCE = "DeviceMaintenance";
	public static final String KEY_SORT_DIR = "SortDir";
	public static final String KEY_HAS_TECHNICAL_INSTALLATION = "HasTechnicalInstallation";
	public static final String KEY_SORTED_BY = "SortedBy";
	public static final String KEY_OWNER = "Owner";
	public static final String KEY_FIRMWARE_MODULE_TYPE = "FirmwareModuleType";
	public static final String KEY_FIRMWARE_MODULE_VERSION = "FirmwareModuleVersion";
	public static final String KEY_EXACT_MATCH = "ExactMatch";
	public static final String KEY_USE_PAGE = "UsePage";
	public static final String RESPONSE = "Response";
	public static final String KEY_USE_PAGES = "UsePages";
	public static final String KEY_NETWORKADDRESS = "NetworkAddress";
	public static final String KEY_ENABLED = "Enabled";
	public static final String KEY_NAME = "Name";
	public static final String KEY_PREFIX = "Prefix";
	public static final String KEY_PLATFORM_FUNCTION_GROUP = "PlatformFunctionGroup";
	public static final String KEY_DOMAINS = "Domain";
	public static final String KEY_MESSAGE = "Message";
	public static final String KEY_FAULTCODE = "FaultCode";
	public static final String KEY_NEW_ORGANIZATION_IDENTIFICATION = "NewOrganizationIdentification";
	public static final String KEY_NEW_ORGANIZATION_PLATFORMFUNCTIONGROUP = "NewPlatformFunctionGroup";
	public static final String KEY_ACTIVATED = "Activated";
	public static final String KEY_DEVICE_MODEL_DESCRIPTION = "Description";
	public static final String KEY_DEVICE_MODEL_MANUFACTURER = "Manufacturer";
	public static final String KEY_DEVICE_MODEL_METERED = "Metered";
	public static final String KEY_DEVICE_MODEL_MODELCODE = "ModelCode";
	public static final String KEY_HAS_SCHEDULE = "HasSchedule";
	public static final String KEY_PUBLICKEYPRESENT = "PublicKeyPresent";
	public static final String KEY_EVENT = "Event";
	public static final String KEY_DESCRIPTION = "Description";
	public static final String KEY_INDEX = "Index";
	public static final String KEY_ISIMMEDIATE = "IsImmediate";
	public static final String KEY_DIMVALUE = "DimValue";
	public static final String KEY_ON = "On";
	public static final String KEY_TRANSITION_TYPE = "TransitionType";
	public static final String KEY_DEVICE_MODEL_FILESTORAGE = "FileStorage";
	public static final String KEY_EXPECTED_INDEX = "ExpectedIndex";
	public static final String KEY_EXPECTED_DIMVALUE = "ExpectedDimValue";
	public static final String KEY_EXPECTED_ON = "ExpectedOn";
}

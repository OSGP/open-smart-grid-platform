/*
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.platform;

import org.opensmartgridplatform.cucumber.core.Keys;

/**
 * This class contains a number of static String variables that are used to put/get values from
 * ScenarioContext or step settings.
 */
public class PlatformKeys extends Keys {

  public static final String CODE = "Code";
  public static final String SCHEDULE_CODE = "LightSchedule";

  public static final String CONTRACT_END_DATE = "ContractEndDate";
  public static final String DC_LIGHTS = "DcLights";
  public static final String DC_MAP = "DcMap";
  public static final String DELEGATE_FUNCTION_GROUP = "DelegateFunctionGroup";
  public static final String DELEGATE_ORGANIZATION_IDENTIFICATION =
      "DelegateOrganizationIdentification";
  public static final String DEVICE_FUNCTION = "DeviceFunction";
  public static final String DEVICE_OUTPUT_SETTINGS = "DeviceOutputSettings";
  public static final String DEVICEFIRMWARE_INSTALLATIONDATE = "DevicefirmwareInstallationDate";
  public static final String DEVICEFIRMWARE_INSTALLED_BY = "DevicefirmwareInstalledBy";

  public static final String DEVICEMODEL_DESCRIPTION = "DeviceModelDescription";
  public static final String DEVICEMODEL_FILESTORAGE = "DeviceModelFileStorage";
  public static final String DEVICEMODEL_MODELCODE = "ModelCode";
  public static final String DEVICEMODEL_NAME = "DeviceModelName";
  public static final String EAN_CODE = "EanCode";
  public static final String EAN_DESCRIPTION = "EanDescription";
  public static final String EMAIL = "Email";
  public static final String END_TIME = "EndTime";
  public static final String FAILED_LOGIN_ATTEMP_TIME_STAMP = "FailedLoginAttemptTimeStamp";
  public static final String FIRMWARE_ACTIVE = "FirmwareActive";
  public static final String FIRMWARE_DESCRIPTION = "FirmwareDescription";
  public static final String FIRMWARE_FILE = "FirmwareFile";
  public static final String FIRMWARE_FILE_FILENAME = "FirmwareFilename";
  public static final String FIRMWARE_FILE_EXISTS = "FirmwareFileExists";
  public static final String FIRMWARE_FILE_IDENTIFICATION = "FirmwareFileIdentification";
  public static final String FIRMWARE_HASH = "FirmwareHash";
  public static final String FIRMWARE_MODULE_VERSION_COMM = "FirmwareModuleVersionComm";
  public static final String FIRMWARE_MODULE_VERSION_FUNC = "FirmwareModuleVersionFunc";
  public static final String FIRMWARE_MODULE_VERSION_MA = "FirmwareModuleVersionMa";
  public static final String FIRMWARE_MODULE_VERSION_MBUS = "FirmwareModuleVersionMbus";
  public static final String FIRMWARE_MODULE_VERSION_SEC = "FirmwareModuleVersionSec";
  public static final String FIRMWARE_MODULE_VERSION_M_BUS_DRIVER_ACTIVE =
      "FirmwareModuleVersionMbda";
  public static final String SIMPLE_VERSION_INFO = "SimpleVersionInfo";
  public static final String FIRMWARE_PUSH_TO_NEW_DEVICES = "FirmwarePushToNewDevices";
  public static final String FIRMWARE_VERSION = "FirmwareVersion";
  public static final String FROM_DATE = "FromDate";
  public static final String KEY_ACTIVATED = "Activated";
  public static final String KEY_ALIAS = "Alias";

  public static final String KEY_DEVICE_LIFECYCLE_STATUS = "DeviceLifecycleStatus";
  public static final String KEY_ACTUAL_LINKTYPE = "ActualLinkType";

  public static final String KEY_ORGANIZATION_NAME = "OrganizationName";
  public static final String KEY_BEGIN_DATE = "BeginDate";

  public static final String KEY_POWER_QUALITY_PROFILE_TYPE = "ProfileType";

  public static final String KEY_CDMA_BATCH_NUMBER = "BatchNumber";
  public static final String KEY_CDMA_MAST_SEGMENT = "MastSegment";

  public static final String KEY_CHANNEL = "Channel";
  public static final String KEY_CITY = "containerCity";

  public static final String KEY_COMM_METHOD = "CommunicationMethod";
  public static final String KEY_CONTAINER_CITY = "ContainerCity";
  public static final String KEY_CONTAINER_MUNICIPALITY = "ContainerMunicipality";
  public static final String KEY_CONTAINER_NUMBER = "ContainerNumber";
  public static final String KEY_CONTAINER_NUMBER_ADDITION = "ContainerNumberAddition";
  public static final String KEY_CONTAINER_POSTALCODE = "ContainerPostalCode";
  public static final String KEY_CONTAINER_STREET = "ContainerStreet";
  public static final String KEY_CORRELATION_UID = "CorrelationUid";
  public static final String KEY_CREATION_TIME = "CreationTime";
  public static final String KEY_MODIFICATION_TIME = "ModificationTime";
  public static final String KEY_DESCRIPTION = "Description";
  public static final String KEY_DESCRIPTION_STARTS_WITH = "Description starts with";
  public static final String KEY_DEVICE_ACTIVATED = "DeviceActivated";
  public static final String KEY_DEVICE_COMMUNICATIONMETHOD = "CommunicationMethod";
  public static final String KEY_DEVICE_COMMUNICATIONPROVIDER = "CommunicationProvider";
  public static final String KEY_DEVICE_PROTOCOL_NAME = "protocolName";
  public static final String KEY_DEVICE_PROTOCOL_VERSION = "protocolVersion";
  public static final String KEY_DEVICE_EXTERNAL_MANAGED = "DeviceExternalManaged";
  public static final String KEY_DEVICE_FUNCTION_GROUP = "DeviceFunctionGroup";
  public static final String KEY_DEVICE_HLS3ACTIVE = "HLS3_active";
  public static final String KEY_DEVICE_HLS4ACTIVE = "HLS4_active";
  public static final String KEY_DEVICE_HLS5ACTIVE = "HLS5_active";
  public static final String KEY_DEVICE_MASTERKEY = "Master_key";
  public static final String KEY_DEVICE_AUTHENTICATIONKEY = "Authentication_key";
  public static final String KEY_DEVICE_ENCRYPTIONKEY = "Encryption_key";
  public static final String KEY_DEVICE_DELIVERY_DATE = "DeliveryDate";
  public static final String KEY_DEVICE_ICCID = "ICC_id";

  public static final String KEY_DEVICE_IDENTIFICATION = "DeviceIdentification";
  public static final String ALIAS = "Alias";
  public static final String CONTAINER_STREET = "ContainerStreet";
  public static final String CONTAINER_NUMBER = "ContainerNumber";
  public static final String CONTAINER_NUMBER_ADDITION = "ContainerNumberAddition";
  public static final String CONTAINER_POSTALCODE = "ContainerPostalCode";
  public static final String CONTAINER_CITY = "ContainerCity";
  public static final String CONTAINER_MUNICIPALITY = "ContainerMunicipality";
  public static final String DEVICE_IN_MAINTENANCE = "InMaintenance";

  public static final String KEY_DEVICE_IDENTIFICATION_E_LABEL = "DeviceIdentificationE";
  public static final String KEY_DEVICE_IDENTIFICATION_G_LABEL = "DeviceIdentificationG";
  public static final String KEY_DEVICE_MAINTENANCE = "DeviceMaintenance";
  public static final String KEY_DEVICE_MODEL = "DeviceModel";
  public static final String KEY_DEVICE_MODEL_DESCRIPTION = "DeviceModelDescription";
  public static final String KEY_DEVICE_MODEL_FILESTORAGE = "FileStorage";
  public static final String KEY_DEVICE_MODEL_MANUFACTURER = "Manufacturer";
  public static final String KEY_DEVICE_MODEL_MODELCODE = "DeviceModelCode";

  public static final String KEY_DEVICE_SUPPLIER = "Supplier";
  public static final String KEY_DEVICE_TYPE = "DeviceType";
  public static final String KEY_DEVICE_UID = "DeviceUid";
  public static final String KEY_DIMVALUE = "DimValue";
  public static final String KEY_DOMAINS = "Domains";
  public static final String KEY_ENABLED = "Enabled";
  public static final String KEY_END_DATE = "EndDate";
  public static final String KEY_EVENT = "Event";
  public static final String KEY_EVENTS = "Events";
  public static final String KEY_EVENTNOTIFICATIONS = "EventNotifications";
  public static final String KEY_EVENTNOTIFICATIONTYPES = "EventNotificationTypes";
  public static final String KEY_EVENTS_NODELIST_EXPECTED = "event-nodelist-expected";
  public static final String KEY_EXACT_MATCH = "ExactMatch";
  public static final String KEY_EXTERNALID = "ExternalId";
  public static final String KEY_FAILED_CONNECTION_COUNT = "FailedConnectionCount";
  public static final String KEY_FAULTCODE = "FaultCode";
  public static final String KEY_FAULTSTRING = "FaultString";
  public static final String KEY_FIRMWARE_IDENTIFICATION = "FirmwareIdentification";
  public static final String KEY_FIRMWARE_MODULE_TYPE = "FirmwareModuleType";
  public static final String KEY_FIRMWARE_MODULE_VERSION = "FirmwareModuleVersion";
  public static final String KEY_GATEWAY_DEVICE_ID = "GatewayDeviceIdentification";
  public static final String KEY_GPS_LATITUDE = "GpsLatitude";
  public static final String KEY_GPS_LONGITUDE = "GpsLongitude";
  public static final String KEY_HAS_SCHEDULE = "HasSchedule";
  public static final String KEY_HAS_TECHNICAL_INSTALLATION = "HasTechnicalInstallation";
  public static final String KEY_INDEBUGMODE = "InDebugMode";
  public static final String KEY_INDEX = "Index";
  public static final String KEY_INDEXES = "Indexes";
  public static final String KEY_INTERNALID = "InternalId";
  public static final String KEY_INTEGRATION_TYPE = "IntegrationType";
  public static final String KEY_IP_ADDR_IS_STATIC = "IpAddressIsStatic";
  public static final String KEY_ISIMMEDIATE = "IsImmediate";
  public static final String KEY_LATITUDE = "gpsLatitude";
  public static final String KEY_LIGHTTYPE = "LightType";
  public static final String KEY_LIGHTVALUES = "LightValues";
  public static final String KEY_TARIFFVALUES = "TariffValues";
  public static final String KEY_LONGITUDE = "gpsLongitude";
  public static final String KEY_MANUFACTURER = "Manufacturer";
  public static final String KEY_MEASUREMENT_FILTER_ID = "MeasurementFilterId";
  public static final String KEY_MEASUREMENT_FILTER_NODE = "MeasurementFilterNode";
  public static final String KEY_MEASUREMENT_ID = "MeasurementId";
  public static final String KEY_MEASUREMENT_NODE = "MeasurementNode";
  public static final String KEY_MEASUREMENT_QUALIFIER = "MeasurementQualifier";
  public static final String KEY_MEASUREMENT_VALUE = "MeasurementValue";
  public static final String KEY_DECODED_MESSAGE = "DecodedMessage";
  public static final String KEY_MESSAGE = "Message";
  public static final String KEY_MESSAGE_DATA = "MessageData";
  public static final String KEY_MESSAGE_TYPE = "MessageType";
  public static final String KEY_MUNICIPALITY = "containerMunicipality";
  public static final String KEY_NAME = "Name";
  public static final String KEY_NETWORKADDRESS = "NetworkAddress";
  public static final String KEY_NEW_ORGANIZATION_IDENTIFICATION = "NewOrganizationIdentification";
  public static final String KEY_NEW_ORGANIZATION_PLATFORMFUNCTIONGROUP =
      "NewPlatformFunctionGroup";
  public static final String KEY_NUMBER = "containerNumber";
  public static final String KEY_NUMBER_ADDITION = "ContainerNumberAddition";
  public static final String KEY_NUMBER_OF_MEASUREMENTS = "NumberOfMeasurements";
  public static final String KEY_NUMBER_OF_PROFILE_ENTRIES = "NumberOfProfileEntries";
  public static final String KEY_NUMBER_OF_PROFILES = "NumberOfProfiles";
  public static final String KEY_NUMBER_OF_SET_POINTS = "NumberOfSetPoints";
  public static final String KEY_NUMBER_OF_SYSTEMS = "NumberOfSystems";
  public static final String KEY_ON = "On";
  public static final String KEY_ORGANIZATION = "Organization";
  public static final String KEY_ORGANIZATION_DESCRIPTION = "Description";
  public static final String KEY_ORGANIZATION_IDENTIFICATION = "OrganizationIdentification";
  public static final String KEY_ORGANIZATION_IDENTIFICATION_TO_FIND =
      "OrganizationIdentificationToFind";
  public static final String KEY_OSLP_RESULT = "OSLPResult";
  public static final String KEY_DOMAIN = "Domain";
  public static final String KEY_DOMAIN_VERSION = "DomainVersion";

  public static final String KEY_OWNER = "Owner";
  public static final String KEY_SECOND_OWNER = "SecondOwner";
  public static final String KEY_PAGE = "Page";
  public static final String KEY_PAGE_SIZE = "PageSize";
  public static final String KEY_PERIOD_TYPE = "PeriodType";
  public static final String KEY_PLATFORM_FUNCTION = "PlatformFunction";
  public static final String KEY_PLATFORM_FUNCTION_GROUP = "PlatformFunctionGroup";
  public static final String KEY_POSTCODE = "containerPostalCode";
  public static final String KEY_PREFERRED_LINKTYPE = "PreferredLinkType";
  public static final String KEY_PREFIX = "Prefix";
  public static final String KEY_PROFILE_ENTRY_ID = "ProfileEntryId";
  public static final String KEY_PROFILE_ENTRY_TIME = "ProfileEntryTime";
  public static final String KEY_PROFILE_ENTRY_VALUE = "ProfileEntryValue";

  public static final String KEY_PROFILE_ID = "ProfileId";
  public static final String KEY_PROFILE_NODE = "ProfileNode";
  public static final String KEY_PROTOCOL = "Protocol";

  public static final String KEY_PROTOCOL_INFO_ID = "ProtocolInfoId";
  public static final String KEY_PROTOCOL_VERSION = "ProtocolVersion";
  public static final String KEY_PUBLIC_KEY = "PublicKey";

  public static final String KEY_PUBLICKEYPRESENT = "PublicKeyPresent";
  public static final String KEY_RELAY_TYPE = "RelayType";
  public static final String KEY_RESULT = "Result";
  public static final String KEY_RESULT_TYPE = "ResultType";
  public static final String KEY_REVOKED = "Revoked";

  public static final String KEY_SCHEDULED_TIME = "ScheduledTime";
  public static final String KEY_SETPOINT_END_TIME = "SetPointEndTime";
  public static final String KEY_SETPOINT_ID = "SetPointId";
  public static final String KEY_SETPOINT_NODE = "SetPointNode";

  public static final String KEY_SETPOINT_START_TIME = "SetPointStartTime";
  public static final String KEY_SETPOINT_VALUE = "SetPointValue";
  public static final String KEY_SORT_DIR = "SortDir";
  public static final String KEY_SORTED_BY = "SortedBy";
  public static final String KEY_STATUS = "Status";
  public static final String KEY_STREET = "containerStreet";

  public static final String KEY_SUPPLIER = "Supplier";
  public static final String KEY_SYSTEM_ID = "SystemId";
  public static final String KEY_SYSTEM_TYPE = "SystemType";
  public static final String KEY_TECHNICAL_INSTALLATION_DATE = "TechnicalInstallationDate";

  public static final String KEY_TIME = "Time";
  public static final String KEY_TRANSITION_TYPE = "TransitionType";
  public static final String KEY_USE_PAGE = "UsePage";
  public static final String KEY_USE_PAGES = "UsePages";
  public static final String KEY_USER_NAME = "UserName";
  public static final String KEY_VERSION = "Version";
  public static final String KEY_RESPONSE_URL = "ResponseUrl";

  public static final String KEY_LAST_COMMUNICATION_TIME = "LastCommunicationTime";
  public static final String LOGIN_ATTEMPT_COUNT = "LoginAttemptCount";
  public static final String MANUFACTURER_CODE = "ManufacturerCode";
  public static final String MANUFACTURER_NAME = "ManufacturerName";
  public static final String MANUFACTURER_USE_PREFIX = "ManufacturerUsePrefix";
  public static final String ORGANIZATION_NAME = "Name";
  public static final String OSGP_IP_ADDRESS = "OsgpIpAddress";
  public static final String OSGP_PORT = "OsgpPort";
  public static final String PHONE_NUMBER = "PhoneNumber";
  public static final String PLATFORM_DOMAINS = "PlatformDomains";
  public static final String RELAY_CONF = "RelayConf";
  public static final String RC_TYPE = "RcType";
  public static final String RESPONSE = "Response";
  public static final String SCHEDULE_ACTIONTIME = "ActionTime";
  public static final String SCHEDULE_CURRENTPAGE = "CurrentPages";
  public static final String SCHEDULE_ENDDAY = "EndDay";

  public static final String SCHEDULE_LIGHTVALUES = "LightValues";
  public static final String SCHEDULE_PAGESIZE = "PageSize";
  public static final String SCHEDULE_SCHEDULEDTIME = "ScheduledTime";
  public static final String SCHEDULE_STARTDAY = "StartDay";
  public static final String SCHEDULE_TARIFFVALUES = "TariffValues";
  public static final String SCHEDULE_TIME = "Time";
  public static final String SCHEDULE_TOTALPAGES = "TotalPages";

  public static final String SCHEDULE_TRIGGERTYPE = "TriggerType";
  public static final String SCHEDULE_TRIGGERWINDOW = "TriggerWindow";

  public static final String SCHEDULE_WEEKDAY = "WeekDay";
  public static final String SEPARATOR_COMMA = ",";
  public static final String SEPARATOR_SEMICOLON = ";";
  public static final String SEPARATOR_COLON = ":";
  public static final String SEPARATOR_SPACE = " ";
  public static final String SEPARATOR_SPACE_COLON_SPACE = " : ";
  public static final String START_TIME = "StartTime";
  public static final String SUPPLIER = "Supplier";
  public static final String UNTIL_DATE = "UntilDate";
  public static final String USE_PREFIX = "UsePrefix";
  public static final String USERNAME = "Username";

  public static final String CURRENT_SEQUENCE_NUMBER = "CurrentSequenceNumber";
  public static final String SEQUENCE_WINDOW = "SequenceWindow";
  public static final String NEW_SEQUENCE_NUMBER = "NewSequenceNumber";
  public static final String NUMBER_TO_ADD_TO_SEQUENCE_NUMBER = "NumberToAddToSequenceNumber";
  public static final String IN_MAINTENANCE = "InMaintenance";

  public static final String HOSTNAME = "Hostname";
  public static final String PORT = "Port";
  public static final String IP_ADDRESS = "IpAddress";
  public static final String BTS_ID = "BtsId";
  public static final String CELL_ID = "CellId";

  public static final String RANDOM_DEVICE = "RandomDevice";

  public static final String KEY_SCHEDULE_DESCRIPTION = "Description";
  public static final String KEY_SCHEDULE_COLOR = "Color";
  public static final String KEY_SCHEDULE_DEFAULT = "DefaultSchedule";
  public static final String KEY_SCHEDULE_SUCCESS = "Success";
  public static final String KEY_SCHEDULE_TEMPLATE = "Template";

  public static final String KEY_IEC61850_SERVERNAME = "ServerName";
  public static final String KEY_IEC61850_PORT = "Port";
  public static final String KEY_IEC61850_ICD_FILENAME = "IcdFilename";

  public static final String FIRMWARE_INSTALLED_BY = "InstalledBy";
  public static final String FIRMWARE_INSTALLATION_DATE = "InstallationDate";
  public static final String KEY_SCHEDULE_VERSION = "Version";
  public static final String KEY_LIGHTMEASUREMENT_IDENTIFICATION = "Identification";
  public static final String KEY_LIGHTMEASUREMENT_DEVICE_IDENTIFICATION =
      "LightMeasurementDeviceIdentification";
  public static final String KEY_LIGHTMEASUREMENT_COLOR = "Color";
  public static final String KEY_LIGHTMEASUREMENT_DIGITAL_INPUT = "DigitalInput";
  public static final String KEY_LIGHTMEASUREMENT_LAST_COMMUNICATION_TIME = "LastCommunicationTime";
  public static final String SMS_TYPE = "SmsType";
  public static final String KEY_LIGHTMEASUREMENT_LASTMESSAGE = "LastMessage";
  public static final String SMS_INDEX = "SmsIndex";

  public static final String TIMESTAMP = "TimeStamp";
  public static final String EVENT_TYPE = "EventType";

  public static final String FROM_TIMESTAMP = "FromTimeStamp";
  public static final String TO_TIMESTAMP = "ToTimeStamp";
  public static final String REQUESTED_PAGE = "RequestedPage";
  public static final String TOTALPAGES = "TotalPages";

  public static final String MESSAGE = "Message";
  public static final String NUMBER_OF_EVENTS = "NumberOfEvents";
  public static final String RANDOM_PLATFORM = "RandomPlatform";

  public static final String KEY_RELAY = "Relais";
  public static final String KEY_RELAYFUNCTION = "Function";

  public static final String LAST_SWITCHING_EVENT_STATE = "LastSwitchingEventState";
  public static final String LAST_SWITCHING_EVENT_TIME = "LastSwitchingEventTime";
  public static final String LAST_KNOWN_STATE = "LastKnownState";
  public static final String LAST_KNOWN_STATE_TIME = "LastKnownStateTime";

  public static final String RELAY_STATUSES = "RelayStatuses";
  public static final String NUMBER_OF_STATUSES = "NumberOfStatuses";
  public static final String DATE_NOW = "DateNow";
  public static final String TARIFF_RELAY_TYPE = "TariffRelayType";
  public static final String LIGHT_RELAY_TYPE = "LightRelayType";

  public static final String DATE = "Date";
  public static final String TIME_UNTIL_ON = "TimeUntilOn";
  public static final String KEY_NUMBER_OF_NOTIFICATIONS_SENT = "NumberOfNotificationsSent";

  public static final String KEY_ASTRONOMICAL_SUNRISE_OFFSET = "SunriseOffset";
  public static final String KEY_ASTRONOMICAL_SUNSET_OFFSET = "SunsetOffset";

  public static final String MBUS_IDENTIFICATION_NUMBER_CHANNEL1 =
      "MbusIdentificationNumberChannel1";
  public static final String MBUS_IDENTIFICATION_NUMBER_CHANNEL2 =
      "MbusIdentificationNumberChannel2";
  public static final String MBUS_IDENTIFICATION_NUMBER_CHANNEL3 =
      "MbusIdentificationNumberChannel3";
  public static final String MBUS_IDENTIFICATION_NUMBER_CHANNEL4 =
      "MbusIdentificationNumberChannel4";

  public static final String KEY_LMD_DESCRIPTION = "LmdDescription";
  public static final String KEY_LMD_CODE = "LmdCode";
  public static final String KEY_LMD_COLOR = "LmdColor";
  public static final String KEY_LMD_DIGITAL_INPUT = "LmdDigitalInput";

  public static final String KEY_DIRECT_ATTACH = "directAttach";
  public static final String KEY_RANDOMISATION_START_WINDOW = "randomisationStartWindow";
  public static final String KEY_MULTIPLICATION_FACTOR = "multiplicationFactor";
  public static final String KEY_NO_OF_RETRIES = "numberOfRetries";
}

/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.platform.smartmetering.glue.steps.database.device;

import static com.alliander.osgp.cucumber.core.ReadSettingsHelper.getLong;
import static com.alliander.osgp.cucumber.core.ReadSettingsHelper.getShort;
import static com.alliander.osgp.cucumber.platform.PlatformDefaults.SMART_METER_E;
import static com.alliander.osgp.cucumber.platform.PlatformDefaults.SMART_METER_G;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.osgp.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.osgp.adapter.protocol.dlms.domain.entities.SecurityKey;
import org.osgp.adapter.protocol.dlms.domain.entities.SecurityKeyType;
import org.osgp.adapter.protocol.dlms.domain.repositories.DlmsDeviceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.alliander.osgp.cucumber.core.ScenarioContext;
import com.alliander.osgp.cucumber.platform.PlatformKeys;
import com.alliander.osgp.cucumber.platform.glue.steps.database.core.DeviceSteps;
import com.alliander.osgp.cucumber.platform.smartmetering.PlatformSmartmeteringDefaults;
import com.alliander.osgp.cucumber.platform.smartmetering.PlatformSmartmeteringKeys;
import com.alliander.osgp.cucumber.platform.smartmetering.builders.entities.DeviceBuilder;
import com.alliander.osgp.cucumber.platform.smartmetering.builders.entities.DlmsDeviceBuilder;
import com.alliander.osgp.cucumber.platform.smartmetering.builders.entities.SmartMeterBuilder;
import com.alliander.osgp.domain.core.entities.Device;
import com.alliander.osgp.domain.core.entities.DeviceAuthorization;
import com.alliander.osgp.domain.core.entities.DeviceModel;
import com.alliander.osgp.domain.core.entities.Manufacturer;
import com.alliander.osgp.domain.core.entities.Organisation;
import com.alliander.osgp.domain.core.entities.ProtocolInfo;
import com.alliander.osgp.domain.core.entities.SmartMeter;
import com.alliander.osgp.domain.core.repositories.DeviceAuthorizationRepository;
import com.alliander.osgp.domain.core.repositories.DeviceModelRepository;
import com.alliander.osgp.domain.core.repositories.DeviceRepository;
import com.alliander.osgp.domain.core.repositories.ManufacturerRepository;
import com.alliander.osgp.domain.core.repositories.OrganisationRepository;
import com.alliander.osgp.domain.core.repositories.ProtocolInfoRepository;
import com.alliander.osgp.domain.core.repositories.SmartMeterRepository;
import com.alliander.osgp.domain.core.valueobjects.DeviceFunctionGroup;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;

/**
 * DLMS device specific steps.
 */
@Transactional(value = "txMgrCore")
public class DlmsDeviceSteps {

    @Autowired
    private SmartMeterRepository smartMeterRepository;

    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private DlmsDeviceRepository dlmsDeviceRepository;

    @Autowired
    private ManufacturerRepository manufacturerRepository;

    @Autowired
    private DeviceModelRepository deviceModelRepository;

    @Autowired
    private ProtocolInfoRepository protocolInfoRepository;

    @Autowired
    private OrganisationRepository organisationRepo;

    @Autowired
    private DeviceAuthorizationRepository deviceAuthorizationRepository;

    @Autowired
    private DeviceSteps deviceSteps;

    @Given("^a dlms device$")
    public void aDlmsDevice(final Map<String, String> inputSettings) throws Throwable {

        final Device device = this.createDeviceInCoreDatabase(inputSettings);
        this.setScenarioContextForDevice(inputSettings, device);

        this.createDeviceAuthorisationInCoreDatabase(device);

        this.createDlmsDeviceInProtocolAdapterDatabase(inputSettings);
    }

    @Given("^all mbus channels are occupied for E-meter \"([^\"]*)\"$")
    public void allMbusChannelsAreOccupiedForEMeter(final String eMeter) throws Throwable {
        /**
         * A smart meter has 4 M-Bus channels available, so make sure that for
         * each channel an M-Bus device is created
         */
        for (int index = 1; index <= 4; index++) {
            final Map<String, String> inputSettings = new HashMap<>();
            inputSettings.put(PlatformSmartmeteringKeys.DEVICE_IDENTIFICATION, "TESTG10240000002" + index);
            inputSettings.put(PlatformSmartmeteringKeys.GATEWAY_DEVICE_IDENTIFICATION, eMeter);
            inputSettings.put(PlatformSmartmeteringKeys.CHANNEL, Integer.toString(index));
            inputSettings.put(PlatformSmartmeteringKeys.MBUS_PRIMARY_ADDRESS, Integer.toString(index));
            inputSettings.put(PlatformSmartmeteringKeys.DEVICE_TYPE, SMART_METER_G);
            this.aDlmsDevice(inputSettings);
        }

    }

    @Then("^the dlms device with identification \"([^\"]*)\" exists$")
    public void theDlmsDeviceWithIdentificationExists(final String deviceIdentification) throws Throwable {

        this.deviceSteps.theDeviceWithIdExists(deviceIdentification);
        this.findExistingDlmsDevice(deviceIdentification);
    }

    @Then("^the dlms device with identification \"([^\"]*)\" exists with device model$")
    public void theDlmsDeviceWithIdentificationExistsWithDeviceModel(final String deviceIdentification,
            final Map<String, String> deviceModelAttributes) throws Throwable {
        this.theDlmsDeviceWithIdentificationExists(deviceIdentification);

        final Device device = this.deviceRepository.findByDeviceIdentification(deviceIdentification);
        final DeviceModel deviceModel = device.getDeviceModel();
        assertEquals(PlatformKeys.DEVICEMODEL_MODELCODE, deviceModelAttributes.get(PlatformKeys.DEVICEMODEL_MODELCODE),
                deviceModel.getModelCode());
        final Manufacturer manufacturer = deviceModel.getManufacturer();
        assertEquals(PlatformKeys.MANUFACTURER_CODE, deviceModelAttributes.get(PlatformKeys.MANUFACTURER_CODE),
                manufacturer.getCode());
    }

    @Then("^the smart meter is registered in the core database$")
    public void theSmartMeterIsRegisteredInTheCoreDatabase(final Map<String, String> settings) throws Throwable {
        final SmartMeter smartMeter = this.smartMeterRepository
                .findByDeviceIdentification(settings.get(PlatformSmartmeteringKeys.DEVICE_IDENTIFICATION));

        assertNotNull(smartMeter);
        assertEquals(smartMeter.getSupplier(), settings.get(PlatformSmartmeteringKeys.SUPPLIER));
        assertEquals(smartMeter.getChannel(), settings.get(PlatformSmartmeteringKeys.CHANNEL));
        assertEquals(smartMeter.getMbusIdentificationNumber(),
                getLong(settings, PlatformSmartmeteringKeys.MBUS_IDENTIFICATION_NUMBER, null));
        assertEquals(smartMeter.getMbusManufacturerIdentification(),
                settings.get(PlatformSmartmeteringKeys.MBUS_MANUFACTURER_IDENTIFICATION));
        assertEquals(smartMeter.getMbusVersion(), getShort(settings, PlatformSmartmeteringKeys.MBUS_VERSION, null));
        assertEquals(smartMeter.getMbusDeviceTypeIdentification(),
                getShort(settings, PlatformSmartmeteringKeys.MBUS_DEVICE_TYPE_IDENTIFICATION, null));
    }

    @Then("^the dlms device with identification \"([^\"]*)\" does not exist$")
    public void theDlmsDeviceWithIdentificationDoesNotExist(final String deviceIdentification) throws Throwable {

        final DlmsDevice dlmsDevice = this.dlmsDeviceRepository.findByDeviceIdentification(deviceIdentification);
        assertNull("DLMS device with identification " + deviceIdentification + " in protocol database", dlmsDevice);

        final Device device = this.deviceRepository.findByDeviceIdentification(deviceIdentification);
        assertNull("DLMS device with identification " + deviceIdentification + " in core database", device);
    }

    @Then("^the new keys are stored in the osgp_adapter_protocol_dlms database security_key table$")
    public void theNewKeysAreStoredInTheOsgpAdapterProtocolDlmsDatabaseSecurityKeyTable() throws Throwable {
        final String keyDeviceIdentification = PlatformSmartmeteringKeys.DEVICE_IDENTIFICATION;
        final String deviceIdentification = (String) ScenarioContext.current().get(keyDeviceIdentification);
        assertNotNull("Device identification must be in the scenario context for key " + keyDeviceIdentification,
                deviceIdentification);

        final DlmsDevice dlmsDevice = this.findExistingDlmsDevice(deviceIdentification);
        final List<SecurityKey> securityKeys = dlmsDevice.getSecurityKeys();

        /*
         * If the new keys are stored, the device should have some no longer
         * valid keys. There should be 1 master key and more than one
         * authentication and encryption keys.
         */
        int numberOfMasterKeys = 0;
        int numberOfAuthenticationKeys = 0;
        int numberOfEncryptionKeys = 0;

        for (final SecurityKey securityKey : securityKeys) {
            switch (securityKey.getSecurityKeyType()) {
            case E_METER_MASTER:
                numberOfMasterKeys += 1;
                break;
            case E_METER_AUTHENTICATION:
                numberOfAuthenticationKeys += 1;
                break;
            case E_METER_ENCRYPTION:
                numberOfEncryptionKeys += 1;
                break;
            default:
                // other keys are not counted
            }
        }

        assertEquals("Number of master keys", 1, numberOfMasterKeys);
        assertTrue("Number of authentication keys > 1", numberOfAuthenticationKeys > 1);
        assertTrue("Number of encryption keys > 1", numberOfEncryptionKeys > 1);
    }

    @Then("^the keys are not changed in the osgp_adapter_protocol_dlms database security_key table$")
    public void theKeysAreNotChangedInTheOsgpAdapterProtocolDlmsDatabaseSecurityKeyTable() throws Throwable {
        final String keyDeviceIdentification = PlatformSmartmeteringKeys.DEVICE_IDENTIFICATION;
        final String deviceIdentification = (String) ScenarioContext.current().get(keyDeviceIdentification);
        assertNotNull("Device identification must be in the scenario context for key " + keyDeviceIdentification,
                deviceIdentification);

        final DlmsDevice dlmsDevice = this.findExistingDlmsDevice(deviceIdentification);
        final List<SecurityKey> securityKeys = dlmsDevice.getSecurityKeys();

        /*
         * If the keys are not changed, the device should only have valid keys.
         * There should be 1 master key and one authentication and encryption
         * key.
         */
        int numberOfMasterKeys = 0;
        int numberOfAuthenticationKeys = 0;
        int numberOfEncryptionKeys = 0;

        for (final SecurityKey securityKey : securityKeys) {
            switch (securityKey.getSecurityKeyType()) {
            case E_METER_MASTER:
                numberOfMasterKeys += 1;
                break;
            case E_METER_AUTHENTICATION:
                numberOfAuthenticationKeys += 1;
                break;
            case E_METER_ENCRYPTION:
                numberOfEncryptionKeys += 1;
                break;
            default:
                // other keys are not counted
            }
            assertNull("security key " + securityKey.getSecurityKeyType() + " valid to date", securityKey.getValidTo());
        }

        assertEquals("Number of master keys", 1, numberOfMasterKeys);
        assertEquals("Number of authentication keys", 1, numberOfAuthenticationKeys);
        assertEquals("Number of encryption keys", 1, numberOfEncryptionKeys);
    }

    @Then("^the stored keys are not equal to the received keys$")
    public void theStoredKeysAreNotEqualToTheReceivedKeys() throws Throwable {
        final String keyDeviceIdentification = PlatformSmartmeteringKeys.DEVICE_IDENTIFICATION;
        final String deviceIdentification = (String) ScenarioContext.current().get(keyDeviceIdentification);
        assertNotNull("Device identification must be in the scenario context for key " + keyDeviceIdentification,
                deviceIdentification);
        final String deviceDescription = "DLMS device with identification " + deviceIdentification;
        final DlmsDevice dlmsDevice = this.findExistingDlmsDevice(deviceIdentification);

        final SecurityKey masterKey = this.findExistingSecurityKey(dlmsDevice, SecurityKeyType.E_METER_MASTER,
                "Master key");
        final String receivedMasterKey = (String) ScenarioContext.current()
                .get(PlatformSmartmeteringKeys.KEY_DEVICE_MASTERKEY);
        assertNotEquals("Stored master key for " + deviceDescription + " must be different from received key",
                receivedMasterKey, masterKey.getKey());

        final SecurityKey authenticationKey = this.findExistingSecurityKey(dlmsDevice,
                SecurityKeyType.E_METER_AUTHENTICATION, "Authentication key");
        final String receivedAuthenticationKey = (String) ScenarioContext.current()
                .get(PlatformSmartmeteringKeys.KEY_DEVICE_AUTHENTICATIONKEY);
        assertNotEquals("Stored authentication key for " + deviceDescription + " must be different from received key",
                receivedAuthenticationKey, authenticationKey.getKey());

        final SecurityKey encryptionKey = this.findExistingSecurityKey(dlmsDevice, SecurityKeyType.E_METER_ENCRYPTION,
                "Encryption key");
        final String receivedEncryptionKey = (String) ScenarioContext.current()
                .get(PlatformSmartmeteringKeys.KEY_DEVICE_AUTHENTICATIONKEY);
        assertNotEquals("Stored encryption key for " + deviceDescription + " must be different from received key",
                receivedEncryptionKey, encryptionKey.getKey());
    }

    @Then("^the stored M-Bus Default key is not equal to the received key$")
    public void theStoredMbusDefaultKeysIsNotEqualToTheReceivedKey() throws Throwable {
        final String keyDeviceIdentification = PlatformSmartmeteringKeys.DEVICE_IDENTIFICATION;
        final String deviceIdentification = (String) ScenarioContext.current().get(keyDeviceIdentification);
        assertNotNull("Device identification must be in the scenario context for key " + keyDeviceIdentification,
                deviceIdentification);

        final String deviceDescription = "DLMS device with identification " + deviceIdentification;
        final DlmsDevice dlmsDevice = this.findExistingDlmsDevice(deviceIdentification);

        final SecurityKey mbusDefaultKey = this.findExistingSecurityKey(dlmsDevice, SecurityKeyType.G_METER_MASTER,
                "M-Bus Default key");
        final String receivedMbusDefaultKey = (String) ScenarioContext.current()
                .get(PlatformSmartmeteringKeys.MBUS_DEFAULT_KEY);
        assertNotEquals("Stored M-Bus Default key for " + deviceDescription + " must be different from received key",
                receivedMbusDefaultKey, mbusDefaultKey.getKey());
    }

    @Then("^a valid m-bus user key is stored$")
    public void aValidMbusUserKeyIsStored(final Map<String, String> settings) throws Throwable {
        final String keyDeviceIdentification = PlatformSmartmeteringKeys.DEVICE_IDENTIFICATION;
        final String deviceIdentification = settings.get(keyDeviceIdentification);
        assertNotNull("The M-Bus device identification must be in the step data for key " + keyDeviceIdentification,
                deviceIdentification);

        final String deviceDescription = "M-Bus device with identification " + deviceIdentification;
        final DlmsDevice dlmsDevice = this.dlmsDeviceRepository.findByDeviceIdentification(deviceIdentification);
        assertNotNull(deviceDescription + " must be in the protocol database", dlmsDevice);

        final List<SecurityKey> securityKeys = dlmsDevice.getSecurityKeys();

        int numberOfMbusDefaultKeys = 0;
        int numberOfMbusUserKeys = 0;
        int numberOfValidMbusUserKeys = 0;

        final Date now = new Date();
        for (final SecurityKey securityKey : securityKeys) {
            switch (securityKey.getSecurityKeyType()) {
            case G_METER_MASTER:
                numberOfMbusDefaultKeys += 1;
                break;
            case G_METER_ENCRYPTION:
                numberOfMbusUserKeys += 1;
                final Date validFrom = securityKey.getValidFrom();
                final Date validTo = securityKey.getValidTo();
                if ((validFrom != null && now.after(validFrom)) && (validTo == null || now.before(validTo))) {
                    numberOfValidMbusUserKeys += 1;
                }
                break;
            default:
                // other keys are not counted
            }
        }

        assertEquals("Number of M-Bus Default keys stored", 1, numberOfMbusDefaultKeys);
        assertTrue("At least one M-Bus User key must be stored", numberOfMbusUserKeys > 0);
        assertEquals("Number of valid M-Bus User keys stored", 1, numberOfValidMbusUserKeys);
    }

    @Then("^the invocation counter for the encryption key of \"([^\"]*)\" should be greater than (\\d++)$")
    public void theInvocationCounterForTheEncryptionKeyOfShouldBeGreaterThan(final String deviceIdentification,
            final Integer invocationCounterLowerBound) throws Throwable {

        final DlmsDevice dlmsDevice = this.findExistingDlmsDevice(deviceIdentification);
        final SecurityKey encryptionKey = this.findExistingSecurityKey(dlmsDevice, SecurityKeyType.E_METER_ENCRYPTION,
                "Encryption key");
        final Integer invocationCounter = encryptionKey.getInvocationCounter();

        assertNotNull("The invocation counter for the encryption key of DLMS device with identification "
                + dlmsDevice.getDeviceIdentification() + " must not be null", invocationCounter);

        assertTrue(
                "The invocation counter for the encryption key of DLMS device with identification "
                        + dlmsDevice.getDeviceIdentification() + " (which is " + invocationCounter
                        + ") must be greater than " + invocationCounterLowerBound,
                invocationCounter > invocationCounterLowerBound);
    }

    private DlmsDevice findExistingDlmsDevice(final String deviceIdentification) {
        final String deviceDescription = "DLMS device with identification " + deviceIdentification;
        final DlmsDevice dlmsDevice = this.dlmsDeviceRepository.findByDeviceIdentification(deviceIdentification);
        assertNotNull(deviceDescription + " must be in the protocol database", dlmsDevice);
        return dlmsDevice;
    }

    private SecurityKey findExistingSecurityKey(final DlmsDevice dlmsDevice, final SecurityKeyType keyType,
            final String keyDescription) {
        final SecurityKey securityKey = dlmsDevice.getValidSecurityKey(keyType);
        assertNotNull(keyDescription + " for DLMS device with identification " + dlmsDevice.getDeviceIdentification()
                + " must be stored", securityKey);
        return securityKey;
    }

    private void setScenarioContextForDevice(final Map<String, String> inputSettings, final Device device) {
        final String deviceType = inputSettings.get(PlatformSmartmeteringKeys.DEVICE_TYPE);
        if (this.isGasSmartMeter(deviceType)) {
            ScenarioContext.current().put(PlatformSmartmeteringKeys.GAS_DEVICE_IDENTIFICATION,
                    device.getDeviceIdentification());
        } else {
            ScenarioContext.current().put(PlatformSmartmeteringKeys.DEVICE_IDENTIFICATION,
                    device.getDeviceIdentification());
        }
    }

    private Device createDeviceInCoreDatabase(final Map<String, String> inputSettings) {

        Device device;
        final ProtocolInfo protocolInfo = this.getProtocolInfo(inputSettings);
        final DeviceModel deviceModel = this.getDeviceModel(inputSettings);
        if (this.isSmartMeter(inputSettings)) {
            final SmartMeter smartMeter = new SmartMeterBuilder().withSettings(inputSettings)
                    .setProtocolInfo(protocolInfo).setDeviceModel(deviceModel).build();
            device = this.smartMeterRepository.save(smartMeter);

        } else {
            device = new DeviceBuilder(this.deviceRepository).withSettings(inputSettings).setProtocolInfo(protocolInfo)
                    .setDeviceModel(deviceModel).build();
            this.deviceRepository.save(device);
        }

        if (inputSettings.containsKey(PlatformSmartmeteringKeys.GATEWAY_DEVICE_IDENTIFICATION)) {
            final Device gatewayDevice = this.deviceRepository.findByDeviceIdentification(
                    inputSettings.get(PlatformSmartmeteringKeys.GATEWAY_DEVICE_IDENTIFICATION));
            device.updateGatewayDevice(gatewayDevice);
            device = this.deviceRepository.save(device);
        }
        return device;
    }

    private void createDeviceAuthorisationInCoreDatabase(final Device device) {
        final Organisation organisation = this.organisationRepo.findByOrganisationIdentification(
                com.alliander.osgp.cucumber.platform.PlatformDefaults.DEFAULT_ORGANIZATION_IDENTIFICATION);
        final DeviceAuthorization deviceAuthorization = device.addAuthorization(organisation,
                DeviceFunctionGroup.OWNER);

        this.deviceAuthorizationRepository.save(deviceAuthorization);
        this.deviceRepository.save(device);
    }

    private void createDlmsDeviceInProtocolAdapterDatabase(final Map<String, String> inputSettings) {
        final DlmsDeviceBuilder dlmsDeviceBuilder = new DlmsDeviceBuilder();
        /*
         * Enable the necessary security key builders in the DLMS device builder
         * before calling withSettings. This allows the withSettings to be
         * called transitively on the enabled security key builders inside the
         * DLMS device builder.
         */
        final String deviceType = inputSettings.getOrDefault(PlatformSmartmeteringKeys.DEVICE_TYPE, SMART_METER_E);
        if (inputSettings.containsKey(PlatformSmartmeteringKeys.LLS1_ACTIVE)
                && "true".equals(inputSettings.get(PlatformSmartmeteringKeys.LLS1_ACTIVE))) {
            dlmsDeviceBuilder.getPasswordBuilder().enable();
        } else if (this.isGasSmartMeter(deviceType)) {
            dlmsDeviceBuilder.getMbusMasterSecurityKeyBuilder().enable();
            /*
             * Don't insert a default value for the M-Bus User key. So only
             * enable the builder if an M-Bus User key is explicitly configured
             * in the step data.
             */
            if (inputSettings.containsKey(PlatformSmartmeteringKeys.MBUS_USER_KEY)) {
                dlmsDeviceBuilder.getMbusEncryptionSecurityKeyBuilder().enable();
            }
        } else if (this.isESmartMeter(deviceType)) {
            dlmsDeviceBuilder.getEncryptionSecurityKeyBuilder().enable();
            dlmsDeviceBuilder.getMasterSecurityKeyBuilder().enable();
            dlmsDeviceBuilder.getAuthenticationSecurityKeyBuilder().enable();
        }

        final DlmsDevice dlmsDevice = dlmsDeviceBuilder.withSettings(inputSettings).build();
        this.dlmsDeviceRepository.save(dlmsDevice);
    }

    private boolean isSmartMeter(final Map<String, String> settings) {
        final String deviceType = settings.get(PlatformSmartmeteringKeys.DEVICE_TYPE);
        return this.isGasSmartMeter(deviceType) || this.isESmartMeter(deviceType);
    }

    private boolean isGasSmartMeter(final String deviceType) {
        return SMART_METER_G.equals(deviceType);
    }

    private boolean isESmartMeter(final String deviceType) {
        return SMART_METER_E.equals(deviceType);
    }

    /**
     * ProtocolInfo is fixed system data, inserted by flyway. Therefore the
     * ProtocolInfo instance will be retrieved from the database, and not built.
     *
     * @param inputSettings
     * @return ProtocolInfo
     */
    private ProtocolInfo getProtocolInfo(final Map<String, String> inputSettings) {
        if (inputSettings.containsKey(PlatformSmartmeteringKeys.PROTOCOL)
                && inputSettings.containsKey(PlatformSmartmeteringKeys.PROTOCOL_VERSION)) {
            return this.protocolInfoRepository.findByProtocolAndProtocolVersion(
                    inputSettings.get(PlatformSmartmeteringKeys.PROTOCOL),
                    inputSettings.get(PlatformSmartmeteringKeys.PROTOCOL_VERSION));
        } else {
            return this.protocolInfoRepository.findByProtocolAndProtocolVersion(PlatformSmartmeteringDefaults.PROTOCOL,
                    PlatformSmartmeteringDefaults.PROTOCOL_VERSION);
        }
    }

    private DeviceModel getDeviceModel(final Map<String, String> inputSettings) {

        if (inputSettings.containsKey(PlatformSmartmeteringKeys.MANUFACTURER_CODE)
                && inputSettings.containsKey(PlatformSmartmeteringKeys.DEVICE_MODEL_CODE)) {
            final Manufacturer manufacturer = this.manufacturerRepository
                    .findByCode(inputSettings.get(PlatformSmartmeteringKeys.MANUFACTURER_CODE));
            return this.deviceModelRepository.findByManufacturerAndModelCode(manufacturer,
                    inputSettings.get(PlatformSmartmeteringKeys.DEVICE_MODEL_CODE));
        }

        return null;
    }

}

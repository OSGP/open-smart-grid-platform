/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.platform.smartmetering.glue.steps.database.device;

import static com.alliander.osgp.cucumber.platform.PlatformDefaults.SMART_METER_E;
import static com.alliander.osgp.cucumber.platform.PlatformDefaults.SMART_METER_G;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Map;

import org.osgp.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.osgp.adapter.protocol.dlms.domain.entities.SecurityKey;
import org.osgp.adapter.protocol.dlms.domain.entities.SecurityKeyType;
import org.osgp.adapter.protocol.dlms.domain.repositories.DlmsDeviceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.alliander.osgp.cucumber.core.Helpers;
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
import com.alliander.osgp.domain.core.entities.Organisation;
import com.alliander.osgp.domain.core.entities.ProtocolInfo;
import com.alliander.osgp.domain.core.entities.SmartMeter;
import com.alliander.osgp.domain.core.repositories.DeviceAuthorizationRepository;
import com.alliander.osgp.domain.core.repositories.DeviceModelRepository;
import com.alliander.osgp.domain.core.repositories.DeviceRepository;
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

    @Then("^the dlms device with identification \"([^\"]*)\" exists$")
    public void theDlmsDeviceWithIdentificationExists(final String deviceIdentification) throws Throwable {

        this.deviceSteps.theDeviceWithIdExists(deviceIdentification);

        final DlmsDevice dlmsDevice = this.dlmsDeviceRepository.findByDeviceIdentification(deviceIdentification);
        assertNotNull("DLMS device with identification " + deviceIdentification + " in protocol database", dlmsDevice);
    }

    @Then("^the smart meter is registered in the core database$")
    public void theSmartMeterIsRegisteredInTheCoreDatabase(final Map<String, String> settings) throws Throwable {
        final SmartMeter smartMeter = this.smartMeterRepository
                .findByDeviceIdentification(settings.get(PlatformSmartmeteringKeys.DEVICE_IDENTIFICATION));

        assertNotNull(smartMeter);
        assertEquals(smartMeter.getSupplier(), settings.get(PlatformSmartmeteringKeys.SUPPLIER));
        assertEquals(smartMeter.getChannel(), settings.get(PlatformSmartmeteringKeys.CHANNEL));
        assertEquals(smartMeter.getMbusIdentificationNumber(),
                Helpers.getLong(settings, PlatformSmartmeteringKeys.MBUS_IDENTIFICATION_NUMBER, null));
        assertEquals(smartMeter.getMbusManufacturerIdentification(),
                settings.get(PlatformSmartmeteringKeys.MBUS_MANUFACTURER_IDENTIFICATION));
        assertEquals(smartMeter.getMbusVersion(),
                Helpers.getShort(settings, PlatformSmartmeteringKeys.MBUS_VERSION, null));
        assertEquals(smartMeter.getMbusDeviceTypeIdentification(),
                Helpers.getShort(settings, PlatformSmartmeteringKeys.MBUS_DEVICE_TYPE_IDENTIFICATION, null));
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

        final String deviceDescription = "DLMS device with identification " + deviceIdentification;
        final DlmsDevice dlmsDevice = this.dlmsDeviceRepository.findByDeviceIdentification(deviceIdentification);
        assertNotNull(deviceDescription + " must be in the protocol database", dlmsDevice);

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

        final String deviceDescription = "DLMS device with identification " + deviceIdentification;
        final DlmsDevice dlmsDevice = this.dlmsDeviceRepository.findByDeviceIdentification(deviceIdentification);
        assertNotNull(deviceDescription + " must be in the protocol database", dlmsDevice);

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
        final DlmsDevice dlmsDevice = this.dlmsDeviceRepository.findByDeviceIdentification(deviceIdentification);
        assertNotNull(deviceDescription + " must be in the protocol database", dlmsDevice);

        final SecurityKey masterKey = dlmsDevice.getValidSecurityKey(SecurityKeyType.E_METER_MASTER);
        assertNotNull("Master key for " + deviceDescription + " must be stored", masterKey);

        final String receivedMasterKey = (String) ScenarioContext.current()
                .get(PlatformSmartmeteringKeys.KEY_DEVICE_MASTERKEY);
        assertNotEquals("Stored master key for " + deviceDescription + " must be different from received key",
                receivedMasterKey, masterKey.getKey());
        final SecurityKey authenticationKey = dlmsDevice.getValidSecurityKey(SecurityKeyType.E_METER_AUTHENTICATION);
        assertNotNull("Authentication key for " + deviceDescription, authenticationKey);

        final String receivedAuthenticationKey = (String) ScenarioContext.current()
                .get(PlatformSmartmeteringKeys.KEY_DEVICE_AUTHENTICATIONKEY);
        assertNotEquals("Stored authentication key for " + deviceDescription + " must be different from received key",
                receivedAuthenticationKey, authenticationKey.getKey());

        final SecurityKey encryptionKey = dlmsDevice.getValidSecurityKey(SecurityKeyType.E_METER_ENCRYPTION);
        assertNotNull("Encryption key for " + deviceDescription, encryptionKey);

        final String receivedEncryptionKey = (String) ScenarioContext.current()
                .get(PlatformSmartmeteringKeys.KEY_DEVICE_AUTHENTICATIONKEY);
        assertNotEquals("Stored encryption key for " + deviceDescription + " must be different from received key",
                receivedEncryptionKey, encryptionKey.getKey());
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
        final DlmsDeviceBuilder dlmsDeviceBuilder = new DlmsDeviceBuilder().withSettings(inputSettings);

        if (inputSettings.containsKey(PlatformSmartmeteringKeys.GATEWAY_DEVICE_IDENTIFICATION)) {
            dlmsDeviceBuilder.getMbusEncryptionSecurityKeyBuilder().enable();
            dlmsDeviceBuilder.getMbusMasterSecurityKeyBuilder().enable();
        } else if (inputSettings.containsKey(PlatformSmartmeteringKeys.LLS1_ACTIVE)
                && "true".equals(inputSettings.get(PlatformSmartmeteringKeys.LLS1_ACTIVE))) {
            dlmsDeviceBuilder.getPasswordBuilder().enable();
        } else {
            dlmsDeviceBuilder.getEncryptionSecurityKeyBuilder().enable();
            dlmsDeviceBuilder.getMasterSecurityKeyBuilder().enable();
            dlmsDeviceBuilder.getAuthenticationSecurityKeyBuilder().enable();
        }

        final DlmsDevice dlmsDevice = dlmsDeviceBuilder.build();
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
        if (inputSettings.containsKey(PlatformKeys.KEY_DEVICE_MODEL)) {
            return this.deviceModelRepository.findByModelCode(inputSettings.get(PlatformKeys.KEY_DEVICE_MODEL));
        }
        return null;
    }
}

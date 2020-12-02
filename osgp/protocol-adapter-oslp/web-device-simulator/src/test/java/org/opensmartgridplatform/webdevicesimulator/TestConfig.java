/**
 * Copyright 2020 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.webdevicesimulator;

import static org.mockito.Mockito.mock;

import java.io.IOException;
import java.security.PrivateKey;
import java.security.PublicKey;

import org.opensmartgridplatform.shared.security.CertificateHelper;
import org.opensmartgridplatform.webdevicesimulator.application.services.DeviceManagementService;
import org.opensmartgridplatform.webdevicesimulator.application.services.OslpLogService;
import org.opensmartgridplatform.webdevicesimulator.domain.repositories.DeviceRepository;
import org.opensmartgridplatform.webdevicesimulator.domain.repositories.OslpLogItemRepository;
import org.opensmartgridplatform.webdevicesimulator.service.RegisterDevice;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:web-device-simulator.properties")
@PropertySource("classpath:web-device-simulator-test.properties")
public class TestConfig {

    @Value("${reboot.delay.seconds:5}")
    private int rebootDelayInSeconds;

    @Value("${response.delay.time:10}")
    private long responseDelayTime;
    @Value("${response.delay.random.range:20}")
    private long responseDelayRandomRange;

    @Value("${checkbox.device.registration.value:false}")
    private boolean checkboxDeviceRegistration;
    @Value("${checkbox.device.reboot.value:false}")
    private boolean checkboxDeviceReboot;
    @Value("${checkbox.light.switching.value:false}")
    private boolean checkboxLightSwitching;
    @Value("${checkbox.tariff.switching.value:false}")
    private boolean checkboxTariffSwitching;
    @Value("${checkbox.event.notification.value:false}")
    private boolean checkboxEventNotification;

    @Value("${configuration.ip.config.fixed.ip.address:192.168.0.100}")
    private String ipConfigFixedIpAddress;
    @Value("${configuration.ip.config.netmask:255.255.255.0}")
    private String ipConfigNetmask;
    @Value("${configuration.ip.config.gateway:192.168.0.1}")
    private String ipConfigGateway;
    @Value("${configuration.osgp.ip.address:168.63.97.65}")
    private String osgpIpAddress;
    @Value("${configuration.osgp.port.number:12122}")
    private int osgpPortNumber;
    @Value("${status.internal.ip.address:127.0.0.1}")
    private String statusInternalIpAddress;

    @Value("${oslp.security.test.signkey.path}")
    private String privateKeySigningServerPath;
    @Value("${oslp.security.simulator.verifykey.path}")
    private String publicKeySimulatorPath;
    @Value("${oslp.security.keytype}")
    private String securityKeyType;
    @Value("${oslp.security.provider}")
    private String securityProvider;

    @Bean
    public Integer rebootDelayInSeconds() {
        return this.rebootDelayInSeconds;
    }

    @Bean
    public Long responseDelayTime() {
        return this.responseDelayTime;
    }

    @Bean
    public Long reponseDelayRandomRange() {
        return this.responseDelayRandomRange;
    }

    @Bean
    public Boolean checkboxDeviceRegistrationValue() {
        return this.checkboxDeviceRegistration;
    }

    @Bean
    public Boolean checkboxDeviceRebootValue() {
        return this.checkboxDeviceReboot;
    }

    @Bean
    public Boolean checkboxLightSwitchingValue() {
        return this.checkboxLightSwitching;
    }

    @Bean
    public Boolean checkboxTariffSwitchingValue() {
        return this.checkboxTariffSwitching;
    }

    @Bean
    public Boolean checkboxEventNotificationValue() {
        return this.checkboxEventNotification;
    }

    @Bean
    public String configurationIpConfigFixedIpAddress() {
        return this.ipConfigFixedIpAddress;
    }

    @Bean
    public String configurationIpConfigNetmask() {
        return this.ipConfigNetmask;
    }

    @Bean
    public String configurationIpConfigGateway() {
        return this.ipConfigGateway;
    }

    @Bean
    public String configurationOsgpIpAddress() {
        return this.osgpIpAddress;
    }

    @Bean
    public Integer configurationOsgpPortNumber() {
        return this.osgpPortNumber;
    }

    @Bean
    public String statusInternalIpAddress() {
        return this.statusInternalIpAddress;
    }

    @Bean
    public PrivateKey privateKeySigningServer() throws IOException {
        return CertificateHelper.createPrivateKey(this.privateKeySigningServerPath, this.securityKeyType,
                this.securityProvider);
    }

    @Bean
    public PublicKey publicKeySimulator() throws IOException {
        return CertificateHelper.createPublicKey(this.publicKeySimulatorPath, this.securityKeyType,
                this.securityProvider);
    }

    @Bean
    public DeviceRepository deviceRepository() {
        return mock(DeviceRepository.class);
    }

    @Bean
    public DeviceManagementService deviceManagementService() {
        return mock(DeviceManagementService.class);
    }

    @Bean
    public RegisterDevice registerDevice() {
        return mock(RegisterDevice.class);
    }

    @Bean
    public OslpLogItemRepository oslpLogItemRepository() {
        return mock(OslpLogItemRepository.class);
    }

    @Bean
    public OslpLogService oslpLogService() {
        return mock(OslpLogService.class);
    }
}

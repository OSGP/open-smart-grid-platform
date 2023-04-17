/*
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.simulator.protocol.dlms.server;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.openmuc.jdlms.AuthenticationMechanism;
import org.openmuc.jdlms.CosemInterfaceObject;
import org.openmuc.jdlms.LogicalDevice;
import org.openmuc.jdlms.SecuritySuite;
import org.openmuc.jdlms.SecuritySuite.EncryptionMechanism;

public class LogicalDeviceBuilder {

  private static final int PUBLIC_CLIENT_CLIENT_ID = 16;
  private int logicalDeviceId;
  private String logicalDeviceName;
  private String manufacturer;
  private long deviceId;
  private int clientId;
  private int securityLevel;
  private String authenticationKeyPath;
  private String encryptionKeyPath;
  private String masterKeyPath;

  private final List<CosemInterfaceObject> cosemClasses = new ArrayList<>();

  public LogicalDevice build() throws IOException {
    final LogicalDevice logicalDevice =
        new LogicalDevice(
            this.logicalDeviceId, this.logicalDeviceName, this.manufacturer, this.deviceId);

    byte[] auth = new byte[0];
    byte[] enc = new byte[0];
    byte[] master = new byte[0];
    if (this.authenticationKeyPath != null) {
      auth = Files.readAllBytes(new File(this.authenticationKeyPath).toPath());
    }
    if (this.encryptionKeyPath != null) {
      enc = Files.readAllBytes(new File(this.encryptionKeyPath).toPath());
    }
    if (this.masterKeyPath != null) {
      master = Files.readAllBytes(new File(this.masterKeyPath).toPath());
    }

    if (1 == this.securityLevel) {
      final SecuritySuite securitySuite =
          SecuritySuite.builder()
              .setAuthenticationKey(auth)
              .setPassword("11111111".getBytes(StandardCharsets.UTF_8))
              .setAuthenticationMechanism(AuthenticationMechanism.LOW)
              .setGlobalUnicastEncryptionKey(enc)
              .setEncryptionMechanism(EncryptionMechanism.AES_GCM_128)
              .build();
      logicalDevice.addRestriction(this.clientId, securitySuite);
      logicalDevice.setMasterKey(master);
    } else if (5 == this.securityLevel) {
      final SecuritySuite securitySuite =
          SecuritySuite.builder()
              .setAuthenticationKey(auth)
              .setAuthenticationMechanism(AuthenticationMechanism.HLS5_GMAC)
              .setGlobalUnicastEncryptionKey(enc)
              .setEncryptionMechanism(EncryptionMechanism.AES_GCM_128)
              .build();
      logicalDevice.addRestriction(this.clientId, securitySuite);
      logicalDevice.setMasterKey(master);
    }

    if (this.clientId != PUBLIC_CLIENT_CLIENT_ID && this.securityLevel != 0) {
      // When creating a logical device with a secured non-public interface, add a public client.
      // This ensures that such devices always have a public client interface in addition to the
      // configured
      // interface (usually: management interface).
      // Not that this approach is not realistic in the sense that the public client should not
      // expose the same
      // objects as the secured interface, but for the purposes of a simulator this simplification
      // should be ok.
      this.addPublicClientTo(logicalDevice);
    }

    logicalDevice.registerCosemObject(this.cosemClasses);
    return logicalDevice;
  }

  private boolean addPublicClientTo(final LogicalDevice logicalDevice) {
    return logicalDevice.addRestriction(PUBLIC_CLIENT_CLIENT_ID, SecuritySuite.builder().build());
  }

  public LogicalDeviceBuilder setLogicalDeviceId(final int logicalDeviceId) {
    this.logicalDeviceId = logicalDeviceId;
    return this;
  }

  public LogicalDeviceBuilder setLogicalDeviceName(final String logicalDeviceName) {
    this.logicalDeviceName = logicalDeviceName;
    return this;
  }

  public LogicalDeviceBuilder setManufacturer(final String manufacturer) {
    this.manufacturer = manufacturer;
    return this;
  }

  public LogicalDeviceBuilder setDeviceId(final long deviceId) {
    this.deviceId = deviceId;
    return this;
  }

  public LogicalDeviceBuilder setClientId(final int clientId) {
    this.clientId = clientId;
    return this;
  }

  public LogicalDeviceBuilder setSecurityLevel(final int securityLevel) {
    this.securityLevel = securityLevel;
    return this;
  }

  public LogicalDeviceBuilder setAuthenticationKeyPath(final String authenticationKeyPath) {
    this.authenticationKeyPath = authenticationKeyPath;
    return this;
  }

  public LogicalDeviceBuilder setEncryptionKeyPath(final String encryptionKeyPath) {
    this.encryptionKeyPath = encryptionKeyPath;
    return this;
  }

  public LogicalDeviceBuilder setMasterKeyPath(final String masterKeyPath) {
    this.masterKeyPath = masterKeyPath;
    return this;
  }

  public LogicalDeviceBuilder addCosemClasses(final CosemInterfaceObject... cosemClasses) {
    this.cosemClasses.addAll(Arrays.asList(cosemClasses));
    return this;
  }

  @Override
  public String toString() {
    return "LogicalDeviceBuilder [logicalDeviceId="
        + this.logicalDeviceId
        + ", logicalDeviceName="
        + this.logicalDeviceName
        + ", manufacturer="
        + this.manufacturer
        + ", deviceId="
        + this.deviceId
        + ", clientId="
        + this.clientId
        + ", authenticationKeyPath="
        + this.authenticationKeyPath
        + ", encryptionKeyPath="
        + this.encryptionKeyPath
        + ", masterKeyPath="
        + this.masterKeyPath
        + "]";
  }
}

/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.domain.core.entities;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.UUID;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;

import org.hibernate.annotations.SortNatural;
import org.opensmartgridplatform.domain.core.valueobjects.FirmwareModuleData;
import org.opensmartgridplatform.shared.domain.entities.AbstractEntity;

/**
 * FirmwareFile entity class holds information about the device model or type.
 *
 * A FirmwareFile can be available for multiple device models, and a device
 * model can be a match for multiple firmware files.
 *
 * A FirmwareFile is uniquely defined by a DeviceModel and the combination of
 * module versions for the firmware modules in the file. A unique identification
 * is introduced to be able to reference a FirmwareFile between separate OSGP
 * components.
 */
@Entity
public class FirmwareFile extends AbstractEntity {

    private static final long serialVersionUID = 6996358385968307111L;

    @Column(unique = true, nullable = false, updatable = false)
    private String identification = newRandomIdentification();

    @ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE }, fetch = FetchType.EAGER)
    @JoinTable(name = "device_model_firmware_file",
            joinColumns = @JoinColumn(name = "firmware_file_id"),
            inverseJoinColumns = @JoinColumn(name = "device_model_id"))
    @OrderBy("modelCode")
    @SortNatural
    private final SortedSet<DeviceModel> deviceModels = new TreeSet<>();

    @OneToMany(mappedBy = "firmwareFile", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private final Set<FirmwareFileFirmwareModule> firmwareModules = new HashSet<>();

    @Column()
    private String filename;

    @Column(length = 100)
    private String description;

    @Column()
    private boolean pushToNewDevices;

    @Lob
    @Column()
    private byte[] file;

    @Column()
    private String hash;

    public FirmwareFile() {
        // Default constructor
    }

    public FirmwareFile(final String identification, final String filename, final String description,
            final boolean pushToNewDevices, final byte[] file, final String hash) {
        this.identification = identification;
        this.filename = filename;
        this.description = description;
        this.pushToNewDevices = pushToNewDevices;
        this.file = file;
        this.hash = hash;
    }

    public FirmwareFile(final String identification, final String filename, final String description,
            final boolean pushToNewDevices) {
        this(identification, filename, description, pushToNewDevices, null, null);
    }

    public FirmwareFile(final String filename, final String description, final boolean pushToNewDevices,
            final byte[] file, final String hash) {
        this(newRandomIdentification(), filename, description, pushToNewDevices, file, hash);
    }

    public FirmwareFile(final String filename, final String description, final boolean pushToNewDevices) {
        this(filename, description, pushToNewDevices, null, null);
    }

    private static String newRandomIdentification() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    public void updateFirmwareModuleData(final Map<FirmwareModule, String> versionsByModule) {
        this.firmwareModules.clear();

        for (final Entry<FirmwareModule, String> versionByModule : versionsByModule.entrySet()) {
            this.addFirmwareModule(versionByModule.getKey(), versionByModule.getValue());
        }
    }

    public String getIdentification() {
        return this.identification;
    }

    public SortedSet<DeviceModel> getDeviceModels() {
        return this.deviceModels;
    }

    public void addDeviceModel(final DeviceModel deviceModel) {
        this.deviceModels.add(deviceModel);
    }

    public void removeDeviceModel(final DeviceModel deviceModel) {
        this.deviceModels.remove(deviceModel);
    }

    /**
     * Returns an unmodifiable map of versions by firmware module.
     *
     * To alter the module versions with this firmware file, use
     * {@link #addFirmwareModule(FirmwareModule, String)} and/or
     * {@link #removeFirmwareModule(FirmwareModule)}. To prevent unexpected
     * situations firmware module versions should probably be configured as
     * close as possible to the registration of this firmware file, and be left
     * as such from that time on.
     */
    public Map<FirmwareModule, String> getModuleVersions() {
        final Map<FirmwareModule, String> moduleVersions = new TreeMap<>();
        for (final FirmwareFileFirmwareModule firmwareModule : this.firmwareModules) {
            moduleVersions.put(firmwareModule.getFirmwareModule(), firmwareModule.getModuleVersion());
        }
        return Collections.unmodifiableMap(moduleVersions);
    }

    /**
     * Registers the version for the given module as included in this firmware
     * file.
     *
     * <strong>NB</strong> Registering firmware module versions should only
     * happen when the firmware file itself is registered. Modifications
     * later-on will probably not be expected and could lead to unpredictable
     * results.
     *
     * @param firmwareModule
     *            the firmware module for which a version should be added as
     *            included with this firmware file.
     * @param moduleVersion
     *            the version of the specific {@code firmwareModule} in this
     *            firmware file.
     *
     * @throws IllegalArgumentException
     *             if this firmware file already has a version of the
     *             {@code firmwareModule}.
     */
    public void addFirmwareModule(final FirmwareModule firmwareModule, final String moduleVersion) {
        final FirmwareFileFirmwareModule firmwareFileFirmwareModule = new FirmwareFileFirmwareModule(this,
                firmwareModule, moduleVersion);
        /*
         * Equals for FirmwareFileFirmwareModule is based on FirmwareFile and
         * FirmwareModule only, so even for a different module version the
         * following call would return true.
         */
        if (this.firmwareModules.contains(firmwareFileFirmwareModule)) {
            throw new IllegalArgumentException(
                    "FirmwareFile already has a module version for " + firmwareModule.getDescription());
        }
        this.firmwareModules.add(firmwareFileFirmwareModule);
    }

    /**
     * Unregisters the version for the given module if it was previously
     * registered as included in this firmware file.
     *
     * <strong>NB</strong> Unregistering firmware module versions should
     * probably never happen, since versions are expected to be properly
     * registered when the firmware file itself is registered. Modifications
     * later-on will probably not be expected and could lead to unpredictable
     * results.
     *
     * @param firmwareModule
     *            the firmware module for which no version should be registered
     *            as included with this firmware file any longer.
     */
    public void removeFirmwareModule(final FirmwareModule firmwareModule) {
        for (final Iterator<FirmwareFileFirmwareModule> iterator = this.firmwareModules.iterator(); iterator
                .hasNext();) {
            final FirmwareFileFirmwareModule firmwareFileFirmwareModule = iterator.next();
            if (firmwareFileFirmwareModule.getFirmwareFile().equals(this)
                    && firmwareFileFirmwareModule.getFirmwareModule().equals(firmwareModule)) {
                iterator.remove();
                firmwareFileFirmwareModule.prepareForRemoval();
            }
        }
    }

    public String getFilename() {
        return this.filename;
    }

    public String getDescription() {
        return this.description;
    }

    public boolean getPushToNewDevices() {
        return this.pushToNewDevices;
    }

    /**
     * @deprecated Different types of modules can vary over time when new types
     *             of devices are added to the platform. Use the more general
     *             {@link #getModuleVersions()} instead.
     */
    @Deprecated
    public String getModuleVersionComm() {
        final Map<FirmwareModule, String> moduleVersions = this.getModuleVersions();
        for (final Entry<FirmwareModule, String> moduleVersion : moduleVersions.entrySet()) {
            final FirmwareModule firmwareModule = moduleVersion.getKey();
            if (FirmwareModuleData.MODULE_DESCRIPTION_COMM.equals(firmwareModule.getDescription())) {
                return moduleVersion.getValue();
            }
        }
        return null;
    }

    /**
     * @deprecated Different types of modules can vary over time when new types
     *             of devices are added to the platform. Use the more general
     *             {@link #getModuleVersions()} instead.
     */
    @Deprecated
    public String getModuleVersionFunc() {
        final Map<FirmwareModule, String> moduleVersions = this.getModuleVersions();
        /*
         * This firmware module version may have been mapped to the '
         * functional' module version, or - for smart meters - to the
         * 'active_firmware' module version. If there is no value for the
         * 'functional' module version, return the 'active_firmware' module
         * version (or null if neither is present).
         */
        String activeFirmwareVersion = null;
        for (final Entry<FirmwareModule, String> moduleVersion : moduleVersions.entrySet()) {
            final FirmwareModule firmwareModule = moduleVersion.getKey();
            if (FirmwareModuleData.MODULE_DESCRIPTION_FUNC.equals(firmwareModule.getDescription())) {
                return moduleVersion.getValue();
            } else if (FirmwareModuleData.MODULE_DESCRIPTION_FUNC_SMART_METERING
                    .equals(firmwareModule.getDescription())) {
                activeFirmwareVersion = moduleVersion.getValue();
            }
        }
        return activeFirmwareVersion;
    }

    /**
     * @deprecated Different types of modules can vary over time when new types
     *             of devices are added to the platform. Use the more general
     *             {@link #getModuleVersions()} instead.
     */
    @Deprecated
    public String getModuleVersionSec() {
        final Map<FirmwareModule, String> moduleVersions = this.getModuleVersions();
        for (final Entry<FirmwareModule, String> moduleVersion : moduleVersions.entrySet()) {
            final FirmwareModule firmwareModule = moduleVersion.getKey();
            if (FirmwareModuleData.MODULE_DESCRIPTION_SEC.equals(firmwareModule.getDescription())) {
                return moduleVersion.getValue();
            }
        }
        return null;
    }

    /**
     * @deprecated Different types of modules can vary over time when new types
     *             of devices are added to the platform. Use the more general
     *             {@link #getModuleVersions()} instead.
     */
    @Deprecated
    public String getModuleVersionMa() {
        final Map<FirmwareModule, String> moduleVersions = this.getModuleVersions();
        for (final Entry<FirmwareModule, String> moduleVersion : moduleVersions.entrySet()) {
            final FirmwareModule firmwareModule = moduleVersion.getKey();
            if (FirmwareModuleData.MODULE_DESCRIPTION_MA.equals(firmwareModule.getDescription())) {
                return moduleVersion.getValue();
            }
        }
        return null;
    }

    /**
     * @deprecated Different types of modules can vary over time when new types
     *             of devices are added to the platform. Use the more general
     *             {@link #getModuleVersions()} instead.
     */
    @Deprecated
    public String getModuleVersionMbus() {
        final Map<FirmwareModule, String> moduleVersions = this.getModuleVersions();
        for (final Entry<FirmwareModule, String> moduleVersion : moduleVersions.entrySet()) {
            final FirmwareModule firmwareModule = moduleVersion.getKey();
            if (FirmwareModuleData.MODULE_DESCRIPTION_MBUS.equals(firmwareModule.getDescription())) {
                return moduleVersion.getValue();
            }
        }
        return null;
    }

    public void setFilename(final String filename) {
        this.filename = filename;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public void setPushToNewDevices(final boolean pushToNewDevices) {
        this.pushToNewDevices = pushToNewDevices;
    }

    public String getHash() {
        return this.hash;
    }

    public void setHash(final String hash) {
        this.hash = hash;
    }

    @Lob
    @Basic(fetch = FetchType.LAZY)
    public byte[] getFile() {
        return this.file;
    }

    public void setFile(final byte[] file) {
        this.file = file;
    }

    @Override
    public String toString() {
        return "FirmwareFile [identification=" + this.identification + ", filename=" + this.filename + ", description="
                + this.description + ", pushToNewDevices=" + this.pushToNewDevices + ", file="
                + Arrays.toString(this.file) + ", hash=" + this.hash + "]";
    }
}

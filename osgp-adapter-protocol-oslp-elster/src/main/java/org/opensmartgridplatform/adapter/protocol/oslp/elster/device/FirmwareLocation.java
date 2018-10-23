/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.oslp.elster.device;

import org.apache.commons.lang3.StringUtils;

/**
 * Value object containing information on firmware location.
 */
public class FirmwareLocation {

    private String domain;
    private String path;

    /**
     * Create a new instance of {@link FirmwareLocation}.
     *
     * @param domain
     *            the domain on which the firmware is located.
     * @param path
     *            the path on which the firmware is located.
     */
    public FirmwareLocation(final String domain, final String path) {
        if (StringUtils.isBlank(domain)) {
            throw new IllegalArgumentException("Domain is empty or null.");
        }

        this.domain = cleanUpDomain(domain);
        this.path = cleanUpPath(path);
    }

    /**
     * Gets the domain on which the firmware is located.
     *
     * @return the domain on which the firmware is located.
     */
    public String getDomain() {
        return this.domain;
    }

    /**
     * Gets the full path on which the firmware is located.
     *
     * @return the full path on which the firmware is located.
     */
    public String getFullPath(final String filename) {
        final StringBuilder pathBuilder = new StringBuilder();

        if (!StringUtils.isBlank(this.path)) {
            pathBuilder.append("/");
            pathBuilder.append(this.path);
        }

        pathBuilder.append("/");
        pathBuilder.append(filename);

        return pathBuilder.toString();
    }

    private static String cleanUpDomain(final String domain) {
        String cleanDomain = domain;

        if (cleanDomain.endsWith("/")) {
            cleanDomain = cleanDomain.substring(0, cleanDomain.length() - 1);
        }

        if (cleanDomain.contains("://")) {
            cleanDomain = cleanDomain.substring(cleanDomain.indexOf("://") + 3);
        }

        return cleanDomain;
    }

    private static String cleanUpPath(final String path) {
        String cleanPath = path;

        if (cleanPath.endsWith("/")) {
            cleanPath = cleanPath.substring(0, cleanPath.length() - 1);
        }

        if (cleanPath.startsWith("/")) {
            cleanPath = cleanPath.substring(1);
        }

        return cleanPath;
    }
}

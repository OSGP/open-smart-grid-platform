/*
 * SPDX-FileCopyrightText: Copyright Contributors to the GXF project
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package org.opensmartgridplatform.cucumber.platform.common.config.ws;

public record NotificationWebServiceConnectionConfig(
    String notificationTargetUri,
    boolean notificationKeystoreUse,
    String notificationKeystoreType,
    String notificationKeystoreLocation,
    String notificationKeystorePassword,
    boolean notificationTruststoreUse,
    String notificationTruststoreType,
    String notificationTruststoreLocation,
    String notificationTruststorePassword) {}

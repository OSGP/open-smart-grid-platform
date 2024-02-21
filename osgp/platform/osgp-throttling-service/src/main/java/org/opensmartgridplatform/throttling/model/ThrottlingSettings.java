// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.throttling.model;

public interface ThrottlingSettings {

  int getMaxConcurrency();

  int getMaxNewConnectionRequests();

  long getMaxNewConnectionResetTimeInMs();
}

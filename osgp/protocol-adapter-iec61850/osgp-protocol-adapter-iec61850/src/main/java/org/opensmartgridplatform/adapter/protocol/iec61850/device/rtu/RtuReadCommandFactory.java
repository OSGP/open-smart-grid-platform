//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.adapter.protocol.iec61850.device.rtu;

public interface RtuReadCommandFactory<T, U> {

  RtuReadCommand<T> getCommand(final U filter);

  RtuReadCommand<T> getCommand(final String node);
}

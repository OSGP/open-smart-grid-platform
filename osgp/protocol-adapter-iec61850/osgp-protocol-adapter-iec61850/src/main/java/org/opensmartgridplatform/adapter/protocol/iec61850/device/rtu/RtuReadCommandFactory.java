/*
 * Copyright 2014-2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.iec61850.device.rtu;

public interface RtuReadCommandFactory<T, U> {

  RtuReadCommand<T> getCommand(final U filter);

  RtuReadCommand<T> getCommand(final String node);
}

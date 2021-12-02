/*
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.simulator.protocol.dlms.cosem.processing;

/** When no filtering has to be done, this filter can be applied. */
class DisabledFilter extends RangeDescriptorFilter {

  DisabledFilter() {
    super(null);
  }

  /**
   * Returns true for every Object.
   *
   * @return true
   */
  @Override
  public boolean match(final Object match) {
    return true;
  }
}

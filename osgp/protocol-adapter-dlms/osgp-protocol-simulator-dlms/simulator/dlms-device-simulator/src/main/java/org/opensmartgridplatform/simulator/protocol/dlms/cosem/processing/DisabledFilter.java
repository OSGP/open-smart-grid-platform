// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

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

// Copyright 2014-2017 Smart Society Services B.V.
// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.simulator.protocol.iec61850.server.logicaldevices;

import com.beanit.openiec61850.BasicDataAttribute;
import com.beanit.openiec61850.BdaVisibleString;
import com.beanit.openiec61850.ServerModel;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Simulates a Light Measurement RTU having 4 light sensors (SPGGIO1, SPGGIO2, SPGGIO3 and SPGGIO4).
 */
public class LightMeasurementRtu extends LogicalDevice {

  private static final Logger LOGGER = LoggerFactory.getLogger(LightMeasurementRtu.class);

  public LightMeasurementRtu(
      final String physicalDeviceName,
      final String logicalDeviceName,
      final ServerModel serverModel) {
    super(physicalDeviceName, logicalDeviceName, serverModel);
  }

  @Override
  public List<BasicDataAttribute> getAttributesAndSetValues(final Date timestamp) {
    // Not needed for the light measurement RTU
    return new ArrayList<>();
  }

  @Override
  public BasicDataAttribute getAttributeAndSetValue(
      final LogicalDeviceNode node, final String value) {
    // Not needed for the light measurement RTU
    return null;
  }

  @Override
  public List<BasicDataAttribute> writeValueAndUpdateRelatedAttributes(
      final String node, final BasicDataAttribute value) {
    final List<BasicDataAttribute> values = new ArrayList<>();

    if (LogicalDeviceNode.SPGGIO1_IND_D.getDescription().equals(node)) {
      LOGGER.info("Update the values for the light sensors");
      final byte[] newValue = ((BdaVisibleString) value).getValue();
      LOGGER.info("New value for {}: {}", node, new String(newValue));

      // Update the status of the 4 SPGGIO nodes, which represent the
      // light sensors, because we use changes in SPGGIO.Ind.d to trigger
      // their update.
      values.addAll(this.setGeneralIO(newValue));
    } else if (LogicalDeviceNode.SPGGIO2_IND_D.getDescription().equals(node)) {
      LOGGER.info("Update reporting control block enabled value");
      final byte[] bool = ((BdaVisibleString) value).getValue();
      final BasicDataAttribute bda = this.setReportingControlBlock(bool);
      if (bda != null) {
        values.add(bda);
      }
    } else {
      LOGGER.info("No special update action needed for setting node {}", value);
    }

    return values;
  }

  private BasicDataAttribute setReportingControlBlock(final byte[] bool) {
    try {
      if (bool == null) {
        LOGGER.error("Null");
        return null;
      }
      if (bool.length == 0) {
        LOGGER.error("Empty array");
        return null;
      }
      final boolean rcbEnabled = (bool[0] - 48) == 1;
      LOGGER.info("rcbEnabled = {}", rcbEnabled);
      return this.setBoolean(LogicalDeviceNode.RCB_ENABLED, rcbEnabled);
    } catch (final Exception e) {
      LOGGER.error("Unable to set BRC.enabled", e);
      return null;
    }
  }

  /**
   * Updates the value for the status of the 4 light sensors.
   *
   * @param bdaValue Array indicating which the status per light sensor. Each element in the array
   *     represents the status for one light sensor. Meaning of an element: 0 = OFF, greater than 0
   *     = ON. When there are less than 4 elements, the remaining light sensors will be set to OFF.
   * @return The updated values.
   */
  private List<BasicDataAttribute> setGeneralIO(final byte[] bdaValue) {
    final List<BasicDataAttribute> values = new ArrayList<>();

    for (short lmIndex = 1; lmIndex <= 4; lmIndex++) {
      boolean stVal = false;
      if (bdaValue != null && bdaValue.length >= lmIndex) {
        stVal = bdaValue[lmIndex - 1] - 48 > 0;
      }
      final BasicDataAttribute bda =
          this.setBoolean(
              LogicalDeviceNode.fromDescription("SPGGIO" + lmIndex + ".Ind.stVal"), stVal);
      values.add(bda);
    }

    return values;
  }
}

/*
 * SPDX-FileCopyrightText: Copyright Contributors to the GXF project
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package org.opensmartgridplatform.dlms.services;

import org.opensmartgridplatform.dlms.objectconfig.PowerQualityRequest;

public enum Protocol {
  DSMR_2_2("DSMR", "2.2", 20, 5, 14, 0, 0),
  DSMR_4_2_2("DSMR", "4.2.2", 43, 27, 14, 16, 10),
  SMR_4_3("SMR", "4.3", 44, 27, 14, 16, 11),
  SMR_5_0_0("SMR", "5.0.0", 49, 27, 14, 27, 18),
  SMR_5_1("SMR", "5.1", 49, 27, 14, 27, 18),
  SMR_5_2("SMR", "5.2", 50, 27, 14, 27, 19),
  SMR_5_5("SMR", "5.5", 51, 27, 14, 27, 19);

  private final String name;
  private final String version;
  private final int nrOfCosemObjects;
  private final int nrOfCosemObjectsOnDemandPrivate;
  private final int nrOfCosemObjectsOnDemandPublic;
  private final int nrOfCosemObjectsPeriodicPrivate;
  private final int nrOfCosemObjectsPeriodicPublic;

  Protocol(
      final String name,
      final String version,
      final int nrOfCosemObjects,
      final int nrOfCosemObjectsOnDemandPrivate,
      final int nrOfCosemObjectsOnDemandPublic,
      final int nrOfCosemObjectsPeriodicPrivate,
      final int nrOfCosemObjectsPeriodicPublic) {
    this.name = name;
    this.version = version;
    this.nrOfCosemObjects = nrOfCosemObjects;
    this.nrOfCosemObjectsOnDemandPublic = nrOfCosemObjectsOnDemandPublic;
    this.nrOfCosemObjectsOnDemandPrivate = nrOfCosemObjectsOnDemandPrivate;
    this.nrOfCosemObjectsPeriodicPublic = nrOfCosemObjectsPeriodicPublic;
    this.nrOfCosemObjectsPeriodicPrivate = nrOfCosemObjectsPeriodicPrivate;
  }

  public String getName() {
    return this.name;
  }

  public String getVersion() {
    return this.version;
  }

  public int getNrOfCosemObjects() {
    return this.nrOfCosemObjects;
  }

  public int getNrOfCosemObjects(final PowerQualityRequest protocol, final Profile profile) {
    if (PowerQualityRequest.ONDEMAND.equals(protocol) && Profile.PUBLIC.equals(profile)) {
      return this.nrOfCosemObjectsOnDemandPublic;
    } else if (PowerQualityRequest.ONDEMAND.equals(protocol) && Profile.PRIVATE.equals(profile)) {
      return this.nrOfCosemObjectsOnDemandPrivate;
    } else if (PowerQualityRequest.PERIODIC.equals(protocol) && Profile.PUBLIC.equals(profile)) {
      return this.nrOfCosemObjectsPeriodicPublic;
    } else if (PowerQualityRequest.PERIODIC.equals(protocol) && Profile.PRIVATE.equals(profile)) {
      return this.nrOfCosemObjectsPeriodicPrivate;
    }
    throw new IllegalArgumentException("Unknown combination");
  }
}

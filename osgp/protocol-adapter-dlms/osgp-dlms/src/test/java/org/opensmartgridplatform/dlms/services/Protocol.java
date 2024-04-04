/*
 * SPDX-FileCopyrightText: Copyright Contributors to the GXF project
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package org.opensmartgridplatform.dlms.services;

import lombok.Getter;
import org.opensmartgridplatform.dlms.objectconfig.PowerQualityRequest;

@Getter
public enum Protocol {
  DSMR_2_2("DSMR", "2.2", 35, 2, 4, 6, 14, 0, 0, 0, 0, false, false),
  DSMR_4_2_2("DSMR", "4.2.2", 71, 11, 27, 6, 14, 6, 13, 5, 6, true, false),
  SMR_4_3("SMR", "4.3", 72, 11, 27, 6, 14, 6, 13, 6, 7, true, false),
  SMR_5_0_0("SMR", "5.0.0", 82, 11, 27, 6, 14, 6, 15, 9, 14, true, true),
  SMR_5_1("SMR", "5.1", 86, 11, 27, 6, 14, 6, 15, 9, 14, true, true),
  SMR_5_2("SMR", "5.2", 93, 11, 27, 6, 14, 6, 15, 10, 15, true, true),
  SMR_5_5("SMR", "5.5", 97, 11, 27, 6, 14, 6, 15, 10, 15, true, true);

  private final String name;
  private final String version;
  private final int nrOfCosemObjects;
  private final int nrOfCosemObjectsPqOnActualSpPrivate;
  private final int nrOfCosemObjectsPqOnActualPpPrivate;
  private final int nrOfCosemObjectsPqOnActualSpPublic;
  private final int nrOfCosemObjectsPqOnActualPpPublic;
  private final int nrOfCosemObjectsPqPeriodicSpPrivate;
  private final int nrOfCosemObjectsPqPeriodicPpPrivate;
  private final int nrOfCosemObjectsPqPeriodicSpPublic;
  private final int nrOfCosemObjectsPqPeriodicPpPublic;
  private final boolean hasDefinableLoadProfile;
  private final boolean hasPqProfiles;

  Protocol(
      final String name,
      final String version,
      final int nrOfCosemObjects,
      final int nrOfCosemObjectsPqOnActualSpPrivate,
      final int nrOfCosemObjectsPqOnActualPpPrivate,
      final int nrOfCosemObjectsPqOnActualSpPublic,
      final int nrOfCosemObjectsPqOnActualPpPublic,
      final int nrOfCosemObjectsPqPeriodicSpPrivate,
      final int nrOfCosemObjectsPqPeriodicPpPrivate,
      final int nrOfCosemObjectsPqPeriodicSpPublic,
      final int nrOfCosemObjectsPqPeriodicPpPublic,
      final boolean hasDefinableLoadProfile,
      final boolean hasPqProfiles) {
    this.name = name;
    this.version = version;
    this.nrOfCosemObjects = nrOfCosemObjects;
    this.nrOfCosemObjectsPqOnActualSpPublic = nrOfCosemObjectsPqOnActualSpPublic;
    this.nrOfCosemObjectsPqOnActualPpPublic = nrOfCosemObjectsPqOnActualPpPublic;
    this.nrOfCosemObjectsPqOnActualSpPrivate = nrOfCosemObjectsPqOnActualSpPrivate;
    this.nrOfCosemObjectsPqOnActualPpPrivate = nrOfCosemObjectsPqOnActualPpPrivate;
    this.nrOfCosemObjectsPqPeriodicSpPublic = nrOfCosemObjectsPqPeriodicSpPublic;
    this.nrOfCosemObjectsPqPeriodicPpPublic = nrOfCosemObjectsPqPeriodicPpPublic;
    this.nrOfCosemObjectsPqPeriodicSpPrivate = nrOfCosemObjectsPqPeriodicSpPrivate;
    this.nrOfCosemObjectsPqPeriodicPpPrivate = nrOfCosemObjectsPqPeriodicPpPrivate;
    this.hasDefinableLoadProfile = hasDefinableLoadProfile;
    this.hasPqProfiles = hasPqProfiles;
  }

  public int getNrOfCosemObjects(final PowerQualityRequest pqRequest, final Profile profile) {
    if (PowerQualityRequest.ACTUAL_SP.equals(pqRequest) && Profile.PUBLIC.equals(profile)) {
      return this.nrOfCosemObjectsPqOnActualSpPublic;
    } else if (PowerQualityRequest.ACTUAL_PP.equals(pqRequest) && Profile.PUBLIC.equals(profile)) {
      return this.nrOfCosemObjectsPqOnActualPpPublic;
    } else if (PowerQualityRequest.ACTUAL_SP.equals(pqRequest) && Profile.PRIVATE.equals(profile)) {
      return this.nrOfCosemObjectsPqOnActualSpPrivate;
    } else if (PowerQualityRequest.ACTUAL_PP.equals(pqRequest) && Profile.PRIVATE.equals(profile)) {
      return this.nrOfCosemObjectsPqOnActualPpPrivate;
    } else if (PowerQualityRequest.PERIODIC_SP.equals(pqRequest)
        && Profile.PUBLIC.equals(profile)) {
      return this.nrOfCosemObjectsPqPeriodicSpPublic;
    } else if (PowerQualityRequest.PERIODIC_PP.equals(pqRequest)
        && Profile.PUBLIC.equals(profile)) {
      return this.nrOfCosemObjectsPqPeriodicPpPublic;
    } else if (PowerQualityRequest.PERIODIC_SP.equals(pqRequest)
        && Profile.PRIVATE.equals(profile)) {
      return this.nrOfCosemObjectsPqPeriodicSpPrivate;
    } else if (PowerQualityRequest.PERIODIC_PP.equals(pqRequest)
        && Profile.PRIVATE.equals(profile)) {
      return this.nrOfCosemObjectsPqPeriodicPpPrivate;
    }
    throw new IllegalArgumentException("Unknown combination");
  }

  public boolean hasDefinableLoadProfile() {
    return this.hasDefinableLoadProfile;
  }

  public boolean hasPqProfiles() {
    return this.hasPqProfiles;
  }
}

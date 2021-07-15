/*
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.reporting;

import com.beanit.openiec61850.BdaReasonForInclusion;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

public class Iec61850BdaReasonForInclusionHelper {

  final List<String> reasons = new ArrayList<>();
  final BdaReasonForInclusion reason;

  public Iec61850BdaReasonForInclusionHelper(final BdaReasonForInclusion reason) {
    this.reason = reason;
    this.init();
  }

  public String getInfo() {
    if (this.reason == null) {
      return "null";
    }
    return StringUtils.join(this.reasons, ", ");
  }

  private void init() {
    this.checkApplicationTrigger();
    this.checkDataChange();
    this.checkDataUpdate();
    this.checkGeneralInterrogation();
    this.checkIntegrity();
    this.checkQualityChange();
  }

  private void checkApplicationTrigger() {
    if (this.reason.isApplicationTrigger()) {
      this.reasons.add("ApplicationTrigger");
    }
  }

  private void checkDataChange() {
    if (this.reason.isDataChange()) {
      this.reasons.add("DataChange");
    }
  }

  private void checkDataUpdate() {
    if (this.reason.isDataUpdate()) {
      this.reasons.add("DataUpdate");
    }
  }

  private void checkGeneralInterrogation() {
    if (this.reason.isGeneralInterrogation()) {
      this.reasons.add("GeneralInterrogation");
    }
  }

  private void checkIntegrity() {
    if (this.reason.isIntegrity()) {
      this.reasons.add("Integrity");
    }
  }

  private void checkQualityChange() {
    if (this.reason.isQualityChange()) {
      this.reasons.add("QualityChange");
    }
  }
}

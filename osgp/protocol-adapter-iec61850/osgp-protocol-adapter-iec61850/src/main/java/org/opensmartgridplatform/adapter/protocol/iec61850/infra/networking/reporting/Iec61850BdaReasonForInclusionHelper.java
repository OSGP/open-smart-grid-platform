package org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.reporting;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import com.beanit.openiec61850.BdaReasonForInclusion;

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

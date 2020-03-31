package org.opensmartgridplatform.simulator.protocol.iec60870.domain;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import org.openmuc.j60870.ASdu;
import org.openmuc.j60870.ASduType;
import org.openmuc.j60870.CauseOfTransmission;
import org.openmuc.j60870.ie.IeQualifierOfInterrogation;
import org.openmuc.j60870.ie.InformationElement;
import org.openmuc.j60870.ie.InformationObject;

public interface Iec60870AsduFactory {

    default IeQualifierOfInterrogation defaultIeQualifierOfInterrogation() {
        final int stationInterrogation = 20;
        return new IeQualifierOfInterrogation(stationInterrogation);
    }

    default ASdu createInterrogationCommandAsdu() {
        return new Iec60870ASduBuilder().withTypeId(ASduType.C_IC_NA_1)
                .withCauseOfTransmission(CauseOfTransmission.ACTIVATION)
                .withInformationObjects(new InformationObject[] { new InformationObject(0,
                        new InformationElement[][] { { this.defaultIeQualifierOfInterrogation() } }) })
                .build();
    }

    default ASdu createInterrogationCommandResponseAsdu() {
        final long timestamp = ZonedDateTime.now(ZoneOffset.UTC).toInstant().toEpochMilli();
        return this.createInterrogationCommandResponseAsdu(timestamp);
    }

    ASdu createInterrogationCommandResponseAsdu(long timestamp);

    default ASdu createActivationTerminationResponseAsdu() {
        return new Iec60870ASduBuilder().withTypeId(ASduType.C_IC_NA_1)
                .withSequenceOfElements(false)
                .withCauseOfTransmission(CauseOfTransmission.ACTIVATION_TERMINATION)
                .withInformationObjects(new InformationObject[] { new InformationObject(0,
                        new InformationElement[][] { { this.defaultIeQualifierOfInterrogation() } }) })
                .build();
    }

}

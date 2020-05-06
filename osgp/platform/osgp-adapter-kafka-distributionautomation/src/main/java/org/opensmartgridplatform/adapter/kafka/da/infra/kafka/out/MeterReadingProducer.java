package org.opensmartgridplatform.adapter.kafka.da.infra.kafka.out;

import org.opensmartgridplatform.adapter.kafka.da.application.mapping.DistributionAutomationMapper;
import org.opensmartgridplatform.adapter.kafka.da.avro.MeterReading;
import org.opensmartgridplatform.domain.da.measurements.MeasurementReport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

// TODO - Rename class once AVRO message format is known
@Service
public class MeterReadingProducer {

    private final KafkaTemplate<String, MeterReading> kafkaTemplate;

    private final DistributionAutomationMapper mapper;

    @Autowired
    public MeterReadingProducer(
            @Qualifier("distributionAutomationKafkaTemplate") final KafkaTemplate<String, MeterReading> kafkaTemplate,
            final DistributionAutomationMapper mapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.mapper = mapper;
    }

    public void send(final MeasurementReport measurementReport) {

        // TODO - Map measurementReport to correct Avro message format and
        // send...
        final MeterReading meterReading = this.mapper.map(measurementReport, MeterReading.class);
        /*
         * No need for callback functionality now; by default, the template is
         * configured with a LoggingProducerListener, which logs errors and does
         * nothing when the send is successful.
         */
        this.kafkaTemplate.sendDefault(meterReading);
    }

}

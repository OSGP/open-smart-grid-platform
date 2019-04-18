package org.opensmartgridplatform.adapter.protocol.iec60870.application.mapping;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Arrays;

import org.junit.Test;
import org.openmuc.j60870.ASdu;
import org.openmuc.j60870.CauseOfTransmission;
import org.openmuc.j60870.IeQuality;
import org.openmuc.j60870.IeShortFloat;
import org.openmuc.j60870.IeTime56;
import org.openmuc.j60870.InformationElement;
import org.openmuc.j60870.InformationObject;
import org.openmuc.j60870.TypeId;
import org.opensmartgridplatform.dto.da.measurements.MeasurementDto;
import org.opensmartgridplatform.dto.da.measurements.MeasurementGroupDto;
import org.opensmartgridplatform.dto.da.measurements.MeasurementReportDto;
import org.opensmartgridplatform.dto.da.measurements.MeasurementReportHeaderDto;
import org.opensmartgridplatform.dto.da.measurements.elements.BitmaskMeasurementElementDto;
import org.opensmartgridplatform.dto.da.measurements.elements.FloatMeasurementElementDto;
import org.opensmartgridplatform.dto.da.measurements.elements.TimestampMeasurementElementDto;

public class Iec60870AsduConverterTest {
    private final Iec60870Mapper mapper = new Iec60870Mapper();

    private static final long timestamp = ZonedDateTime.now(ZoneOffset.UTC).toInstant().toEpochMilli();

    // @formatter:off
    private static final MeasurementReportDto MEASUREMENT_REPORT_DTO = new MeasurementReportDto(
            new MeasurementReportHeaderDto(
                    "M_ME_TF_1",
                    "SPONTANEOUS",
                    1,
                    2),
            Arrays.asList(new MeasurementGroupDto(
                    "1",
                    Arrays.asList(new MeasurementDto(Arrays.asList(
                            new FloatMeasurementElementDto(10.0f),
                            new BitmaskMeasurementElementDto((byte) 0),
                            new TimestampMeasurementElementDto(timestamp)))))));

    private static final ASdu ASDU = new ASdu(
            TypeId.M_ME_TF_1,
            false,
            CauseOfTransmission.SPONTANEOUS,
            false,
            false,
            1,
            2,
            new InformationObject[] {
                    new InformationObject(
                    1,
                    new InformationElement[][] { {
                        new IeShortFloat(10.0f),
                        new IeQuality(false, false, false, false, false),
                        new IeTime56(timestamp) } } )});
    // @formatter:on

    @Test
    public void shouldConvertAsduToMeasurementReportDto() {
        // Arrange
        final MeasurementReportDto expected = MEASUREMENT_REPORT_DTO;
        final ASdu source = ASDU;

        // Act
        final MeasurementReportDto actual = this.mapper.map(source, MeasurementReportDto.class);

        // Assert
        assertThat(actual).isEqualTo(expected);
    }
}

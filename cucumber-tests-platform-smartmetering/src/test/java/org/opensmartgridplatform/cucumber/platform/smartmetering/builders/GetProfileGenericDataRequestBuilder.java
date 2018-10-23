package org.opensmartgridplatform.cucumber.platform.smartmetering.builders;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Map;

import javax.xml.datatype.XMLGregorianCalendar;

import org.opensmartgridplatform.adapter.ws.schema.smartmetering.bundle.GetProfileGenericDataRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.common.CaptureObjectDefinitions;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.common.ObisCodeValues;
import org.opensmartgridplatform.cucumber.platform.helpers.DateConverter;
import org.opensmartgridplatform.cucumber.platform.smartmetering.PlatformSmartmeteringKeys;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.monitoring.ObisCodeValuesFactory;

public class GetProfileGenericDataRequestBuilder {

    private static String DATE_FORMAT = "yyyy-MM-dd hh:mm:ss";

    private ObisCodeValues obisCodeValues;
    private XMLGregorianCalendar beginDate;
    private XMLGregorianCalendar endDate;
    private CaptureObjectDefinitions selectedValues;

    public GetProfileGenericDataRequestBuilder fromParameterMap(final Map<String, String> parameterMap)
            throws ParseException {

        final SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);

        this.beginDate = DateConverter
                .createXMLGregorianCalendar(sdf.parse(parameterMap.get(PlatformSmartmeteringKeys.KEY_BEGIN_DATE)));
        this.endDate = DateConverter
                .createXMLGregorianCalendar(sdf.parse(parameterMap.get(PlatformSmartmeteringKeys.KEY_END_DATE)));
        this.obisCodeValues = ObisCodeValuesFactory.fromParameterMap(parameterMap);

        return this;
    }

    public GetProfileGenericDataRequestBuilder withObisCodeValues(final ObisCodeValues obisCodeValues) {
        this.obisCodeValues = obisCodeValues;
        return this;
    }

    public GetProfileGenericDataRequestBuilder withBeginDate(final XMLGregorianCalendar beginDate) {
        this.beginDate = beginDate;
        return this;
    }

    public GetProfileGenericDataRequestBuilder withEndDate(final XMLGregorianCalendar endDate) {
        this.endDate = endDate;
        return this;
    }

    public GetProfileGenericDataRequestBuilder withSelectedValues(final CaptureObjectDefinitions selectedValues) {
        this.selectedValues = selectedValues;
        return this;
    }

    public GetProfileGenericDataRequest build() {
        final GetProfileGenericDataRequest request = new GetProfileGenericDataRequest();
        request.setObisCode(this.obisCodeValues);
        request.setBeginDate(this.beginDate);
        request.setEndDate(this.endDate);
        request.setSelectedValues(this.selectedValues);
        return request;
    }

}

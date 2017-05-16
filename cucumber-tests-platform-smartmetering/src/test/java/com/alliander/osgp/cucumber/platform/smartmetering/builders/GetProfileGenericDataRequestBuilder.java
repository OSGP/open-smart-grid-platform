package com.alliander.osgp.cucumber.platform.smartmetering.builders;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Map;

import javax.xml.datatype.XMLGregorianCalendar;

import com.alliander.osgp.adapter.ws.schema.smartmetering.bundle.GetProfileGenericDataRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.common.CaptureObjectDefinitions;
import com.alliander.osgp.adapter.ws.schema.smartmetering.common.ObisCodeValues;
import com.alliander.osgp.cucumber.platform.helpers.DateConverter;
import com.alliander.osgp.cucumber.platform.smartmetering.PlatformSmartmeteringKeys;

public class GetProfileGenericDataRequestBuilder {

    private static String DATE_FORMAT = "yyyy-MM-dd hh:mm:ss";

    private ObisCodeValues obisCodeValues;
    private XMLGregorianCalendar beginDate;
    private XMLGregorianCalendar endDate;
    private CaptureObjectDefinitions selectedValues;

    public GetProfileGenericDataRequestBuilder fromParameterMap(final Map<String, String> parameterMap)
            throws ParseException {

        final SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);

        this.beginDate = DateConverter.createXMLGregorianCalendar(sdf.parse(parameterMap.get(PlatformSmartmeteringKeys.KEY_BEGIN_DATE)));
        this.endDate = DateConverter.createXMLGregorianCalendar(sdf.parse(parameterMap.get(PlatformSmartmeteringKeys.KEY_END_DATE)));
        this.obisCodeValues = new ObisCodeValuesBuilder()
                .withObisCodeA(Short.parseShort(parameterMap.get(PlatformSmartmeteringKeys.OBIS_CODE_A)))
                .withObisCodeB(Short.parseShort(parameterMap.get(PlatformSmartmeteringKeys.OBIS_CODE_B)))
                .withObisCodeC(Short.parseShort(parameterMap.get(PlatformSmartmeteringKeys.OBIS_CODE_C)))
                .withObisCodeD(Short.parseShort(parameterMap.get(PlatformSmartmeteringKeys.OBIS_CODE_D)))
                .withObisCodeE(Short.parseShort(parameterMap.get(PlatformSmartmeteringKeys.OBIS_CODE_E)))
                .withObisCodeF(Short.parseShort(parameterMap.get(PlatformSmartmeteringKeys.OBIS_CODE_F))).build();

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

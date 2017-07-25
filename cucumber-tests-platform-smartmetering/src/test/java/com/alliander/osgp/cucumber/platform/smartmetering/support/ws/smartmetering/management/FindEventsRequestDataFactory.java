package com.alliander.osgp.cucumber.platform.smartmetering.support.ws.smartmetering.management;

import static com.alliander.osgp.cucumber.core.Helpers.getDate;

import java.util.GregorianCalendar;
import java.util.Map;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.joda.time.DateTime;

import com.alliander.osgp.adapter.ws.schema.smartmetering.management.EventLogCategory;
import com.alliander.osgp.adapter.ws.schema.smartmetering.management.FindEventsRequestData;
import com.alliander.osgp.cucumber.core.Helpers;
import com.alliander.osgp.cucumber.platform.smartmetering.PlatformSmartmeteringKeys;

public class FindEventsRequestDataFactory {

    public static FindEventsRequestData fromParameterMap(final Map<String, String> requestParameters) {

        final EventLogCategory eventLogCategory = EventLogCategory
                .fromValue(Helpers.getString(requestParameters, PlatformSmartmeteringKeys.EVENT_TYPE));
        final XMLGregorianCalendar beginDate = createXMLGregorianCalendar(requestParameters,
                PlatformSmartmeteringKeys.KEY_BEGIN_DATE);
        final XMLGregorianCalendar endDate = createXMLGregorianCalendar(requestParameters,
                PlatformSmartmeteringKeys.KEY_END_DATE);

        final FindEventsRequestData request = new FindEventsRequestData();

        request.setEventLogCategory(eventLogCategory);
        request.setFrom(beginDate);
        request.setUntil(endDate);

        return request;
    }

    private static final XMLGregorianCalendar createXMLGregorianCalendar(final Map<String, String> settings,
            final String key) {

        final DateTime date = getDate(settings, key, new DateTime());

        try {
            final GregorianCalendar gregCal = new GregorianCalendar();
            gregCal.setTime(date.toDate());
            return DatatypeFactory.newInstance().newXMLGregorianCalendar(gregCal);
        } catch (final DatatypeConfigurationException e) {
            throw new RuntimeException(e);
        }
    }
}

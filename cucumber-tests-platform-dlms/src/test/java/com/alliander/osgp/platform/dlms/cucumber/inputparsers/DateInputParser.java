package com.alliander.osgp.platform.dlms.cucumber.inputparsers;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Parses the date of a cucumber input string to a java.util.Date instance.
 *
 * Use this class to ensure the same format is used over all scenarios, and
 * suppress any parsing exception to avoid clutter in the builders.
 *
 */
public class DateInputParser {

    private static final DateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    /**
     * Parses the date of a cucumber input string to a java.util.Date instance.
     *
     * @param input
     *            string with format "yyyy-MM-dd"
     * @return Date instance
     */
    public static Date parse(final String input) {
        try {
            return FORMAT.parse(input);
        } catch (final ParseException e) {
            // TODO Auto-generated catch block
            return null;
        }
    }
}

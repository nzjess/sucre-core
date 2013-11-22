package org.ubercraft.sucre.coercer;

import static org.ubercraft.sucre.common.AssertUtil.notNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;

public class DateCoercer implements Coercer {

    private final List<SimpleDateFormat> dateFormats;

    public DateCoercer(TimeZone timezone, String... dateFormatStrings) {
        if (dateFormatStrings == null || dateFormatStrings.length == 0) {
            throw new IllegalArgumentException("must specify at least one date format");
        }
        Set<String> dateFormatStringSet = new LinkedHashSet<String>();
        for (String dateFormatString : dateFormatStrings) {
            dateFormatStringSet.add(notNull(dateFormatString, "date format must not be null"));
        }
        this.dateFormats = new ArrayList<SimpleDateFormat>(dateFormatStringSet.size());
        for (String dateFormatString : dateFormatStringSet) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormatString);
            simpleDateFormat.setTimeZone(timezone);
            this.dateFormats.add(simpleDateFormat);
        }
    }

    @Override
    public Date coerce(Object value, Class<?> type, boolean strict) {
        if (value == null || value instanceof Date) {
            return (Date)value;
        }
        String string = CoercerUtil.coerceToString(value);
        for (SimpleDateFormat dateFormat : dateFormats) {
            try {
                return dateFormat.parse(string);
            }
            catch (ParseException e) {
                // ignored
            }
        }
        if (strict) {
            throw new CoercerException("could not parse as date: " + string);
        }
        return null;
    }
}

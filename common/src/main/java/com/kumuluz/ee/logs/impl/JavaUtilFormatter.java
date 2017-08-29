package com.kumuluz.ee.logs.impl;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class JavaUtilFormatter extends Formatter {

    private static final DateTimeFormatter timestampFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    @Override
    public String format(LogRecord record) {

        ZonedDateTime zonedDateTime = Instant.ofEpochMilli(record.getMillis()).atZone(ZoneId.systemDefault());

        String source = record.getSourceClassName() != null ? record.getSourceClassName() : record.getLoggerName();
        String message = formatMessage(record);
        String throwable = "";

        if (record.getThrown() != null) {

            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            pw.println();
            record.getThrown().printStackTrace(pw);
            pw.close();
            throwable = sw.toString();
        }

        return zonedDateTime.format(timestampFormat) + " " +
                record.getLevel().getName() + " -- " +
                source + " -- " +
                message + throwable + "\n";
    }
}

package com.kumuluz.ee.logs.impl;

import java.io.UnsupportedEncodingException;
import java.util.logging.*;

public class JavaUtilConsoleHandler extends StreamHandler {

    /**
     * Create a {@link JavaUtilConsoleHandler} for {@link System#out}.
     */
    public JavaUtilConsoleHandler() {

        setLevel(Level.INFO);
        setFormatter(new JavaUtilFormatter());
        setOutputStream(System.out);

        try {
            setEncoding(null);
        } catch (UnsupportedEncodingException ignored) {
        }
    }

    /**
     * Publish a {@link LogRecord}.
     * <p>
     * The logging request was made initially to a {@link Logger} object,
     * which initialized the {@link LogRecord} and forwarded it here.
     * <p>
     * @param  record  description of the log event. A null record is
     *                 silently ignored and is not published
     */
    @Override
    public void publish(LogRecord record) {
        super.publish(record);

        flush();
    }

    /**
     * Override {@link StreamHandler#close} to do a flush but not
     * to close the output stream.  That is, we do <b>not</b>
     * close {@link System#out}.
     */
    @Override
    public void close() {
        flush();
    }
}

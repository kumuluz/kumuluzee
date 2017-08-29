package com.kumuluz.ee.logs.impl;

import java.io.UnsupportedEncodingException;
import java.util.logging.*;

public class JavaUtilConsoleHandler extends StreamHandler {

    /**
     * Create a <tt>JavaUtilConsoleHandler</tt> for <tt>System.out</tt>.
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
     * Publish a <tt>LogRecord</tt>.
     * <p>
     * The logging request was made initially to a <tt>Logger</tt> object,
     * which initialized the <tt>LogRecord</tt> and forwarded it here.
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
     * Override <tt>StreamHandler.close</tt> to do a flush but not
     * to close the output stream.  That is, we do <b>not</b>
     * close <tt>System.out</tt>.
     */
    @Override
    public void close() {
        flush();
    }
}

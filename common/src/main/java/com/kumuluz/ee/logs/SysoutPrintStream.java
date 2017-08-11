package com.kumuluz.ee.logs;

import java.io.PrintStream;

/**
 * Directs messages from IronJacamar to System.out. This avoids the need for jboss-stdio dependency.
 *
 * @author Marcos Koch Salvador
 * @since 2.4.0
 */
public class SysoutPrintStream extends PrintStream {

    public SysoutPrintStream() {
    	super(System.out, true);
    }
	
}

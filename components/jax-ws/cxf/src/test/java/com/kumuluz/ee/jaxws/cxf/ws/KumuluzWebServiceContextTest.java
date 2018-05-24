package com.kumuluz.ee.jaxws.cxf.ws;

import com.kumuluz.ee.jaxws.cxf.impl.WebServiceContextBean;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class KumuluzWebServiceContextTest {

    @Test
    public void shouldUseRightContext() throws InterruptedException {

        WsThread one = new WsThread();
        WsThread two = new WsThread();

        one.start();
        two.start();
        one.join();
        two.join();

        assertTrue("Thread 1 context not properly propagated", one.isSuccessful());
        assertTrue("Thread 2 context not properly propagated", two.isSuccessful());
    }

    public static class WsThread extends Thread {
        private boolean successful = true;

        public void run() {
            String threadName = Thread.currentThread().getName();

            KumuluzWebServiceContext.getInstance().setMessageContext(new WebServiceContextBean(threadName));

            for (int i = 0; i < 1000; i++) {
                //hoping for race condition to occur in 1k iterations
                successful &= threadName.equals(KumuluzWebServiceContext.getInstance().getMessageContext().get("id"));
            }

        }

        public boolean isSuccessful() {
            return successful;
        }
    }


}

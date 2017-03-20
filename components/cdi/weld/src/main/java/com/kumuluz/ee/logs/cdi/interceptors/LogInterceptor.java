/**
 * Copyright (c) Sunesis d.o.o.
 */
package com.kumuluz.ee.logs.cdi.interceptors;

import com.kumuluz.ee.logs.LogCommons;
import com.kumuluz.ee.logs.LogManager;
import com.kumuluz.ee.logs.cdi.Log;
import com.kumuluz.ee.logs.cdi.LogParams;
import com.kumuluz.ee.logs.messages.SimpleLogMessage;
import com.kumuluz.ee.logs.types.LogMethodContext;
import com.kumuluz.ee.logs.types.LogMethodMessage;

import javax.annotation.Priority;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import java.util.Arrays;
import java.util.HashMap;


/**
 * @author Rok Povse
 * @author Marko Skrjanec
 */
@Log
@Interceptor
@Priority(Interceptor.Priority.APPLICATION)
public class LogInterceptor {

    @AroundInvoke
    public Object logMethodEntryAndExit(InvocationContext context) throws Exception {

        // get annotation either from class or method
        Log annotation = context.getMethod().getDeclaredAnnotation(Log.class) != null ? context.getMethod()
                .getDeclaredAnnotation(Log.class) : context.getMethod().getDeclaringClass().getDeclaredAnnotation(Log
                .class);

        // get annotation params
        LogParams[] value = annotation.value();
        boolean methodCall = annotation.methodCall();

        // get logger
        LogCommons logger = LogManager.getCommonsLogger(context.getTarget().getClass().getSuperclass().getName());

        // set message
        LogMethodMessage message = new LogMethodMessage();

        // set metrics
        for (LogParams logParam : value) {
            if (LogParams.METRICS.equals(logParam)) {
                message.enableMetrics();
                break;
            }
        }

        SimpleLogMessage msg = new SimpleLogMessage();
        msg.setMessage("Entering method.");
        msg.setFields(new HashMap<String, String>());

        // set method call
        if (methodCall) {
            msg.getFields().put("class", context.getMethod().getDeclaringClass().getName());
            msg.getFields().put("method", context.getMethod().getName());

            if (context.getParameters() != null && context.getParameters().length > 0) {
                msg.getFields().put("parameters", Arrays.toString(context.getParameters()));
            }

        }
        message.enableCall(msg);

        // log entry
        LogMethodContext logMethodContext = logger.logMethodEntry(message);

        Object result = context.proceed();

        // set method call
        msg.setMessage("Exiting method.");
        msg.getFields().put("result", result != null ? result.toString() : null);
        logMethodContext.setCallExitMessage(msg);

        // log exit
        logger.logMethodExit(logMethodContext);

        return result;
    }
}
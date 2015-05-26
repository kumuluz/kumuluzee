package com.kumuluz.ee.jetty.jsp.test.beans;

import java.util.Date;

/**
 * @author Tilen
 */
public class TimeBean {

    private Date currentTime;

    public TimeBean() {

        currentTime = new Date();
    }

    public Date getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(Date currentTime) {
        this.currentTime = currentTime;
    }
}
